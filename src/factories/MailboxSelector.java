package factories;

import db.DatabaseException;
import server.dao.MailboxDAO;
import server.model.Mailbox;
import server.model.Message;

/**
 * This is a mock/dummy strategy to select a mailbox to propagate message.
 * We should balance mailboxes across platforms and preferrably do not reuse the same
 * mailbox for the same listing.
 */
public class MailboxSelector {
    public static Mailbox selectFor(Message message) throws DatabaseException {
        return MailboxDAO.getAnyMailbox();
    }
}
