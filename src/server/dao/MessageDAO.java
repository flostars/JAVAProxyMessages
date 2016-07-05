package server.dao;

import db.DatabaseException;
import db.manager.DBManager;
import server.model.Mailbox;
import server.model.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageDAO {
	private static final DBManager db = new DBManager();

	public static Message createMessage(String platformCode, String targetPlatformListingId, String senderEmailAddress,
			String senderEmailSubject, String senderEmailBody, String webhookUrl) throws DatabaseException {
		Message message = new Message(platformCode, targetPlatformListingId, senderEmailAddress, senderEmailSubject,
				senderEmailBody, webhookUrl);

		db.createMessage(message);

		return message;
	}

	public static void setPropagated(Message message) throws DatabaseException {
		db.updateMessage(message, "propagated", Boolean.TRUE);
		message.setPropagated(true);
	}

	public static void askedToExtractEmail(Message message) throws DatabaseException {
		db.updateMessage(message, "askedToExtractEmail", Boolean.TRUE);
		message.setPropagated(true);
	}

	public List<Message> messagesPendingEmailPropagation() throws DatabaseException {
		return db.listMessages("WHERE propagationEmail is not null and propagationEmail <> '' and propagated = FALSE");
	}

	public void setPropagationEmail(Message message, String propagationEmail) throws DatabaseException {
		db.updateMessage(message, "propagationEmail", propagationEmail);
		message.setPropagationEmail(propagationEmail);
	}

	public Message findByID(Integer messageID) throws DatabaseException {
		return db.findMessage("where id=" + messageID);
	}

	public void setMailIdentifier(Message message, String token, String paddedEmailBody) throws DatabaseException {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("identifierToken", token);
		attributes.put("senderEmailBody", paddedEmailBody);

		db.updateMessage(message, attributes);
		message.setIdentifierToken(token);
		message.setSenderEmailBody(paddedEmailBody);
	}

	public Message findByToken(Mailbox mailbox, String token) throws DatabaseException {
		return db.findMessage(" where mailboxId=" + mailbox.getId() + " and identifierToken='" + token + "'");
	}

	public void setMailbox(Message message, Mailbox mailbox) throws DatabaseException {
		db.updateMessage(message, "mailboxId", mailbox.getId());
	}
}
