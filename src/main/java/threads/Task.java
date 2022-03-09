package threads;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Task implements Runnable {

    private int patternId;
    private int maillistId;
    private int taskId;
    private int countStreams;
    private static int count = 0;

    private Thread thread;
    private boolean stop = false;
    private boolean end  = false;

    private HashMap<Integer, Sender> mapSenders = new HashMap<>();
    private int countSenders = 0; // TODO количество отправщиков должно соответствовать количеству потоков
    private int numberSender = 0;

    public Task(int patternId, int maillistId, int taskId, int countStreams) {
        this.patternId    = patternId;
        this.maillistId   = maillistId;
        this.taskId       = taskId;
        this.countStreams = countStreams; // TODO

        count++;

        thread = new Thread(this, "Task " + taskId);
        thread.start();
    }

    @Override
    public void run() {
        System.out.println("task run");

        try {
            System.out.println(countStreams);

            for (int i = 0; i < countStreams; i++) {
                newSender(patternId, maillistId, taskId);
                System.out.println("new Sender " + i);

            }

            System.out.println(mapSenders.size());
            System.out.println(mapSenders);

            while (!isStop() && !isEnd()) {
                if (isStop()) {
                    System.out.println("task break");
                    break;
                }

//                Iterator<Map.Entry<Integer, Sender>> iterator = mapSenders.entrySet().iterator();

//                while (iterator.hasNext()) {
//                    Sender sender = iterator.next().getValue();
//
//                    if (!cheackSender(sender)) {
//                        mapSenders.remove(sender.getSenderNumber());
//                        countSenders--;
//                    }
//
//                }

//                for (Map.Entry<Integer, Sender> entry : mapSenders.entrySet()) {
//                    int senderId  = entry.getKey();
//                    Sender sender = mapSenders.get(senderId);
//
//                    if (!cheackSender(sender)) {
//                        mapSenders.remove(sender.getSenderNumber());
//                        countSenders--;
//                    }
//                }

                // TODO проверять количество запущенных потоков

                try {
                    Thread.sleep(10000);

                    if (mapSenders != null) {

                        System.out.println();
                        System.out.println("mapSenders count " + mapSenders.size());

                        for (Map.Entry<Integer, Sender> entry : mapSenders.entrySet()) {
                            int senderId = entry.getKey();

                            Sender sender = mapSenders.get(senderId);

                            int senderId_tmp = sender.getSenderNumber();

                            System.out.println(senderId + " " + senderId_tmp + " line " + sender.getLine());

//                        if (!cheackSender(sender)) {
//                            mapSenders.remove(sender.getSenderNumber());
//                            countSenders--;
//                        }
                        }
                    } else {
                        System.out.println("mapSenders == null");
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            System.err.println(e.getMessage());
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            stop = true;
            count--;

            System.out.println("task finally");
        }
    }

    public void newSender(int patternId, int maillistId, int taskId) {
        mapSenders.put(
            numberSender++,
            new Sender(
                patternId,
                maillistId,
                taskId
            )
        );

        countSenders++;
    }

    public boolean stop() {
        System.out.println("count              = " + count);
        System.out.println("count_senders      = " + countSenders);
        System.out.println("map_senders.size() = " + mapSenders.size());

        for (Map.Entry<Integer, Sender> entry : mapSenders.entrySet()) { // TODO java.util.ConcurrentModificationException
            int senderId = entry.getKey();

            Sender sender = mapSenders.get(senderId);

//            if (!sender.isStop() || sender.stop()) { // TODO проверять жив ли поток отправщика
//
//            }

            if (!sender.isStop()) {
                if (sender.stop()) {

                } else {
                    System.out.println("Не получилось остановить отправщик");
                }
            }

            Thread threadTmp = sender.getThread();

            if (threadTmp.isAlive()) {
                if (threadTmp.isInterrupted()) {
                    System.out.println("thread_tmp.isInterrupted() true"); // TODO ???
                } else {
                    System.out.println("thread_tmp.isInterrupted() false"); // TODO ??? thread_tmp.isInterrupted() false
                }
            }

            if (sender.isStop() ) {
//                map_senders.remove(sender_id);
                countSenders--;
            }
        }

        System.out.println("count              = " + count);
        System.out.println("count_senders      = " + countSenders);
        System.out.println("map_senders.size() = " + mapSenders.size());

        if (countSenders <= 0) {
            mapSenders = null;
            stop       = true;

            return true;
        } else {
            System.out.println("Не удалось удалить все отправщики в " + taskId + " (количество оставшихся " + countSenders + ")");

            return false;
        }
    }

    public boolean cheackSender(Sender sender) {
        boolean run = true;
        Thread threadTmp = sender.getThread();

//        if (sender.isStop() || !sender.getThread().isAlive()) { // TODO проверять жив ли поток отправщика
//            if (!sender.isStop()) {
//                sender.stop();
//            }
//
//            return false;
//        }

        if (sender.isEnd()) {
            run = false;
            end = true;
            stop();
        }

        if (sender.isStop()) {
            run = false;
        }

        if (isStop() && threadTmp.isAlive()) {
//            thread_tmp.stop(); // TODO убить поток
            threadTmp.isInterrupted();

            run = false;
        }

        return run;
    }

//    public void showSenders() {
//        Iterator<Map.Entry<Integer, Sender>> iterator = mapSenders.entrySet().iterator();
//
//        while (iterator.hasNext()) {
//            Sender sender = iterator.next().getValue();
//
//            sender.
//        }
//    }

    @Override
    public String toString() {
        return "{\"pattern_id\": "     + patternId +
                ",\"maillist_id\": "   + maillistId +
                ",\"task_id\"= "       + taskId +
                ",\"count_streams\": " + countStreams +
                '}';
    }

    @Override
    protected void finalize() {
        count--;
    }

    public int getPatternId() {
        return patternId;
    }

    public int getMaillistId() {
        return maillistId;
    }

    public int getTaskId() {
        return taskId;
    }

    public HashMap<Integer, Sender> getMapSenders() {
        return mapSenders;
    }

    public int getCountStreams() {
        return countStreams;
    }

    public Thread getThread() {
        return thread;
    }

    public boolean isStop() {
        return stop;
    }

    public boolean isEnd() {
        return end;
    }

    public int getCountSenders() {
        return countSenders;
    }

    public int getNumberSender() {
        return numberSender;
    }
}