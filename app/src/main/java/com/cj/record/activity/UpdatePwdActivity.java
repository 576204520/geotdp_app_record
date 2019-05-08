package com.cj.record.activity;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.baen.BaseObjectBean;
import com.cj.record.mvp.base.BaseMvpActivity;
import com.cj.record.mvp.contract.UserContract;
import com.cj.record.mvp.presenter.UserPresenter;
import com.cj.record.utils.Common;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.Urls;
import com.cj.record.views.MaterialEditTextNoEmoji;
import com.cj.record.views.ProgressDialog;

import net.qiujuer.genius.ui.widget.Button;


import butterknife.BindView;
import butterknife.OnClick;

public class UpdatePwdActivity extends BaseMvpActivity<UserPresenter> implements UserContract.View {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.email_textView)
    TextView emailTextView;
    @BindView(R.id.submit_button)
    Button submitButton;
    @BindView(R.id.oldPassword_editText)
    MaterialEditTextNoEmoji oldPasswordEditText;
    @BindView(R.id.newPassword_editText)
    MaterialEditTextNoEmoji newPasswordEditText;
    @BindView(R.id.newPassword2_editText)
    MaterialEditTextNoEmoji newPassword2EditText;

    private String userID;
    private String email;
    private String newPassword;

    @Override
    public int getLayoutId() {
        return R.layout.activity_update_pwd;
    }

    @Override
    public void initView() {
        mPresenter = new UserPresenter();
        mPresenter.attachView(this);

        toolbar.setTitle(R.string.user_update_pwd_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userID = (String) SPUtils.get(this, Urls.SPKey.USER_ID, "");
        email = (String) SPUtils.get(this, Urls.SPKey.USER_EMAIL, "");

        emailTextView.setText(TextUtils.isEmpty(email) ? "" : email);
    }

    @OnClick({R.id.submit_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.submit_button:
                doSubmit();
                break;
        }
    }

    private void doSubmit() {
        String oldPassword = oldPasswordEditText.getText().toString().trim();
        newPassword = newPasswordEditText.getText().toString().trim();
        String newPassword2 = newPassword2EditText.getText().toString().trim();
        if (TextUtils.isEmpty(oldPassword)) {
            ToastUtil.showToastS(this, getString(R.string.user_update_pwd_p1));
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            ToastUtil.showToastS(this, getString(R.string.user_update_pwd_p2));
            return;
        }
        if (oldPassword.equals(newPassword)) {
            ToastUtil.showToastS(this, getString(R.string.user_update_pwd_p3));
            return;
        }
        if (TextUtils.isEmpty(newPassword2)) {
            ToastUtil.showToastS(this, getString(R.string.user_update_pwd_p4));
            return;
        }
        if (!newPassword.equals(newPassword2)) {
            ToastUtil.showToastS(this, getString(R.string.user_update_pwd_p5));
            return;
        }
        mPresenter.resetPassword(userID, email, oldPassword, newPassword, newPassword2);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    }

    @Override
    public void onSuccessUpdateVersion(BaseObjectBean<String> bean) {

    }

    @Override
    public void onSuccessUpdateInfo(BaseObjectBean<String> bean) {

    }

    @Override
    public void onSuccessResetPassword(BaseObjectBean<String> bean) {
        if (bean.isStatus()) {
            String passwordOld = (String) SPUtils.get(this, Urls.SPKey.USER_PWD, "");
            if (!TextUtils.isEmpty(passwordOld)) {
                SPUtils.put(this, Urls.SPKey.USER_PWD, newPassword);
            }
            finish();
            setResult(RESULT_OK);
        } else {
            Common.showMessage(UpdatePwdActivity.this, bean.getMessage());
        }
    }

    @Override
    public void onSuccessCheckOperate(BaseObjectBean<String> bean) {

    }

    @Override
    public void onSuccessInitDB() {

    }
}
