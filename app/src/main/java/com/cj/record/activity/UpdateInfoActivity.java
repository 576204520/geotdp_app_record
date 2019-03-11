package com.cj.record.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.idst.nls.internal.utils.L;
import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.baen.JsonResult;
import com.cj.record.utils.Common;
import com.cj.record.utils.JsonUtils;
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
 * Created by Administrator on 2019/3/8.
 */

public class UpdateInfoActivity extends BaseActivity {
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
        toolbar.setTitle("修改账号信息");
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
                showUpdateDialog(idCardTextView, "身份证号", idCardTextView.getText().toString(), InputType.TYPE_CLASS_TEXT);
                break;
            case R.id.certificateNumber3_cardView:
                showUpdateDialog(certificateNumber3TextView, "描述员证书号", certificateNumber3TextView.getText().toString(), InputType.TYPE_CLASS_TEXT);
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
            ToastUtil.showToastS(this, "身份证号未填写");
            return;
        }
        if (TextUtils.isEmpty(certificateNumber3)) {
            ToastUtil.showToastS(this, "描述员证书号未填写");
            return;
        }
        showPPW();
        Map<String, String> map = new HashMap<>();
        map.put("userID", userID);
        map.put("email", email);
        map.put("idCard", idCard);
        map.put("certificateNumber3", certificateNumber3);
        OkGo.<String>post(Urls.UPDATE_USER)
                .params(map)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        //注意这里已经是在主线程了
                        String data = response.body();//这个就是返回来的结果
                        L.e("修改个人信息:" + data);
                        if (JsonUtils.isGoodJson(data)) {
                            Gson gson = new Gson();
                            JsonResult jsonResult = gson.fromJson(data, JsonResult.class);
                            //如果登陆成功，保存用户名和密码到数据库,并保存到baen
                            if (jsonResult.getStatus()) {
                                finish();
                                setResult(RESULT_OK);
                            } else {
                                Common.showMessage(UpdateInfoActivity.this, jsonResult.getMessage());
                            }
                        } else {
                            ToastUtil.showToastS(UpdateInfoActivity.this, "服务器异常，请联系客服");
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dismissPPW();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ToastUtil.showToastS(UpdateInfoActivity.this, "网络连接错误");
                    }
                });

    }

    private void showUpdateDialog(TextView tv, String title, String msg, int intputType) {
        new MaterialDialog.Builder(this).title(title).inputType(intputType).input("请输入正确号码", msg, false, new MaterialDialog.InputCallback() {
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


}
