package com.cj.record.model;


import com.cj.record.baen.BaseObjectBean;
import com.cj.record.contract.DictionaryContract;
import com.cj.record.net.RetrofitClient;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;

/**
 * @author azheng
 * @date 2018/6/4.
 * GitHub：https://github.com/RookieExaminer
 * Email：wei.azheng@foxmail.com
 * Description：
 */
public class DictionaryModel implements DictionaryContract.Model {
    @Override
    public Flowable<BaseObjectBean<String>> uploadDictionary(Map<String, String> params) {
        return RetrofitClient.getInstance().getApi().uploadDictionary(params);
    }

    @Override
    public Flowable<BaseObjectBean<String>> downloadDictionary(String relateID, String verCode) {
        return RetrofitClient.getInstance().getApi().downloadDictionary(relateID, verCode);
    }
}
