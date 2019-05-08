package com.cj.record.mvp.model;

import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.Hole;
import com.cj.record.baen.PageBean;
import com.cj.record.db.HoleDao;
import com.cj.record.mvp.contract.HoleContract;
import com.cj.record.mvp.net.RetrofitClient;

import io.reactivex.Flowable;

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

    @Override
    public Flowable<PageBean<Hole>> loadData(String projectID, int page, int size, String search, String sort) {
        return HoleDao.getInstance().loadData(projectID, page, size, search, sort);
    }

    @Override
    public Flowable<Boolean> delete(Hole hole) {
        return HoleDao.getInstance().deleteHole(hole);
    }

}
