package wss;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import service.SettingsMail;
import threads.Mailing;

import javax.net.ssl.SSLContext;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;

public class WSSChatClient {
    private static WebSocket webSocket = null;
    private static WebSocketFactory webSocketFactory;
    private static WebSocketAdapter webSocketAdapter;
    private Mailing mailing;

    public static boolean result = false;

    public WSSChatClient(Mailing mailing) {
        this.mailing = mailing;
    }

    public WSSChatClient() {
        restart();
    }

    private void restart() {
        try {
            webSocketFactory = new WebSocketFactory();

//            webSocketAdapter = new WebSocketAdapter() {
//                @Override
//                public void onTextMessage(WebSocket websocket, String message) {
//                    // Received a text message.
//                }
//                @Override
//                public void onConnectError(WebSocket websocket, WebSocketException e){
//                    System.err.println(e.getMessage());
//                    System.err.println(e.getLocalizedMessage());
//                }
//            };

            webSocketAdapter = new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket ws, String message) {
                    try {
                        if (message.equals("partner not registered")) {
                            System.err.println(message);
                            return;
                        }
                        if (!message.contains("{")) {
                            System.err.println("json error |||" + message + "|||");
                            return;
                        }

                        System.out.println("json |||" + message + "|||");

                        JSONObject jsonArray = (JSONObject) getArrayFromJSON(message);

                        String command = String.valueOf(jsonArray.get("subject"));
    //                    String command = String.valueOf(jsonArray.get("message"));
                        String dataIn = String.valueOf(jsonArray.get("message"));

                        System.out.println(dataIn);

                        int patternId;
                        int maillistId;
                        int taskId;

                        JSONObject json;
                        ArrayList<JSONObject> tasksList;
                        HashMap<String, Integer> mapTask;

                        System.out.println("command: " + command);

                        switch (command) {
                            // TODO get_tasks получить информацию о запущенных задачаз
                            case "tasks":
                                sendText("tasks", mailing.toString());

                                break;
                            case "tasks_start":
                                json = (JSONObject) JSONValue.parse(dataIn);

                                System.out.println(json);

                                int countStreams = Integer.parseInt(String.valueOf(json.get("count_streams")));
                                tasksList        = (ArrayList<JSONObject>) json.get("tasks");

                                System.out.println(tasksList);

                                if (tasksList != null) {
                                    for (JSONObject jsonObject : tasksList) {
                                        System.out.println(jsonObject);

                                        mapTask = (HashMap<String, Integer>) jsonObject;

                                        System.out.println(mapTask.size());

                                        patternId  = Integer.parseInt(String.valueOf(mapTask.get("pattern_id")));
                                        maillistId = Integer.parseInt(String.valueOf(mapTask.get("maillist_id")));
                                        taskId     = Integer.parseInt(String.valueOf(mapTask.get("task_id")));

                                        mailing.newTask(patternId, maillistId, taskId, countStreams);
                                        // TODO проверять запущена ли уже задача
                                        // TODO уменьшение количество потоков, переделать на количество писем в час

                                        sendText("tasks", mailing.toString()); // TODO подтверждение запуска
                                    }
                                } else {
                                    System.err.println("Запущенных задач не обнаружено");
                                }

                                break;
                            case "tasks_stop":
                                json       = (JSONObject) JSONValue.parse(dataIn);

                                System.out.println("json " + json);

                                tasksList = (ArrayList<JSONObject>) json.get("tasks");

                                System.out.println("tasks_list " + tasksList);

                                System.out.println("tasks_list");
                                System.out.println(tasksList);

                                if (tasksList != null) {
                                    System.out.println("tasks_list.count " + tasksList.size());

                                    for (JSONObject jsonObject : tasksList) {
                                        System.out.println(jsonObject);

                                        mapTask = (HashMap<String, Integer>) jsonObject;

                                        System.out.println(mapTask.size());

                                        taskId = Integer.parseInt(String.valueOf(mapTask.get("task_id")));

                                        mailing.delTask(taskId);
                                        // TODO проверять запущена ли уже задача
                                        // TODO уменьшение количество потоков, переделать на количество писем в час

                                        sendText("tasks", mailing.toString()); // TODO подтверждение запуска
                                    }
                                } else {
                                    System.out.println("tasks_list == null");
                                }

                                sendText("tasks", mailing.toString()); // TODO подтверждение остановки

                                break;
    //                            case "":
    //                              break;
    //                        case "restart": // TODO перезапускать все
    //                            break;
                            case "exit":
                                System.out.println("========================STOP========================");
                                System.exit(0);
                                break;
                            case "close":
                                webSocket.sendClose();
                                break;
                            case "info":
    //                             TODO
                                break;
                            case "status":
                                break;
                            default:
                                System.out.println("Error command - " + command);
                                break;
                        }
                    } catch (Exception e) {
                        System.err.println("XXX" + message + "XXX");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException e){
                    System.err.println(e.getMessage());
                    System.err.println(e.getLocalizedMessage());
                }
            };

            // Create a custom SSL context.
            SSLContext context = NaiveSSLContext.getInstance("TLS"); // TODO постараться убрать дополнительный файл

            // Set the custom SSL context.
            webSocketFactory.setSSLContext(context);
            webSocketFactory.setVerifyHostname(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean connectToWSS() {
        boolean result = false;

        try {
//            if (webSocket != null && webSocket.isOpen()) {
            if (checkWSS()) {
                webSocket.sendClose();
                webSocket.disconnect();
            }

            restart();

            System.out.println(SettingsMail.getUrlWSS());

            webSocketFactory.setConnectionTimeout(120000);
            webSocket = webSocketFactory.createSocket(SettingsMail.getUrlWSS());
            webSocket.addListener(webSocketAdapter);

            webSocket.connect();
            result = true;

            sendText("tasks", mailing.toString());
        } catch (com.neovisionaries.ws.client.WebSocketException e) {
            System.out.println("Не удалось переподключиться к WSS сокету");
            Thread.sleep(30000);

            return connectToWSS();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            WSSChatClient.result = result;

            return result; // TODO поправить
        }
    }

    public boolean checkWSS() {
        if (webSocket != null && webSocket.isOpen()) {
            return true;
        }

        return false;
    }

    public boolean closeWSS() {
       return webSocket.sendClose() != null;
    }

    public void sendText(String subject, String text) {
        if (webSocket != null && webSocket.isOpen()) {
            text = forJSON(text);
            webSocket.sendText("{\"to\":\"worker:javamail_mailing\", \"subject\":\"" + subject + "\", \"message\":\"" + text + "\"}");
        } else {
            System.err.println("error send");
        }
    }

    public static String forJSON(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        int len = input.length();

        final StringBuilder result = new StringBuilder(len + len / 4); // сделаем небольшой запас, чтобы не выделять память потом
        final StringCharacterIterator iterator = new StringCharacterIterator(input);
        char ch = iterator.current();

        while (ch != CharacterIterator.DONE) {
            switch (ch) {
                case '\n': result.append("<br>"); break;
                case '\r': result.append("\\r");  break;
                case '\'': result.append("\\\'"); break;
//                case '"': result.append("\\\""); break;
                case '\"': result.append("\\\""); break;
                case '\t': result.append(" ");    break;
                default: result.append(ch);       break;
            }

            ch = iterator.next();
        }

        return result.toString();
    }

    private static Object getArrayFromJSON(String jsonStr) {
        try {
            return new JSONParser().parse(jsonStr);
        } catch (ParseException e) {
            return false;
        }
    }

//    private String getJsonFromMap(ConcurrentHashMap<String, EmailAccount> map) {
//        StringBuffer tmpStr = new StringBuffer("{ ");
//
//        for (Map.Entry<String, EmailAccount> e: map.entrySet()){
//            tmpStr.append("\"").append(e.getKey()).append("\": ").append(e.getValue()).append(",");
//        }
//
//        return tmpStr.substring(0, tmpStr.length() - 1) + "}";
//    }

}