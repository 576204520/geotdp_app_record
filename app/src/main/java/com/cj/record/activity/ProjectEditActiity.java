package com.cj.record.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.baen.Hole;
import com.cj.record.baen.JsonResult;
import com.cj.record.baen.Project;
import com.cj.record.db.HoleDao;
import com.cj.record.db.MediaDao;
import com.cj.record.db.ProjectDao;
import com.cj.record.db.RecordDao;
import com.cj.record.utils.Common;
import com.cj.record.utils.DateUtil;
import com.cj.record.utils.JsonUtils;
import com.cj.record.utils.ObsUtils;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.UpdateUtil;
import com.cj.record.utils.Urls;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static me.iwf.photopicker.PhotoPreview.REQUEST_CODE;

/**
 * Created by Administrator on 2018/5/24.
 */

public class ProjectEditActiity extends BaseActivity implements ObsUtils.ObsLinstener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.projec_edit_number)
    EditText projecEditNumber;
    @BindView(R.id.projec_edit_relevance)
    TextView projecEditRelevance;
    @BindView(R.id.projec_edit_name)
    EditText projecEditName;
    @BindView(R.id.projec_edit_code)
    EditText projecEditCode;
    @BindView(R.id.projec_edit_leader)
    EditText projecEditLeader;
    @BindView(R.id.projec_edit_company)
    EditText projecEditCompany;
    @BindView(R.id.projec_edit_owner)
    EditText projecEditOwner;
    @BindView(R.id.projec_edit_address)
    EditText projecEditAddress;
    @BindView(R.id.projec_edit_describe)
    EditText projecEditDescribe;
    @BindView(R.id.projec_edit_laborUnit)
    EditText projecEditLaborUnit;

    private boolean isEdit;//true编辑、false添加
    private ProjectDao projectDao;
    private HoleDao holeDao;
    private RecordDao recordDao;
    private MediaDao mediaDao;
    private Project project;
    private ObsUtils obsUtils;
    private String oldNumber;

    @Override
    public int getLayoutId() {
        return R.layout.activity_project_edit;
    }

    @Override
    public void initData() {
        super.initData();
        obsUtils = new ObsUtils();
        obsUtils.setObsLinstener(this);
        obsUtils.execute(1);
    }

    @Override
    public void initView() {
        toolbar.setTitle("项目详情与编辑");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_clear_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }


    //有数据的情况，初始化页面
    private void initPage(Project project) {
        projecEditNumber.setText(project.getSerialNumber());
        projecEditName.setText(project.getFullName());
        projecEditCode.setText(project.getCode());
        projecEditLeader.setText(project.getLeaderName());
        projecEditCompany.setText(project.getCompanyName());
        projecEditOwner.setText(project.getOwner());
        projecEditAddress.setText(project.getAddress());
        projecEditDescribe.setText(project.getDescribe());
        projecEditLaborUnit.setText(project.getLaborUnit());

    }

    @OnClick({R.id.projec_edit_relevance, R.id.project_zxing})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.projec_edit_relevance:
                doRelevance();
                break;
            case R.id.project_zxing:
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
    }

    /**
     * 关联获取项目信息
     */
    public void doRelevance() {
        //检查网络
        if (!haveNet()) {
            return;
        }
        String number = projecEditNumber.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            Common.showMessage(this, "请输入项目序列号");
            return;
        }
        if (TextUtils.isEmpty(userID)) {
            Common.showMessage(this, "用户信息丢失，请尝试重新登陆");
            return;
        }
        boolean isHave = projectDao.checkNumber(userID, number, project.getId());
        if (isHave) {
            Common.showMessage(this, "该序列号本地已经存在");
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("project.serialNumber", number);
        map.put("userID", userID);
        map.put("verCode", UpdateUtil.getVerCode(this) + "");
        showPPW();
        OkGo.<String>post(Urls.GET_PROJECT_INFO_BY_KEY_POST)
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
                                String result = jsonResult.getResult();
                                Project mProject = gson.fromJson(result.toString(), Project.class);
                                project.setSerialNumber(mProject.getSerialNumber());
                                project.setFullName(Common.replaceAll(mProject.getFullName()));
                                project.setCode(mProject.getCode());
                                project.setLeaderName(mProject.getRealName());
                                project.setCompanyName(mProject.getCompanyName());
                                project.setOwner(mProject.getOwner());
                                project.setAddress(mProject.getProName() + mProject.getCityName() + "" + mProject.getDisName() + mProject.getAddress());
                                project.setDescribe(mProject.getDescribe());
                                project.setProjectID(mProject.getProjectID());
                                project.setUpload(mProject.isUpload());
                                project.setCompanyID(mProject.getCompanyID());
                                project.setLaborUnit(mProject.getLaborUnit());
                                obsUtils.execute(2);
                            }
                            Common.showMessage(ProjectEditActiity.this, jsonResult.getMessage());
                        } else {
                            Common.showMessage(ProjectEditActiity.this, "关联项目，服务器异常，请联系客服");
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
                        Common.showMessage(ProjectEditActiity.this, "关联项目，网络连接错误");
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project_edit, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.act_save:
                if (TextUtils.isEmpty(projecEditName.getText().toString())) {
                    Common.showMessage(ProjectEditActiity.this, "请输入项目名称");
                    return true;
                }
                project.setFullName(projecEditName.getText().toString());
                projectDao.update(project);
                setResult(RESULT_OK);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!isEdit && TextUtils.isEmpty(project.getSerialNumber())) {
            projectDao.delete(project);
        }
        setResult(RESULT_OK);
        //该方法自动调用finish()
        super.onBackPressed();
    }

    @Override
    public void onSubscribe(int type) {
        switch (type) {
            case 1:
                isEdit = getIntent().getBooleanExtra(MainActivity.FROMTYPE, false);
                projectDao = new ProjectDao(mContext);
                holeDao = new HoleDao(this);
                recordDao = new RecordDao(this);
                mediaDao = new MediaDao(this);
                if (isEdit) {
                    //true编辑
                    project = (Project) getIntent().getSerializableExtra(MainActivity.PROJECT);
                    oldNumber = project.getSerialNumber();
                } else {
                    //false添加
                    project = new Project(mContext);
                    projectDao.add(project);
                }

                break;
            case 2:
                if (isEdit) {
                    if (!TextUtils.isEmpty(oldNumber) && !oldNumber.equals(project.getSerialNumber())) {
                        project.setState("1");
                        holeDao.updateState(project.getId());
                        recordDao.updateState(project.getId());
                        mediaDao.updateState(project.getId());
                    }
                    projectDao.update(project);
                } else {
                    projectDao.add(project);
                }
                break;
        }
    }

    @Override
    public void onComplete(int type) {
        switch (type) {
            case 1:
                initPage(project);
                break;
            case 2:
                initPage(project);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    projecEditNumber.setText(result);
                    doRelevance();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
