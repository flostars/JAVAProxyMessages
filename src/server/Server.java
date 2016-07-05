package server;

import static server.params.SendMessageParams.PARAM_MESSAGE_BODY;
import static server.params.SendMessageParams.PARAM_MESSAGE_SUBJECT;
import static server.params.SendMessageParams.PARAM_PLATFORM_CODE;
import static server.params.SendMessageParams.PARAM_PLATFORM_LISTING_ID;
import static server.params.SendMessageParams.PARAM_SENDER_EMAIL;
import static server.params.SendMessageParams.PARAM_WEBHOOK_URL;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import db.DatabaseException;
import jobs.EmailPropagateMessageJob;
import jobs.MonitorIncomingEmails;
import jobs.Performable;
import jobs.PropagateMessageJob;
import jobs.TriggerWebhookJob;
import server.dao.MessageDAO;
import server.model.Message;
import server.params.CraigslistWebhookParams;
import server.response.BadRequestResponse;
import server.response.ErrorResponse;
import server.response.SuccessResponse;
import server.validator.SendMessageRequestValidator;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class Server {

	public static final String CRAIGSLIST_RECEIVE_EMAIL_PATH = "/craigslist_email_is";
	private final static MessageDAO messageDAO = new MessageDAO();

	static abstract class JsonRoute extends Route {
		private final Gson gson;

		JsonRoute(String path, Gson gson) {
			super(path);
			this.gson = gson;
		}

		public String render(Object element) {
			try {
				return gson.toJson(element);
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
			return null;
		}
	}

	static abstract class StringRoute extends Route {
		StringRoute(String path) {
			super(path);
		}

		public String render(Object element) {
			return element.toString();
		}
	}

	protected static Gson gson;

	public static void run() {
		cronJob();
		gson = new Gson();
		Spark.setPort(8102);

		Spark.get(new Route("/index") {
			@Override
			public Object handle(Request request, Response response) {
				return null;
			}
		});

		Spark.get(new Route("/send_message") {
			@Override
			public Object handle(Request request, Response response) {
				System.out.println("Handling");
				SendMessageRequestValidator validator = new SendMessageRequestValidator(request);
				validator.validate();
				if (validator.hasErrors()) {
					System.out.println("Errors");
					return gson.toJson(new BadRequestResponse(response).put("details", validator.getErrors()).getMap());
				} else {
					System.out.println("Will create message");
					try {
						request.queryParams(PARAM_PLATFORM_LISTING_ID);
						Message message = MessageDAO.createMessage(request.queryParams(PARAM_PLATFORM_CODE),
								request.queryParams(PARAM_PLATFORM_LISTING_ID), request.queryParams(PARAM_SENDER_EMAIL),
								request.queryParams(PARAM_MESSAGE_SUBJECT), request.queryParams(PARAM_MESSAGE_BODY),
								request.queryParams(PARAM_WEBHOOK_URL));

						System.out.println("Message created");
						return gson.toJson(new SuccessResponse(response).put("referenceID", message.getId()).getMap());
					} catch (DatabaseException e) {
						e.printStackTrace();
						return gson.toJson(new ErrorResponse(response, e).getMap());
					}
				}
			}
		});
		Spark.post(new Route("/send_message") {
			@Override
			public Object handle(Request request, Response response) {
				System.out.println("Handling");
				SendMessageRequestValidator validator = new SendMessageRequestValidator(request);
				validator.validate();
				if (validator.hasErrors()) {
					System.out.println("Errors");
					return gson.toJson(new BadRequestResponse(response).put("details", validator.getErrors()).getMap());
				} else {
					System.out.println("Will create message");
					try {
						request.queryParams(PARAM_PLATFORM_LISTING_ID);
						Message message = MessageDAO.createMessage(request.queryParams(PARAM_PLATFORM_CODE),
								request.queryParams(PARAM_PLATFORM_LISTING_ID), request.queryParams(PARAM_SENDER_EMAIL),
								request.queryParams(PARAM_MESSAGE_SUBJECT), request.queryParams(PARAM_MESSAGE_BODY),
								request.queryParams(PARAM_WEBHOOK_URL));
						
						System.out.println("Message created");
						return gson.toJson(new SuccessResponse(response).put("referenceID", message.getId()).getMap());
					} catch (DatabaseException e) {
						e.printStackTrace();
						return gson.toJson(new ErrorResponse(response, e).getMap());
					}
				}
			}
		});

		/*
		 * Webhook for Craigslist email extractor service to notify us when it
		 * managed to find an email on the page.
		 */
		Spark.get(new Route(CRAIGSLIST_RECEIVE_EMAIL_PATH) {
			@Override
			public Object handle(Request request, Response response) {
				String messageIDStr = request.queryParams(CraigslistWebhookParams.PARAM_MESSAGE_ID);
				String propagationEmail = request.queryParams(CraigslistWebhookParams.PARAM_EMAIL);
				try {
					System.out.println("Add email "+propagationEmail +" for id "+ messageIDStr);
					Integer messageID = Integer.parseInt(messageIDStr);
					Message message = messageDAO.findByID(messageID);
					messageDAO.setPropagationEmail(message, propagationEmail);
					return gson.toJson(new SuccessResponse(response).getMap());
				} catch (DatabaseException | NumberFormatException e) {
					e.printStackTrace(); // TODO handle
					return gson.toJson(new ErrorResponse(response, e).getMap());
				}
			}
		});

		Spark.get(new StaticRoute("/*"));
		// Spark.get(new StaticRoute("/*/*"));
		// Spark.get(new StaticRoute("/*/*/*"));
		/*
		 * Spark.after( new Filter() {
		 * 
		 * @Override public void handle(Request request, Response response) {
		 * response.header("Content-Disposition",
		 * "attachment; filename=\"a.csv\""); } });
		 */
	}

	public static void cronJob() {
		List<Performable> jobs = new ArrayList<>();
		jobs.add(new EmailPropagateMessageJob());
		jobs.add(new MonitorIncomingEmails());
		jobs.add(new PropagateMessageJob());
		jobs.add(new TriggerWebhookJob());
		for (final Performable job : jobs) {
			Thread one = new Thread(new Runnable() {
				public void run() {
					while (true) {
					try {
							System.out.println("Will run job " + job.getClass());
							runCronJob(job);
							Thread.sleep(1000 * 60 * 2);
					} catch (InterruptedException v) {
						System.out.println(v);
					}
					}
				}

				public void runCronJob(Performable propagate) throws InterruptedException {
					try {
						propagate.perform();
					} catch (DatabaseException e) {
						e.printStackTrace();
					}
				}
			});

			one.start();
		}
	}

	static class StaticRoute extends Route {
		StaticRoute(String route) {
			super(route);
		}

		@Override
		public Object handle(Request request, Response response) {
			String path = request.pathInfo();
			if (path.startsWith("/"))
				path = path.substring(1);
			if (path.equals(""))
				path = "index.html";
			try {
				InputStream fis = Server.class.getResourceAsStream(resourcePath + path);
				org.apache.log4j.lf5.util.StreamUtils.copy(fis, response.raw().getOutputStream());
				fis.close();
				response.body("");
			} catch (Exception e) {
				halt(404);
				e.printStackTrace();
			}
			return null;
		}
	}

	static String resourcePath = "/taras/freamwork/server/files/";
}