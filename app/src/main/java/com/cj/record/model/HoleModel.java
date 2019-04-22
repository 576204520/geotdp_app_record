package com.cj.record.model;

import com.cj.record.baen.BaseObjectBean;
import com.cj.record.contract.HoleContract;
import com.cj.record.net.RetrofitClient;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2019/3/19.
 */

public class HoleModel implements HoleContract.Model {
    @Override
    public Flowable<BaseObjectBean<String>> relate(String userID, String relateID, String holeID, String verCode) {
        return RetrofitClient.getInstance().getApi().relateHole(userID, relateID, holeID, verCode);
    }

    @Override
    public Flowable<BaseObjectBean<String>> downLoadHole(String userID, String holeID, String verCode) {
        return RetrofitClient.getInstance().getApi().downLoadHole(userID, holeID, verCode);
    }

    @Override
    public Flowable<BaseObjectBean<String>> getRelateList(String userID, String serialNumber, String verCode) {
        return RetrofitClient.getInstance().getApi().getRelateList(userID, serialNumber, verCode);
    }

    @Override
    public Flowable<BaseObjectBean<String>> getDownLoadList(String userID, String serialNumber, String verCode) {
        return RetrofitClient.getInstance().getApi().getDownLoadList(userID, serialNumber, verCode);
    }

    @Override
    public Flowable<BaseObjectBean<String>> checkUser(String projectID, String userID, String testType, String holeType, String verCode) {
        return RetrofitClient.getInstance().getApi().checkUser(projectID, userID, testType, holeType, verCode);
    }

}
