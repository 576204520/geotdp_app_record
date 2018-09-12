package com.cj.record.activity;

import android.Manifest;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.utils.L;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.UpdateUtil;
import com.cj.record.utils.Urls;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.internal.Utils;
import io.reactivex.functions.Action;

/**
 * Created by Administrator on 2018/5/23.
 */

public class WelcomeActivity extends BaseActivity {
    @Override
    public int getLayoutId() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        return R.layout.activity_welcome;
    }

    @Override
    public void initData() {
        super.initData();
        new MyCount(2000, 1000).start();
    }

    @Override
    public void initView() {

    }

    /**
     * 定义一个倒计时的内部类
     */
    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            requestPermissions();
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }
    }

    private void requestPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)
                .subscribe(new io.reactivex.functions.Consumer<Permission>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Permission permission) throws Exception {
                        switch (permission.name) {
                            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                                if (permission.granted) {
                                    //检查是否是自动登陆
                                    boolean isAuto = (boolean) SPUtils.get(WelcomeActivity.this, Urls.SPKey.USER_AUTO,false);
                                    if(isAuto){
                                        // 检查userid是否存在
                                        String userID = (String) SPUtils.get(WelcomeActivity.this, Urls.SPKey.USER_ID, "");
                                        BaseActivity.userID = userID;
                                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                    }else{
                                        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                                    }

                                    finish();
                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    ToastUtil.showToastS(mContext, "取消存储授权,不能存储图片文件");
                                } else {
                                    ToastUtil.showToastS(mContext, "您已经禁止弹出存储的授权操作,请在设置中手动开启");
                                }
                                break;
                            case Manifest.permission.CAMERA:
                                if (permission.granted) {
                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    ToastUtil.showToastS(mContext, "取消照相机授权");
                                } else {
                                    ToastUtil.showToastS(mContext, "您已经禁止弹出照相机的授权操作,请在设置中手动开启");
                                }
                                break;
                            case Manifest.permission.ACCESS_FINE_LOCATION:
                                if (permission.granted) {
                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    ToastUtil.showToastS(mContext, "取消定位授权,不能获取定位信息");
                                } else {
                                    ToastUtil.showToastS(mContext, "您已经禁止弹出定位的授权操作,请在设置中手动开启");
                                }
                            default:
                                break;
                        }

                    }
                }, new io.reactivex.functions.Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        Log.i("--->>", "onError", throwable);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });
    }
}
