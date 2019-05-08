package com.cj.record.activity;

import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cj.record.R;
import com.cj.record.baen.BaseObjectBean;
import com.cj.record.mvp.base.BaseMvpActivity;
import com.cj.record.mvp.contract.UserContract;
import com.cj.record.mvp.presenter.UserPresenter;
import com.cj.record.utils.Common;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.Urls;
import com.cj.record.views.ProgressDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2019/3/8.
 */

public class UpdateInfoActivity extends BaseMvpActivity<UserPresenter> implements UserContract.View {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.email_textView)
    TextView emailTextView;
    @BindView(R.id.idCard_textView)
    TextView idCardTextView;
    @BindView(R.id.certificateNumber3_textView)
    TextView certificateNumber3TextView;
    @BindView(R.id.hint_textView)
    TextView hintTextView;

    private String userID;
    private String email;

    @Override
    public int getLayoutId() {
        return R.layout.activity_update_info;
    }

    @Override
    public void initView() {
        mPresenter = new UserPresenter();
        mPresenter.attachView(this);

        toolbar.setTitle(R.string.user_update_info_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userID = (String) SPUtils.get(this, Urls.SPKey.USER_ID, "");
        email = (String) SPUtils.get(this, Urls.SPKey.USER_EMAIL, "");
        String realName = (String) SPUtils.get(this, Urls.SPKey.USER_REALNAME, "");
        String idCard = (String) SPUtils.get(this, Urls.SPKey.USER_IDCARD, "");
        String certificatenumber3 = (String) SPUtils.get(this, Urls.SPKey.USER_CERTIFICATENUMBER3, "");

        String hint = getIntent().getExtras().getString(MainActivity.HINT);
        hintTextView.setText(hint);

        emailTextView.setText(TextUtils.isEmpty(email) ? "" : email);
        idCardTextView.setText(TextUtils.isEmpty(idCard) ? "" : idCard);
        certificateNumber3TextView.setText(TextUtils.isEmpty(certificatenumber3) ? "" : certificatenumber3);

    }

    @OnClick({R.id.idCard_cardView, R.id.certificateNumber3_cardView, R.id.submit_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.idCard_cardView:
                showUpdateDialog(idCardTextView, getString(R.string.user_update_info_cardid), idCardTextView.getText().toString(), InputType.TYPE_CLASS_TEXT);
                break;
            case R.id.certificateNumber3_cardView:
                showUpdateDialog(certificateNumber3TextView, getString(R.string.user_update_info_msyid), certificateNumber3TextView.getText().toString(), InputType.TYPE_CLASS_TEXT);
                break;
            case R.id.submit_button:
                doSubmit();
                break;
        }
    }

    private void doSubmit() {
        String idCard = idCardTextView.getText().toString().trim();
        String certificateNumber3 = certificateNumber3TextView.getText().toString().trim();
        if (TextUtils.isEmpty(idCard)) {
            ToastUtil.showToastS(this, getString(R.string.user_update_info_no_card));
            return;
        }
        if (TextUtils.isEmpty(certificateNumber3)) {
            ToastUtil.showToastS(this, getString(R.string.user_update_info_no_msyid));
            return;
        }
        mPresenter.updateInfo(userID, email, idCard, certificateNumber3);
    }

    private void showUpdateDialog(TextView tv, String title, String msg, int intputType) {
        new MaterialDialog.Builder(this).title(title).inputType(intputType).input(getString(R.string.user_update_info_please), msg, false, new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog dialog, CharSequence input) {
                if (!TextUtils.isEmpty(input.toString().trim())) {
                    tv.setText(input.toString().trim());
                }
                dialog.dismiss();
            }
        }).show();
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
        if (bean.isStatus()) {
            finish();
            setResult(RESULT_OK);
        } else {
            Common.showMessage(UpdateInfoActivity.this, bean.getMessage());
        }
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

}
