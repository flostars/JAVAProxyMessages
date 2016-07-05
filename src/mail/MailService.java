package mail;

import db.DatabaseException;
import factories.MailboxSelector;
import factories.MessageableSelector;
import server.dao.MessageDAO;
import server.model.Mailbox;
import server.model.Message;

import java.io.IOException;

public class MailService {

    private static MessageDAO messageDAO = new MessageDAO();

    public static void propagateMessage(Message message) throws DatabaseException, IOException {
        System.out.println("Will propagate message " + message.getId() + " to " + message.getPropagationEmail());

        Mailbox mailbox = MailboxSelector.selectFor(message);

        if (mailbox != null) {
            messageDAO.setMailbox(message, mailbox);
            Messageable messageable = MessageableSelector.serviceFor(mailbox);

            if (messageable != null) {
                System.out.println("Will send email for message " + message.getId());
                messageable.sendMessage(mailbox, message);
                messageDAO.setPropagated(message);
            } else {
                System.out.println("Could not find Messageable for " + mailbox.getEmailAddress());
                // TODO handle this, some error must be logged
            }
        }
        else {
            System.out.println("Could not find a free Mailbox");
            // TODO handle this, some error must be logged
        }


    }
}
