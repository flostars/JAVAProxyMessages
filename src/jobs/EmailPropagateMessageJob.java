package jobs;

import config.SystemConfiguration;
import db.DatabaseException;
import db.manager.DBManager;
import mail.MailIdentifier;
import mail.MailIdentifierStrategy;
import mail.MailService;
import platform.Platform;
import platform.PlatformFactory;
import server.dao.MessageDAO;
import server.model.Message;
import server.model.PlatformCode;

import java.io.IOException;
import java.util.List;

public class EmailPropagateMessageJob extends GenericJob implements Performable{

    private List<Message> messages;
    private MessageDAO messageDAO = new MessageDAO();

    public void perform() throws DatabaseException {
        messages = messageDAO.messagesPendingEmailPropagation();
        
        log("Found " + messages.size() + " messages requiring propagation");
        
        MailIdentifierStrategy identifierStrategy = SystemConfiguration.emailIdentifierStrategy;

        for (Message message : messages) {
            try {
                MailIdentifier.addIdentifier(message, identifierStrategy);
                MailService.propagateMessage(message);
            } catch (IOException e) {
                e.printStackTrace(); // TODO handle?
            }
        }
    }

    public static void main(String[] args) throws DatabaseException {
        new EmailPropagateMessageJob().perform();
    }

}
