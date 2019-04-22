package com.cj.record.presenter;


import com.cj.record.baen.BaseObjectBean;
import com.cj.record.base.BasePresenter;
import com.cj.record.contract.ChatContract;
import com.cj.record.contract.DictionaryContract;
import com.cj.record.model.ChatModel;
import com.cj.record.model.DictionaryModel;
import com.cj.record.net.RxScheduler;

import java.util.List;

import io.reactivex.functions.Consumer;
import okhttp3.MultipartBody;

/**
 * @author azheng
 * @date 2018/6/4.
 * GitHub：https://github.com/RookieExaminer
 * Email：wei.azheng@foxmail.com
 * Description：
 */
public class ChatPresenter extends BasePresenter<ChatContract.View> implements ChatContract.Presenter {

    private ChatContract.Model model;

    public ChatPresenter() {
        model = new ChatModel();
    }

    @Override
    public void myUserForCompanyOrProject(String userID) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        model.myUserForCompanyOrProject(userID)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.onSuccessMyUserForCompanyOrProject(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onError(throwable);
                    }
                });
    }

    @Override
    public void myFriendList(String userID) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        model.myFriendList(userID)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.onSuccessMyFriendList(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onError(throwable);
                    }
                });
    }

    @Override
    public void addUserFriend(String userID, String targetUserID) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.addUserFriend(userID, targetUserID)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.hideLoading();
                        mView.onSuccessAddUserFriend(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.hideLoading();
                        mView.onError(throwable);
                    }
                });
    }

    @Override
    public void deleteUserFriend(String userID, String targetUserID) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.deleteUserFriend(userID, targetUserID)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.hideLoading();
                        mView.onSuccessDeleteUserFriend(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.hideLoading();
                        mView.onError(throwable);
                    }
                });
    }

    @Override
    public void sendMessage(String userID, String targetUserID, String message) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        model.sendMessage(userID, targetUserID, message)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.onSuccessSendMessage(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onError(throwable);
                    }
                });
    }

    @Override
    public void readMessageCallBack(String userID, String chatRecordId) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        model.readMessageCallBack(userID, chatRecordId)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.onSuccessReadMessageCallBack(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onError(throwable);
                    }
                });
    }

    @Override
    public void myChatRecord(String userID, String targetUserID) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        model.myChatRecord(userID, targetUserID)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.onSuccessMyChatRecord(bean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onError(throwable);
                    }
                });
    }
}
