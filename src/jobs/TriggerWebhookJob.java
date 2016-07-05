package jobs;

import db.DatabaseException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import server.dao.MessageDAO;
import server.dao.ResponseDAO;
import server.model.Message;
import server.model.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import static server.params.ResponseWebhookParams.*;

public class TriggerWebhookJob extends GenericJob implements Performable {

    private List<Response> responses = new LinkedList<>();
    private ResponseDAO responseDAO = new ResponseDAO();
    private MessageDAO messageDAO = new MessageDAO();

    public void perform() throws DatabaseException {
        responses = responseDAO.responsesPendingWebhook();
        
        log("Responses pending webhook progaration, count: " + responses.size());

        for (Response response : responses){
            try {
                Message message = messageDAO.findByID(response.getMessageID());
                if (message!=null) {
                    String webhookUrl = message.getWebhookUrl();

                    boolean success = notifyWebhook(webhookUrl, response);
                    if (success){
                        responseDAO.webhookNotified(response);
                    }
                } else {
                    // TODO handle error
                }
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean notifyWebhook(String webhookUrl, Response response) {
        try {
            HttpClient httpClient = new HttpClient();
            PostMethod postMethod = new PostMethod(webhookUrl);

            postMethod.addParameter(PARAM_MESSAGE_ID, ""+response.getId());
            postMethod.addParameter(PARAM_SENDER_EMAIL, response.getResponseEmailAddress());
            postMethod.addParameter(PARAM_RESPONSE_SUBJECT, response.getResponseEmailSubject());
            postMethod.addParameter(PARAM_RESPONSE_BODY, response.getResponseEmailBody());

            try {
                int returnCode = httpClient.executeMethod(postMethod);
                System.out.println("Webhook return code is " + returnCode);
                String webhookResponse = postMethod.getResponseBodyAsString();
                // TODO parse as JSON or see if response code is 200
            } finally {
                postMethod.releaseConnection();
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) throws DatabaseException, IOException, URISyntaxException {
//        new TriggerWebhookJob().perform();
    }



}
