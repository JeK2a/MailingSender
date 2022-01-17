package wss;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import service.SettingsMail;
import threads.Mailing;

import javax.net.ssl.SSLContext;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class WSSChatClient {
    private static WebSocket webSocket = null;
    private static WebSocketFactory webSocketFactory;
    private static WebSocketAdapter webSocketAdapter;

    public static boolean result = false;

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

                        int pattern_id;
                        int maillist_id;
                        int task_id;

                        System.out.println("command: " + command);

                        switch (command) {
                            case "tasks_start":
                                jsonArray   = (JSONObject) getArrayFromJSON(data_in);

                                pattern_id  = Integer.parseInt(String.valueOf(jsonArray.get("pattern_id")));
                                maillist_id = Integer.parseInt(String.valueOf(jsonArray.get("maillist_id")));
                                task_id     = Integer.parseInt(String.valueOf(jsonArray.get("task_id")));

                                Mailing.newTask(pattern_id, maillist_id, task_id);

                                break;
                            case "tasks_stop":
                                jsonArray = (JSONObject) getArrayFromJSON(data_in);
                                task_id   = Integer.parseInt(String.valueOf(jsonArray.get("task_id")));
                                Mailing.delTask(task_id);

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
        } catch (com.neovisionaries.ws.client.WebSocketException e) {
            System.out.println("Не удалось переподключиться к WSS сокету");
            Thread.sleep(30000);
            return connectToWSS();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            WSSChatClient.result = result;
            return result;
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