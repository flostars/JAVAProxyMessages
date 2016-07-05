package server.dao;


import db.DatabaseException;
import db.manager.DBManager;
import server.model.Mailbox;

import java.util.List;

public class MailboxDAO {
    private static final DBManager db = new DBManager();

    public static Mailbox getAnyMailbox() throws DatabaseException {
        return db.findMailbox("");
    }

    public static List<Mailbox> listMailboxes() throws DatabaseException {
        return db.listMailboxes();
    }
}
