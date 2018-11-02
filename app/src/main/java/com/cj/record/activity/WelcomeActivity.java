package com.cj.record.activity;

import android.Manifest;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.baen.Hole;
import com.cj.record.baen.Project;
import com.cj.record.baen.Record;
import com.cj.record.db.HoleDao;
import com.cj.record.db.ProjectDao;
import com.cj.record.db.RecordDao;
import com.cj.record.utils.L;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.UpdateUtil;
import com.cj.record.utils.Urls;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import butterknife.BindView;
import butterknife.internal.Utils;
import io.reactivex.functions.Action;

/**
 * Created by Administrator on 2018/5/23.
 */

public class WelcomeActivity extends BaseActivity {
    private ProjectDao projectDao;
    private HoleDao holeDao;
    private RecordDao recordDao;
    @BindView(R.id.welcome_hint)
    TextView welcomeHint;

    @Override
    public int getLayoutId() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        return R.layout.activity_welcome;
    }

    @Override
    public void initData() {
        super.initData();
        new MyCount(2000, 1000).start();
    }

    @Override
    public void initView() {

    }

    /**
     * 定义一个倒计时的内部类
     */
    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            requestPermissions();
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }
    }

    private void requestPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)
                .subscribe(new io.reactivex.functions.Consumer<Permission>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Permission permission) throws Exception {
                        switch (permission.name) {
                            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                                if (permission.granted) {
                                    //遍历所有字段，检查长度
                                    projectDao = new ProjectDao(WelcomeActivity.this);
                                    holeDao = new HoleDao(WelcomeActivity.this);
                                    recordDao = new RecordDao(WelcomeActivity.this);
                                    boolean initData = (boolean) SPUtils.get(WelcomeActivity.this, Urls.SPKey.DATA_INIT, false);
                                    if (!initData) {
                                        welcomeHint.setVisibility(View.VISIBLE);
                                        List<Project> projectList = projectDao.getAll();
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
                                        SPUtils.put(WelcomeActivity.this, Urls.SPKey.DATA_INIT, true);
                                    }
                                    //检查是否是自动登陆
                                    boolean isAuto = (boolean) SPUtils.get(WelcomeActivity.this, Urls.SPKey.USER_AUTO, false);
                                    if (isAuto) {
                                        // 检查userid是否存在
                                        String userID = (String) SPUtils.get(WelcomeActivity.this, Urls.SPKey.USER_ID, "");
                                        BaseActivity.userID = userID;
                                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                    } else {
                                        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                                    }
                                    finish();
                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    ToastUtil.showToastS(mContext, "取消存储授权,不能存储图片文件");
                                } else {
                                    ToastUtil.showToastS(mContext, "您已经禁止弹出存储的授权操作,请在设置中手动开启");
                                }
                                break;
                            case Manifest.permission.CAMERA:
                                if (permission.granted) {
                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    ToastUtil.showToastS(mContext, "取消照相机授权");
                                } else {
                                    ToastUtil.showToastS(mContext, "您已经禁止弹出照相机的授权操作,请在设置中手动开启");
                                }
                                break;
                            case Manifest.permission.ACCESS_FINE_LOCATION:
                                if (permission.granted) {
                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    ToastUtil.showToastS(mContext, "取消定位授权,不能获取定位信息");
                                } else {
                                    ToastUtil.showToastS(mContext, "您已经禁止弹出定位的授权操作,请在设置中手动开启");
                                }
                            default:
                                break;
                        }

                    }
                }, new io.reactivex.functions.Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        Log.i("--->>", "onError", throwable);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });
    }
}
