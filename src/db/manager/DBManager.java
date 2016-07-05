package db.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import db.DatabaseException;
import server.model.Mailbox;
import server.model.Message;
import server.model.Response;

public class DBManager {
    Connection conn;
    String user = "root";
    String password = "";
    String dbUrl = "jdbc:mysql://localhost/proxymessages";
    PreparedStatement createMessage;
    PreparedStatement createMailBox;
    PreparedStatement createResponse;

    public void createMailBox(Mailbox mailBox) throws DatabaseException {
        int param = 1;
        try {
            createMailBox.setString(param++, mailBox.getEmailAddress());
            createMailBox.setString(param++, mailBox.getPassword());
            createMailBox.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void createMessage(Message message) throws DatabaseException {
        try {
            int param = 0;
            createMessage.setDate(++param, new java.sql.Date(message.getReceivedDate().getTime()));
            createMessage.setString(++param, message.getPlatformCode());
            createMessage.setString(++param, message.getTargetPlatformListingId());
            createMessage.setString(++param, message.getSenderEmailAddress());
            createMessage.setString(++param, message.getSenderEmailSubject());
            createMessage.setString(++param, message.getSenderEmailBody());
            createMessage.setString(++param, message.getWebhookUrl());
            createMessage.setBoolean(++param, message.isAskedToExtractEmail());
            createMessage.setString(++param, message.getPropagationEmail());
            createMessage.setBoolean(++param, message.isPropagated());
            createMessage.setBoolean(++param, message.isHasResponse());

            createMessage.setString(++param, message.getIdentifierToken());
            createMessage.setObject(++param, message.getMailboxId());
            createMessage.executeUpdate();
            ResultSet rs = createMessage.getGeneratedKeys();
            rs.next();
            int generatedID = rs.getInt(1);
            message.setId(generatedID);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void createResponse(Response response) throws DatabaseException {
        try {
            int param = 0;
            createResponse.setDate(++param, new java.sql.Date(response.getCreatedDate().getTime()));
            createResponse.setObject(++param, response.getMessageID());
            createResponse.setString(++param, response.getResponseEmailAddress());
            createResponse.setString(++param, response.getResponseEmailSubject());
            createResponse.setString(++param, response.getResponseEmailBody());
            createResponse.setBoolean(++param, response.isWebhookNotified());

            createResponse.executeUpdate();
            ResultSet rs = createResponse.getGeneratedKeys();
            rs.next();
            int generatedID = rs.getInt(1);
            response.setId(generatedID);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public void updateMessage(Message message, Map<String, Object> attributes) throws DatabaseException {
        if (attributes.size()>0) {
            try {
                String sql = "update message set ";
                for (String key : attributes.keySet()){
                    sql += key + " = ?,";
                }
                sql = sql.substring(0, sql.length()-1);
                sql += "where id = ?";

                PreparedStatement updateMessage = conn.prepareStatement(sql);
                int param = 0;
                for (String key : attributes.keySet()){
                    updateMessage.setObject(++param, attributes.get(key));
                }
                updateMessage.setObject(++param, message.getId());

                updateMessage.executeUpdate();
            } catch (SQLException e) {
                throw new DatabaseException(e);
            }
        }
    }

    public void updateMessage(Message message, String property, Object value) throws DatabaseException {
        updateRecord("message", message.getId(), property, value);
    }

    public void updateResponse(Response response, String property, Object value) throws DatabaseException {
        updateRecord("response", response.getId(), property, value);
    }

    public void updateRecord(String tableName, Integer id, String property, Object value) throws DatabaseException {
        try {
            PreparedStatement updateMessage = conn.prepareStatement("update " + tableName +
                    " set " + property + " = ? where id = ?");
            updateMessage.setObject(1, value);
            updateMessage.setObject(2, id);

            updateMessage.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public List<Message> listMessages(String conditions) throws DatabaseException {
        List<Message> messages = new LinkedList<>();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("select * from message " + conditions);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(initMessage(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return messages;
    }

    private Message initMessage(ResultSet rs) throws DatabaseException {
        Message message = new Message();
        try {
            message.setId(rs.getInt("id"));
            message.setReceivedDate(rs.getDate("receivedDate"));
            message.setPlatformCode(rs.getString("platformCode"));
            message.setTargetPlatformListingId(rs.getString("targetPlatformListingId"));

            message.setSenderEmailAddress(rs.getString("senderEmailAddress"));
            message.setSenderEmailSubject(rs.getString("senderEmailSubject"));
            message.setSenderEmailBody(rs.getString("senderEmailBody"));

            message.setWebhookUrl(rs.getString("webhookUrl"));

            message.setAskedToExtractEmail(rs.getBoolean("askedToExtractEmail"));
            message.setPropagationEmail(rs.getString("propagationEmail"));
            message.setPropagated(rs.getBoolean("propagated"));
            message.setHasResponse(rs.getBoolean("hasResponse"));

            message.setIdentifierToken(rs.getString("identifierToken"));
            message.setMailboxId(rs.getInt("mailboxId"));
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return message;
    }

    private Response initResponse(ResultSet rs) throws DatabaseException {
        Response response = new Response();
        try {
            response.setId(rs.getInt("id"));
            response.setCreatedDate(rs.getDate("createdDate"));
            response.setMessageID(rs.getInt("messageId"));
            response.setResponseEmailAddress(rs.getString("responseEmailAddress"));
            response.setResponseEmailSubject(rs.getString("responseEmailSubject"));
            response.setResponseEmailBody(rs.getString("responseEmailBody"));
            response.setWebhookNotified(rs.getBoolean("webhookNotified"));
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return response;
    }

    public DBManager() {
		super();
		try {
			getConnection();
			initpreparedStatments();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
    }

    public void initpreparedStatments() {
        String createMessageQuery = "insert into message (receivedDate, platformCode, targetPlatformListingId, "
                + "senderEmailAddress, senderEmailSubject, senderEmailBody, webhookUrl, askedToExtractEmail, " +
                "propagationEmail, propagated, hasResponse, identifierToken, mailboxId) values "
                + " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String mailBox = "insert into mailbox (emailAddress, password, createdDate) values(?, ?, now())";
        String createResponseQuery = "insert into response (createdDate, messageId, responseEmailAddress, " +
                "responseEmailSubject, responseEmailBody, webhookNotified) values (?, ?, ?, ?, ?, ?)";
        try {
            createMessage = conn.prepareStatement(createMessageQuery, Statement.RETURN_GENERATED_KEYS);
            createMailBox = conn.prepareStatement(mailBox, Statement.RETURN_GENERATED_KEYS);
            createResponse = conn.prepareStatement(createResponseQuery, Statement.RETURN_GENERATED_KEYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getConnection() throws DatabaseException {
        String myDriver = "org.gjt.mm.mysql.Driver";
        String myUrl = dbUrl;
        try {
            Class.forName(myDriver);
            if (conn == null) {
                conn = DriverManager.getConnection(myUrl, user, password);
            } else if (!conn.isValid(100)) {
                conn = DriverManager.getConnection(myUrl, user, password);
            }
        } catch (ClassNotFoundException e) {
            throw new DatabaseException(e);
        } catch (Exception e) {
			throw new DatabaseException(e);
        }
    }

    public void closeConnection() throws DatabaseException {
        try {
            conn.close();
        } catch (Exception e) {
			throw new DatabaseException(e);
        }
    }

    public void deleteAllMailboxes() throws DatabaseException {
        getConnection();
        Statement st = null;
        try {
            st = conn.createStatement();
            String sqls = "delete from mailbox;";
            st.execute(sqls);
        } catch (Exception e) {
			throw new DatabaseException(e);
		}
	}

	public List<Mailbox> listMailboxes() throws DatabaseException {
		List<Mailbox> mails = new ArrayList<>();
		try {
			getConnection();
			Statement st = null;
			st = conn.createStatement();
			String sql = "Select id, emailAddress, password from mailbox";
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				mails.add(initMailbox(rs));
			}
		} catch (Exception e) {
			throw new DatabaseException(e);
		}
		
		return mails;
    }

    private Mailbox initMailbox(ResultSet rs) throws SQLException {
        Mailbox mailbox = new Mailbox();
        mailbox.setId(rs.getInt(1));
        mailbox.setEmailAddress(rs.getString(2));
        mailbox.setPassword(rs.getString(3));

        return mailbox;
    }

    public Mailbox findMailbox(String conditions) throws DatabaseException {
        try {
            PreparedStatement stmt = conn.prepareStatement("select * from mailbox " + conditions);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return initMailbox(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return null;
    }

    public Message findMessage(String conditions) throws DatabaseException {
        try {
            PreparedStatement stmt = conn.prepareStatement("select * from message " + conditions);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return initMessage(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return null;
    }

    public List<Response> listResponses(String conditions) throws DatabaseException {
        List<Response> responses = new LinkedList<>();
        try {
            PreparedStatement stmt = conn.prepareStatement("select * from response " + conditions);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                responses.add(initResponse(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return responses;
    }
}
