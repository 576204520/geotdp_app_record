package com.cj.record.utils;

import android.util.Log;

import com.cj.record.adapter.ProjectAdapter;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/6/5.
 */

public class ObsUtils {

    public void execute(final int type) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                obsLinstener.onSubscribe(type);
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

                    }

                    @Override
                    public void onComplete() {
                        obsLinstener.onComplete(type);
                    }
                });
    }

    private ObsLinstener obsLinstener;

    public void setObsLinstener(ObsLinstener obsLinstener) {
        this.obsLinstener = obsLinstener;
    }

    public interface ObsLinstener {
        void onSubscribe(int type);

        void onComplete(int type);
    }
}
