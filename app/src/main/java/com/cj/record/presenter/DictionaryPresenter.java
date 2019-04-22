package com.cj.record.presenter;


import com.cj.record.baen.BaseObjectBean;
import com.cj.record.base.BasePresenter;
import com.cj.record.contract.DictionaryContract;
import com.cj.record.model.DictionaryModel;
import com.cj.record.net.RxScheduler;

import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;
import okhttp3.MultipartBody;

/**
 * @author azheng
 * @date 2018/6/4.
 * GitHub：https://github.com/RookieExaminer
 * Email：wei.azheng@foxmail.com
 * Description：
 */
public class DictionaryPresenter extends BasePresenter<DictionaryContract.View> implements DictionaryContract.Presenter {

    private DictionaryContract.Model model;

    public DictionaryPresenter() {
        model = new DictionaryModel();
    }

    @Override
    public void uploadDictionary(Map<String, String> params) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.uploadDictionary(params)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.hideLoading();
                        mView.onSuccessUpload(bean);
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
    public void downloadDictionary(String relateID, String verCode) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }
        mView.showLoading();
        model.downloadDictionary(relateID, verCode)
                .compose(RxScheduler.<BaseObjectBean<String>>Flo_io_main())
                .as(mView.<BaseObjectBean<String>>bindAutoDispose())
                .subscribe(new Consumer<BaseObjectBean<String>>() {
                    @Override
                    public void accept(BaseObjectBean<String> bean) throws Exception {
                        mView.hideLoading();
                        mView.onSuccessDownload(bean);
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
