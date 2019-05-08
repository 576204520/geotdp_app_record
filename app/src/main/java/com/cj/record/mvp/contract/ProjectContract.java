package com.cj.record.mvp.contract;

import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.PageBean;
import com.cj.record.baen.Project;
import com.cj.record.mvp.base.BaseView;

import io.reactivex.Flowable;

public interface ProjectContract {
    interface Model {
        Flowable<BaseObjectBean<String>> relate(String serialNumber, String userID, String verCode);

        Flowable<PageBean<Project>> loadData(String userID, int page, int size, String search);

        Flowable<Project> addOrUpdate(Project project, boolean updateState);

        Flowable<Boolean> delete(Project project);

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

        void delete(Project project);

        void relate(String serialNumber, String userID, String verCode);
    }
}
