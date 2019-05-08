package com.cj.record.mvp.presenter;


import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.PageBean;
import com.cj.record.baen.Project;
import com.cj.record.mvp.base.BasePresenter;
import com.cj.record.mvp.contract.ProjectContract;
import com.cj.record.mvp.model.ProjectModel;
import com.cj.record.mvp.net.RxScheduler;

import io.reactivex.functions.Consumer;

public class ProjectPresenter extends BasePresenter<ProjectContract.View> implements ProjectContract.Presenter {

    private ProjectContract.Model model;

    public ProjectPresenter() {
        model = new ProjectModel();
    }

    @Override
    public void addOrUpdate(Project project, boolean updateState) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.addOrUpdate(project, updateState)
                .compose(RxScheduler.<Project>Flo_io_main())
                .as(mView.<Project>bindAutoDispose())
                .subscribe(new Consumer<Project>() {
                    @Override
                    public void accept(Project p) throws Exception {
                        mView.hideLoading();
                        mView.onSuccessAddOrUpdate();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onError(throwable);
                        mView.hideLoading();
                    }
                });

    }

    @Override
    public void loadData(String userID, int page, int size, String search) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        model.loadData(userID, page, size, search)
                .compose(RxScheduler.<PageBean<Project>>Flo_io_main())
                .as(mView.<PageBean<Project>>bindAutoDispose())
                .subscribe(new Consumer<PageBean<Project>>() {
                    @Override
                    public void accept(PageBean<Project> pageBean) throws Exception {
                        mView.onSuccessList(pageBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onError(throwable);
                    }
                });
    }

    @Override
    public void delete(Project project) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.delete(project)
                .compose(RxScheduler.<Boolean>Flo_io_main())
                .as(mView.<Boolean>bindAutoDispose())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean p) throws Exception {
                        mView.hideLoading();
                        mView.onSuccessDelete();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onError(throwable);
                        mView.hideLoading();
                    }
                });
    }

    @Override
    public void relate(String serialNumber, String userID, String verCode) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.relate(serialNumber, userID, verCode)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.hideLoading();
                        mView.onSuccessRelate(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.hideLoading();
                        mView.onError(throwable);
                    }
                });
    }

}
