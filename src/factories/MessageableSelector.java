package factories;

import mail.Messageable;
import mail.google.GMail;
import server.model.Mailbox;

/**
 * Created by irina on 3/7/16.
 */
public class MessageableSelector {
    public static Messageable serviceFor(Mailbox mailbox){
        String emailSuffix = mailbox.getEmailSuffix();
        if ("gmail.com".equals(emailSuffix)){
            return new GMail();
        } else {
            return null;
        }
    }
}
