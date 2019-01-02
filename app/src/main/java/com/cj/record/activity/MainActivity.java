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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.baen.Hole;
import com.cj.record.baen.JsonResult;
import com.cj.record.baen.LocalUser;
import com.cj.record.baen.Project;
import com.cj.record.baen.Record;
import com.cj.record.baen.VersionVo;
import com.cj.record.db.HoleDao;
import com.cj.record.db.ProjectDao;
import com.cj.record.db.RecordDao;
import com.cj.record.fragment.ProjectListFragment;
import com.cj.record.fragment.TestFragment;
import com.cj.record.service.DownloadService;
import com.cj.record.utils.FileUtil;
import com.cj.record.utils.L;
import com.cj.record.utils.ObsUtils;
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
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Action;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, ObsUtils.ObsLinstener {
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
    private ObsUtils obsUtils;
    private ProjectDao projectDao;
    private HoleDao holeDao;
    private RecordDao recordDao;

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
        //子线程遍历所有数据库
        obsUtils = new ObsUtils();
        obsUtils.setObsLinstener(this);
        obsUtils.execute(1);
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

    @Override
    public void onSubscribe(int type) {
        switch (type) {
            case 1:
                projectDao = new ProjectDao(MainActivity.this);
                holeDao = new HoleDao(MainActivity.this);
                recordDao = new RecordDao(MainActivity.this);
                //遍历所有字段，检查长度
                File newFile = new File(Urls.DATABASE_BASE);
                boolean initData = (boolean) SPUtils.get(MainActivity.this, Urls.SPKey.DATA_INIT, false);
                if (!initData && newFile.exists() && !TextUtils.isEmpty(userID)) {
                    L.e("onSubscribe:遍历数据库开始");
                    List<Project> projectList = projectDao.getAll(userID);
                    if (projectList != null && projectList.size() > 0) {
                        for (Project project : projectList) {
                            //项目名称 200
                            if (project.getFullName().length() > 200) {
                                project.setFullName(project.getFullName().substring(0, 200));
                                projectDao.update(project);
                            }
                            List<Hole> holeList = holeDao.getHoleListByProjectID(project.getId());
                            if (holeList != null && holeList.size() > 0) {
                                for (Hole hole : holeList) {
                                    //勘探点编号 20
                                    if (hole.getCode().length() > 20) {
                                        hole.setCode(hole.getCode().substring(0, 20));
                                        holeDao.update(hole);
                                    }
                                    List<Record> recordList = recordDao.getRecordListByHoleID(hole.getId());
                                    if (recordList != null && recordList.size() > 0) {
                                        for (Record record : recordList) {
                                            /**
                                             记录：编号（code）20、其他描述（description）50、
                                             取土：试验类型（testType）100
                                             岩土：地质成因（causes）150、
                                             填土：主要成分（zycf）50、次要成分（cycf）50、颜色（ys）50、
                                             黏性土：包含物（bhw）50、夹层（jc）50
                                             粉土：包含物、夹层
                                             砂土：矿物组成（kwzc）50、颜色、颗粒形状（klxz）50、湿度（sd）50、夹层
                                             碎石土：母岩成分（mycf）50、夹层
                                             冲填土：物质成分（wzcf）50、颜色
                                             粉黏互层：包含物
                                             黄土状粘性土：包含物
                                             黄土状粉土：包含物
                                             淤泥：包含物、状态（zt）50
                                             */
                                            int have = 0;
                                            if (record.getCode().length() > 20) {
                                                record.setCode(record.getCode().substring(0, 20));
                                                have++;
                                            }
                                            if (record.getDescription().length() > 50) {
                                                record.setDescription(record.getDescription().substring(0, 50));
                                                have++;
                                            }
                                            if (record.getTestType().length() > 100) {
                                                record.setTestType(record.getTestType().substring(0, 100));
                                                have++;
                                            }
                                            if (record.getCauses().length() > 150) {
                                                record.setCauses(record.getCauses().substring(0, 150));
                                                have++;
                                            }
                                            if (record.getZycf().length() > 50) {
                                                record.setZycf(record.getZycf().substring(0, 50));
                                                have++;
                                            }
                                            if (record.getCycf().length() > 50) {
                                                record.setCycf(record.getCycf().substring(0, 50));
                                                have++;
                                            }
                                            if (record.getYs().length() > 50) {
                                                record.setYs(record.getYs().substring(0, 50));
                                                have++;
                                            }
                                            if (record.getBhw().length() > 50) {
                                                record.setBhw(record.getBhw().substring(0, 50));
                                                have++;
                                            }
                                            if (record.getJc().length() > 50) {
                                                record.setJc(record.getJc().substring(0, 50));
                                                have++;
                                            }
                                            if (record.getKwzc().length() > 50) {
                                                record.setKwzc(record.getKwzc().substring(0, 50));
                                                have++;
                                            }
                                            if (record.getKlxz().length() > 50) {
                                                record.setKlxz(record.getKlxz().substring(0, 50));
                                                have++;
                                            }
                                            if (record.getSd().length() > 50) {
                                                record.setSd(record.getSd().substring(0, 50));
                                                have++;
                                            }
                                            if (record.getMycf().length() > 50) {
                                                record.setMycf(record.getMycf().substring(0, 50));
                                                have++;
                                            }
                                            if (record.getWzcf().length() > 50) {
                                                record.setWzcf(record.getWzcf().substring(0, 50));
                                                have++;
                                            }
                                            if (record.getZt().length() > 50) {
                                                record.setZt(record.getZt().substring(0, 50));
                                                have++;
                                            }
                                            if (have > 0) {
                                                recordDao.update(record);
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                    SPUtils.put(MainActivity.this, Urls.SPKey.DATA_INIT, true);
                }
                boolean initData2 = (boolean) SPUtils.get(MainActivity.this, Urls.SPKey.DATA_INIT2, false);
                if (!initData2 && newFile.exists() && !TextUtils.isEmpty(userID)) {
                    L.e("onSubscribe:遍历数据库开始---第二次");
                    List<Project> projectList = projectDao.getAll(userID);
                    if (projectList != null && projectList.size() > 0) {
                        for (Project project : projectList) {
                            List<Record> recordList = recordDao.getRecordListByProjectIDAndType(project.getId(), Record.TYPE_SCENE_OPERATEPERSON);
                            if (recordList != null && recordList.size() > 0) {
                                for (Record record : recordList) {
                                    if (record.getTestType().length() > 50) {
                                        record.setTestType(record.getTestType().substring(0, 50));
                                        recordDao.update(record);
                                    }
                                }
                            }
                        }
                    }
                    SPUtils.put(MainActivity.this, Urls.SPKey.DATA_INIT2, true);
                }
                break;
        }
    }

    @Override
    public void onComplete(int type) {
        switch (type) {
            case 1:
                L.e("onComplete:遍历数据库结束");
                break;
        }
    }
}
