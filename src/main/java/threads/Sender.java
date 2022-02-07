package threads;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
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

    public Sender(int patternId, int maillistId, int taskId) {
        this.patternId  = patternId;
        this.maillistId = maillistId;
        this.taskId     = taskId;

        senderNumber = number;

        thread = new Thread(this, "Sender " + senderNumber);
        thread.start();

        count++;
        number++;
    }

    @Override
    public void run() {
        System.out.println("sender run");

        try {
            List<BasicNameValuePair> params = new ArrayList<>(4); // Request parameters and other properties.

            params.add(new BasicNameValuePair("type", "email"));
            params.add(new BasicNameValuePair("pattern_id",  String.valueOf(patternId)));
            params.add(new BasicNameValuePair("maillist_id", String.valueOf(maillistId)));
            params.add(new BasicNameValuePair("task_id",     String.valueOf(taskId)));

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost              = new HttpPost(SettingsMail.getUrl());

            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            while (!stop && !end) {
                //Execute and get the response.
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity     = httpResponse.getEntity();
                StringBuilder response    = new StringBuilder();

                if (httpEntity != null) {
                    int ch;

                    try (InputStream inputStream = httpEntity.getContent()) {
                        while ((ch = inputStream.read()) != -1) {
                            response.append((char) ch);
                        }
                    }
                }

//                System.out.println(response);

                JSONObject jsonArray = (JSONObject) getArrayFromJSON(response);
                String answer        = String.valueOf(jsonArray.get("answer"));

                System.out.println(answer);

                switch (answer) {
                    case "Все задачи вополнены":
                        stop = true;
                        end  = true;
                        return;
                    case "получатель в блеклисте":
                        break;
                    default:
                        System.out.println(senderNumber + " count - " + ++j);

                        int count       = Integer.parseInt(String.valueOf(jsonArray.get("count")));
                        int countDone  = Integer.parseInt(String.valueOf(jsonArray.get("count_done")));
                        int countError = Integer.parseInt(String.valueOf(jsonArray.get("count_error")));
                        int countTotal = Integer.parseInt(String.valueOf(jsonArray.get("count_total")));

                        if (countDone >= countTotal) {
                            stop = true;
                            end  = true;

                            return;
                        }

                        break;
                }

//                System.out.println(answer);

                System.out.println("sender while");
            }

            System.out.println("sender break");
            System.err.println("end");
        } catch (Throwable e) {
            System.err.println(e.getMessage());
            System.err.println(e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            stop = true;
            count--;

            System.out.println("sender finally");
        }

//        return;
    }

    private static Object getArrayFromJSON(String jsonStr) {
        try {
            return new JSONParser().parse(jsonStr);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println(e);

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
}