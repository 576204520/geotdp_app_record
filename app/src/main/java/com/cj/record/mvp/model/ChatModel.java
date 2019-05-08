package com.cj.record.mvp.model;

import com.cj.record.baen.BaseObjectBean;
import com.cj.record.mvp.contract.ChatContract;
import com.cj.record.mvp.net.RetrofitClient;

import io.reactivex.Flowable;

/**
 * Created by Administrator on 2019/4/12.
 */

public class ChatModel implements ChatContract.Model {


    @Override
    public Flowable<BaseObjectBean<String>> myUserForCompanyOrProject(String userID) {
        return RetrofitClient.getInstance().getApi().myUserForCompanyOrProject(userID);
    }

    @Override
    public Flowable<BaseObjectBean<String>> myFriendList(String userID) {
        return RetrofitClient.getInstance().getApi().myFriendList(userID);
    }

    @Override
    public Flowable<BaseObjectBean<String>> addUserFriend(String userID, String targetUserID) {
        return RetrofitClient.getInstance().getApi().addUserFriend(userID, targetUserID);
    }
    @Override
    public Flowable<BaseObjectBean<String>> deleteUserFriend(String userID, String targetUserID) {
        return RetrofitClient.getInstance().getApi().deleteUserFriend(userID, targetUserID);
    }

    @Override
    public Flowable<BaseObjectBean<String>> sendMessage(String userID, String targetUserID, String message) {
        return RetrofitClient.getInstance().getApi().sendMessage(userID, targetUserID, message);
    }

    @Override
    public Flowable<BaseObjectBean<String>> readMessageCallBack(String userID, String chatRecordId) {
        return RetrofitClient.getInstance().getApi().readMessageCallBack(userID, chatRecordId);
    }

    @Override
    public Flowable<BaseObjectBean<String>> myChatRecord(String userID, String targetUserID) {
        return RetrofitClient.getInstance().getApi().myChatRecord(userID, targetUserID);
    }
}
