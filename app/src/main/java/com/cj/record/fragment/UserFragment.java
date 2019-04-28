package com.cj.record.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.offlinemap.OfflineMapActivity;
import com.cj.record.BuildConfig;
import com.cj.record.R;
import com.cj.record.activity.AboutActivity;
import com.cj.record.activity.DictionaryActvity;
import com.cj.record.activity.HelpActivtiy;
import com.cj.record.activity.LoginActivity;
import com.cj.record.activity.MainActivity;
import com.cj.record.activity.UpdatePwdActivity;
import com.cj.record.base.App;
import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.VersionVo;
import com.cj.record.base.BaseMvpFragment;
import com.cj.record.contract.UserContract;
import com.cj.record.presenter.UserPresenter;
import com.cj.record.utils.Common;
import com.cj.record.utils.JsonUtils;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.SqlcipherUtil;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.UpdateUtil;
import com.cj.record.utils.Urls;
import com.cj.record.views.ProgressDialog;

import net.qiujuer.genius.ui.widget.Button;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2019/4/8.
 */

public class UserFragment extends BaseMvpFragment<UserPresenter> implements UserContract.View {


    @BindView(R.id.user_icon)
    ImageView userIcon;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_email)
    TextView userEmail;
    @BindView(R.id.user_rl)
    RelativeLayout userRl;
    @BindView(R.id.user_dictionary)
    CardView userDictionary;
    @BindView(R.id.user_help)
    CardView userHelp;
    @BindView(R.id.user_map)
    CardView userMap;
    @BindView(R.id.user_update)
    CardView userUpdate;
    @BindView(R.id.user_about)
    CardView userAbout;
    @BindView(R.id.user_decode)
    CardView userDecode;
    @BindView(R.id.user_logout)
    Button userLogout;

    @Override
    protected void initView(View view) {
        mPresenter = new UserPresenter();
        mPresenter.attachView(this);
        //debug模式，显示解密按钮
        if (BuildConfig.ISDEBUG) {
            userDecode.setVisibility(View.VISIBLE);
        } else {
            userDecode.setVisibility(View.GONE);
        }

        userName.setText((String) SPUtils.get(mActivity, Urls.SPKey.USER_REALNAME, ""));
        userEmail.setText((String) SPUtils.get(mActivity, Urls.SPKey.USER_EMAIL, ""));
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_user;
    }


    @OnClick({R.id.user_dictionary, R.id.user_help, R.id.user_map, R.id.user_update, R.id.user_about,
            R.id.user_logout, R.id.user_rl, R.id.user_icon, R.id.user_name,
            R.id.user_email,
            R.id.user_decode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_dictionary:
                startActivity(DictionaryActvity.class);
                break;
            case R.id.user_help:
                Intent intent = new Intent(mActivity, HelpActivtiy.class);
                intent.setAction("MainActivity");
                startActivity(intent);
                break;
            case R.id.user_map:
                startActivity(new Intent(mActivity, OfflineMapActivity.class));
                break;
            case R.id.user_update:
                //检查版本
                mPresenter.versionCheck(App.userID, UpdateUtil.getVerCode(mActivity) + "");
                break;
            case R.id.user_about:
                startActivity(new Intent(mActivity, AboutActivity.class));
                break;
            case R.id.user_logout:
                logoutDialog();
                break;
            case R.id.user_rl:
            case R.id.user_icon:
            case R.id.user_name:
            case R.id.user_email:
                startActivityForResult(UpdatePwdActivity.class, MainActivity.USER_GO_MY);
                break;
            case R.id.user_decode:
                try {
                    SqlcipherUtil.decrypt(Urls.DATABASE_BASE, Urls.APP_KEY);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ToastUtil.showToastS(mActivity, "解密完成");

                break;
        }
    }

    private void logoutDialog() {
        new AlertDialog.Builder(mActivity)
                .setTitle(R.string.hint)
                .setMessage(R.string.main_logout_msg)
                .setNegativeButton(R.string.agree,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SPUtils.put(mActivity, Urls.SPKey.USER_ID, "");
                                SPUtils.put(mActivity, Urls.SPKey.USER_EMAIL, "");
                                SPUtils.put(mActivity, Urls.SPKey.USER_REALNAME, "");
                                SPUtils.put(mActivity, Urls.SPKey.USER_IDCARD, "");
                                SPUtils.put(mActivity, Urls.SPKey.USER_CERTIFICATENUMBER3, "");
                                SPUtils.put(mActivity, Urls.SPKey.USER_PWD, "");
                                SPUtils.put(mActivity, Urls.SPKey.USER_AUTO, false);
                                startActivity(LoginActivity.class);
                                mActivity.finish();
                            }
                        })
                .setPositiveButton(R.string.disagree, null)
                .setCancelable(false)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //修改个人信息返回
        if (requestCode == MainActivity.USER_GO_MY && resultCode == RESULT_OK) {
            SPUtils.put(mActivity, Urls.SPKey.USER_AUTO, false);
            startActivity(LoginActivity.class);
            mActivity.finish();
        }
    }

    @Override
    public void showLoading() {
        ProgressDialog.getInstance().show(mActivity);
    }

    @Override
    public void hideLoading() {
        ProgressDialog.getInstance().dismiss();
    }

    @Override
    public void onError(Throwable throwable) {
        ToastUtil.showToastS(mActivity, throwable.toString());
    }

    @Override
    public void onSuccess(BaseObjectBean<String> bean) {

    }

    @Override
    public void onSuccessUpdateVersion(BaseObjectBean<String> bean) {
        if (bean.isStatus()) {
            VersionVo version = JsonUtils.getInstance().fromJson(bean.getResult().toString(), VersionVo.class);
            String type = version.getType();
            String content = "版本:" + version.getName() + "\n更新内容:" + version.getDescription() + "\n大小:" + version.getSize();
            int code = Integer.parseInt(version.getCode());
            //0-不用更新  1-可以更新 2必须更新
            if (UpdateUtil.getVerCode(mActivity) < code) {
                if ("2".equals(type)) {
                    Common.showViesionDialog(mActivity, content, true);
                } else {
                    Common.showViesionDialog(mActivity, content, false);
                }
            } else {
                ToastUtil.showToastS(mActivity, "已是最新版本");
            }
        } else {
            ToastUtil.showToastS(mActivity, bean.getMessage());
        }
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
}
