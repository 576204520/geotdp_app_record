package com.cj.record.presenter;


import android.content.Context;

import com.cj.record.activity.MainActivity;
import com.cj.record.db.DBManager;
import com.cj.record.utils.FileUtil;
import com.cj.record.utils.L;
import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.LocalUser;
import com.cj.record.base.BasePresenter;
import com.cj.record.contract.UserContract;
import com.cj.record.model.UserModel;
import com.cj.record.net.RxScheduler;
import com.cj.record.utils.NetUtil;
import com.cj.record.utils.Urls;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author azheng
 * @date 2018/6/4.
 * GitHub：https://github.com/RookieExaminer
 * Email：wei.azheng@foxmail.com
 * Description：
 */
public class UserPresenter extends BasePresenter<UserContract.View> implements UserContract.Presenter {

    private UserContract.Model model;

    public UserPresenter() {
        model = new UserModel();
    }

    @Override
    public void login(String email, String password) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.login(email, password)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.onSuccess(bean);
                        mView.hideLoading();
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
    public void versionCheck(String userID, String verCode) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        model.versionCheck(userID, verCode)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.onSuccessUpdateVersion(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onError(throwable);
                    }
                });
    }

    @Override
    public void updateInfo(String userID, String email, String idCard, String certificateNumber3) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.updateInfo(userID, email, idCard, certificateNumber3)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.onSuccessUpdateInfo(bean);
                        mView.hideLoading();
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
    public void resetPassword(String userID, String email, String oldPassword, String newPassword, String newPassword2) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.resetPassword(userID, email, oldPassword, newPassword, newPassword2)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.onSuccessResetPassword(bean);
                        mView.hideLoading();
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
    public void checkOperate(String userID, String operatePerson, String testType) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.checkOperate(userID, operatePerson, testType)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.onSuccessCheckOperate(bean);
                        mView.hideLoading();
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
    public void initDB(Context context) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                //初始化数据库
                FileUtil.initDB(context);
                DBManager.init(context, Urls.DATABASE_BASE, Urls.APP_KEY);
                e.onNext("");
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {

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
                        mView.onSuccessInitDB();
                    }
                });

    }
}
