package com.cj.record.activity;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.cj.record.baen.JsonResult;
import com.cj.record.baen.Record;
import com.cj.record.db.RecordDao;
import com.cj.record.utils.L;
import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.baen.DropItemVo;
import com.cj.record.baen.Hole;
import com.cj.record.baen.Project;
import com.cj.record.db.HoleDao;
import com.cj.record.db.ProjectDao;
import com.cj.record.fragment.HoleLocationFragment;
import com.cj.record.fragment.HoleSceneFragment;
import com.cj.record.utils.Common;
import com.cj.record.utils.DateUtil;
import com.cj.record.utils.GPSutils;
import com.cj.record.utils.JsonUtils;
import com.cj.record.utils.ObsUtils;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.UpdateUtil;
import com.cj.record.utils.Urls;
import com.cj.record.views.MaterialBetterSpinner;
import com.cj.record.views.MaterialEditTextElevation;
import com.cj.record.views.MaterialEditTextNoEmoji;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import net.qiujuer.genius.ui.widget.Button;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/29.
 */

public class HoleEditActivity extends BaseActivity implements ObsUtils.ObsLinstener {
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
    //    @BindView(R.id.hole_scene_fragment)
//    FrameLayout holeSceneFragment;
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
    HoleSceneFragment sceneFragment;

    private boolean isEdit;//true编辑、false添加
    private Hole hole;
    private HoleDao holeDao;
    private RecordDao recordDao;
    private Project project;
    private ProjectDao projectDao;
    private List<DropItemVo> sprTypeList;
    private ObsUtils obsUtils;
    private List<Record> recordList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_hole_edit;
    }

    @Override
    public void initData() {
        super.initData();
        isEdit = getIntent().getBooleanExtra(MainActivity.FROMTYPE, false);
        project = (Project) getIntent().getSerializableExtra(MainActivity.PROJECT);
        holeDao = new HoleDao(this);
        recordDao = new RecordDao(this);
        projectDao = new ProjectDao(this);
        obsUtils = new ObsUtils();
        obsUtils.setObsLinstener(this);
        obsUtils.execute(1);
    }

    @Override
    public void onSubscribe(int type) {
        switch (type) {
            case 1:
                if (isEdit) {
                    //true编辑
                    hole = (Hole) getIntent().getSerializableExtra(MainActivity.HOLE);
                } else {
                    //false添加
                    hole = new Hole(mContext, project.getId());
                    holeDao.add(hole);
                }
                break;
            case 2:
                recordList = recordDao.getSceneRecord(hole.getId());
                break;

        }
    }

    @Override
    public void onComplete(int type) {
        switch (type) {
            case 1:
                initPage(hole);
                break;
            case 2:
                if (recordList != null && recordList.size() > 0) {
                    initScene();
                }
                break;
        }
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
            obsUtils.execute(2);
        }
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

//    private void initSenceFragment(String holeType) {
//        sceneFragment = new HoleSceneFragment();
//        Bundle bundle = new Bundle();
//        //向detailFragment传入参数hole
//        bundle.putSerializable(MainActivity.HOLE, hole);
//        //传入参数区分是钻孔、探井
//        bundle.putString(MainActivity.EXTRA_HOLE_TYPE, holeType);
//        sceneFragment.setArguments(bundle);
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.replace(R.id.hole_scene_fragment, sceneFragment, "type");
//        ft.commit();
//    }

    private void initScene() {
        changeType(hole.getType());
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

    private void initLocationFragment() {
        locationFragment = new HoleLocationFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.HOLE, hole);
        locationFragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.hole_location_fragment, locationFragment, "type");
        ft.commit();
    }


    @Override
    public void initView() {
        toolbar.setTitle("编辑勘探点");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_clear_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
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
            holeDao.delete(hole);
        }
        //不管是否关联，只要定位了就判断信息是否完善
        if ("1".equals(hole.getLocationState())) {
            int complete;
            if ("探井".equals(hole.getType())) {
                complete = recordDao.checkTJ(hole.getId());
                if (complete < 2) {
                    finishDialog("勘探点数据不完整，请完善（描述员、场景）记录，否则不保留定位信息");
                    return;
                }
            } else {
                complete = recordDao.checkZK(hole.getId());
                if (complete < 4) {
                    finishDialog("勘探点数据不完整，请完善（司钻员、钻机、描述员、场景）记录,否则不保留定位信息");
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
                .setNegativeButton("继续编辑", null)
                .setPositiveButton("取消定位并退出",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                hole.setLocationState("0");
                                hole.setMapLatitude("");
                                hole.setMapLongitude("");
                                holeDao.update(hole);
                                //清除定位信息，删除所有记录
                                List<Record> recordList = recordDao.getRecordListByHoleID(hole.getId());
                                if (recordList != null && recordList.size() > 0) {
                                    for (Record record : recordList) {
                                        record.delete(HoleEditActivity.this);
                                    }
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
            Common.showMessage(HoleEditActivity.this, "请输入勘探点编号");
            return;
        }
        if (code.length() > 20) {
            Common.showMessage(HoleEditActivity.this, "勘探点编号长度不能超过20");
            return;
        }
        //判断hole完整性，钻孔（描述员、司钻员、钻机、场景）、探井（描述员、场景）
        if ("1".equals(hole.getLocationState())) {
            int complete;
            if ("探井".equals(hole.getType())) {
                complete = recordDao.checkTJ(hole.getId());
                if (complete < 2) {
                    Common.showMessage(this, "勘探点数据不完整，请完善（描述员、场景）记录");
                    return;
                }
            } else {
                complete = recordDao.checkZK(hole.getId());
                if (complete < 4) {
                    Common.showMessage(this, "勘探点数据不完整，请完善（司钻员、钻机、描述员、场景）记录");
                    return;
                }
            }
        }
        //如果是探井的类型，判断是否有司钻员、钻机的记录，如果有则删除
        if ("探井".equals(hole.getType())) {
            List<Record> tjRecord = recordDao.getRecordListForJzAndZj(hole.getId());
            if (tjRecord != null && tjRecord.size() > 0) {
                for (Record record : tjRecord) {
                    record.delete(HoleEditActivity.this);
                }
            }
        }
        hole.setCode(holeCode.getText().toString().trim().trim());
        hole.setType(holeType.getText().toString().trim());
        hole.setElevation(holeElevation.getText().toString().trim());
        hole.setDepth(holeDepth.getText().toString().trim());
        hole.setUpdateTime(DateUtil.date2Str(new Date()));
        hole.setRadius(holeRadius.getText().toString().trim());
        holeDao.add(hole);
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
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    holeLatitude.setText(String.valueOf(locationFragment.aMapLocation.getLatitude()));
                    holeLongitude.setText(String.valueOf(locationFragment.aMapLocation.getLongitude()));
                    holeTime.setText(String.valueOf(df.format(new Date(locationFragment.aMapLocation.getTime()))));
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
                    holeDao.add(hole);
                    //加载scene布局
//                    initSenceFragment(hole.getType());
//                    holeSceneFragment.setVisibility(View.VISIBLE);
                    sceneItemLl.setVisibility(View.VISIBLE);
                    changeType(hole.getType());
                } else {
                    Common.showMessage(this, "未能获取定位信息，请稍等");
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
        Record record = recordDao.getRecordByType(hole.getId(), type);
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
            Common.showMessage(this, "所在项目未关联");
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
            if (data != null) {
                Hole relateHole = (Hole) data.getExtras().getSerializable(MainActivity.HOLE);
                relate(relateHole);
            }
        }
        if (requestCode == MainActivity.RECORD_GO_EDIT && resultCode == RESULT_OK) {
            hole = holeDao.queryForId(hole.getId());
            initPage(hole);
        }
    }

    private void relate(final Hole relateHole) {
        if (TextUtils.isEmpty(userID)) {
            Common.showMessage(this, "用户信息丢失，请尝试重新登陆");
            return;
        }
        //遍历数据库，查找是否关联
        if (holeDao.checkRelatedNoHole(hole.getId(), relateHole.getId(), hole.getProjectID())) {
            Common.showMessage(this, "该发布点本地已经存在关联");
            return;
        }
        showPPW();
        Map<String, String> map = new HashMap<>();
        map.put("userID", userID);
        map.put("relateID", relateHole.getId());
        map.put("holeID", hole.getId());
        map.put("verCode", UpdateUtil.getVerCode(this) + "");
        OkGo.<String>post(Urls.DO_RELATE_HOLE)
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
                                Hole h = gson.fromJson(result.toString().trim(), Hole.class);
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
                                holeDao.add(hole);
                                project.setUpdateTime(DateUtil.date2Str(new Date()) + "");
                                projectDao.update(project);
                            }
                            Common.showMessage(HoleEditActivity.this, jsonResult.getMessage());
                        } else {
                            Common.showMessage(HoleEditActivity.this, "关联勘探点，服务器异常，请联系客服");
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
                        Common.showMessage(HoleEditActivity.this, "关联勘探点，网络连接错误");
                    }
                });
    }

    public List<DropItemVo> getSprType() {
        sprTypeList = new ArrayList<>();
        sprTypeList.add(new DropItemVo("1", "钻孔"));
        sprTypeList.add(new DropItemVo("2", "探井"));
        sprTypeList.add(new DropItemVo("3", "先井后钻"));
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
        if ("探井".equals(type)) {
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
                holeCode.setError("请输入勘察点编号");
            } else if (code.length() > 20) {
                holeCode.setError("勘探点编号长度不能超过20");
            } else {
                //根据查询结果判断是否有一样的code
                List<Hole> list = holeDao.getHoleByCode(HoleEditActivity.this, code, hole.getProjectID());
                if (list != null && list.size() > 0) {
                    holeCode.setError("勘察点编号重复");
                }
            }
        }
    };
}
