package platform;

import config.SystemConfiguration;
import db.DatabaseException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.client.utils.URIBuilder;
import server.Server;
import server.dao.MessageDAO;
import server.model.Message;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import static server.params.CraigslistWebhookParams.*;

public class CraigslistPlatform extends Platform {

	private MessageDAO messageDAO = new MessageDAO();
	private MessageFormat craigslist_email_webhook = new MessageFormat(PARAM_MESSAGE_ID + "={0}");

	@Override
	public void doPropagateMessage(Message message) throws IOException, DatabaseException {
		// TODO implement
		System.out.println("Will propagate message " + message.getId() + " to Craigslist");

		boolean success = askToExtractEmail(message);
		if (success) {
			messageDAO.askedToExtractEmail(message);
		}
	}

	private boolean askToExtractEmail(Message message) throws IOException {
		HttpClient httpClient = new HttpClient();
		GetMethod get = null;
		System.out.println("Webhook is " + webhookFor(message));
		System.out.println(message.getId());
		try {
			URIBuilder uriBuilder = new URIBuilder(SystemConfiguration.craigslistPropagatorEndpoint)
					.addParameter("url", message.getTargetPlatformListingId())
					.addParameter("_apikey", SystemConfiguration.apiKeyForEmailRPC)
					.addParameter("cookie",String.valueOf(message.getId()));
			System.out.println(uriBuilder.build().toString());
			get = new GetMethod(uriBuilder.build().toString());

//			httpClient.executeMethod(get);

			String response = get.getResponseBodyAsString();
			System.out.println("Response from Craigslist propagator: " + response);
			// TODO parse response and see if success

		} catch (URISyntaxException e) {
			throw new IOException(e);
		} finally {
			if (get != null) {
				get.releaseConnection();
			}
		}
		return true;
	}

	private String webhookFor(Message message) {
		return craigslist_email_webhook.format(new Object[] { message.getId() });
	}
}
