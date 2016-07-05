package mail;

import db.DatabaseException;
import server.dao.MessageDAO;
import server.model.Mailbox;
import server.model.Message;

public class MailIdentifier {

    private static MessageDAO messageDAO = new MessageDAO();

    public static void addIdentifier(Message message, MailIdentifierStrategy strategy) throws DatabaseException {
        String existingToken = strategy.extractToken(message.getSenderEmailBody());
        if (existingToken != null && message.getIdentifierToken() != null) {
            System.out.println("Message already has the token present");
        } else {
            String token = strategy.generateToken();
            String paddedEmailBody = strategy.padMessage(message.getSenderEmailBody(), token);
            messageDAO.setMailIdentifier(message, token, paddedEmailBody);
        }
    }

    public static Message matchIncomingEmailToMessage(Mailbox mailbox, String emailBody, MailIdentifierStrategy strategy) throws DatabaseException {
        String token = strategy.extractToken(emailBody);
        System.out.println("Token is " + token);
        Message message = messageDAO.findByToken(mailbox, token);
        return message;
    }
}
