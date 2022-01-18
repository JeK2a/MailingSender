package threads;

import java.util.HashMap;
import java.util.Map;

public class Task implements Runnable {

    private int pattern_id;
    private int maillist_id;
    private int task_id;

    private static int count  = 0;
    private static int number = 0;
    private int count_senders = 1;

    private Thread thread;

    private boolean stop = false;

    private static HashMap<Integer, Sender> map_senders = new HashMap<>();

    public Task(int pattern_id, int maillist_id, int task_id) {
        this.pattern_id  = pattern_id;
        this.maillist_id = maillist_id;
        this.task_id     = task_id;

        thread = new Thread(this, "Task " + task_id);
        thread.start();

        count++;
        number++;
    }

    @Override
    public void run() {
        for (int i = 0; i < count_senders; i++) {
            newSender(pattern_id, maillist_id, task_id);
        }

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

    public static void newSender(int pattern_id, int maillist_id, int task_id) {
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
}