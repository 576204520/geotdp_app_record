package com.cj.record.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.baen.JsonResult;
import com.cj.record.baen.LocalUser;
import com.cj.record.utils.MD5Utils;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.Urls;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/23.
 */

public class LoginActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.login_email)
    EditText loginEmail;
    @BindView(R.id.login_password)
    EditText loginPassword;
    @BindView(R.id.login_logo_rl)
    RelativeLayout loginLogoRl;
    @BindView(R.id.login_remember)
    AppCompatCheckBox loginRemember;
    @BindView(R.id.login_auto)
    AppCompatCheckBox loginAuto;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initData() {
        super.initData();
//        loginEmail.setText("576204520@qq.com");
//        loginPassword.setText("123456");
    }

    @Override
    public void initView() {
        loginRemember.setOnCheckedChangeListener(this);
        loginAuto.setOnCheckedChangeListener(this);
        String pwd = (String) SPUtils.get(mContext, Urls.SPKey.USER_PWD, "");
        String username = (String) SPUtils.get(mContext, Urls.SPKey.USER_EMAIL, "");
        loginEmail.setText(username);
        if (!TextUtils.isEmpty(pwd)) {
            loginPassword.setText(pwd);
            loginRemember.setChecked(true);
            boolean isAuto = (boolean) SPUtils.get(mContext, Urls.SPKey.USER_AUTO, false);
            loginAuto.setChecked(isAuto);
        }

    }

    @OnClick({R.id.login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login:
                doLogin();
                break;
        }
    }

    private void doLogin() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            ToastUtil.showToastS(mContext, "请输入邮箱账号");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.showToastS(mContext, "请输入密码");
            return;
        }
        //检查网络
        if(!haveNet()){
            return;
        }
        showPPW();
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("password", MD5Utils.MD5(password));
        OkGo.<String>post(Urls.LOGIN_POST)
                .params(map)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        //注意这里已经是在主线程了
                        String data = response.body();//这个就是返回来的结果
                        Gson gson = new Gson();
                        JsonResult jsonResult = gson.fromJson(data, JsonResult.class);
                        //如果登陆成功，保存用户名和密码到数据库,并保存到baen
                        if (jsonResult.getStatus()) {
                            String result = jsonResult.getResult();
                            LocalUser localUser = gson.fromJson(result.toString(), LocalUser.class);
                            if (loginRemember.isChecked()) {
                                SPUtils.put(mContext, Urls.SPKey.USER_PWD, password);
                            } else {
                                SPUtils.put(mContext, Urls.SPKey.USER_PWD, "");
                            }
                            if (loginAuto.isChecked()) {
                                SPUtils.put(mContext, Urls.SPKey.USER_AUTO, true);
                            } else {
                                SPUtils.put(mContext, Urls.SPKey.USER_AUTO, false);
                            }
                            SPUtils.put(mContext, Urls.SPKey.USER_ID, localUser.getId());
                            SPUtils.put(mContext, Urls.SPKey.USER_EMAIL, localUser.getEmail());
                            SPUtils.put(mContext, Urls.SPKey.USER_REALNAME, localUser.getRealName());
                            BaseActivity.userID = localUser.getId();
                            startActivity(MainActivity.class);
                            finish();
                        }
                        ToastUtil.showToastS(mContext, jsonResult.getMessage() + "");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dismissPPW();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ToastUtil.showToastS(mContext, "网络连接错误");
                    }
                });
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.login_remember:
                if (!loginRemember.isChecked() && loginAuto.isChecked()) {
                    loginAuto.setChecked(false);
                }
                break;
            case R.id.login_auto:
                if (loginAuto.isChecked() && !loginRemember.isChecked()) {
                    loginRemember.setChecked(true);
                }
                break;
        }
    }

}
