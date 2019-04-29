package com.cj.record.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.cj.record.R;
import com.cj.record.base.BaseActivity;
import com.cj.record.baen.Gps;
import com.cj.record.baen.Hole;
import com.cj.record.baen.Media;
import com.cj.record.baen.Record;
import com.cj.record.baen.Template;
import com.cj.record.db.GpsDao;
import com.cj.record.db.HoleDao;
import com.cj.record.db.MediaDao;
import com.cj.record.db.RecordDao;
import com.cj.record.fragment.RecordLocationFragment;
import com.cj.record.fragment.RecordMediaFragment;
import com.cj.record.fragment.record.RecordBaseFragment;
import com.cj.record.fragment.record.RecordEditDPTFragment;
import com.cj.record.fragment.record.RecordEditFrequencyFragment;
import com.cj.record.fragment.record.RecordEditGetEarthFragment;
import com.cj.record.fragment.record.RecordEditGetWaterFragment;
import com.cj.record.fragment.record.RecordEditLayerFragment;
import com.cj.record.fragment.record.RecordEditRemarkFragment;
import com.cj.record.fragment.record.RecordEditSPTFragment;
import com.cj.record.fragment.record.RecordEditWaterFragment;
import com.cj.record.fragment.record.inhole.RecordFZRFragment;
import com.cj.record.fragment.record.inhole.RecordJZFragment;
import com.cj.record.fragment.record.inhole.RecordZJFragment;
import com.cj.record.fragment.record.inhole.RecordMSYFragment;
import com.cj.record.fragment.record.inhole.RecordCJFragment;
import com.cj.record.fragment.record.inhole.RecordGCSFragment;
import com.cj.record.fragment.record.inhole.RecordVideoFragment;
import com.cj.record.utils.Common;
import com.cj.record.utils.DateUtil;
import com.cj.record.utils.GPSutils;
import com.cj.record.utils.ObsUtils;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.Urls;
import com.cj.record.views.MaterialEditTextElevation;
import com.cj.record.views.MaterialEditTextNoEmoji;

import net.qiujuer.genius.ui.widget.Button;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/5.
 */

public class RecordEditActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.record_beginDepth)
    MaterialEditTextElevation recordBeginDepth;
    @BindView(R.id.record_endDepth)
    MaterialEditTextElevation recordEndDepth;
    @BindView(R.id.record_content_fl)
    FrameLayout recordContentFl;
    @BindView(R.id.record_code)
    MaterialEditTextNoEmoji recordCode;
    @BindView(R.id.record_description)
    MaterialEditTextNoEmoji recordDescription;
    @BindView(R.id.record_dptup_btn)
    Button recordDptupBtn;
    @BindView(R.id.record_edit_note_tv)
    TextView recordEditNoteTv;

    private RecordBaseFragment recordBaseFragment;
    private RecordLocationFragment locationFragment;
    private RecordMediaFragment mediaFragment;
    private Hole hole;
    private boolean isEdit;
    private Record record;
    private String recordType;
    private Record recordOld;
    private Gps gpsOld;
    private AMapLocation amapLocation;

    @Override
    public int getLayoutId() {
        return R.layout.activity_record_edit;
    }


    @Override
    public void initView() {
        isEdit = getIntent().getBooleanExtra(MainActivity.FROMTYPE, false);
        hole = (Hole) getIntent().getSerializableExtra(MainActivity.HOLE);
        if (isEdit) {
            //true编辑
            record = (Record) getIntent().getSerializableExtra(MainActivity.RECORD);
            recordType = record.getType();
            // 获取的记录就改成历史记录，关联的gps也关联到历史记录,历史记录要修改状态，重新上传
            recordOld = (Record) record.clone();
            recordOld.setId(Common.getUUID());
            recordOld.setUpdateId(record.getId());
            recordOld.setState("1");//到这里，都是说明将要保存这条记录，并打算上传，状态设置为1
            gpsOld = GpsDao.getInstance().getGpsByRecord(record.getId());
            if (gpsOld != null) {
                gpsOld.setRecordID(recordOld.getId());
            }
        } else {
            //false添加
            recordType = getIntent().getStringExtra(MainActivity.EXTRA_RECORD_TYPE);
            record = new Record(RecordEditActivity.this, hole, recordType);
            RecordDao.getInstance().addOrUpdate(record);
        }
        initPage(record);
    }

    private void initPage(Record record) {
        toolbar.setTitle("编辑" + recordType);
        if (recordType.equals(Record.TYPE_SCENE_OPERATEPERSON)) {
            toolbar.setTitle("编辑司钻员");
        }
        if (recordType.equals(Record.TYPE_SCENE_PRINCIPAL)) {
            toolbar.setTitle("编辑项目负责人");
        }
        if (recordType.equals(Record.TYPE_SCENE_TECHNICIAN)) {
            toolbar.setTitle("编辑项目工程师");
        }
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_clear_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        //加载定位测模块
        initLocationFragment(hole);
        //加载媒体的模块
        initMediaFragment(record);
        if (null != record.getCode()) {
            recordCode.setText(record.getCode());
        }
        recordBeginDepth.setText(record.getBeginDepth());
        recordEndDepth.setText(record.getEndDepth());
        recordDescription.setText(record.getDescription());
        if (recordType.equals(Record.TYPE_FREQUENCY)) {
            setRecordEditBaseFragment(new RecordEditFrequencyFragment());
        } else if (recordType.equals(Record.TYPE_LAYER)) {
            setRecordEditBaseFragment(new RecordEditLayerFragment());
        } else if (recordType.equals(Record.TYPE_GET_EARTH)) {
            setRecordEditBaseFragment(new RecordEditGetEarthFragment());
        } else if (recordType.equals(Record.TYPE_GET_WATER)) {
            setRecordEditBaseFragment(new RecordEditGetWaterFragment());
        } else if (recordType.equals(Record.TYPE_DPT)) {
            recordDptupBtn.setVisibility(View.VISIBLE);
            setRecordEditBaseFragment(new RecordEditDPTFragment());
        } else if (recordType.equals(Record.TYPE_SPT)) {
            setRecordEditBaseFragment(new RecordEditSPTFragment());
        } else if (recordType.equals(Record.TYPE_WATER)) {
            setRecordEditBaseFragment(new RecordEditWaterFragment());
        } else if (recordType.equals(Record.TYPE_SCENE)) {
            setRecordEditBaseFragment(new RecordEditRemarkFragment());
        } else if (recordType.equals(Record.TYPE_SCENE_OPERATEPERSON)) {//机长
            setRecordEditBaseFragment(new RecordJZFragment());
        } else if (recordType.equals(Record.TYPE_SCENE_OPERATECODE)) {//编号
            setRecordEditBaseFragment(new RecordZJFragment());
        } else if (recordType.equals(Record.TYPE_SCENE_RECORDPERSON)) {//描述员
            setRecordEditBaseFragment(new RecordMSYFragment());
        } else if (recordType.equals(Record.TYPE_SCENE_SCENE)) {//场景
            setRecordEditBaseFragment(new RecordCJFragment());
        } else if (recordType.equals(Record.TYPE_SCENE_PRINCIPAL)) {//负责人
            setRecordEditBaseFragment(new RecordFZRFragment());
        } else if (recordType.equals(Record.TYPE_SCENE_TECHNICIAN)) {//工程师
            setRecordEditBaseFragment(new RecordGCSFragment());
        } else if (recordType.equals(Record.TYPE_SCENE_VIDEO)) {//短视频
            setRecordEditBaseFragment(new RecordVideoFragment());
        }

        //短视频 负责人 工程师 机长、钻机、场景、描述
        if (recordType.equals(Record.TYPE_SCENE_VIDEO) || recordType.equals(Record.TYPE_SCENE_PRINCIPAL) || recordType.equals(Record.TYPE_SCENE_TECHNICIAN) || recordType.equals(Record.TYPE_SCENE_OPERATEPERSON) || recordType.equals(Record.TYPE_SCENE_OPERATECODE) || recordType.equals(Record.TYPE_SCENE_RECORDPERSON) || recordType.equals(Record.TYPE_SCENE_SCENE)) {
            recordBeginDepth.setVisibility(View.GONE);
            recordEndDepth.setVisibility(View.GONE);
            recordCode.setVisibility(View.GONE);
            //修改注释内容
            if (recordType.equals(Record.TYPE_SCENE_OPERATEPERSON)) {
                recordEditNoteTv.setText(R.string.record_operatepersion);
            } else if (recordType.equals(Record.TYPE_SCENE_OPERATECODE)) {
                recordEditNoteTv.setText(R.string.record_operatecode);
            } else if (recordType.equals(Record.TYPE_SCENE_RECORDPERSON)) {
                recordEditNoteTv.setText(R.string.record_recordpersion);
            } else if (recordType.equals(Record.TYPE_SCENE_SCENE)) {
                recordEditNoteTv.setText(R.string.record_scenescene);
            } else if (recordType.equals(Record.TYPE_SCENE_TECHNICIAN)) {
                recordEditNoteTv.setText(R.string.record_technician);
            } else if (recordType.equals(Record.TYPE_SCENE_VIDEO)) {
                recordEditNoteTv.setText(R.string.record_video);
            } else {
                recordEditNoteTv.setText(R.string.record_principal);
            }
        }

        // 取水、动探、标灌、水位
        if (recordType.equals(Record.TYPE_GET_WATER) || recordType.equals(Record.TYPE_DPT) || recordType.equals(Record.TYPE_SPT) || recordType.equals(Record.TYPE_WATER)) {
            recordBeginDepth.setVisibility(View.GONE);
            recordEndDepth.setVisibility(View.GONE);
        }
        //备注
        if (recordType.equals(Record.TYPE_SCENE)) {
            recordBeginDepth.setVisibility(View.GONE);
            recordEndDepth.setVisibility(View.GONE);
            recordEditNoteTv.setText(R.string.record_scene);
        }
        //非回次
        if (!recordType.equals(Record.TYPE_FREQUENCY)) {
            recordDescription.setHint("编辑其他描述");
            recordDescription.setFloatingLabelText("其他描述");
        }
        //岩土
        if (recordType.equals(Record.TYPE_LAYER)) {
            recordEditNoteTv.setText(R.string.record_layer);
        }
        //模板是否显示 回次 岩土 取土 取水 动探
//        if (recordType.equals(Record.TYPE_FREQUENCY) || recordType.equals(Record.TYPE_LAYER) || recordType.equals(Record.TYPE_GET_EARTH) || recordType.equals(Record.TYPE_GET_WATER) || recordType.equals(Record.TYPE_DPT)) {
//            recordTemplateLl.setVisibility(View.VISIBLE);
//        }
    }

    private void setRecordEditBaseFragment(RecordBaseFragment recordBaseFragment) {
        this.recordBaseFragment = recordBaseFragment;
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.RECORD, record);
        recordBaseFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.record_content_fl, recordBaseFragment, "type" + recordType);
        ft.commit();
    }

    private void initMediaFragment(Record record) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.RECORD, record);
        mediaFragment = new RecordMediaFragment();
        mediaFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mediaFrame, mediaFragment, "mediaFragment");
        ft.commit();
    }

    private void initLocationFragment(Hole hole) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.HOLE, hole);
        locationFragment = new RecordLocationFragment();
        locationFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.locationFrame, locationFragment, "locationFragment");
        ft.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_record_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishDialog();
                return true;
            case R.id.act_save:
                if (save()) {
                    onBackPressed();
                }
                return true;
            case R.id.act_help:
                Intent intent = new Intent(this, HelpActivtiy.class);
                intent.setAction(recordType);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //验证，并完善record信息
    private boolean verify() {
        if (!Common.haveGps(this)) {
            return false;
        }
        amapLocation = locationFragment.aMapLocation;
        if (amapLocation == null) {
            ToastUtil.showToastS(this, "无法获取定位信息");
            return false;
        }
        //记录编号和其他描述长度限制
        if (recordCode.getText().toString().trim().length() > 20) {
            recordCode.setError("编号长度不能超过20");
            return false;
        }
        if (recordDescription.getText().toString().trim().length() > 50) {
            recordDescription.setError("描述长度不能超过50");
            return false;
        }
        if ("".equals(recordBeginDepth.getText().toString())) {
            recordBeginDepth.setText(recordBeginDepth.getHint());
        }
        if ("".equals(recordEndDepth.getText().toString())) {
            recordEndDepth.setText(recordEndDepth.getHint());
        }
        //取消必选项，只有取土不需要终止深度
        if (recordType.equals(Record.TYPE_FREQUENCY) || recordType.equals(Record.TYPE_LAYER) || recordType.equals(Record.TYPE_GET_EARTH)) {
            if (Double.valueOf(recordBeginDepth.getText().toString()) >= Double.valueOf(recordEndDepth.getText().toString())) {
                recordEndDepth.setError("终止深度必须大于起始深度");
                return false;
            }
        }
        //起始值和终止值都不能重叠
        if (recordType.equals(Record.TYPE_FREQUENCY) || recordType.equals(Record.TYPE_LAYER)) {
            if (RecordDao.getInstance().validatorBeginDepth(record, recordType, recordBeginDepth.getText().toString())) {
                recordBeginDepth.setError("与其他记录重叠");
                return false;
            }

            if (RecordDao.getInstance().validatorEndDepth(record, recordType, recordEndDepth.getText().toString())) {
                recordEndDepth.setError("与其他记录重叠");
                return false;
            }
        }

        if ("".equals(recordCode.getText().toString().trim())) {
            recordCode.setError("记录编号不能为空");
            return false;
        }
        //获取不同fragment中表单验证
        if (!recordBaseFragment.validator()) {
            return false;
        }
        //每条记录的第一条至少有一张照片
        //先查询本身这条记录是否添加了照片 添加不包括项目负责人，工程师，提钻录像、备注
        if (!recordType.equals(Record.TYPE_SCENE_PRINCIPAL) && !recordType.equals(Record.TYPE_SCENE_TECHNICIAN) && !recordType.equals(Record.TYPE_SCENE_VIDEO) && !recordType.equals(Record.TYPE_SCENE)) {
            if (mediaFragment.photoList == null || mediaFragment.photoList.size() == 0) {
                //当前记录没有，在查询当前类别下是否存在有照片的记录，不包括历史记录
                List<Record> recordList = RecordDao.getInstance().getRecordListByType(hole.getId(), recordType);
                if (recordList != null && recordList.size() > 0) {
                    int photoSize = 0;
                    for (Record record : recordList) {
                        List<Media> list = MediaDao.getInstance().getMediaListByRecordID(record.getId());
                        if (list != null && list.size() > 0) {
                            for (Media media : list) {
                                if (media.getLocalPath().endsWith("jpg")) {
                                    photoSize++;
                                }
                            }
                        }
                    }
                    if (photoSize == 0) {
                        ToastUtil.showToastS(this, "请添加至少一张照片");
                        return false;
                    }
                } else {
                    ToastUtil.showToastS(this, "请添加至少一张照片");
                    return false;
                }
            }
        }
        //获取不同fragment中record参数
        record = recordBaseFragment.getRecord();
        //保存记录本身的相关参数
        if (recordType.equals(Record.TYPE_GET_WATER) || recordType.equals(Record.TYPE_DPT) || recordType.equals(Record.TYPE_SPT) || recordType.equals(Record.TYPE_WATER)) {
            record.setBeginDepth(recordBaseFragment.getBegin());
            record.setEndDepth(recordBaseFragment.getEnd());
        } else {
            record.setBeginDepth(recordBeginDepth.getText().toString());
            record.setEndDepth(recordEndDepth.getText().toString());
        }
        record.setType(recordBaseFragment.getTypeName());
        record.setTitle(recordBaseFragment.getTitle());
        record.setCode(recordCode.getText().toString());
        record.setDescription(recordDescription.getText().toString());
        record.setState("1");
        record.setIsDelete("0");
        record.setUpdateTime(GPSutils.utcToTimeZoneDate(amapLocation.getTime()));
        record.setRecordPerson((String) SPUtils.get(this, Urls.SPKey.USER_REALNAME, ""));
        return true;
    }


    private boolean save() {
        if (!verify()) {
            return false;
        }
        RecordDao.getInstance().addOrUpdate(record);
        //保存记录下gps数据
        Gps gps = new Gps(record, amapLocation, recordType);
        GpsDao.getInstance().addOrUpdate(gps);
        //保存记录下媒体数据
        saveMediaList();
        //修改所属勘探点上传状态state，如果是新增，添加记录数量recordCount
        hole.setState("1");
        hole.setStateGW("1");
        hole.setUpdateTime(DateUtil.date2Str(new Date()));
        if (!isEdit) {
            int count = Integer.parseInt(hole.getRecordsCount());
            count++;
            hole.setRecordsCount(String.valueOf(count));
        }
        HoleDao.getInstance().addOrUpdate(hole);
        //如果是编辑模式，保存历史记录及其gps
        if (isEdit) {
            RecordDao.getInstance().addOrUpdate(recordOld);
            GpsDao.getInstance().addOrUpdate(gpsOld);
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        //该方法自动调用finish()
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishDialog();
        }
        return true;
    }

    /**
     * 删除勘察点dialog
     */
    private void finishDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.hint)
                .setMessage("是否退出编辑记录吗")
                .setNegativeButton("保存并退出",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (save()) {
                                    onBackPressed();
                                }
                            }
                        })
                .setPositiveButton("放弃保存退出",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!isEdit) {
                                    record.delete(RecordEditActivity.this);
                                }
                                onBackPressed();
                            }
                        })
                .show();
    }

    @OnClick({R.id.record_dptup_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.record_dptup_btn:
                //如果保存成功了，新建record，初始页面，继续编辑
                if (save()) {
                    isEdit = false;
                    record = new Record(RecordEditActivity.this, hole, Record.TYPE_DPT);
                    RecordDao.getInstance().addOrUpdate(record);
                    initPage(record);
                }
                break;
        }
    }


    public void saveMediaList() {
        MediaDao.getInstance().getMediaListByRecordIDSave(record.getId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.EDIT_GO_TEMPLATE && resultCode == RESULT_OK) {
            if (data != null) {
                Template template = (Template) data.getSerializableExtra(MainActivity.TEMPLATE);
                record.templateToRecord(template);
                initPage(record);
            }
        }
    }
}
