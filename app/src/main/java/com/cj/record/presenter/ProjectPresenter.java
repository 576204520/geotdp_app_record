package com.cj.record.presenter;


import android.content.Context;

import com.alibaba.idst.nls.internal.utils.L;
import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.PageBean;
import com.cj.record.baen.Project;
import com.cj.record.base.BaseActivity;
import com.cj.record.base.BasePresenter;
import com.cj.record.contract.ProjectContract;
import com.cj.record.contract.UserContract;
import com.cj.record.db.HoleDao;
import com.cj.record.db.MediaDao;
import com.cj.record.db.ProjectDao;
import com.cj.record.db.RecordDao;
import com.cj.record.model.ProjectModel;
import com.cj.record.model.UserModel;
import com.cj.record.net.RxScheduler;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
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
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                if (updateState) {
                    HoleDao.getInstance().updateState(project.getId());
                    RecordDao.getInstance().updateState(project.getId());
                    MediaDao.getInstance().updateState(project.getId());
                    project.setState("1");
                }
                ProjectDao.getInstance().addOrUpdate(project);
                e.onNext("");
                e.onComplete();
            }
        }).subscribe(new Observer<String>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                L.e("接收数据,当前线程"+Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e);
                mView.hideLoading();

            }

            @Override
            public void onComplete() {
                mView.hideLoading();
                mView.onSuccessAddOrUpdate();
            }
        });
    }

    @Override
    public void loadData(String userID, int page, int size, String search) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        Observable.create(new ObservableOnSubscribe<PageBean<Project>>() {
            @Override
            public void subscribe(ObservableEmitter<PageBean<Project>> e) throws Exception {
                List<Project> list = ProjectDao.getInstance().getAll(userID, page, size, search);
                int totleSize = ProjectDao.getInstance().getAllCount(userID);
                PageBean<Project> pageBean = new PageBean<>();
                pageBean.setTotleSize(totleSize);
                pageBean.setPage(page);
                pageBean.setSize(size);
                pageBean.setList(list);
                e.onNext(pageBean);
                e.onComplete();
            }
        }).subscribe(new Observer<PageBean<Project>>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(PageBean<Project> pageBean) {
                mView.onSuccessList(pageBean);
            }

            @Override
            public void onError(Throwable e) {
                mView.hideLoading();
                mView.onError(e);
            }

            @Override
            public void onComplete() {
                mView.hideLoading();
            }
        });
    }

    @Override
    public void delete(Context context, Project project) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                project.delete(context);
                e.onNext("");
                e.onComplete();
            }
        }).subscribe(new Observer<String>() {

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e);
                mView.hideLoading();
            }

            @Override
            public void onComplete() {
                mView.hideLoading();
                mView.onSuccessDelete();
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
