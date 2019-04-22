package com.cj.record.baen;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/4/15.
 */

public class Friends implements Serializable {
    private String ids;
    private String mainUserIds;
    private String friendUserIds;
    private String friendNickname;
    private String friendPhoneNo;
    private String isDelete;
    private String createTime;
    private String conversation;
    private String unreadNum;

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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFriendNickname() {
        return friendNickname;
    }

    public void setFriendNickname(String friendNickname) {
        this.friendNickname = friendNickname;
    }

    public String getFriendUserIds() {
        return friendUserIds;
    }

    public void setFriendUserIds(String friendUserIds) {
        this.friendUserIds = friendUserIds;
    }

    public String getFriendPhoneNo() {
        return friendPhoneNo;
    }

    public void setFriendPhoneNo(String friendPhoneNo) {
        this.friendPhoneNo = friendPhoneNo;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getMainUserIds() {
        return mainUserIds;
    }

    public void setMainUserIds(String mainUserIds) {
        this.mainUserIds = mainUserIds;
    }

    public String getUnreadNum() {
        return unreadNum;
    }

    public void setUnreadNum(String unreadNum) {
        this.unreadNum = unreadNum;
    }
}
