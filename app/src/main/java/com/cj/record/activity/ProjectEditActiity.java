package com.cj.record.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cj.record.R;
import com.cj.record.base.App;
import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.PageBean;
import com.cj.record.baen.Project;
import com.cj.record.base.BaseMvpActivity;
import com.cj.record.contract.ProjectContract;
import com.cj.record.db.ProjectDao;
import com.cj.record.presenter.ProjectPresenter;
import com.cj.record.utils.Common;
import com.cj.record.utils.JsonUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.UpdateUtil;
import com.cj.record.views.ProgressDialog;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;
import butterknife.OnClick;

import static me.iwf.photopicker.PhotoPreview.REQUEST_CODE;

/**
 * Created by Administrator on 2018/5/24.
 */

public class ProjectEditActiity extends BaseMvpActivity<ProjectPresenter> implements ProjectContract.View {
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
    private Project project;
    private String oldNumber;

    @Override
    public int getLayoutId() {
        return R.layout.activity_project_edit;
    }

    @Override
    public void initView() {
        mPresenter = new ProjectPresenter();
        mPresenter.attachView(this);
        toolbar.setTitle(R.string.project_edit_title);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_clear_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        isEdit = getIntent().getBooleanExtra(MainActivity.FROMTYPE, false);
        if (isEdit) {
            //true编辑
            project = (Project) getIntent().getSerializableExtra(MainActivity.PROJECT);
            oldNumber = project.getSerialNumber();
        } else {
            //false添加
            project = new Project(this);
            ProjectDao.getInstance().add(project);
        }

        initPage(project);
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
        String number = projecEditNumber.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            Common.showMessage(this, getString(R.string.project_edit_hint_num));
            return;
        }
        if (TextUtils.isEmpty(App.userID)) {
            Common.showMessage(this, getString(R.string.project_edit_hint_user));
            return;
        }
        boolean isHave = ProjectDao.getInstance().checkNumber(App.userID, number, project.getId());
        if (isHave) {
            Common.showMessage(this, getString(R.string.project_edit_hint_num_have));
            return;
        }

        mPresenter.relate(number, App.userID, UpdateUtil.getVerCode(this) + "");
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
                    Common.showMessage(ProjectEditActiity.this, getString(R.string.project_edit_hint_name));
                    return true;
                }
                project.setFullName(projecEditName.getText().toString());
                ProjectDao.getInstance().addOrUpdate(project);
                setResult(RESULT_OK);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!isEdit && TextUtils.isEmpty(project.getSerialNumber())) {
            ProjectDao.getInstance().delete(project);
        }
        setResult(RESULT_OK);
        //该方法自动调用finish()
        super.onBackPressed();
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
                    Toast.makeText(this, R.string.project_edit_hint_zxing, Toast.LENGTH_LONG).show();
                }
            }
        }
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
    public void onSuccessAddOrUpdate() {
        initPage(project);
    }

    @Override
    public void onSuccessDelete() {

    }

    @Override
    public void onSuccessList(PageBean<Project> pageBean) {

    }

    @Override
    public void onSuccessRelate(BaseObjectBean<String> bean) {
        ToastUtil.showToastS(this, bean.getMessage());
        if (bean.isStatus()) {
            Project mProject = JsonUtils.getInstance().fromJson(bean.getResult(), Project.class);
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
        }
        if (!TextUtils.isEmpty(oldNumber) && !oldNumber.equals(project.getSerialNumber())) {
            mPresenter.addOrUpdate(project, true);
        } else {
            mPresenter.addOrUpdate(project, false);
        }

    }
}
