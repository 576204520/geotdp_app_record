package com.cj.record.model;


import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.Project;
import com.cj.record.contract.ProjectContract;
import com.cj.record.contract.UserContract;
import com.cj.record.net.RetrofitClient;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author azheng
 * @date 2018/6/4.
 * GitHub：https://github.com/RookieExaminer
 * Email：wei.azheng@foxmail.com
 * Description：
 */
public class ProjectModel implements ProjectContract.Model {

    @Override
    public Flowable<BaseObjectBean<String>> relate(String serialNumber, String userID, String verCode) {
        return RetrofitClient.getInstance().getApi().relateProject(serialNumber, userID, verCode);
    }
}
