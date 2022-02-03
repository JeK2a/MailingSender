import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import service.SettingsMail;
import threads.Mailing;
import wss.WSSChatClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MailingSender {
    public static void main(String[] args) {
        new SettingsMail();

        Mailing mailing = new Mailing();
        WSSChatClient wssChatClient = new WSSChatClient(mailing);
        wssChatClient.connectToWSS();

        while (true) {
            try {
                Thread.sleep(30000);
                wssChatClient.connectToWSS(); // TODO проверять и перезапускать WSS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            showThreads();
        }
    }

    private static void showThreads() {
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup parent;
        while ((parent = rootGroup.getParent()) != null) {
            rootGroup = parent;
        }

//                listThreads(rootGroup, "");
        System.out.println("=================================================================================");
        countThreads(rootGroup, "");
    }

    // List all threads and recursively list all subgroup
    public static void countThreads(ThreadGroup group, String indent) { // TODO раскоментировать
        int nt = group.activeCount();
        Thread[] threads = new Thread[nt * 2 + 10];
        nt = group.enumerate(threads, false);

        // List every thread in the group

        HashMap<String, Integer> hashMap = new HashMap<>();

        for (int i = 0; i < nt; i++) {
            Thread thread = threads[i];

            String threadName = thread.getName();

            String regex =
                (
                    (
                        (
                            threadName.indexOf("-") < threadName.indexOf(" ") &&
                            threadName.contains("-")
                        ) ||
                        !threadName.contains(" ")
                    ) ?
                        "-" :
                        " "
                );

            String[] arr_srt = threadName.split(regex);

            if (arr_srt.length > 1) {
                threadName = arr_srt[0];
            }

            hashMap.compute(threadName, (key, value) -> (value == null) ? 1 : value + 1);
        }

        hashMap.forEach((key, value) -> System.out.println("    Threads[" + key + " count " + value + "]"));

        // Recursively list all subgroups
        int ng = group.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[ng * 2 + 10];
        ng = group.enumerate(groups, false);

        for (int i = 0; i < ng; i++) {
            countThreads(groups[i], indent + "  ");
        }
    }

}