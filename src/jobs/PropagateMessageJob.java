package jobs;

import db.DatabaseException;
import db.manager.DBManager;
import platform.Platform;
import platform.PlatformFactory;
import server.model.Message;
import server.model.PlatformCode;

import java.io.IOException;
import java.util.List;

public class PropagateMessageJob extends GenericJob implements Performable{

    private List<Message> messages;
    private DBManager db = new DBManager();

    public void perform() throws DatabaseException {
        messages = db.listMessages("WHERE askedToExtractEmail = FALSE");
        
        log("Messages to propagate to craigslist extractor count: " + messages.size());

        for (Message message : messages) {
            try {
                PlatformCode platformCode = PlatformCode.valueOf(message.getPlatformCode());
                Platform platform = PlatformFactory.getPlatform(platformCode);
                if (platform != null) {
                    platform.propagateMessage(message);

                } else {
                    // TODO what to do here?
                }
            } catch (IOException e) {
                e.printStackTrace(); // TODO handle?
            }
        }
    }

    public static void main(String[] args) throws DatabaseException {
        new PropagateMessageJob().perform();
    }
}
