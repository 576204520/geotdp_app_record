package com.cj.record.contract;

import android.content.Context;

import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.PageBean;
import com.cj.record.baen.Project;
import com.cj.record.base.BaseView;
import com.cj.record.db.HoleDao;
import com.cj.record.db.MediaDao;
import com.cj.record.db.ProjectDao;
import com.cj.record.db.RecordDao;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;

public interface ProjectContract {
    interface Model {
        Flowable<BaseObjectBean<String>> relate(String serialNumber, String userID, String verCode);
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

        void onSuccessList(PageBean<Project> pageBean);

        void onSuccessRelate(BaseObjectBean<String> bean);
    }

    interface Presenter {

        void addOrUpdate(Project project, boolean updateState);

        void loadData(String userID, int page, int size, String search);

        void delete(Context context, Project project);

        void relate(String serialNumber, String userID, String verCode);
    }
}
