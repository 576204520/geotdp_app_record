package com.cj.record.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.Urls;

import butterknife.internal.Utils;

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
            // 检查userid是否存在
            String userID = (String) SPUtils.get(WelcomeActivity.this, Urls.SPKey.USER_ID, "");
            String email = (String) SPUtils.get(WelcomeActivity.this, Urls.SPKey.USER_EMAIL, "");
            String realName = (String) SPUtils.get(WelcomeActivity.this, Urls.SPKey.USER_REALNAME, "");
            //添加到base里，全局用
            if (TextUtils.isEmpty(userID) || TextUtils.isEmpty(email) || TextUtils.isEmpty(realName)) {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            } else {
                BaseActivity.userID = userID;
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            }
            finish();
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }
    }
}
