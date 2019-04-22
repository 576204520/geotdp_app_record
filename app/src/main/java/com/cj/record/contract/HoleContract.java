package com.cj.record.contract;

import android.content.Context;

import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.Hole;
import com.cj.record.baen.LocalUser;
import com.cj.record.baen.PageBean;
import com.cj.record.baen.Project;
import com.cj.record.baen.Record;
import com.cj.record.base.BaseView;
import com.cj.record.db.GpsDao;
import com.cj.record.db.HoleDao;
import com.cj.record.db.MediaDao;
import com.cj.record.db.ProjectDao;
import com.cj.record.db.RecordDao;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;

/**
 * Created by Administrator on 2019/3/18.
 */

public interface HoleContract {

    interface Model {
        Flowable<BaseObjectBean<String>> relate(String userID, String relateID, String holeID, String verCode);

        Flowable<BaseObjectBean<String>> downLoadHole(String userID, String holeID, String verCode);

        Flowable<BaseObjectBean<String>> getRelateList(String userID, String serialNumber, String verCode);

        Flowable<BaseObjectBean<String>> getDownLoadList(String userID, String serialNumber, String verCode);

        Flowable<BaseObjectBean<String>> checkUser(String projectID, String userID, String testType, String holeType, String verCode);
    }

    interface View extends BaseView {
        @Override
        void showLoading();

        @Override
        void hideLoading();

        @Override
        void onError(Throwable throwable);

        void onSuccessAddOrUpdate();

        void onSuccessDelete();

        void onSuccessList(PageBean<Hole> pageBean);

        void onSuccessRelate(BaseObjectBean<String> bean);

        void onSuccessRelateMore(BaseObjectBean<String> bean, Hole newHole);

        void onSuccessNoRelateList(List<Hole> noRelateList);

        void onSuccessDownloadHole(BaseObjectBean<String> bean, LocalUser localUser);

        void onSuccessRelateList(BaseObjectBean<String> bean);

        void onSuccessDownloadList(BaseObjectBean<String> bean);

        void onSuccessCheckUser(BaseObjectBean<String> bean);

        void onSuccessGetScene(List<Record> recordList);

        void onSuccessUpload(BaseObjectBean<Integer> bean);
    }

    interface Presenter {

        /**
         * 添加或者修改
         *
         * @param hole
         */
        void addOrUpdate(Hole hole);

        /**
         * 加载list 刷新、加载更多
         *
         * @param projectID
         * @param page
         * @param size
         * @param search
         * @param sort
         */
        void loadData(String projectID, int page, int size, String search, String sort);

        /**
         * 删除
         *
         * @param context
         * @param hole
         */
        void delete(Context context, Hole hole);

        /**
         * 关联
         *
         * @param userID
         * @param relateID
         * @param holeID
         * @param verCode
         */
        void relate(String userID, String relateID, String holeID, String verCode);

        /**
         * 关联多个
         *
         * @param checkList
         * @param context
         * @param holeDao
         * @param project
         * @param verCode
         */
        void relateMore(List<Hole> checkList, Context context, HoleDao holeDao, Project project, String verCode);

        /**
         * 下载获取数据
         *
         * @param localUserList
         * @param verCode
         */
        void downLoadHole(List<LocalUser> localUserList, String verCode);

        /**
         * 上传数据
         */
        void uploadHole(Context context, Project project, Hole uploadHole);

        /**
         * 获取关联勘探点的列表
         *
         * @param userID
         * @param serialNumber
         * @param verCode
         */
        void getRelateList(String userID, String serialNumber, String verCode);

        /**
         * 获取下载数据的列表
         *
         * @param userID
         * @param serialNumber
         * @param verCode
         */
        void getDownLoadList(String userID, String serialNumber, String verCode);

        /**
         * 上传之前，校验用户信息
         *
         * @param projectID
         * @param userID
         * @param testType
         * @param holeType
         * @param verCode
         */
        void checkUser(String projectID, String userID, String testType, String holeType, String verCode);

        /**
         * 获取场景等记录信息
         *
         * @param holeID
         */
        void getSceneRecord(String holeID);
    }
}
