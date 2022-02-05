package threads;

import java.util.HashMap;
import java.util.Map;

public class Mailing implements Runnable {

    private final HashMap <Integer, Task> mapTasks = new HashMap<>();
    private int countTasks = 0;

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

    public void newTask(int patternId, int maillistId, int taskId, int countStreams) {
        mapTasks.put(
            taskId,
            new Task(
                patternId,
                maillistId,
                taskId,
                countStreams
            )
        );

        countTasks++;
    }

    public void delTask(int taskId) {
        System.out.println("delTask(int task_id) " + taskId);

        if (mapTasks.get(taskId).stop()) {
            mapTasks.remove(taskId);
            System.out.println("Задача была удалена");
        } else {
            System.err.println("Не удалось удалить задачу " + taskId);
        }
    }

    @Override
    public String toString() {
        StringBuilder json = new StringBuilder("{\"tasks\": [");

        String tasksStr = "";

        for (Map.Entry<Integer, Task> entry : mapTasks.entrySet()) {
            json.append(entry.getValue().getTaskId()).append(","); // TODO
//            json.append(entry.getValue().getTask_id()).append("(").append(entry.getValue().getMap_senders()).append("),");
        }

        if (mapTasks.size() > 0) {
            json = new StringBuilder(json.substring(0, json.length() - 1));
        }

        json.append(tasksStr);

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