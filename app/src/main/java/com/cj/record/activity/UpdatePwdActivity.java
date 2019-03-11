package com.cj.record.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.baen.JsonResult;
import com.cj.record.utils.Common;
import com.cj.record.utils.JsonUtils;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.Urls;
import com.cj.record.views.MaterialEditTextNoEmoji;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.qiujuer.genius.ui.widget.Button;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2019/3/8.
 */

public class UpdatePwdActivity extends BaseActivity {
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

    @Override
    public int getLayoutId() {
        return R.layout.activity_update_pwd;
    }

    @Override
    public void initView() {
        toolbar.setTitle("修改密码");
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
        String newPassword = newPasswordEditText.getText().toString().trim();
        String newPassword2 = newPassword2EditText.getText().toString().trim();
        if (TextUtils.isEmpty(oldPassword)) {
            ToastUtil.showToastS(this, "请输入老密码");
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            ToastUtil.showToastS(this, "请输入新密码");
            return;
        }
        if (oldPassword.equals(newPassword)) {
            ToastUtil.showToastS(this, "新密码与老密码一致，请重新填写");
            return;
        }
        if (TextUtils.isEmpty(newPassword2)) {
            ToastUtil.showToastS(this, "请输入确认密码");
            return;
        }
        if (!newPassword.equals(newPassword2)) {
            ToastUtil.showToastS(this, "两次输入密码不一致");
            return;
        }

        showPPW();
        Map<String, String> map = new HashMap<>();
        map.put("userID", userID);
        map.put("email", email);
        map.put("oldPassword", oldPassword);
        map.put("newPassword", newPassword);
        map.put("newPassword2",newPassword2);
        OkGo.<String>post(Urls.UPDATE_PWD)
                .params(map)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        //注意这里已经是在主线程了
                        String data = response.body();//这个就是返回来的结果
                        if (JsonUtils.isGoodJson(data)) {
                            Gson gson = new Gson();
                            JsonResult jsonResult = gson.fromJson(data, JsonResult.class);
                            //如果登陆成功，保存用户名和密码到数据库,并保存到baen
                            if (jsonResult.getStatus()) {
                                String passwordOld = (String) SPUtils.get(mContext, Urls.SPKey.USER_PWD, "");
                                if (!TextUtils.isEmpty(passwordOld)) {
                                    SPUtils.put(mContext, Urls.SPKey.USER_PWD, newPassword);
                                }
                                finish();
                                setResult(RESULT_OK);
                            } else {
                                Common.showMessage(UpdatePwdActivity.this, jsonResult.getMessage());
                            }
                        } else {
                            ToastUtil.showToastS(UpdatePwdActivity.this, "服务器异常，请联系客服");
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
                        ToastUtil.showToastS(UpdatePwdActivity.this, "网络连接错误");
                    }
                });

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
