package threads;

import wss.WSSChatClient;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Mailing implements Runnable {

    private final HashMap <Integer, Task> map_tasks = new HashMap<>();

    private Thread thread;
    private boolean stop = false;

    public Mailing() {
        thread = new Thread(this, "Mailing");
        thread.start();
    }

    @Override
    public void run() {
//        newTask(1214, 892, 1302, 5); // TODO Test

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

    public void newTask(int pattern_id, int maillist_id, int task_id, int count_streams) {
        map_tasks.put(
            task_id,
            new Task(
                pattern_id,
                maillist_id,
                task_id,
                count_streams
            )
        );
    }

    public void delTask(int task_id) {
        map_tasks.get(task_id).stop();
    }

    @Override
    public String toString() {
        StringBuilder json = new StringBuilder("{\"tasks\": [");

        String tasks_str = "";

        for (Map.Entry<Integer, Task> entry : map_tasks.entrySet()) {
            json.append(entry.getValue().getTask_id()).append("(" + entry.getValue().getMap_senders() + "),");
            tasks_str += entry.getValue().getTask_id() + "(" + entry.getValue().getMap_senders() + "),"; // TODO
//            json.append("\"").append(entry.getKey().replace("\"", "\\\\\"")).append("\": ").append(entry.getValue()).append(",");
        }

        if (map_tasks.size() > 0) {
//            json.substring(0, json.length() - 1);
            tasks_str.substring(0, tasks_str.length() - 1);
//            s.substring(0, s.length() - 1)
        }

        json.append(tasks_str);

        json.append("]}");

        return json.toString();
    }

//    private String getJsonFromMap(ConcurrentHashMap<String, MyFolder> map) {
//        StringBuffer tmpStr = new StringBuffer("{ ");
//
//        for(Map.Entry<String, MyFolder> e: map.entrySet()){
//            tmpStr.append("\"").append(e.getKey().replace("\"", "\\\\\"")).append("\": ").append(e.getValue()).append(",");
//        }
//
//        return tmpStr.substring(0, tmpStr.length() - 1) + "}";
//    }
}