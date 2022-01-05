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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import service.SettingsMail;

public class Sender implements Runnable {

    String pattern_id;
    String maillist_id;
    String task_id;

    public Sender(int pattern_id, int maillist_id, int task_id) {
        this.pattern_id  = String.valueOf(pattern_id);
        this.maillist_id = String.valueOf(maillist_id);
        this.task_id     = String.valueOf(task_id);
    }

    @Override
    public void run() {
        try {
//            while (true) {
            for (int i = 0; i < 3; i++) {
                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpPost httpPost = new HttpPost(SettingsMail.getUrl());

                // Request parameters and other properties.
                List<BasicNameValuePair> params = new ArrayList<>(2);

                params.add(new BasicNameValuePair("pattern_id", pattern_id));
                params.add(new BasicNameValuePair("maillist_id", maillist_id));
                params.add(new BasicNameValuePair("task_id", task_id));
                params.add(new BasicNameValuePair("type", "email"));

                httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

                //Execute and get the response.
                HttpResponse httpResponse = httpclient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();

                StringBuilder response = new StringBuilder();

                int data;

                if (httpEntity != null) {
                    try (InputStream inputStream = httpEntity.getContent()) {
                        //                int data = inputStream.read();
                        while ((data = inputStream.read()) != -1) {
                            response.append((char) data);
                        }
                    }
                }

                System.out.println(response);

                JSONObject jsonArray = (JSONObject) getArrayFromJSON(response);

//                String tmp = String.valueOf(jsonArray.get("tmp"));

                String answer   = String.valueOf(jsonArray.get("answer"));
                int count       = Integer.parseInt(String.valueOf(jsonArray.get("count")));
                int count_done  = Integer.parseInt(String.valueOf(jsonArray.get("count_done")));
                int count_error = Integer.parseInt(String.valueOf(jsonArray.get("count_error")));
                int count_total = Integer.parseInt(String.valueOf(jsonArray.get("count_total")));

                System.out.println(answer);

                System.out.println(count);
                System.out.println(count_done);
                System.out.println(count_error);
                System.out.println(count_total);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}