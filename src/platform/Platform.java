package platform;

import db.DatabaseException;
import db.manager.DBManager;
import server.dao.MessageDAO;
import server.model.Message;

import java.io.IOException;

public abstract class Platform {

    private MessageDAO messageDAO = new MessageDAO();

    public void propagateMessage(Message message) throws DatabaseException, IOException {
        doPropagateMessage(message);
    }

    public abstract void doPropagateMessage(Message message) throws IOException, DatabaseException;
}
