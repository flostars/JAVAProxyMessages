package server.model;

import java.util.Date;

public class Mailbox {
	
	private Integer id;
	private String emailAddress;
    private String password;
    private Date createdDate;
    private Integer proxyId;

    public String getEmailSuffix(){
        if (emailAddress != null) {
            String[] parts = emailAddress.split("@");
            return parts[parts.length-1];
        } else {
            return null;
        }
    }

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

    
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getProxyId() {
        return proxyId;
    }

    public void setProxyId(Integer proxyId) {
        this.proxyId = proxyId;
    }
}
