package threads;

import wss.WSSChatClient;

import java.lang.management.ManagementFactory;
import java.util.HashMap;

public class Mailing implements Runnable {

    private static HashMap <Integer, Task> map_tasks = new HashMap<>();

    @Override
    public void run() {
//        WSSChatClient wssChatClient = new WSSChatClient();
//        wssChatClient.connectToWSS();

        Mailing.newTask(1214, 892, 1302); // TODO Test



//        for (int i = 0; i < 10; i++) {
//            try {
//                Thread.sleep(2000);
//                System.out.print(
//                        "("
//                                + Thread.activeCount() + ","
//                                + ManagementFactory.getThreadMXBean().getThreadCount() + ","
////                + ManagementFactory.getRuntimeMXBean().getName() + ","
////                + ManagementFactory.getMemoryMXBean().getObjectPendingFinalizationCount()
//                                + ") == "
//                );
//                System.out.println(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1024 / 1024);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        //          = ;
//         = ;
//             = ;

//        for (int i = 0; i < 10; i++) {
//            WSSChatClient.sendText("Test!", "Test!!!");
//
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }


//        wssChatClient.closeWSS();
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