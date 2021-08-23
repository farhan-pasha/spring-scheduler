package com.keembay.scheduler.dto;

import java.util.Date;

public class ArchiveNotificationDTO{
    private String userName;
    private String email;
    private String mobile;
    private Date expiryDate;
    private String sentMessage;
    private Date createdOn;
    private String messageType;

    public ArchiveNotificationDTO() {
    }

    public ArchiveNotificationDTO(String userName, String email, String mobile, Date expiryDate, String messageType,
                                  String sentMessage, Date createdOn) {
        this.userName = userName;
        this.email = email;
        this.mobile = mobile;
        this.expiryDate = expiryDate;
        this.messageType = messageType;
        this.sentMessage = sentMessage;
        this.createdOn = createdOn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getSentMessage() {
        return sentMessage;
    }

    public void setSentMessage(String sentMessage) {
        this.sentMessage = sentMessage;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
