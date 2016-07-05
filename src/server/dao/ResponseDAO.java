package server.dao;

import db.DatabaseException;
import db.manager.DBManager;
import server.model.Response;

import java.util.Date;
import java.util.List;

public class ResponseDAO {
    private DBManager db = new DBManager();


    public void createResponse(Response response) throws DatabaseException {
        response.setCreatedDate(new Date());

        db.createResponse(response);
    }

    public void webhookNotified(Response response) throws DatabaseException {
        db.updateResponse(response, "webhookNotified", true);
    }

    public List<Response> responsesPendingWebhook() throws DatabaseException {
        return db.listResponses("where webhookNotified = FALSE"); // TODO maybe add a date we last attempted to notify
    }
}
