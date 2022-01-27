package threads;

import wss.WSSChatClient;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Mailing implements Runnable {

    private final HashMap <Integer, Task> map_tasks = new HashMap<>();
    private int count_tasks = 0;

    private Thread thread;
    private boolean stop = false;

    public Mailing() {
        thread = new Thread(this, "Mailing");
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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

        count_tasks++;
    }

    public void delTask(int task_id) {
        System.out.println("delTask(int task_id) " + task_id);

        if (map_tasks.get(task_id).stop()) {
            map_tasks.remove(task_id);
            System.out.println("Задача была удалена");
        } else {
            System.err.println("Не удалось удалить задачу " + task_id);
        }
    }

    @Override
    public String toString() {
        StringBuilder json = new StringBuilder("{\"tasks\": [");

        String tasks_str = "";

        for (Map.Entry<Integer, Task> entry : map_tasks.entrySet()) {
            json.append(entry.getValue().getTask_id()).append(","); // TODO
//            json.append(entry.getValue().getTask_id()).append("(").append(entry.getValue().getMap_senders()).append("),");
        }

        if (map_tasks.size() > 0) {
            json = new StringBuilder(json.substring(0, json.length() - 1));
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