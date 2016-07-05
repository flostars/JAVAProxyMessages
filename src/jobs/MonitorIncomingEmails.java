package jobs;

import java.util.List;

import config.SystemConfiguration;
import db.DatabaseException;
import db.manager.DBManager;
import factories.MessageableSelector;
import mail.MailIdentifier;
import mail.MailIdentifierStrategy;
import mail.Messageable;
import mail.google.GMail;
import server.dao.MailboxDAO;
import server.dao.ResponseDAO;
import server.model.Mailbox;
import server.model.Message;
import server.model.Response;

public class MonitorIncomingEmails extends GenericJob implements Performable {

	private ResponseDAO responseDAO = new ResponseDAO();
	private MailboxDAO mailboxDAO = new MailboxDAO();

	public void perform(){
		try {
			List<Mailbox> allMailboxes = mailboxDAO.listMailboxes();
			for (Mailbox mailbox : allMailboxes) {
				log("Monitoring mailbox " + mailbox.getEmailAddress());
				Messageable reader = MessageableSelector.serviceFor(mailbox);
				List<Response> allIncomingMessages = reader.readMessages(mailbox);
				
				log("Incoming messages count for " + mailbox.getEmailAddress() + ": " + allIncomingMessages.size());
				
				for (Response response : allIncomingMessages) {
                    System.out.println(response);
					storeResponse(mailbox, response);
				}
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	private void storeResponse(Mailbox mailbox, Response response) throws DatabaseException {
		String emailBody = response.getResponseEmailBody();
		Message message = MailIdentifier.matchIncomingEmailToMessage(mailbox, emailBody, SystemConfiguration.emailIdentifierStrategy);
		if (message != null){
			response.setMessageID(message.getId());
			responseDAO.createResponse(response);
		} else {
            System.out.println("Could not map incoming email to message");
            // TODO handle this
		}
	}

	public static void main(String args[]) {
		new MonitorIncomingEmails().perform();
	}
}
