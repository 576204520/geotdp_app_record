package com.cj.record.mvp.contract;

import com.cj.record.baen.BaseObjectBean;
import com.cj.record.mvp.base.BaseView;

import java.util.Map;

import io.reactivex.Flowable;

/**
 * Created by Administrator on 2019/4/3.
 */

public interface DictionaryContract {
    interface Model {
        Flowable<BaseObjectBean<String>> uploadDictionary(Map<String, String> params);

        Flowable<BaseObjectBean<String>> downloadDictionary(String relateID, String verCode);
    }

    interface View extends BaseView {
        @Override
        void showLoading();

        @Override
        void hideLoading();

        @Override
        void onError(Throwable throwable);

        void onSuccessUpload(BaseObjectBean<String> bean);

        void onSuccessDownload(BaseObjectBean<String> bean);
    }

    interface Presenter {
        /**
         * 上传字典库
         *
         */
        void uploadDictionary(Map<String, String> params);

        /**
         * 获取字典库列表
         *
         * @param relateID
         * @param verCode
         */
        void downloadDictionary(String relateID, String verCode);

    }
}
