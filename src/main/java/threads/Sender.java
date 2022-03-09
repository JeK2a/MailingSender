package threads;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import service.SettingsMail;

public class Sender implements Runnable {

    private static int count  = 0;
    private static int number = 0;
    private static int j = 0;

    private int senderNumber;

    private int patternId;
    private int maillistId;
    private int taskId;

    private boolean stop = false;
    private boolean end  = false;

    Thread thread;

    private int line = 0;

    public Sender(int patternId, int maillistId, int taskId) {
        this.patternId  = patternId;
        this.maillistId = maillistId;
        this.taskId     = taskId;

        senderNumber = number++;

        thread = new Thread(this, "Sender " + senderNumber);
        thread.start();

        count++;
//        number++;
    }

    @Override
    public void run() {
        System.out.println("sender run");

        try {
            line = 1;

//            CloseableHttpResponse response = client.execute(request);
            List<BasicNameValuePair> params = new ArrayList<>(4); // Request parameters and other properties.

            params.add(new BasicNameValuePair("type", "email"));
            params.add(new BasicNameValuePair("pattern_id",  String.valueOf(patternId)));
            params.add(new BasicNameValuePair("maillist_id", String.valueOf(maillistId)));
            params.add(new BasicNameValuePair("task_id",     String.valueOf(taskId)));

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost              = new HttpPost(SettingsMail.getUrl());

            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            // TODO params +
//            HttpRequestBase request = new HttpGet(SettingsMail.getUrl()); //or HttpPost

            RequestConfig.Builder requestConfig = RequestConfig.custom(); // TODO вернуть
//            requestConfig.setConnectTimeout(60 * 1000);
            requestConfig.setConnectionRequestTimeout(20 * 1000);
//            requestConfig.setSocketTimeout(60 * 1000);
//
////            request.setConfig(requestConfig.build()); -
            httpPost.setConfig(requestConfig.build());
            // TODO params +

            while (!stop && !end) {
                line = 2;
//                String test = senderNumber + " ";
//                test += "1";
                System.out.println();

                if (senderNumber > 0) {
                    System.out.printf("%-" + senderNumber + "s" + senderNumber + " 1 sender while start", " ");
                } else {
                    System.out.print(senderNumber + " 1 sender while start");
                }
                //Execute and get the response.
                line = 3;
                HttpResponse httpResponse = httpClient.execute(httpPost); // TODO timeout
//              TODO  org.apache.http.NoHttpResponseException: my.tdfort.ru:443 failed to respond

                line = 4;
                HttpEntity httpEntity     = httpResponse.getEntity();
                line = 5;
                StringBuilder response    = new StringBuilder();
                line = 6;

                if (httpEntity != null) {
                    line = 7;
                    int ch;

                    try (InputStream inputStream = httpEntity.getContent()) {
                        line = 8;
                        while ((ch = inputStream.read()) != -1) {
                            line = 9;
                            response.append((char) ch);
                            line = 10;
                        }
                        line = 11;
                    }
                    line = 12;
                }

//                System.out.println(response);

                line = 13;

                if (response.toString().toCharArray()[0] != '{') {
                    line = 14;
                    System.err.println(senderNumber + " sender json error |||" + response + "|||");
                    line = 15;
                    continue;
                }

                line = 16;

                JSONObject jsonArray = (JSONObject) getArrayFromJSON(response);
                line = 17;
                String answer        = String.valueOf(jsonArray.get("answer"));
                line = 18;

//                System.out.println(answer);

                switch (answer) {
                    case "Все задачи вополнены":
                        line = 19;
                        stop = true;
                        end  = true;
                        return;
                    case "получатель в блеклисте":
                        line = 20;
                        continue;
                    default:
                        line = 21;
//                        System.out.println(senderNumber + " 2 count - " + ++j);

                        System.out.println();

                        if (senderNumber > 0) {
                            System.out.printf("%-" + senderNumber + "s" + senderNumber + " 2 count " + ++j, " ");
                        } else {
                            System.out.printf(senderNumber + " 2 count " + ++j);
                        }

//                        System.out.printf("%-" + senderNumber + "s" + senderNumber + " 2 count " + ++j, " ");
//                        test += "2";

                        line = 22;
                        int count      = Integer.parseInt(String.valueOf(jsonArray.get("count")));
                        int countDone  = Integer.parseInt(String.valueOf(jsonArray.get("count_done")));
                        int countError = Integer.parseInt(String.valueOf(jsonArray.get("count_error")));
                        int countTotal = Integer.parseInt(String.valueOf(jsonArray.get("count_total")));
                        line = 23;

                        if (countDone >= countTotal) {
                            line = 24;
                            stop = true;
                            end  = true;

                            return;
                        }
                        line = 25;

                        break;
                }

                line = 26;

//                System.out.println(answer);

//                System.out.println(senderNumber + " 3 sender while end");

                System.out.println();

                if (senderNumber > 0) {
                    System.out.printf("%-" + senderNumber + "s" + senderNumber + " 3 sender while end", " ");
                } else {
                    System.out.print(senderNumber + " 3 sender while end");
                }

//                System.out.printf("%-" + senderNumber + "s" + senderNumber + " 3 sender while end", " ");
//                test += "3";
//
//                System.out.println(test);
                line = 27;
            }

            line = 28;

            System.out.println(senderNumber + "sender break");
            System.err.println("end");

            line = 29;
        } catch (Throwable e) {
            line = 30;
            System.err.println(e.getMessage());
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            line = 30;
            stop = true;
            count--;

            System.out.println(senderNumber + "sender finally");
        }
    }

    private static Object getArrayFromJSON(String jsonStr) {
        try {
            return new JSONParser().parse(jsonStr);
        } catch (ParseException e) {
            System.err.println(e);
            System.err.println(jsonStr);
            e.printStackTrace();

            return false;
        }
    }

    private static Object getArrayFromJSON(StringBuilder jsonStr) {
        return getArrayFromJSON(String.valueOf(jsonStr));
    }

    @Override
    protected void finalize() {
        count--;
    }

    public boolean stop() {
        stop = true;

        if (thread.isAlive()) {
//            thread.stop(); // TODO убить поток
            thread.isInterrupted();
        }

        return true;
    }

    public static int getCount() {
        return count;
    }

    public int getSenderNumber() {
        return senderNumber;
    }

    public int getPatternId() {
        return patternId;
    }

    public int getMaillistId() {
        return maillistId;
    }

    public int getTaskId() {
        return taskId;
    }

    public boolean isStop() {
        return stop;
    }

    public boolean isEnd() {
        return end;
    }

    public Thread getThread() {
        return thread;
    }

    @Override
    public String toString() {
        return "sn:" + senderNumber;
    }

    public int getLine() {
        return line;
    }

    public static int getNumber() {
        return number;
    }

    public static int getJ() {
        return j;
    }


}