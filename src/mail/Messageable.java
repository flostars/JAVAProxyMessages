package mail;

import java.util.List;

import server.model.Mailbox;
import server.model.Message;
import server.model.Response;

public interface Messageable {
	public void sendMessage(Mailbox emailBox, Message message);

	public List<Response> readMessages(Mailbox emailBox);
}
