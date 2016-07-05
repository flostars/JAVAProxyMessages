package server.model;

import java.util.Date;

public class Response {
    private Integer id;
    private Integer messageID;

    private String responseEmailAddress;
    private String responseEmailSubject;
    private String responseEmailBody;

    private Date createdDate;
    private boolean webhookNotified;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMessageID() {
        return messageID;
    }

    public void setMessageID(Integer messageID) {
        this.messageID = messageID;
    }

    public String getResponseEmailAddress() {
        return responseEmailAddress;
    }

    public void setResponseEmailAddress(String responseEmailAddress) {
        this.responseEmailAddress = responseEmailAddress;
    }

    public String getResponseEmailSubject() {
        return responseEmailSubject;
    }

    public void setResponseEmailSubject(String responseEmailSubject) {
        this.responseEmailSubject = responseEmailSubject;
    }

    public String getResponseEmailBody() {
        return responseEmailBody;
    }

    public void setResponseEmailBody(String responseEmailBody) {
        this.responseEmailBody = responseEmailBody;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isWebhookNotified() {
        return webhookNotified;
    }

    public void setWebhookNotified(boolean webhookNotified) {
        this.webhookNotified = webhookNotified;
    }

    @Override
    public String toString() {
        return "Response{" +
                "id=" + id +
                ", messageID=" + messageID +
                ", responseEmailAddress='" + responseEmailAddress + '\'' +
                ", responseEmailSubject='" + responseEmailSubject + '\'' +
                ", responseEmailBody='" + responseEmailBody + '\'' +
                ", createdDate=" + createdDate +
                ", webhookNotified=" + webhookNotified +
                '}';
    }
}
