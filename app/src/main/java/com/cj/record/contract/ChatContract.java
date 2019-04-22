package com.cj.record.contract;

import com.cj.record.baen.BaseObjectBean;
import com.cj.record.base.BaseView;

import io.reactivex.Flowable;

/**
 * Created by Administrator on 2019/4/12.
 */

public interface ChatContract {
    interface Model {
        Flowable<BaseObjectBean<String>> myUserForCompanyOrProject(String userID);

        Flowable<BaseObjectBean<String>> myFriendList(String userID);

        Flowable<BaseObjectBean<String>> addUserFriend(String userID, String targetUserID);

        Flowable<BaseObjectBean<String>> deleteUserFriend(String userID, String targetUserID);

        Flowable<BaseObjectBean<String>> sendMessage(String userID, String targetUserID, String message);

        Flowable<BaseObjectBean<String>> readMessageCallBack(String userID, String chatRecordId);

        Flowable<BaseObjectBean<String>> myChatRecord(String userID, String targetUserID);
    }

    interface View extends BaseView {
        @Override
        void showLoading();

        @Override
        void hideLoading();

        @Override
        void onError(Throwable throwable);

        void onSuccessMyUserForCompanyOrProject(BaseObjectBean<String> pageBean);

        void onSuccessMyFriendList(BaseObjectBean<String> pageBean);

        void onSuccessAddUserFriend(BaseObjectBean<String> pageBean);

        void onSuccessDeleteUserFriend(BaseObjectBean<String> pageBean);

        void onSuccessSendMessage(BaseObjectBean<String> pageBean);

        void onSuccessReadMessageCallBack(BaseObjectBean<String> pageBean);

        void onSuccessMyChatRecord(BaseObjectBean<String> pageBean);

    }

    interface Presenter {

        void myUserForCompanyOrProject(String userID);

        void myFriendList(String userID);

        void addUserFriend(String userID, String targetUserID);

        void deleteUserFriend(String userID, String targetUserID);

        void sendMessage(String userID, String targetUserID, String message);

        void readMessageCallBack(String userID, String chatRecordId);

        void myChatRecord(String userID, String targetUserID);

    }
}
