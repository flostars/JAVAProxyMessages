import mail.google.GMail;
import server.model.Mailbox;

public class Test {

	public static void main(String[] args) {
		GMail mail = new GMail();
		// mail.send("taras.elance@gmail.com", "Hi", "Hi please response me for
		// test pupropose");
		Mailbox box = new Mailbox();
		box.setPassword("FrederickKeyn12");
		box.setEmailAddress("FrederickKeyn@gmail.com");
		mail.getMessages(box);
	}

}
