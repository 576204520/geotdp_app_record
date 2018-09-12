package com.cj.record.activity;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.baen.JsonResult;
import com.cj.record.baen.LocalUser;
import com.cj.record.baen.VersionVo;
import com.cj.record.fragment.ProjectListFragment;
import com.cj.record.fragment.TestFragment;
import com.cj.record.service.DownloadService;
import com.cj.record.utils.FileUtil;
import com.cj.record.utils.L;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.UpdateUtil;
import com.cj.record.utils.Urls;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import junit.runner.Version;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import io.reactivex.functions.Action;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private TextView name;
    private TextView email;
    private Fragment mFragment;
    private ProjectListFragment projectListFragment;
    private TestFragment testFragment;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    public static final String FROMTYPE = "fromtype";//区分编辑还是添加
    public static final int PROJECT_GO_EDIT = 101;
    public static final int PROJECT_GO_LIST = 102;
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

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        super.initData();
        //初始化数据库
        initDB();
        //初始化project布局
        initProject();
        //检查版本
        UpdateUtil.checkVersion(MainActivity.this, false);
    }

    @Override
    public void initView() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navView.setNavigationItemSelectedListener(this);
        //获取navigation头部的布局
        View headerView = navView.getHeaderView(0);
        name = (TextView) headerView.findViewById(R.id.header_name);
        email = (TextView) headerView.findViewById(R.id.header_email);
        //从sp文件拿到用户信息，在登录成功时保存的
        name.setText((String) SPUtils.get(mContext, Urls.SPKey.USER_REALNAME, ""));
        email.setText((String) SPUtils.get(mContext, Urls.SPKey.USER_EMAIL, ""));

    }

    private void initProject() {
        //初始化加载项目
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        projectListFragment = new ProjectListFragment();
        ft.add(R.id.main_frame, projectListFragment);
        ft.commit();
        //记录当前的fragment
        mFragment = projectListFragment;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finishDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.act_add:
                Bundle bundle = new Bundle();
                bundle.putBoolean(FROMTYPE, false);
                startActivityForResult(ProjectEditActiity.class, bundle, PROJECT_GO_EDIT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        L.e("onActivityResult:requestCode=" + requestCode + "--resultCode=" + resultCode);
        if (resultCode == RESULT_OK) {
            if (projectListFragment != null) {
                projectListFragment.onRefresh();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.nav_project:
                if (projectListFragment == null) {
                    projectListFragment = new ProjectListFragment();
                }
                switchContent(mFragment, projectListFragment);
                break;
            case R.id.nav_setting:
                startActivity(SettingActivity.class);
                break;
            case R.id.nav_help:
                Intent intent = new Intent(MainActivity.this, HelpActivtiy.class);
                intent.setAction("MainActivity");
                startActivity(intent);
                break;
            case R.id.nav_logout:
                logoutDialog();
                break;
            case R.id.nav_quit:
                quitDialog();
                break;
            case R.id.nav_test:
                if (testFragment == null) {
                    testFragment = new TestFragment();
                }
                switchContent(mFragment, testFragment);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.hint)
                .setMessage("注销后将清空当前用户的登录信息（但不会清除该用户的项目信息），返回到登录页面重新登陆。确定注销吗？")
                .setNegativeButton(R.string.agree,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SPUtils.put(mContext, Urls.SPKey.USER_ID, "");
                                SPUtils.put(mContext, Urls.SPKey.USER_EMAIL, "");
                                SPUtils.put(mContext, Urls.SPKey.USER_REALNAME, "");
                                SPUtils.put(mContext, Urls.SPKey.USER_PWD, "");
                                SPUtils.put(mContext, Urls.SPKey.USER_AUTO, false);

                                finish();
                                startActivity(LoginActivity.class);
                            }
                        })
                .setPositiveButton(R.string.disagree, null)
                .setCancelable(false)
                .show();
    }

    private void quitDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.hint)
                .setMessage("将关闭APP")
                .setNegativeButton(R.string.agree,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                .setPositiveButton(R.string.disagree, null)
                .setCancelable(false)
                .show();
    }

    public void switchContent(Fragment from, Fragment to) {
        if (mFragment != to) {
            mFragment = to;
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                ft.hide(from).add(R.id.main_frame, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                ft.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }

        }
    }


    /**
     * 检查数据库文件是否存在
     */
    public void initDB() {
        File newFile = new File(Urls.DATABASE_BASE);
        if (!newFile.exists()) {
            try {
                //在指定的文件夹中创建文件
                FileUtil.CreateText(Urls.DATABASE_PATH);
                //查看老版数据库是否存在
                File oldFile = new File(Urls.DATABASE_BASE_OLD);
                if (!oldFile.exists()) {
                    copyDB(Urls.DATABASE_BASE);
                } else {
                    copyDBForOld(Urls.DATABASE_BASE_OLD, Urls.DATABASE_BASE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 从资源文件里复制
     */
    public void copyDB(String path) {
        try {
            InputStream is = getResources().openRawResource(R.raw.gcdz);
            FileOutputStream fos = new FileOutputStream(path);
            byte[] buffer = new byte[8192];
            int count;
            while ((count = is.read(buffer)) >= 0) {
                fos.write(buffer, 0, count);
            }
            fos.close();
            is.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * 从老地址中复制
     */
    public void copyDBForOld(String oldPath, String path) {
        try {
            FileInputStream fis = new FileInputStream(oldPath);
            FileOutputStream fos = new FileOutputStream(path);
            byte[] buffer = new byte[8192];
            int count;
            while ((count = fis.read(buffer)) >= 0) {
                fos.write(buffer, 0, count);
            }
            fis.close();
            fos.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * 退出
     */
    private void finishDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.hint)
                .setMessage("是否退出应用")
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
}
