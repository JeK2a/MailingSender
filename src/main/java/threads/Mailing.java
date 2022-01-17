package threads;

import wss.WSSChatClient;

import java.util.HashMap;

public class Mailing implements Runnable {

    private static HashMap <Integer, Task> map_tasks = new HashMap<>();

    @Override
    public void run() {
        WSSChatClient wssChatClient = new WSSChatClient();
        wssChatClient.connectToWSS();

        for (int i = 0; i < 10; i++) {
            WSSChatClient.sendText("Test!", "Test!!!");

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        wssChatClient.closeWSS();
    }

    public static void newTask(int pattern_id, int maillist_id, int task_id) {
        map_tasks.put(
            task_id,
            new Task(
                pattern_id,
                maillist_id,
                task_id
            )
        );
    }

    public static void delTask(int task_id) {
        map_tasks.get(task_id).stop();
    }

}