package com.cj.record.activity.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.cj.record.R;
import com.cj.record.activity.HoleEditActivity;
import com.cj.record.activity.MainActivity;
import com.cj.record.activity.RelateHoleActivity;
import com.cj.record.utils.Common;
import com.cj.record.utils.NetUtil;
import com.cj.record.utils.ToastUtil;
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

    public void initMust(Bundle savedInstanceState) {

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

    /**
     * 检查网络
     */
    public boolean haveNet() {
        if (NetUtil.getNetWorkState(mContext) < 0) {
            new AlertDialog.Builder(this)
                    .setTitle("网络设置提示")
                    .setMessage("网络连接不可用,是否进行设置？")
                    .setNegativeButton("去设置",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                                }
                            })
                    .setPositiveButton("取消", null)
                    .setCancelable(false)
                    .show();
            return false;
        }else{
            return true;
        }
    }


}
