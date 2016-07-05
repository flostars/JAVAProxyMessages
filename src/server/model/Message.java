package server.model;

import java.util.Date;

public class Message {
    private Integer id;
    private Date receivedDate;
    private String platformCode;
    private String targetPlatformListingId;
    private String senderEmailAddress;
    private String senderEmailSubject;
    private String senderEmailBody;
    private String webhookUrl;
    private boolean askedToExtractEmail;
    private String propagationEmail;
    private boolean propagated;
    private boolean hasResponse;

    private String identifierToken;
    private Integer mailboxId;

    public Message() {
        this.receivedDate = new Date();
    }

    public Message(String platformCode, String targetPlatformListingId,
                   String senderEmailAddress, String senderEmailSubject, String senderEmailBody, String webhookUrl) {
        this();
        this.platformCode = platformCode;
        this.targetPlatformListingId = targetPlatformListingId;
        this.senderEmailAddress = senderEmailAddress;
        this.senderEmailBody = senderEmailBody;
        this.webhookUrl = webhookUrl;
        this.senderEmailSubject = senderEmailSubject;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

    public String getTargetPlatformListingId() {
        return targetPlatformListingId;
    }

    public void setTargetPlatformListingId(String targetPlatformListingId) {
        this.targetPlatformListingId = targetPlatformListingId;
    }

    public String getSenderEmailAddress() {
        return senderEmailAddress;
    }

    public void setSenderEmailAddress(String senderEmailAddress) {
        this.senderEmailAddress = senderEmailAddress;
    }

    public String getSenderEmailSubject() {
        return senderEmailSubject;
    }

    public void setSenderEmailSubject(String senderEmailSubject) {
        this.senderEmailSubject = senderEmailSubject;
    }

    public String getSenderEmailBody() {
        return senderEmailBody;
    }

    public void setSenderEmailBody(String senderEmailBody) {
        this.senderEmailBody = senderEmailBody;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public boolean isAskedToExtractEmail() {
        return askedToExtractEmail;
    }

    public void setAskedToExtractEmail(boolean askedToExtractEmail) {
        this.askedToExtractEmail = askedToExtractEmail;
    }

    public String getPropagationEmail() {
        return propagationEmail;
    }

    public void setPropagationEmail(String propagationEmail) {
        this.propagationEmail = propagationEmail;
    }

    public boolean isPropagated() {
        return propagated;
    }

    public void setPropagated(boolean propagated) {
        this.propagated = propagated;
    }

    public boolean isHasResponse() {
        return hasResponse;
    }

    public void setHasResponse(boolean hasResponse) {
        this.hasResponse = hasResponse;
    }

    public String getIdentifierToken() {
        return identifierToken;
    }

    public void setIdentifierToken(String identifierToken) {
        this.identifierToken = identifierToken;
    }

    public Integer getMailboxId() {
        return mailboxId;
    }

    public void setMailboxId(Integer mailboxId) {
        this.mailboxId = mailboxId;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", receivedDate=" + receivedDate +
                ", platformCode='" + platformCode + '\'' +
                ", targetPlatformListingId='" + targetPlatformListingId + '\'' +
                ", senderEmailAddress='" + senderEmailAddress + '\'' +
                ", senderEmailSubject='" + senderEmailSubject + '\'' +
                ", senderEmailBody='" + senderEmailBody + '\'' +
                ", webhookUrl='" + webhookUrl + '\'' +
                ", askedToExtractEmail=" + askedToExtractEmail +
                ", propagationEmail='" + propagationEmail + '\'' +
                ", propagated=" + propagated +
                ", hasResponse=" + hasResponse +
                ", identifierToken='" + identifierToken + '\'' +
                ", mailboxId=" + mailboxId +
                '}';
    }
}
