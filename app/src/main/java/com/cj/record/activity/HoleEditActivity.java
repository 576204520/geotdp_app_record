package com.cj.record.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cj.record.mvp.base.App;
import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.LocalUser;
import com.cj.record.baen.PageBean;
import com.cj.record.baen.Record;
import com.cj.record.mvp.base.BaseMvpActivity;
import com.cj.record.mvp.contract.HoleContract;
import com.cj.record.db.HoleDao;
import com.cj.record.db.RecordDao;
import com.cj.record.mvp.presenter.HolePresenter;
import com.cj.record.R;
import com.cj.record.baen.DropItemVo;
import com.cj.record.baen.Hole;
import com.cj.record.baen.Project;
import com.cj.record.db.ProjectDao;
import com.cj.record.fragment.HoleLocationFragment;
import com.cj.record.utils.Common;
import com.cj.record.utils.DateUtil;
import com.cj.record.utils.GPSutils;
import com.cj.record.utils.JsonUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.UpdateUtil;
import com.cj.record.views.MaterialBetterSpinner;
import com.cj.record.views.MaterialEditTextElevation;
import com.cj.record.views.MaterialEditTextNoEmoji;

import net.qiujuer.genius.ui.widget.Button;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/29.
 */

public class HoleEditActivity extends BaseMvpActivity<HolePresenter> implements HoleContract.View {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.hole_code_relate)
    MaterialEditTextNoEmoji holeCodeRelate;
    @BindView(R.id.hole_code)
    MaterialEditTextNoEmoji holeCode;
    @BindView(R.id.hole_doRelate)
    Button holeDoRelate;
    @BindView(R.id.hole_type)
    MaterialBetterSpinner holeType;
    @BindView(R.id.hole_elevation)
    MaterialEditTextElevation holeElevation;
    @BindView(R.id.hole_depth)
    MaterialEditTextElevation holeDepth;
    @BindView(R.id.hole_description)
    TextView holeDescription;
    @BindView(R.id.hole_doLocation)
    Button holeDoLocation;
    @BindView(R.id.hole_latitude)
    MaterialEditTextNoEmoji holeLatitude;
    @BindView(R.id.hole_longitude)
    MaterialEditTextNoEmoji holeLongitude;
    @BindView(R.id.hole_time)
    MaterialEditTextNoEmoji holeTime;
    @BindView(R.id.hole_radius)
    MaterialEditTextNoEmoji holeRadius;
    @BindView(R.id.hole_location_ll)
    LinearLayout holeLocationLl;
    @BindView(R.id.hole_location_fragment)
    FrameLayout holeLocationFragment;
    @BindView(R.id.hole_description_title)
    TextView holeDescriptionTitle;
    @BindView(R.id.hole_description_ll)
    LinearLayout holeDescriptionLl;
    @BindView(R.id.scene_jizhang_tv)
    TextView sceneJizhangTv;
    @BindView(R.id.scene_jizhang)
    TextView sceneJizhang;
    @BindView(R.id.scene_zuanji_tv)
    TextView sceneZuanjiTv;
    @BindView(R.id.scene_zuanji)
    TextView sceneZuanji;
    @BindView(R.id.scene_miaoshu_tv)
    TextView sceneMiaoshuTv;
    @BindView(R.id.scene_miaoshu)
    TextView sceneMiaoshu;
    @BindView(R.id.scene_changjing_tv)
    TextView sceneChangjingTv;
    @BindView(R.id.scene_changjing)
    TextView sceneChangjing;
    @BindView(R.id.scene_fuze_tv)
    TextView sceneFuzeTv;
    @BindView(R.id.scene_fuze)
    TextView sceneFuze;
    @BindView(R.id.scene_gongcheng_tv)
    TextView sceneGongchengTv;
    @BindView(R.id.scene_gongcheng)
    TextView sceneGongcheng;
    @BindView(R.id.scene_tizuan_tv)
    TextView sceneTizuanTv;
    @BindView(R.id.scene_tizuan)
    TextView sceneTizuan;
    @BindView(R.id.scene_jizhang_fl)
    FrameLayout sceneJizhangFl;
    @BindView(R.id.scene_zuanji_fl)
    FrameLayout sceneZuanjiFl;
    @BindView(R.id.scene_miaoshu_fl)
    FrameLayout sceneMiaoshuFl;
    @BindView(R.id.scene_changjing_fl)
    FrameLayout sceneChangjingFl;
    @BindView(R.id.scene_fuze_fl)
    FrameLayout sceneFuzeFl;
    @BindView(R.id.scene_gongcheng_fl)
    FrameLayout sceneGongchengFl;
    @BindView(R.id.scene_tizuan_fl)
    FrameLayout sceneTizuanFl;
    @BindView(R.id.scene_item_ll)
    LinearLayout sceneItemLl;

    HoleLocationFragment locationFragment;

    private boolean isEdit;//true编辑、false添加
    private Hole hole;
    private Project project;
    private List<DropItemVo> sprTypeList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_hole_edit;
    }

    @Override
    public void initView() {
        mPresenter = new HolePresenter();
        mPresenter.attachView(this);

        isEdit = getIntent().getBooleanExtra(MainActivity.FROMTYPE, false);
        project = (Project) getIntent().getSerializableExtra(MainActivity.PROJECT);

        toolbar.setTitle(R.string.hole_edit_title);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_clear_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        if (isEdit) {
            //true编辑
            hole = (Hole) getIntent().getSerializableExtra(MainActivity.HOLE);
        } else {
            //false添加
            hole = new Hole(this, project.getId());
            HoleDao.getInstance().add(hole);
        }

        initPage(hole);
    }


    private void initPage(Hole hole) {
        //判断是否定位
        if (TextUtils.isEmpty(hole.getMapLatitude()) || TextUtils.isEmpty(hole.getMapLongitude())) {
            holeLocationLl.setVisibility(View.GONE);
            holeDoLocation.setVisibility(View.VISIBLE);
            sceneItemLl.setVisibility(View.GONE);
        } else {
            holeLocationLl.setVisibility(View.VISIBLE);
            holeDoLocation.setVisibility(View.GONE);
//            initSenceFragment(hole.getType());
            sceneItemLl.setVisibility(View.VISIBLE);
            changeType(hole.getType());
            mPresenter.getSceneRecord(hole.getId());
        }
        //初始化地图
        initLocationFragment();
        holeCode.setText(hole.getCode());
//        holeCode.addTextChangedListener(edtCodeChangeListener);
        holeCodeRelate.setText(hole.getRelateCode());
        holeType.setText(hole.getType());
        holeElevation.setText(hole.getElevation());
        holeDepth.setText(hole.getDepth());
        holeRadius.setText(hole.getRadius());
        holeLatitude.setText(hole.getMapLatitude());
        holeLongitude.setText(hole.getMapLongitude());
        holeTime.setText(hole.getMapTime());
        if (TextUtils.isEmpty(hole.getDescription())) {
            holeDescriptionLl.setVisibility(View.GONE);
        } else {
            holeDescriptionLl.setVisibility(View.VISIBLE);
        }
        //勘探类型下拉选择
        holeType.setAdapter(this, getSprType());
        holeType.setOnItemClickListener(sprTypeListener);
    }


    private void initLocationFragment() {
        locationFragment = new HoleLocationFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.HOLE, hole);
        locationFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.hole_location_fragment, locationFragment, "type");
        ft.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hole_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.act_save:
                save();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //添加模式，为定位并且未关联，退出要删除该孔
        if (!isEdit && TextUtils.isEmpty(hole.getRelateID()) && TextUtils.isEmpty(hole.getRelateCode()) && hole.getLocationState().equals("0")) {
            HoleDao.getInstance().delete(hole);
        }
        //不管是否关联，只要定位了就判断信息是否完善
        if ("1".equals(hole.getLocationState())) {
            int complete;
            if (Hole.TYPE_TJ.equals(hole.getType())) {
                complete = RecordDao.getInstance().checkTJ(hole.getId());
                if (complete < 2) {
                    finishDialog(getString(R.string.hole_edit_finish_hint2));
                    return;
                }
            } else {
                complete = RecordDao.getInstance().checkZK(hole.getId());
                if (complete < 4) {
                    finishDialog(getString(R.string.hole_edit_finish_hint4));
                    return;
                }
            }
        }
        setResult(RESULT_OK);
        //该方法自动调用finish()
        super.onBackPressed();
    }

    /**
     * 退出提示
     */

    private void finishDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.hint)
                .setMessage(msg)
                .setNegativeButton(R.string.hole_edit_finish_keep_edit, null)
                .setPositiveButton(R.string.hole_edit_finish_out_edit,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //判断钻孔下是否已有记录，有记录保留钻孔和定位信息，没有则删除钻孔
                                List<Record> recordList = RecordDao.getInstance().getRecordListByHoleID(hole.getId());
                                if (recordList == null || recordList.size() == 0) {
                                    HoleDao.getInstance().delete(hole);
                                }
                                setResult(RESULT_OK);
                                finish();
                            }
                        })
                .show();
    }

    private void save() {
        String code = holeCode.getText().toString().trim().trim();
        if (TextUtils.isEmpty(code)) {
            Common.showMessage(HoleEditActivity.this, getString(R.string.hole_edit_save_no_code));
            return;
        }
        if (code.length() > 20) {
            Common.showMessage(HoleEditActivity.this, getString(R.string.hole_edit_save_long_code));
            return;
        }
        //判断hole完整性，钻孔（描述员、司钻员、钻机、场景）、探井（描述员、场景）
        if ("1".equals(hole.getLocationState())) {
            int complete;
            if (Hole.TYPE_TJ.equals(hole.getType())) {
                complete = RecordDao.getInstance().checkTJ(hole.getId());
                if (complete < 2) {
                    Common.showMessage(this, getString(R.string.hole_edit_save_hint2));
                    return;
                }
            } else {
                complete = RecordDao.getInstance().checkZK(hole.getId());
                if (complete < 4) {
                    Common.showMessage(this, getString(R.string.hole_edit_save_hint4));
                    return;
                }
            }
        }
        //如果是探井的类型，判断是否有司钻员、钻机的记录，如果有则删除
        if (Hole.TYPE_TJ.equals(hole.getType())) {
            List<Record> tjRecord = RecordDao.getInstance().getRecordListForJzAndZj(hole.getId());
            if (tjRecord != null && tjRecord.size() > 0) {
                for (Record record : tjRecord) {
                    record.delete();
                }
            }
        }
        hole.setCode(holeCode.getText().toString().trim().trim());
        hole.setType(holeType.getText().toString().trim());
        hole.setElevation(holeElevation.getText().toString().trim());
        hole.setDepth(holeDepth.getText().toString().trim());
        hole.setUpdateTime(DateUtil.date2Str(new Date()));
        hole.setRadius(holeRadius.getText().toString().trim());
        HoleDao.getInstance().addOrUpdate(hole);
        setResult(RESULT_OK);
        finish();
    }

    @OnClick({R.id.hole_doRelate, R.id.hole_doLocation, R.id.scene_jizhang_fl,
            R.id.scene_zuanji_fl, R.id.scene_miaoshu_fl, R.id.scene_changjing_fl,
            R.id.scene_fuze_fl, R.id.scene_gongcheng_fl, R.id.scene_tizuan_fl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.hole_doRelate:
                doRelete();
                break;
            case R.id.hole_doLocation:
                if (!Common.haveGps(this)) {
                    return;
                }
                //判断定位信息是否为空
                if (locationFragment.aMapLocation != null) {
                    //地图上添加点
                    locationFragment.location();
                    //定位信息赋值，显示
                    holeLatitude.setText(String.valueOf(locationFragment.aMapLocation.getLatitude()));
                    holeLongitude.setText(String.valueOf(locationFragment.aMapLocation.getLongitude()));
                    holeTime.setText(GPSutils.utcToTimeZoneDate(locationFragment.aMapLocation.getTime()));
                    holeRadius.setText(hole.getRadius());
                    holeLocationLl.setVisibility(View.VISIBLE);
                    holeDoLocation.setVisibility(View.GONE);
                    //停止定位
                    locationFragment.stop();
                    //保存hole
                    hole.setMapLatitude(String.valueOf(locationFragment.aMapLocation.getLatitude()));
                    hole.setMapLongitude(String.valueOf(locationFragment.aMapLocation.getLongitude()));
                    //创建时间改为定位时间
                    hole.setMapTime(GPSutils.utcToTimeZoneDate(locationFragment.aMapLocation.getTime()));
                    hole.setCreateTime(GPSutils.utcToTimeZoneDate(locationFragment.aMapLocation.getTime()));
                    hole.setLocationState("1");
                    HoleDao.getInstance().addOrUpdate(hole);
                    sceneItemLl.setVisibility(View.VISIBLE);
                    changeType(hole.getType());
                } else {
                    Common.showMessage(this, getString(R.string.hole_edit_locatin_hint));
                }
                break;
            case R.id.hole_description_title:
                if (holeDescription.getVisibility() == View.GONE) {
                    holeDescription.setVisibility(View.VISIBLE);
                } else {
                    holeDescription.setVisibility(View.GONE);
                }
                break;
            case R.id.scene_jizhang_fl:
                checkRecord(Record.TYPE_SCENE_OPERATEPERSON);
                break;
            case R.id.scene_zuanji_fl:
                checkRecord(Record.TYPE_SCENE_OPERATECODE);
                break;
            case R.id.scene_miaoshu_fl:
                checkRecord(Record.TYPE_SCENE_RECORDPERSON);
                break;
            case R.id.scene_changjing_fl:
                checkRecord(Record.TYPE_SCENE_SCENE);
                break;
            case R.id.scene_fuze_fl:
                checkRecord(Record.TYPE_SCENE_PRINCIPAL);
                break;
            case R.id.scene_gongcheng_fl:
                checkRecord(Record.TYPE_SCENE_TECHNICIAN);
                break;
            case R.id.scene_tizuan_fl:
                checkRecord(Record.TYPE_SCENE_VIDEO);
                break;
        }
    }

    private void checkRecord(String type) {
        Record record = RecordDao.getInstance().getRecordByType(hole.getId(), type);
        if (record == null) {
            goAdd(type);
        } else {
            goEdit(record);
        }
    }

    private void goAdd(String type) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(MainActivity.FROMTYPE, false);
        bundle.putSerializable(MainActivity.HOLE, hole);
        bundle.putString(MainActivity.EXTRA_RECORD_TYPE, type);
        startActivityForResult(RecordEditActivity.class, bundle, MainActivity.RECORD_GO_EDIT);
    }

    private void goEdit(Record record) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(MainActivity.FROMTYPE, true);
        bundle.putSerializable(MainActivity.HOLE, hole);
        bundle.putSerializable(MainActivity.RECORD, record);
        startActivityForResult(RecordEditActivity.class, bundle, MainActivity.RECORD_GO_EDIT);
    }

    private void doRelete() {
        if (TextUtils.isEmpty(project.getSerialNumber())) {
            Common.showMessage(this, getString(R.string.hole_list_norelate_project));
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.RELATE_TYPE, MainActivity.HAVE_NOALL);
            bundle.putString(MainActivity.SERIALNUMBER, project.getSerialNumber());
            startActivityForResult(RelateHoleActivity.class, bundle, MainActivity.GO_LOCAL_CREATE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.GO_LOCAL_CREATE && resultCode == RESULT_OK) {
            Hole relateHole = (Hole) data.getSerializableExtra(MainActivity.HOLE);
            if (relateHole != null) {
                //遍历数据库，查找是否关联
                if (HoleDao.getInstance().checkRelatedNoHole(hole.getId(), relateHole.getId(), project.getId())) {
                    Common.showMessage(this, relateHole.getCode() + getString(R.string.hole_list_relate_ishave));
                    return;
                }
                mPresenter.relate(App.userID, relateHole.getId(), hole.getId(), UpdateUtil.getVerCode(this) + "");
            }
        }
        if (requestCode == MainActivity.RECORD_GO_EDIT && resultCode == RESULT_OK) {
            hole = HoleDao.getInstance().queryById(hole.getId());
            initPage(hole);
        }
    }


    public List<DropItemVo> getSprType() {
        sprTypeList = new ArrayList<>();
        sprTypeList.add(new DropItemVo("1", Hole.TYPE_ZK));
        sprTypeList.add(new DropItemVo("2", Hole.TYPE_TJ));
        sprTypeList.add(new DropItemVo("3", Hole.TYPE_XJHZ));
        return sprTypeList;
    }

    MaterialBetterSpinner.OnItemClickListener sprTypeListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hole.setType(sprTypeList.get(i).getName());
            changeType(sprTypeList.get(i).getName());
        }
    };

    private void changeType(String type) {
        if (Hole.TYPE_TJ.equals(type)) {
            sceneJizhangFl.setVisibility(View.GONE);
            sceneZuanjiFl.setVisibility(View.GONE);
            sceneTizuanFl.setVisibility(View.GONE);
        } else {
            sceneJizhangFl.setVisibility(View.VISIBLE);
            sceneZuanjiFl.setVisibility(View.VISIBLE);
            sceneTizuanFl.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 当前code改变时验证是否重复
     */
    TextWatcher edtCodeChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String code = holeCode.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                holeCode.setError(getString(R.string.hole_edit_change_no));
            } else if (code.length() > 20) {
                holeCode.setError(getString(R.string.hole_edit_change_long));
            } else {
                //根据查询结果判断是否有一样的code
                List<Hole> list = HoleDao.getInstance().getHoleByCode(HoleEditActivity.this, code, hole.getProjectID());
                if (list != null && list.size() > 0) {
                    holeCode.setError(getString(R.string.hole_edit_change_again));
                }
            }
        }
    };

    @Override
    public void showLoading() {
        com.cj.record.views.ProgressDialog.getInstance().show(this);
    }

    @Override
    public void hideLoading() {
        com.cj.record.views.ProgressDialog.getInstance().dismiss();
    }

    @Override
    public void onError(Throwable throwable) {
        ToastUtil.showToastS(this, throwable.toString());
    }

    @Override
    public void onSuccessAddOrUpdate() {

    }

    @Override
    public void onSuccessDelete() {

    }

    @Override
    public void onSuccessList(PageBean<Hole> pageBean) {

    }

    @Override
    public void onSuccessRelate(BaseObjectBean<String> bean) {
        if (bean.isStatus()) {
            Hole h = JsonUtils.getInstance().fromJson(bean.getResult(), Hole.class);
            //修改界面,就该hole
            holeCodeRelate.setText(h.getCode());
            hole.setRelateCode(h.getCode());
            hole.setRelateID(h.getId());
            if (!TextUtils.isEmpty(h.getUploadID())) {
                hole.setUploadID(h.getUploadID());
            }
            if (!TextUtils.isEmpty(h.getDepth())) {
                holeDepth.setText(h.getDepth());
                hole.setDepth(h.getDepth());
            }
            if (!TextUtils.isEmpty(h.getElevation())) {
                holeElevation.setText(h.getElevation());
                hole.setElevation(h.getElevation());
            }
            if (!TextUtils.isEmpty(h.getDescription())) {
                holeDescriptionLl.setVisibility(View.VISIBLE);
                holeDescription.setText(h.getDescription());
                hole.setDescription(h.getDescription());
            } else {
                holeDescriptionLl.setVisibility(View.GONE);
                holeDescription.setText("");
                hole.setDescription("");
            }
            if (!TextUtils.isEmpty(h.getType())) {
                hole.setType(h.getType());
                holeType.setText(h.getType());
            }
            hole.setState("1");
            hole.setStateGW("1");
            HoleDao.getInstance().addOrUpdate(hole);
            //项目的修改时间修改
            project.setUpdateTime(DateUtil.date2Str(new Date()) + "");
            ProjectDao.getInstance().addOrUpdate(project);
            initPage(hole);
        } else {
            Common.showMessage(HoleEditActivity.this, bean.getMessage());
        }

    }

    @Override
    public void onSuccessRelateMore(BaseObjectBean<String> bean, Hole newHole) {

    }

    @Override
    public void onSuccessNoRelateList(List<Hole> noRelateList) {

    }

    @Override
    public void onSuccessDownloadHole(BaseObjectBean<String> bean, LocalUser localUser) {

    }

    @Override
    public void onSuccessRelateList(BaseObjectBean<String> bean) {

    }

    @Override
    public void onSuccessDownloadList(BaseObjectBean<String> bean) {

    }

    @Override
    public void onSuccessCheckUser(BaseObjectBean<String> bean) {

    }

    @Override
    public void onSuccessGetScene(List<Record> recordList) {
        if (recordList != null && recordList.size() > 0) {
            changeType(hole.getType());
            sceneJizhang.setText("");
            sceneJizhangTv.setTextColor(getResources().getColor(R.color.colorTexthintGrey));
            sceneZuanji.setText("");
            sceneZuanjiTv.setTextColor(getResources().getColor(R.color.colorTexthintGrey));
            sceneMiaoshu.setText("");
            sceneMiaoshuTv.setTextColor(getResources().getColor(R.color.colorTexthintGrey));
            sceneChangjing.setText("");
            sceneChangjingTv.setTextColor(getResources().getColor(R.color.colorTexthintGrey));
            sceneFuze.setText("");
            sceneFuzeTv.setTextColor(getResources().getColor(R.color.colorTexthintGrey));
            sceneGongcheng.setText("");
            sceneGongchengTv.setTextColor(getResources().getColor(R.color.colorTexthintGrey));
            sceneTizuan.setText("");
            sceneTizuanTv.setTextColor(getResources().getColor(R.color.colorTexthintGrey));
            for (Record record : recordList) {
                if (record.getType().equals(Record.TYPE_SCENE_OPERATEPERSON)) {
                    sceneJizhang.setText(record.getOperatePerson());
                    sceneJizhangTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else if (record.getType().equals(Record.TYPE_SCENE_OPERATECODE)) {
                    sceneZuanji.setText(record.getTestType());
                    sceneZuanjiTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else if (record.getType().equals(Record.TYPE_SCENE_RECORDPERSON)) {
                    sceneMiaoshu.setText(record.getRecordPerson());
                    sceneMiaoshuTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else if (record.getType().equals(Record.TYPE_SCENE_SCENE)) {
                    sceneChangjing.setText(record.getOperatePerson());
                    sceneChangjingTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else if (record.getType().equals(Record.TYPE_SCENE_PRINCIPAL)) {
                    sceneFuze.setText(record.getOperatePerson());
                    sceneFuzeTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else if (record.getType().equals(Record.TYPE_SCENE_TECHNICIAN)) {
                    sceneGongcheng.setText(record.getOperatePerson());
                    sceneGongchengTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else if (record.getType().equals(Record.TYPE_SCENE_VIDEO)) {
                    sceneTizuan.setText(record.getOperatePerson());
                    sceneTizuanTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        }
    }

    @Override
    public void onSuccessUpload(BaseObjectBean<Integer> bean) {

    }

}
