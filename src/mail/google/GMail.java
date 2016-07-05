package mail.google;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

import mail.Messageable;
import server.model.Mailbox;
import server.model.Response;

public class GMail implements Messageable {
	private static String host = "smtp.gmail.com";
	private static final Properties props = makeProperties();

	private static Properties makeProperties() {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");
		return props;
	}

	private static Authenticator createAuthenticator(Mailbox mailbox) {
		final String username = mailbox.getEmailAddress();
		final String password = mailbox.getPassword();
		return new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		};
	}

	public static void doSendMessage(Authenticator authenticator, String from, String to, String subject, String body,
			File... attachments) {
		Session session = Session.getInstance(props, authenticator);
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);

			if (attachments.length > 0) {
				Multipart multipart = new MimeMultipart();
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setContent(body, "text/html");
				multipart.addBodyPart(messageBodyPart);

				for (File attachment : attachments) {
					messageBodyPart = new MimeBodyPart();
					DataSource source = new FileDataSource(attachment);
					messageBodyPart.setDataHandler(new DataHandler(source));
					messageBodyPart.setFileName(attachment.getName());
					multipart.addBodyPart(messageBodyPart);
				}

				message.setContent(multipart);
			} else {
				message.setText(body);
			}
			Transport.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Response> getMessages(Mailbox box) {
		List<Response> allResponses = new ArrayList<>();
		Properties props = new Properties();
		props.setProperty("mail.store.protocol", "imaps");
		try {
			Session session = Session.getInstance(props, null);
			Store store = session.getStore();
			store.connect(host, box.getEmailAddress(), box.getPassword());
			Folder inbox = store.getFolder("INBOX");
			Flags seen = new Flags(Flags.Flag.SEEN);
			FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
			inbox.open(Folder.READ_WRITE);
			Message messages[] = inbox.search(unseenFlagTerm);
			Response responce = null;
			System.out.println("found " + messages.length + " unread messages");
			for (int i = 0; i < messages.length; i++) {
				responce = new Response();
				Message msg = messages[i];
				Address[] in = msg.getFrom();
				String emails = "";
				for (Address address : in) {
					emails += address.toString() + ",";
				}
				responce.setResponseEmailAddress(emails);
				responce.setResponseEmailBody(emailBodyAsText(msg.getContent()));
				responce.setResponseEmailSubject(msg.getSubject());
				allResponses.add(responce);
				inbox.setFlags(new Message[] {msg}, new Flags(Flags.Flag.SEEN), true);
			}
		} catch (Exception mex) {
			mex.printStackTrace();
		}
		return allResponses;
	}

	private String emailBodyAsText(Object messageContent) {
		if (messageContent instanceof Multipart) {

			Multipart multipart = (Multipart) messageContent;

			try {
				for (int j = 0; j < multipart.getCount(); j++) {

					BodyPart bodyPart = multipart.getBodyPart(j);
					String disposition = bodyPart.getDisposition();

					if (disposition != null && (disposition.equalsIgnoreCase("ATTACHMENT"))) {
						System.out.println("Email has attachments");

						DataHandler handler = bodyPart.getDataHandler();
						System.out.println("file name : " + handler.getName());
						return ""; // TODO maybe still some text here?
					} else {
						return bodyPart.getContent().toString();
					}
				}
			} catch (MessagingException | IOException e) {
				e.printStackTrace();
			}
		} else {
			return messageContent.toString();
		}
		return "";
	}

	public static void main(String... args) {
		// File[] attachments = new File[args.length - 3];
		// for (int i = 3; i < args.length; i++) {
		// attachments[i - 3] = new File(args[i]);
		// }
		// doSendMessage(args[0], args[1], args[2], args[3], attachments);
	}

	@Override
	public void sendMessage(Mailbox emailBox, server.model.Message message) {
		Authenticator authenticator = createAuthenticator(emailBox);
		doSendMessage(authenticator, emailBox.getEmailAddress(), message.getPropagationEmail(),
				message.getSenderEmailSubject(), message.getSenderEmailBody());

	}

	@Override
	public List<Response> readMessages(Mailbox emailBox) {
		return getMessages(emailBox);
	}
}
