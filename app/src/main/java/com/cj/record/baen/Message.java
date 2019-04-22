package com.cj.record.baen;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/4/16.
 */

public class Message implements Serializable {
    private String ids;
    private String conversation;
    private String message;
    private String messageType;
    private String receiverIds;
    private String receiverNo;
    private String receiverTime;
    private String senderIds;
    private String senderTime;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getReceiverIds() {
        return receiverIds;
    }

    public void setReceiverIds(String receiverIds) {
        this.receiverIds = receiverIds;
    }

    public String getReceiverNo() {
        return receiverNo;
    }

    public void setReceiverNo(String receiverNo) {
        this.receiverNo = receiverNo;
    }

    public String getReceiverTime() {
        return receiverTime;
    }

    public void setReceiverTime(String receiverTime) {
        this.receiverTime = receiverTime;
    }

    public String getSenderIds() {
        return senderIds;
    }

    public void setSenderIds(String senderIds) {
        this.senderIds = senderIds;
    }

    public String getSenderTime() {
        return senderTime;
    }

    public void setSenderTime(String senderTime) {
        this.senderTime = senderTime;
    }
}
