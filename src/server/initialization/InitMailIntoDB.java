package server.initialization;

import db.DatabaseException;
import db.manager.DBManager;
import server.model.Mailbox;

public class InitMailIntoDB {

	public static void main(String[] args) throws DatabaseException {
		DBManager db = new DBManager();
		db.deleteAllMailboxes();
		Mailbox box = new Mailbox();
		box.setEmailAddress("FrederickKeyn@gmail.com");
		box.setPassword("FrederickKeyn12");
		try {
			db.createMailBox(box);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

}
