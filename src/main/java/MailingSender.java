import service.SettingsMail;
import threads.Mailing;

public class MailingSender {
    public static void main(String[] args) {

//        SettingsMail settings_mail = new SettingsMail();
        new SettingsMail();

        Thread myTreadMailing = new Thread(
                new Mailing()
        );

        myTreadMailing.start();
    }
}