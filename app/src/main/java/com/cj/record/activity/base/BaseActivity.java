package com.cj.record.activity.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.cj.record.R;
import com.cj.record.views.ProgressPopupWindow;

import java.util.Timer;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    public Context mContext;
    public ProgressPopupWindow progressPopupWindow;
    public static String userID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mContext = getApplicationContext();
        ButterKnife.bind(this);
        initMust(savedInstanceState);
        init();
    }

    private final void init() {
        initData();
        initView();
    }

    public void initMust(Bundle savedInstanceState){

    }
    public void initData() {

    }

    public abstract int getLayoutId();

    public abstract void initView();

    public void showPPW() {
        if (progressPopupWindow == null) {
            progressPopupWindow = new ProgressPopupWindow(this);
        }
        progressPopupWindow.showPopupWindow();
    }

    public void dismissPPW() {
        if (progressPopupWindow != null) {
            progressPopupWindow.dismiss();
        }
    }

    /**
     * 通过Class跳转界面
     **/
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(cls, null, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }
}
