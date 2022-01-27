package wss;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.json.simple.JSONArray;
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
import java.util.List;

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

            webSocketAdapter = new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket ws, String message) {
                try {
                    if (!message.contains("{")) {
                        System.err.println("json error |||" + message + "|||");
                        return;
                    }

                    System.out.println("json |||" + message + "|||");

                    JSONObject jsonArray = (JSONObject) getArrayFromJSON(message);

                    String command = String.valueOf(jsonArray.get("subject"));
//                    String command = String.valueOf(jsonArray.get("message"));
                    String data_in = String.valueOf(jsonArray.get("message"));

                    System.out.println(data_in);

                    int pattern_id;
                    int maillist_id;
                    int task_id;

                    JSONObject json;
                    ArrayList<JSONObject> tasks_list;
                    HashMap<String, Integer> task_map;

                    System.out.println("command: " + command);

                    switch (command) {
                        // TODO get_tasks получить информацию о запущенных задачаз
                        case "tasks":
                            sendText("tasks", mailing.toString());

                            break;
                        case "tasks_start":
//                            String jsonStr = "{\"tesks\":[{\"position\":\"1\",\"task_id\":1302,\"maillist_id\":892,\"pattern_id\":1214},{\"position\":\"2\",\"task_id\":1318,\"maillist_id\":820,\"pattern_id\":1224}],\"count_streams\":\"5\"}";

                            json = (JSONObject) JSONValue.parse(data_in);

                            System.out.println(json);

                            int count_streams = Integer.parseInt(String.valueOf(json.get("count_streams")));
                            tasks_list        = (ArrayList<JSONObject>) json.get("tasks");

                            System.out.println(tasks_list);

                            if (tasks_list != null) {
                                for (JSONObject task_json : tasks_list) {
                                    System.out.println(task_json);

                                    task_map = (HashMap<String, Integer>) task_json;

                                    System.out.println(task_map.size());

                                    pattern_id  = Integer.parseInt(String.valueOf(task_map.get("pattern_id")));
                                    maillist_id = Integer.parseInt(String.valueOf(task_map.get("maillist_id")));
                                    task_id     = Integer.parseInt(String.valueOf(task_map.get("task_id")));

                                    mailing.newTask(pattern_id, maillist_id, task_id, count_streams);
                                    // TODO проверять запущена ли уже задача
                                    // TODO уменьшение количество потоков, переделать на количество писем в час

                                    Thread.sleep(500);

                                    sendText("tasks", mailing.toString()); // TODO подтверждение запуска
                                }
                            } else {
                                System.err.println("Запущенных задач не обнаружено");
                            }

                            break;
                        case "tasks_stop":
                            json       = (JSONObject) JSONValue.parse(data_in);

                            System.out.println("json " + json);

                            tasks_list = (ArrayList<JSONObject>) json.get("tasks");

                            System.out.println("tasks_list " + tasks_list);

                            System.out.println("tasks_list");
                            System.out.println(tasks_list);

                            if (tasks_list != null) {
                                System.out.println("tasks_list.count " + tasks_list.size());


                                for (JSONObject task_json : tasks_list) {
                                    System.out.println(task_json);

                                    task_map = (HashMap<String, Integer>) task_json;

                                    System.out.println(task_map.size());

                                    task_id     = Integer.parseInt(String.valueOf(task_map.get("task_id")));

                                    mailing.delTask(task_id);
                                    // TODO проверять запущена ли уже задача
                                    // TODO уменьшение количество потоков, переделать на количество писем в час

                                    Thread.sleep(500);

                                    sendText("tasks", mailing.toString()); // TODO подтверждение запуска
                                }
                            } else {
                                System.out.println("tasks_list == null");
                            }

                            Thread.sleep(500);

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
            };

            // Create a custom SSL context.
            SSLContext context = NaiveSSLContext.getInstance("TLS");

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
            if (webSocket != null && webSocket.isOpen()) {
                webSocket.sendClose();
                webSocket.disconnect();
            }

            restart();

            System.out.println(SettingsMail.getUrlWSS());

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

    public boolean closeWSS() {
       return webSocket.sendClose() != null;
    }

    public static void sendText(String subject, String text) {
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