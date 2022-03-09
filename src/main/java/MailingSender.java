import service.SettingsMail;
import threads.Mailing;
import wss.WSSChatClient;

import java.util.HashMap;

public class MailingSender {
    public static void main(String[] args) {
        Mailing mailing             = null;
        WSSChatClient wssChatClient = null;

        try {
            new SettingsMail();

            mailing       = new Mailing();
            wssChatClient = new WSSChatClient(mailing);
            wssChatClient.connectToWSS();

            while (true) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

//                if (!wssChatClient.checkWSS()) { // TODO return
//                    wssChatClient.connectToWSS();
//                }
//                wssChatClient.connectToWSS(); // TODO проверять и перезапускать WSS
//                showThreads();

                wssChatClient.sendText("tasks", mailing.toString());
            }

        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
        } finally {
            wssChatClient.closeWSS();
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

            String[] arrSrt = threadName.split(regex);

            if (arrSrt.length > 1) {
                threadName = arrSrt[0];
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