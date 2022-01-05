package threads;

public class Mailing implements Runnable {

    @Override
    public void run() {

        int pattern_id  = 1214;
        int maillist_id = 892;
        int task_id     = 1302;

        Thread myTreadTask = new Thread(
            new Task(
                pattern_id,
                maillist_id,
                task_id
            )
        );

        myTreadTask.start();
    }
}