package com.cj.record.mvp.model;


import com.cj.record.baen.BaseObjectBean;
import com.cj.record.mvp.contract.UserContract;
import com.cj.record.mvp.net.RetrofitClient;

import io.reactivex.Flowable;

/**
 * @author azheng
 * @date 2018/6/4.
 * GitHub：https://github.com/RookieExaminer
 * Email：wei.azheng@foxmail.com
 * Description：
 */
public class UserModel implements UserContract.Model {
    @Override
    public Flowable<BaseObjectBean<String>> login(String username, String password) {
        return RetrofitClient.getInstance().getApi().login(username, password);
    }

    @Override
    public Flowable<BaseObjectBean<String>> versionCheck(String userID, String verCode) {
        return RetrofitClient.getInstance().getApi().versionCheck(userID, verCode);
    }

    @Override
    public Flowable<BaseObjectBean<String>> updateInfo(String userID, String email, String idCard, String certificateNumber3) {
        return RetrofitClient.getInstance().getApi().updateInfo(userID, email, idCard, certificateNumber3);
    }

    @Override
    public Flowable<BaseObjectBean<String>> resetPassword(String userID, String email, String oldPassword, String newPassword, String newPassword2) {
        return RetrofitClient.getInstance().getApi().resetPassword(userID, email, oldPassword, newPassword,newPassword2);
    }

    @Override
    public Flowable<BaseObjectBean<String>> checkOperate(String userID, String operatePerson, String testType) {
        return RetrofitClient.getInstance().getApi().checkOperate(userID, operatePerson, testType);
    }
}
