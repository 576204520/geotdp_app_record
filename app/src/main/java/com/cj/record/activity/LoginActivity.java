package com.cj.record.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.cj.record.R;
import com.cj.record.base.App;
import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.LocalUser;
import com.cj.record.base.BaseMvpActivity;
import com.cj.record.contract.UserContract;
import com.cj.record.presenter.UserPresenter;
import com.cj.record.utils.JsonUtils;
import com.cj.record.utils.MD5Utils;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.Urls;
import com.cj.record.views.ProgressDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/23.
 */

public class LoginActivity extends BaseMvpActivity<UserPresenter> implements UserContract.View, CompoundButton.OnCheckedChangeListener {

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
    public void initView() {
        mPresenter = new UserPresenter();
        mPresenter.attachView(this);
        loginRemember.setOnCheckedChangeListener(this);
        loginAuto.setOnCheckedChangeListener(this);
        String username = (String) SPUtils.get(this, Urls.SPKey.USER_EMAIL, "");
        String pwd = (String) SPUtils.get(this, Urls.SPKey.USER_PWD, "");
        boolean isAuto = (boolean) SPUtils.get(this, Urls.SPKey.USER_AUTO, false);
        loginEmail.setText(username);
        loginAuto.setChecked(isAuto);
        if (!TextUtils.isEmpty(pwd)) {
            loginPassword.setText(pwd);
            loginRemember.setChecked(true);
        }

    }

    @OnClick({R.id.login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login:
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    ToastUtil.showToastS(this, getString(R.string.user_hint_email));
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    ToastUtil.showToastS(this, getString(R.string.user_hint_password));
                    return;
                }
                mPresenter.login(email, MD5Utils.MD5(password));
                break;
        }
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

    @Override
    public void showLoading() {
        ProgressDialog.getInstance().show(this);
    }

    @Override
    public void hideLoading() {
        ProgressDialog.getInstance().dismiss();
    }

    @Override
    public void onError(Throwable throwable) {
        ToastUtil.showToastS(this, throwable.toString());
    }

    @Override
    public void onSuccess(BaseObjectBean<String> bean) {
        ToastUtil.showToastS(this, bean.getMessage());
        if (bean.isStatus()) {
            LocalUser localUser = JsonUtils.getInstance().fromJson(bean.getResult(), LocalUser.class);
            if (loginRemember.isChecked()) {
                SPUtils.put(this, Urls.SPKey.USER_PWD, loginPassword.getText().toString().trim());
            } else {
                SPUtils.put(this, Urls.SPKey.USER_PWD, "");
            }
            if (loginAuto.isChecked()) {
                SPUtils.put(this, Urls.SPKey.USER_AUTO, true);
            } else {
                SPUtils.put(this, Urls.SPKey.USER_AUTO, false);
            }
            saveUserToSP(localUser);
            startActivity(MainActivity.class);
            finish();
        } else {
            LocalUser localUser = JsonUtils.getInstance().fromJson(bean.getResult(), LocalUser.class);
            if (localUser != null) {
                saveUserToSP(localUser);
                Bundle bundle = new Bundle();
                bundle.putString(MainActivity.HINT, bean.getMessage());
                startActivity(UpdateInfoActivity.class, bundle);
            }
        }
    }

    @Override
    public void onSuccessUpdateVersion(BaseObjectBean<String> bean) {

    }

    @Override
    public void onSuccessUpdateInfo(BaseObjectBean<String> bean) {

    }

    @Override
    public void onSuccessResetPassword(BaseObjectBean<String> bean) {

    }

    @Override
    public void onSuccessCheckOperate(BaseObjectBean<String> bean) {

    }

    @Override
    public void onSuccessInitDB() {

    }


    private void saveUserToSP(LocalUser localUser) {
        App.userID = localUser.getId();
        SPUtils.put(this, Urls.SPKey.USER_ID, localUser.getId());
        SPUtils.put(this, Urls.SPKey.USER_EMAIL, localUser.getEmail());
        SPUtils.put(this, Urls.SPKey.USER_REALNAME, localUser.getRealName());
        SPUtils.put(this, Urls.SPKey.USER_IDCARD, TextUtils.isEmpty(localUser.getIdCard()) ? "" : localUser.getIdCard());
        SPUtils.put(this, Urls.SPKey.USER_CERTIFICATENUMBER3, TextUtils.isEmpty(localUser.getCertificateNumber3()) ? "" : localUser.getCertificateNumber3());
    }

}
