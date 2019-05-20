package com.cj.record.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.cj.record.R;
import com.cj.record.adapter.ViewPagerAdapter;
import com.cj.record.mvp.base.App;
import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.VersionVo;
import com.cj.record.mvp.base.BaseFragment;
import com.cj.record.mvp.base.BaseMvpActivity;
import com.cj.record.mvp.contract.UserContract;
import com.cj.record.fragment.ProjectListFragment;
import com.cj.record.fragment.ChatFragment;
import com.cj.record.fragment.UserFragment;
import com.cj.record.mvp.presenter.UserPresenter;
import com.cj.record.utils.Common;
import com.cj.record.utils.JsonUtils;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.UpdateUtil;
import com.cj.record.utils.Urls;
import com.cj.record.views.MViewPager;
import com.cj.record.views.ProgressDialog;
import com.jpeng.jptabbar.JPTabBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class MainActivity extends BaseMvpActivity<UserPresenter> implements UserContract.View {

    @BindView(R.id.main_viewPager)
    MViewPager viewPager;
    @BindView(R.id.main_tabLayout)
    JPTabBar tabLayout;
    List<BaseFragment> fragments = new ArrayList<>();
    private ViewPagerAdapter viewPagerAdapter;

    public static final String FROMTYPE = "fromtype";//区分编辑还是添加
    public static final int PROJECT_GO_EDIT = 101;
    public static final int PROJECT_GO_LIST = 102;
    public static final int USER_GO_MY = 107;//去修改密码界面
    public static final String PROJECT = "project";
    public static final String SERIALNUMBER = "serialnumber";
    public static final int HOLE_GO_EDIT = 201;
    public static final int HOLE_GO_LIST = 202;
    public static final String HOLE = "hole";
    public static final String EXTRA_HOLE_TYPE = "holeType";//钻孔类型
    public static final int RECORD_GO_EDIT = 301;
    public static final String RECORD = "record";
    public static final String EXTRA_RECORD_TYPE = "recordType";//记录类型
    public static final String RELATE_TYPE = "relateType";//获取关联孔列表，区分
    public static final int HAVE_NOALL = 1;//没有选择框 編輯界面
    public static final int HAVE_SOME = 2;//勘察点有 關聯勘察點
    public static final int HAVE_ALL = 3;//人物有 獲取數據
    public static final int GO_LOCAL_CREATE = 401;
    public static final int GO_RELATE_CREATE = 402;
    public static final int GO_DOWNLOAD_CREATE = 403;
    public static final String CHECKLIST = "checkList";
    public static final String LOCALUSERLIST = "localUserList";
    public static final String SN = "serialNumber";
    public static final int EDIT_GO_TEMPLATE = 501;
    public static final String TEMPLATE = "template";
    public static final String HINT = "hint";

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    public void initView() {
        mPresenter = new UserPresenter();
        mPresenter.attachView(this);
        //检查版本
        mPresenter.versionCheck((String) SPUtils.get(this, Urls.SPKey.USER_ID, ""), UpdateUtil.getVerCode(MainActivity.this) + "");
        //初始化数据库
        mPresenter.initDB(this);
    }


    @Override
    public void onBackPressed() {
        finishDialog();
    }

    /**
     * 退出
     */
    private void finishDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.hint)
                .setMessage(R.string.main_finish_msg)
                .setNegativeButton(R.string.agree,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.this.finish();
                            }
                        })
                .setPositiveButton(R.string.disagree, null)
                .setCancelable(false)
                .show();
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
        if (bean.isStatus()) {
            VersionVo version = JsonUtils.getInstance().fromJson(bean.getResult().toString(), VersionVo.class);
            String type = version.getType();
            String content = "版本:" + version.getName() + "\n更新内容:" + version.getDescription() + "\n大小:" + version.getSize();
            int code = Integer.parseInt(version.getCode());
            //0-不用更新  1-可以更新 2必须更新
            if (UpdateUtil.getVerCode(this) < code) {
                if ("2".equals(type)) {
                    Common.showViesionDialog(MainActivity.this, content, true);
                } else {
                    Common.showViesionDialog(MainActivity.this, content, false);
                }
            }
        } else {
            ToastUtil.showToastS(this, bean.getMessage());
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
        fragments.add(new ProjectListFragment());
        fragments.add(new ChatFragment());
        fragments.add(new UserFragment());
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments, null);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setTitles("首页", "朋友", "用户")
                .setNormalIcons(R.mipmap.label_home_n, R.mipmap.label_friends_n, R.mipmap.label_my_n)
                .setSelectedIcons(R.mipmap.label_home_s, R.mipmap.label_friends_s, R.mipmap.label_my_s)
                .generate();
        tabLayout.setIconSize(21);
        tabLayout.setSelectedColor(getResources().getColor(R.color.colorPrimary));
        tabLayout.setNormalColor(getResources().getColor(R.color.colorTexthintGrey));
        tabLayout.setTabTextSize(9);
        tabLayout.setContainer(viewPager);
    }


}
