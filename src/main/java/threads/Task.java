package threads;

import java.util.HashMap;
import java.util.Map;

public class Task implements Runnable {

    private int pattern_id;
    private int maillist_id;
    private int task_id;
    private int count_streams;
    private static int count = 0;

    private Thread thread;
    private boolean stop = false;

    private HashMap<Integer, Sender> map_senders = new HashMap<>();
    private int count_senders = 0; // TODO количество отправщиков должно соответствовать количеству потоков
    private int number_sender = 0;

    public Task(int pattern_id, int maillist_id, int task_id, int count_streams) {
        this.pattern_id    = pattern_id;
        this.maillist_id   = maillist_id;
        this.task_id       = task_id;
        this.count_streams = count_streams; // TODO

        count++;

        thread = new Thread(this, "Task " + task_id);
        thread.start();
    }

    @Override
    public void run() {
        System.out.println("task run");
        try {
            System.out.println(count_streams);

            for (int i = 0; i < count_streams; i++) {
                newSender(pattern_id, maillist_id, task_id);
                System.out.println("new Sender " + i);

            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(map_senders.size());

            System.out.println(map_senders);

            while (true) {
                if (isStop()) {
                    System.out.println("task break");
                    break;
                }

                for (Map.Entry<Integer, Sender> entry : map_senders.entrySet()) {
                    int sender_id = entry.getKey();

                    Sender sender = map_senders.get(sender_id);

                    if (cheackSender(sender)) {

                    } else {
                        map_senders.remove(sender.getSender_number());
                        count_senders--;
                    }
                }

                // TODO проверять количество запущенных потоков

                try {
                    Thread.sleep(10000);
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

    public void newSender(int pattern_id, int maillist_id, int task_id) {
        map_senders.put(
            number_sender++,
            new Sender(
                pattern_id,
                maillist_id,
                task_id
            )
        );

        count_senders++;
    }

    public boolean stop() {
        System.out.println("count              =" + count);
        System.out.println("count_senders      =" + count_senders);
        System.out.println("map_senders.size() = " + map_senders.size());

        for (Map.Entry<Integer, Sender> entry : map_senders.entrySet()) { // TODO java.util.ConcurrentModificationException
            int sender_id = entry.getKey();

            Sender sender = map_senders.get(sender_id);

//            if (!sender.isStop() || sender.stop()) { // TODO проверять жив ли поток отправщика
//
//            }

            if (!sender.isStop()) {
                if (sender.stop()) {

                } else {
                    System.out.println("Не получилось остановить отправщик");
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Thread thread_tmp = sender.getThread();

            if (thread_tmp.isAlive()) {
                if (thread_tmp.isInterrupted()) {
                    System.out.println("thread_tmp.isInterrupted() true");
                } else {
                    System.out.println("thread_tmp.isInterrupted() false");
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (sender.isStop() ) {
//                map_senders.remove(sender_id);
                count_senders--;
            }
        }

        System.out.println("count              =" + count);
        System.out.println("count_senders      =" + count_senders);
        System.out.println("map_senders.size() = " + map_senders.size());

        if (count_senders == 0) {
            map_senders = null;
            stop        = true;

            return true;
        } else {
            System.out.println("Не удалось удалить все отправщики в " + task_id + " (количество оставшихся " + count_senders + ")");

            return false;
        }
    }

    public boolean cheackSender(Sender sender) {
        boolean run = true;
        Thread thread_tmp = sender.getThread();

//        if (sender.isStop() || !sender.getThread().isAlive()) { // TODO проверять жив ли поток отправщика
//            if (!sender.isStop()) {
//                sender.stop();
//            }
//
//            return false;
//        }

        if (sender.isStop()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            run = false;
        }

        if (isStop() && thread_tmp.isAlive()) {
//            thread_tmp.stop(); // TODO убить поток
            thread_tmp.isInterrupted();

            run = false;
        }

        return run;
    }

    @Override
    public String toString() {
        return "{\"pattern_id\": "     + pattern_id    +
                ",\"maillist_id\": "   + maillist_id   +
                ",\"task_id\"= "       + task_id       +
                ",\"count_streams\": " + count_streams +
                '}';
    }

    public int getPattern_id() {
        return pattern_id;
    }

    public int getMaillist_id() {
        return maillist_id;
    }

    public int getTask_id() {
        return task_id;
    }

    public HashMap<Integer, Sender> getMap_senders() {
        return map_senders;
    }

    public int getCount_streams() {
        return count_streams;
    }

    public Thread getThread() {
        return thread;
    }

    public boolean isStop() {
        return stop;
    }

    public int getCount_senders() {
        return count_senders;
    }

    public int getNumber_sender() {
        return number_sender;
    }
}