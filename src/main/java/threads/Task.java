package threads;

public class Task implements Runnable {

    int pattern_id;
    int maillist_id;
    int task_id;

    public Task(int pattern_id, int maillist_id, int task_id) {
        this.pattern_id  = pattern_id;
        this.maillist_id = maillist_id;
        this.task_id     = task_id;
    }

    @Override
    public void run() {
        Thread myTreadSender = new Thread(
            new Sender(
                pattern_id,
                maillist_id,
                task_id
            )
        );

        myTreadSender.start();
    }
}