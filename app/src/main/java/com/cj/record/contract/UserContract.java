package com.cj.record.contract;

import android.content.Context;

import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.LocalUser;
import com.cj.record.base.BaseView;

import io.reactivex.Flowable;


public interface UserContract {
    interface Model {
        Flowable<BaseObjectBean<String>> login(String username, String password);

        Flowable<BaseObjectBean<String>> versionCheck(String userID, String verCode);

        Flowable<BaseObjectBean<String>> updateInfo(String userID, String email, String idCard, String certificateNumber3);

        Flowable<BaseObjectBean<String>> resetPassword(String userID, String email, String oldPassword, String newPassword, String newPassword2);

        Flowable<BaseObjectBean<String>> checkOperate(String userID, String operatePerson, String testType);
    }

    interface View extends BaseView {
        @Override
        void showLoading();

        @Override
        void hideLoading();

        @Override
        void onError(Throwable throwable);

        void onSuccess(BaseObjectBean<String> bean);

        void onSuccessUpdateVersion(BaseObjectBean<String> bean);

        void onSuccessUpdateInfo(BaseObjectBean<String> bean);

        void onSuccessResetPassword(BaseObjectBean<String> bean);

        void onSuccessCheckOperate(BaseObjectBean<String> bean);

        void onSuccessInitDB();
    }


    interface Presenter {
        /**
         * 登陆
         *
         * @param username
         * @param password
         */
        void login(String username, String password);

        /**
         * 检查版本更新
         *
         * @param userID
         * @param verCode
         */
        void versionCheck(String userID, String verCode);

        /**
         * 修改用户信息
         *
         * @param userID
         * @param email
         * @param idCard
         * @param certificateNumber3
         */
        void updateInfo(String userID, String email, String idCard, String certificateNumber3);

        /**
         * 修改密码
         *
         * @param userID
         * @param email
         * @param oldPassword
         * @param newPassword
         * @param newPassword2
         */
        void resetPassword(String userID, String email, String oldPassword, String newPassword, String newPassword2);

        /**
         * 校验机长信息
         *
         * @param userID
         * @param operatePerson
         * @param testType
         */
        void checkOperate(String userID, String operatePerson, String testType);

        void initDB(Context context);
    }
}
