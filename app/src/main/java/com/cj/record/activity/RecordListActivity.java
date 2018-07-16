package com.cj.record.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.adapter.RecordAdapter;
import com.cj.record.adapter.SpacesItemDecoration;
import com.cj.record.baen.DropItemVo;
import com.cj.record.baen.Hole;
import com.cj.record.baen.Record;
import com.cj.record.db.RecordDao;
import com.cj.record.utils.ObsUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.views.MaterialBetterSpinner;
import com.cj.record.views.dialog.RecordInfoDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/6/5.
 */

public class RecordListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        ObsUtils.ObsLinstener, RecordAdapter.OnItemListener, View.OnClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refresh;
    @BindView(R.id.sprSort)
    MaterialBetterSpinner sprSort;
    @BindView(R.id.sprSequence)
    MaterialBetterSpinner sprSequence;

    private List<DropItemVo> listSort;
    private List<DropItemVo> listSequence;
    private Hole hole;
    private List<Record> dataList;
    private List<Record> newList;
    private RecordDao recordDao;
    private ObsUtils obsUtils;
    private RecordAdapter recordAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int total = 0;//总条数
    private int size = 20;
    private int page = 0;//页数 初始值1
    private Dialog chooseDialog;
    private RecordInfoDialog recordInfoDialog;//记录详情
    private DropItemVo divSort;//分类
    private DropItemVo divSequence;//排序的对象

    @Override
    public int getLayoutId() {
        return R.layout.activity_record_list;
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
        toolbar.setTitle(hole.getCode());
        toolbar.setSubtitle("记录列表");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initRecycleView() {
        recordAdapter = new RecordAdapter(this, dataList);
        //默认打开第一条数据的按钮
        recordAdapter.openFrist();
        recordAdapter.setOnItemListener(this);
        refresh.setOnRefreshListener(this);
        recycler.addItemDecoration(new SpacesItemDecoration(6));
        linearLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(linearLayoutManager);
        //addOnScrollListener到不到效果不知为什么
        recycler.addOnScrollListener(onScrollListener);
        recycler.setAdapter(recordAdapter);
    }


    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (total > size) {
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                if (dataList.size() != total) {
                    if ((visibleItemCount > 0 && (lastVisibleItemPosition) >= totalItemCount - 1)) {
                        onLoadMore();
                    }
                }
            }
        }
    };


    @Override
    public void onSubscribe(int type) {
        switch (type) {
            case 1:
                hole = (Hole) getIntent().getSerializableExtra(MainActivity.HOLE);
                dataList = new ArrayList<>();
                recordDao = new RecordDao(this);
                recordInfoDialog = new RecordInfoDialog();
                getLayerTypeList();
                getModeList();
                break;
            case 2:
                getLayerTypeList();
                sprSort.setTag(divSort.getId());
                //排序这里都是固定值，不需要重新获取
                sprSequence.setTag(divSequence.getId());
                //获取分页数据
                page = 0;
                total = recordDao.getSortCountMap(hole.getId()).get(1);
                dataList = recordDao.getRecordList(hole.getId(), size, page, sprSort.getTag().toString(), sprSequence.getTag().toString());
                break;
            case 3:
                page++;
                newList.clear();
                newList = recordDao.getRecordList(hole.getId(), size, page, sprSort.getTag().toString(), sprSequence.getTag().toString());
                break;
        }
    }

    @Override
    public void onComplete(int type) {
        switch (type) {
            case 1:
                //记录分类显示
                divSort = listSort.get(0);
                sprSort.setAdapter(this, listSort);
                sprSort.setOnItemClickListener(sortListener);
                //记录排序
                divSequence = listSequence.get(0);
                sprSequence.setAdapter(this, listSequence);
                sprSequence.setOnItemClickListener(sequenceListener);
                //初始化列表布局
                initRecycleView();
                //调用刷新方法
                onRefresh();
                break;
            case 2:
                //刷新列表
                recordAdapter.refresh(dataList);
                recordAdapter.openFrist();
                refresh.setRefreshing(false);
                //刷新记录分类
                sprSort.refresh(listSort);
                sprSort.setText(getsortValue(divSort.getId()));
                //刷新记录排序
                sprSequence.setText(divSequence.getValue());
                break;
            case 3:
                recordAdapter.loadMore(newList);
                break;
        }
    }

    private List<DropItemVo> getLayerTypeList() {
        Map<Integer, Integer> countMap = new RecordDao(this).getSortCountMap(hole.getId());
        listSort = new ArrayList<DropItemVo>();
        listSort.add(new DropItemVo("", "全部记录(" + countMap.get(1) + ")"));
        listSort.add(new DropItemVo(Record.TYPE_FREQUENCY, "回次记录(" + countMap.get(2) + ")"));
        listSort.add(new DropItemVo(Record.TYPE_LAYER, "岩土记录(" + countMap.get(3) + ")"));
        listSort.add(new DropItemVo(Record.TYPE_WATER, "水位记录(" + countMap.get(4) + ")"));
        listSort.add(new DropItemVo(Record.TYPE_DPT, "动探记录(" + countMap.get(5) + ")"));
        listSort.add(new DropItemVo(Record.TYPE_SPT, "标贯记录(" + countMap.get(6) + ")"));
        listSort.add(new DropItemVo(Record.TYPE_GET_EARTH, "取土记录(" + countMap.get(7) + ")"));
        listSort.add(new DropItemVo(Record.TYPE_GET_WATER, "取水记录(" + countMap.get(8) + ")"));
        listSort.add(new DropItemVo(Record.TYPE_SCENE, "备注记录(" + countMap.get(9) + ")"));
        return listSort;
    }

    private String getsortValue(String id) {
        for (DropItemVo div : listSort) {
            if (id.equals(div.getId())) {
                return div.getValue();
            }
        }
        return listSort.get(0).getValue();
    }

    private List<DropItemVo> getModeList() {
        listSequence = new ArrayList<DropItemVo>();
        listSequence.add(new DropItemVo("1", "最新修改"));
        listSequence.add(new DropItemVo("2", "最早修改"));
        listSequence.add(new DropItemVo("3", "由浅到深"));
        listSequence.add(new DropItemVo("4", "由深到浅"));
        return listSequence;
    }

    /**
     * sort点击事件
     */
    MaterialBetterSpinner.OnItemClickListener sortListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            divSort = listSort.get(i);
            onRefresh();
        }
    };
    /**
     * sequence点击事件
     */
    MaterialBetterSpinner.OnItemClickListener sequenceListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            divSequence = listSequence.get(i);
            onRefresh();
        }
    };

    @Override
    public void onRefresh() {
        obsUtils.execute(2);
    }

    private void onLoadMore() {
        obsUtils.execute(3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_record_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK);
                finish();
                return true;
            case R.id.act_add:
                if (TextUtils.isEmpty(hole.getUserID()) || hole.getUserID().equals(userID)) {
                    showChooseDialog();
                } else {
                    ToastUtil.showToastS(this, "不可以编辑他人数据");
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        //该方法自动调用finish()
        super.onBackPressed();
    }

    @Override
    public void detailClick(int position) {
        Record record = dataList.get(position);
        recordInfoDialog.show(this, record, hole.getCode());
    }

    @Override
    public void editClick(int position) {
        if (TextUtils.isEmpty(hole.getUserID()) || hole.getUserID().equals(userID)) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(MainActivity.FROMTYPE, true);
            bundle.putSerializable(MainActivity.HOLE, hole);
            bundle.putSerializable(MainActivity.RECORD, dataList.get(position));
            startActivityForResult(RecordEditActivity.class, bundle, MainActivity.RECORD_GO_EDIT);
        } else {
            ToastUtil.showToastS(this,"不可以编辑他人数据");
        }

    }

    @Override
    public void deleteClick(int position) {
        deleteDialog(position);
    }

    private void deleteDialog(final int position) {
        final Record record = dataList.get(position);
        int con = record.getNotUploadCount() == 0 ? R.string.confirmDelete : R.string.confirmDelete4;
        new AlertDialog.Builder(this)
                .setTitle(R.string.hint)
                .setMessage(con)
                .setNegativeButton(R.string.record_camera_cancel_dialog_yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //删除记录，其实是添加这条记录的updateID为本记录id
                                Record delRecord = recordDao.queryForId(record.getId());
                                delRecord.setUpdateId(record.getId());
                                delRecord.setState("1");
                                recordDao.update(delRecord);
                                onRefresh();
                            }
                        })
                .setPositiveButton(R.string.record_camera_cancel_dialog_no, null)
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            onRefresh();
        }
    }

    private void showChooseDialog() {
        View view = getLayoutInflater().inflate(R.layout.choose_record, null);
        if (chooseDialog == null) {
            chooseDialog = new Dialog(this, R.style.transparentFrameWindowStyle);
            chooseDialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            Window window = chooseDialog.getWindow();
            // 设置显示动画
            if (window != null) {
                window.setWindowAnimations(R.style.main_menu_animstyle);
                WindowManager.LayoutParams wl = window.getAttributes();
                wl.x = 0;
                wl.y = this.getWindowManager().getDefaultDisplay().getHeight();
                // 以下这两句是为了保证按钮可以水平满屏
                wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
                wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                // 设置显示位置
                chooseDialog.onWindowAttributesChanged(wl);
            }
            // 设置点击外围解散
            chooseDialog.setCanceledOnTouchOutside(true);
            chooseDialog.show();
        } else {
            chooseDialog.show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_hc:
                goEditByType(Record.TYPE_FREQUENCY);
                break;
            case R.id.record_yt:
                goEditByType(Record.TYPE_LAYER);
                break;
            case R.id.record_dt:
                goEditByType(Record.TYPE_DPT);
                break;
            case R.id.record_bg:
                goEditByType(Record.TYPE_SPT);
                break;
            case R.id.record_qt:
                goEditByType(Record.TYPE_GET_EARTH);
                break;
            case R.id.record_qs:
                goEditByType(Record.TYPE_GET_WATER);
                break;
            case R.id.record_sw:
                goEditByType(Record.TYPE_WATER);
                break;
            case R.id.record_bz:
                goEditByType(Record.TYPE_SCENE);
                break;
        }
        if (chooseDialog.isShowing()) {
            chooseDialog.dismiss();
        }
    }

    private void goEditByType(String recordType) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(MainActivity.FROMTYPE, false);
        bundle.putSerializable(MainActivity.HOLE, hole);
        bundle.putString(MainActivity.EXTRA_RECORD_TYPE, recordType);
        startActivityForResult(RecordEditActivity.class, bundle, MainActivity.RECORD_GO_EDIT);
    }

}