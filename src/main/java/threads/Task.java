package threads;

import java.util.HashMap;
import java.util.Map;

public class Task implements Runnable {

    private final int count_streams;
    private int pattern_id;
    private int maillist_id;
    private int task_id;
//    private int count_senders = 1;

    private static int count  = 0;
    private static int number = 0;

    private Thread thread;
    private boolean stop = false;

    private final HashMap<Integer, Sender> map_senders = new HashMap<>();

    public Task(int pattern_id, int maillist_id, int task_id, int count_streams) {
        this.pattern_id    = pattern_id;
        this.maillist_id   = maillist_id;
        this.task_id       = task_id;
        this.count_streams = count_streams; // TODO

        thread = new Thread(this, "Task " + task_id);
        thread.start();

        count++;
        number++;
    }

    @Override
    public void run() {
        System.out.println(count_streams);

        for (int i = 0; i < count_streams; i++) {
            newSender(pattern_id, maillist_id, task_id);
            System.out.println("new Sender i");

        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(map_senders.size());

        System.out.println(map_senders);

//        while (true) {
//            try {
//                Thread.sleep(5000);
//
//                if (stop) {
//                    break;
//                }
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void newSender(int pattern_id, int maillist_id, int task_id) {
        map_senders.put(
            number++,
            new Sender(
                pattern_id,
                maillist_id,
                task_id
            )
        );
    }

    public boolean stop() {
        for (Map.Entry<Integer, Sender> entry : map_senders.entrySet()) {
            int sender_id = entry.getKey();

            if (entry.getValue().stop()) {
                map_senders.remove(sender_id);
                count--;
            }
        }

        if (map_senders.size() == 0) {
            stop = true;

            return true;
        }

        return false;
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

    public static int getCount() {
        return count;
    }

    public HashMap<Integer, Sender> getMap_senders() {
        return map_senders;
    }
}