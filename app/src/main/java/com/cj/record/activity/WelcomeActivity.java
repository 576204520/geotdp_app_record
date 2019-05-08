package com.cj.record.activity;

import android.Manifest;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.mvp.base.App;
import com.cj.record.mvp.base.BaseActivity;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.Urls;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import io.reactivex.functions.Action;


public class WelcomeActivity extends BaseActivity {
    @BindView(R.id.welcome_hint)
    TextView welcomeHint;

    @Override
    public int getLayoutId() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        return R.layout.activity_welcome;
    }

    @Override
    public void initView() {
        new MyCount(2000, 1000).start();
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
                                    boolean isAuto = (boolean) SPUtils.get(WelcomeActivity.this, Urls.SPKey.USER_AUTO, false);
                                    if (isAuto) {
                                        // 检查userid是否存在
                                        String userID = (String) SPUtils.get(WelcomeActivity.this, Urls.SPKey.USER_ID, "");
                                        App.userID = userID;
                                        startActivity(MainActivity.class);
                                    } else {
                                        startActivity(LoginActivity.class);
                                    }
                                    finish();
                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    ToastUtil.showToastS(WelcomeActivity.this, getString(R.string.welcome_hint_no_file));
                                } else {
                                    ToastUtil.showToastS(WelcomeActivity.this, getString(R.string.welcome_hint_go_setting));
                                }
                                break;
                            case Manifest.permission.CAMERA:
                                if (permission.granted) {
                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    ToastUtil.showToastS(WelcomeActivity.this, getString(R.string.welcome_hint_no_camera));
                                } else {
                                    ToastUtil.showToastS(WelcomeActivity.this, getString(R.string.welcome_hint_go_setting));
                                }
                                break;
                            case Manifest.permission.ACCESS_FINE_LOCATION:
                                if (permission.granted) {
                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    ToastUtil.showToastS(WelcomeActivity.this, getString(R.string.welcome_hint_no_location));
                                } else {
                                    ToastUtil.showToastS(WelcomeActivity.this, getString(R.string.welcome_hint_go_setting));
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
