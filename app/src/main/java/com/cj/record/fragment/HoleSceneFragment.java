package com.cj.record.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.activity.MainActivity;
import com.cj.record.activity.RecordEditActivity;
import com.cj.record.activity.base.BaseFragment;
import com.cj.record.baen.Hole;
import com.cj.record.baen.Record;
import com.cj.record.db.HoleDao;
import com.cj.record.db.RecordDao;
import com.cj.record.utils.ObsUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;


/**
 * 根据hole 查询hole之下相关record，并找出对应的media数据，文件
 */

public class HoleSceneFragment extends BaseFragment implements ObsUtils.ObsLinstener {

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
    @BindView(R.id.scene_title)
    TextView sceneTitle;
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

    private Hole hole;
    private String type;//区分钻孔还是钻井的类别
    private RecordDao recordDao;
    private List<Record> recordList;
    private ObsUtils obsUtils;
    private Drawable drawableBottom;
    private Drawable drawableRight;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_hole_scene;
    }

    @Override
    public void initData() {
        super.initData();
        hole = (Hole) getArguments().getSerializable(MainActivity.HOLE);
        type = getArguments().getString(MainActivity.EXTRA_HOLE_TYPE);
        recordDao = new RecordDao(mActivity);
        recordList = new ArrayList<>();
        drawableBottom = getResources().getDrawable(R.mipmap.ic_keyboard_arrow_down_black_36dp);
        drawableRight = getResources().getDrawable(R.mipmap.ic_keyboard_arrow_right_black_36dp);
        obsUtils = new ObsUtils();
        obsUtils.setObsLinstener(this);
        obsUtils.execute(1);
    }

    @Override
    public void initView() {
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

    @OnClick({R.id.scene_title, R.id.scene_jizhang_fl, R.id.scene_zuanji_fl, R.id.scene_miaoshu_fl, R.id.scene_changjing_fl, R.id.scene_fuze_fl, R.id.scene_gongcheng_fl, R.id.scene_tizuan_fl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.scene_title:
                if (sceneItemLl.getVisibility() == View.GONE) {
                    sceneItemLl.setVisibility(View.VISIBLE);
                    sceneTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableBottom, null);
                } else {
                    sceneItemLl.setVisibility(View.GONE);
                    sceneTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
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

    @Override
    public void onSubscribe(int type) {
        switch (type) {
            case 1:
                recordList = recordDao.getSceneRecord(hole.getId());
                break;
        }
    }

    @Override
    public void onComplete(int type) {
        switch (type) {
            case 1:
                if (recordList != null && recordList.size() > 0) {
                    initPage();
                }
                break;
        }
    }

    private void initPage() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            obsUtils.execute(1);
        }
    }
}
