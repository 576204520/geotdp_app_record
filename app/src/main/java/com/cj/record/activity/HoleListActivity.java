package com.cj.record.activity;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.idst.nls.internal.utils.L;
import com.cj.record.R;
import com.cj.record.base.App;
import com.cj.record.adapter.HoleAdapter;
import com.cj.record.adapter.SpacesItemDecoration;
import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.DropItemVo;
import com.cj.record.baen.Gps;
import com.cj.record.baen.Hole;
import com.cj.record.baen.LocalUser;
import com.cj.record.baen.PageBean;
import com.cj.record.baen.Project;
import com.cj.record.baen.Record;
import com.cj.record.base.BaseMvpActivity;
import com.cj.record.contract.HoleContract;
import com.cj.record.db.GpsDao;
import com.cj.record.db.HoleDao;
import com.cj.record.db.ProjectDao;
import com.cj.record.db.RecordDao;
import com.cj.record.presenter.HolePresenter;
import com.cj.record.utils.Common;
import com.cj.record.utils.DateUtil;
import com.cj.record.utils.JsonUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.UpdateUtil;
import com.cj.record.views.MaterialBetterSpinner;
import com.cj.record.views.ProgressDialog;
import com.cj.record.views.dialog.HoleInfoDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;


/**
 * Created by Administrator on 2018/5/29.
 */

public class HoleListActivity extends BaseMvpActivity<HolePresenter> implements HoleContract.View, SwipeRefreshLayout.OnRefreshListener,
        HoleAdapter.OnItemListener, View.OnClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refresh;
    @BindView(R.id.sprSort)
    MaterialBetterSpinner sprSort;
    @BindView(R.id.hole_search_iv)
    ImageView holeSearchIv;
    @BindView(R.id.hole_search_et)
    EditText holeSearchEt;

    private Project project;
    private List<Hole> dataList;
    private List<Hole> newList;
    private HoleAdapter holeAdapter;
    private int total = 0;//总条数
    private int size = 20;
    private int page = 1;//页数 初始值1
    private List<DropItemVo> listSort;
    private LinearLayoutManager linearLayoutManager;
    private String search = "";
    private List<Hole> checkList;//关联多次的list
    private List<LocalUser> localUserList;//获取数据的userList
    private Hole uploadHole;
    private Dialog chooseDialog;
    private HoleInfoDialog holeInfoDialog;//详情
    private String jzTestType = "";

    @Override
    public int getLayoutId() {
        return R.layout.activity_hole_list;
    }


    @Override
    public void initView() {
        mPresenter = new HolePresenter();
        mPresenter.attachView(this);

        project = (Project) getIntent().getSerializableExtra(MainActivity.PROJECT);
        dataList = new ArrayList<>();
        newList = new ArrayList<>();

        holeInfoDialog = new HoleInfoDialog();
        toolbar.setTitle(project.getFullName());
        toolbar.setSubtitle("勘探点列表");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //排序选择
        sprSort.setAdapter(this, initLayerTypeList());
        sprSort.setText(listSort.get(0).getValue());
        sprSort.setTag(listSort.get(0).getId());
        sprSort.setOnItemClickListener(sortListener);
        //搜索框添加监听
        holeSearchEt.addTextChangedListener(textWatcher);
        initRecycleView();
        onRefresh();
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            onRefresh();
        }
    };

    private void initRecycleView() {
        holeAdapter = new HoleAdapter(this, dataList, project);
        //默认打开第一条数据的按钮
        holeAdapter.openFrist();
        holeAdapter.setOnItemListener(this);
        refresh.setOnRefreshListener(this);
        recycler.addItemDecoration(new SpacesItemDecoration(6));
        linearLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(linearLayoutManager);
        //addOnScrollListener到不到效果不知为什么
        recycler.addOnScrollListener(onScrollListener);
        recycler.setAdapter(holeAdapter);
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


    private List<DropItemVo> initLayerTypeList() {
        listSort = new ArrayList<DropItemVo>();
        listSort.add(new DropItemVo("1", " 按最后修改时间优先显示"));
        listSort.add(new DropItemVo("2", " 按编号优先显示"));
//        listSort.add(new DropItemVo("3", "按上传进度排序"));
        listSort.add(new DropItemVo("4", "按定位时间优先显示"));
        listSort.add(new DropItemVo("5", "已关联优先显示"));
        listSort.add(new DropItemVo("6", "按关联钻孔编号优先显示"));

        return listSort;
    }

    /**
     * sort点击事件
     */
    MaterialBetterSpinner.OnItemClickListener sortListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            DropItemVo d = listSort.get(i);
            sprSort.setTag(d.getId());
            sprSort.setText(d.getValue());
            onRefresh();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hole_list, menu);
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
                showChooseDialog();
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
    public void onRefresh() {
        page = 0;
        mPresenter.loadData(project.getId(), page, size, holeSearchEt.getText().toString().trim(), sprSort.getTag().toString());
    }

    private void onLoadMore() {
        page++;
        mPresenter.loadData(project.getId(), page, size, holeSearchEt.getText().toString().trim(), sprSort.getTag().toString());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //单个关联回调
        if (requestCode == MainActivity.GO_LOCAL_CREATE && resultCode == RESULT_OK) {
            Hole relateHole = (Hole) data.getSerializableExtra(MainActivity.HOLE);
            if (relateHole != null) {
                //遍历数据库，查找是否关联
                if (HoleDao.getInstance().checkRelatedNoHole(uploadHole.getId(), relateHole.getId(), project.getId())) {
                    Common.showMessage(this, relateHole.getCode() + getString(R.string.hole_list_relate_ishave));
                    return;
                }
                mPresenter.relate(App.userID, relateHole.getId(), uploadHole.getId(), UpdateUtil.getVerCode(this) + "");
            }
        }
        //关联列表回调
        if (requestCode == MainActivity.GO_RELATE_CREATE && resultCode == RESULT_OK) {
            checkList = (List<Hole>) data.getSerializableExtra(MainActivity.CHECKLIST);
            if (checkList != null && checkList.size() > 0) {
                mPresenter.relateMore(checkList, this, HoleDao.getInstance(), project, UpdateUtil.getVerCode(this) + "");
            }
            return;
        }
        //下载获取数据
        if (requestCode == MainActivity.GO_DOWNLOAD_CREATE && resultCode == RESULT_OK) {
            localUserList = (List<LocalUser>) data.getSerializableExtra(MainActivity.LOCALUSERLIST);
            if (localUserList != null && localUserList.size() > 0) {
                mPresenter.downLoadHole(localUserList, UpdateUtil.getVerCode(this) + "");
            }
            return;
        }
        if (resultCode == RESULT_OK) {
            onRefresh();
        }
    }


    public void addOrUpdate(Hole hole, LocalUser localUser) {
        hole.setProjectID(project.getId());
        //下载的勘探点都是未关联、已经定位的不需要重新定位、
        if (hole.getMapLatitude() != null && hole.getMapLongitude() != null) {
            hole.setLocationState("1");
        } else {
            hole.setLocationState("0");//可能获取没有定位信息的钻孔需要定位
        }
        hole.setRelateID("");
        hole.setRelateCode("");
        hole.setIsDelete("0");
        HoleDao.getInstance().addOrUpdate(hole);
        List<Record> recordList = hole.getRecordList();
        if (recordList != null && recordList.size() > 0) {
            for (Record record : recordList) {
                //同勘探点，要判断是否存在
                if (!localUser.getDeptID().equals(App.userID)) {
                    record.setId(Common.getUUID());
                }
                record.setProjectID(project.getId());
                record.setHoleID(hole.getId());
                record.setState("1");
                record.setIsDelete("0");
                record.setUpdateId(record.getUpdateId() == null ? "" : record.getUpdateId());//这里有历史记录，不能情况updateID
                RecordDao.getInstance().addOrUpdate(record);
                List<Gps> gpsList = record.getGpsList();
                if (gpsList != null && gpsList.size() > 0) {
                    for (Gps gps : gpsList) {
                        if (!localUser.getDeptID().equals(App.userID)) {
                            gps.setId(Common.getUUID());
                        }
                        gps.setProjectID(project.getId());
                        gps.setHoleID(hole.getId());
                        gps.setRecordID(record.getId());
                        GpsDao.getInstance().addOrUpdate(gps);
                    }
                }
            }
        }
    }

    @Override
    public void detailClick(int position) {
        Hole hole = dataList.get(position);
        holeInfoDialog.show(this, project, hole);
    }

    @Override
    public void editClick(int position) {
        Hole hole = dataList.get(position);
        if (TextUtils.isEmpty(hole.getUserID()) || hole.getUserID().equals(App.userID)) {
            goEdit(hole);
        } else {
            Common.showMessage(this, getString(R.string.hole_list_orther_people));
        }
    }

    private void goEdit(Hole hole) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(MainActivity.FROMTYPE, true);
        bundle.putSerializable(MainActivity.PROJECT, project);
        bundle.putSerializable(MainActivity.HOLE, hole);
        startActivityForResult(HoleEditActivity.class, bundle, MainActivity.HOLE_GO_EDIT);
    }

    @Override
    public void recordListClick(int position) {
        final Hole hole = dataList.get(position);
        if (TextUtils.isEmpty(hole.getMapLatitude()) || TextUtils.isEmpty(hole.getMapLongitude())) {
            showMsgDialog(hole, "勘探点未定位,是否编辑勘察点，进行定位操作");
            return;
        }
        //查看信息是否编录完整
        int complete;
        if ("探井".equals(hole.getType())) {
            complete = RecordDao.getInstance().checkTJ(hole.getId());
            if (complete < 2) {
                showMsgDialog(hole, "勘探点数据不完整，请完善（描述员、场景）记录");
                return;
            }
        } else {
            complete = RecordDao.getInstance().checkZK(hole.getId());
            if (complete < 4) {
                showMsgDialog(hole, "勘探点数据不完整，请完善（司钻员、钻机、描述员、场景）记录");
                return;
            }
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.HOLE, dataList.get(position));
        startActivityForResult(RecordListActivity.class, bundle, MainActivity.HOLE_GO_LIST);
    }

    private void showMsgDialog(Hole hole, String msg) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.hint)
                .setMessage(msg)
                .setNegativeButton(R.string.record_camera_cancel_dialog_yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                goEdit(hole);
                            }
                        })
                .setPositiveButton(R.string.record_camera_cancel_dialog_no, null)
                .setCancelable(false)
                .show();
    }

    @Override
    public void checkClick(int position) {
        Hole hole = dataList.get(position);
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra(PreviewActivity.EXTRA_HOLE, hole);
        startActivity(intent);
    }

    @Override
    public void uploadClick(final int position) {
        uploadHole = dataList.get(position);
        //判断项目是否关联
        if (TextUtils.isEmpty(project.getSerialNumber())) {
            Common.showMessage(this, getString(R.string.hole_list_is_relate));
            return;
        }
        //判断是否是自己的项目
        if (uploadHole.getUserID() != null && !uploadHole.getUserID().equals(App.userID)) {
            Common.showMessage(this, getString(R.string.hole_list_is_orther));
            return;
        }
        //判断是否定位
        if (TextUtils.isEmpty(uploadHole.getMapLatitude()) || TextUtils.isEmpty(uploadHole.getMapLongitude())) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.hint)
                    .setMessage(R.string.hole_list_is_location)
                    .setNegativeButton(R.string.record_camera_cancel_dialog_yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goEdit(uploadHole);
                                }
                            })
                    .setPositiveButton(R.string.record_camera_cancel_dialog_no, null)
                    .setCancelable(false)
                    .show();
            return;
        }
        //已经定位，但是数据不全
        int complete;
        if (Hole.TYPE_TJ.equals(uploadHole.getType())) {
            complete = RecordDao.getInstance().checkTJ(uploadHole.getId());
            if (complete < 2) {
                Common.showMessage(this, getString(R.string.hole_edit_save_hint2));
                return;
            }
        } else {
            complete = RecordDao.getInstance().checkZK(uploadHole.getId());
            if (complete < 4) {
                Common.showMessage(this, getString(R.string.hole_edit_save_hint4));
                return;
            }
            Record jz = RecordDao.getInstance().getRecordByType(uploadHole.getId(), Record.TYPE_SCENE_OPERATEPERSON);
            if (jz != null) {
                if (!TextUtils.isEmpty(jz.getTestType())) {
                    jzTestType = jz.getTestType();
                } else {
                    Common.showMessage(this, getString(R.string.hole_list_is_jz_code));
                    return;
                }
            } else {
                Common.showMessage(this, getString(R.string.hole_list_is_jz));
                return;
            }
        }
        //判断是否关联勘探点
        boolean isRelate;
        if (TextUtils.isEmpty(project.getProjectID())) {
            Common.showMessage(this, getString(R.string.hole_list_is_projectid));
            return;
        } else if (TextUtils.isEmpty(uploadHole.getRelateCode()) || TextUtils.isEmpty(uploadHole.getRelateID())) {
            isRelate = false;
        } else if (project.isUpload() && TextUtils.isEmpty(uploadHole.getUploadID())) {
            isRelate = false;
        } else {
            isRelate = true;
        }
        if (!isRelate) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.hint)
                    .setMessage(R.string.hole_list_is_relate_again)
                    .setNegativeButton(R.string.record_camera_cancel_dialog_yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(MainActivity.RELATE_TYPE, MainActivity.HAVE_NOALL);
                                    bundle.putString(MainActivity.SERIALNUMBER, project.getSerialNumber());
                                    startActivityForResult(RelateHoleActivity.class, bundle, MainActivity.GO_LOCAL_CREATE);
                                }
                            })
                    .setPositiveButton(R.string.record_camera_cancel_dialog_no, null)
                    .setCancelable(false)
                    .show();
            return;
        }
        if (uploadHole.getNotUploadCount() > 0) {
            //上传操作,先校验用户信息
            mPresenter.checkUser(project.getProjectID(), App.userID, jzTestType, uploadHole.getType(), UpdateUtil.getVerCode(this) + "");
        } else {
            Common.showMessage(this, getString(R.string.hole_list_no_upload_record));
        }
    }


    @Override
    public void deleteClick(int position) {
        deleteDialog(position);
    }

    /**
     * 删除勘察点dialog
     */
    private void deleteDialog(final int position) {
        final Hole hole = dataList.get(position);
        int con = hole.getNotUploadCount() == 0 ? R.string.confirmDelete : R.string.confirmDelete2;
        new AlertDialog.Builder(this)
                .setTitle(R.string.hint)
                .setMessage(con)
                .setNegativeButton(R.string.record_camera_cancel_dialog_yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //标记要删除的项
                                mPresenter.delete(HoleListActivity.this, dataList.get(position));
                            }
                        })
                .setPositiveButton(R.string.record_camera_cancel_dialog_no, null)
                .setCancelable(false)
                .show();
    }

    private void showChooseDialog() {
        View view = getLayoutInflater().inflate(R.layout.choose_hole, null);
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
            case R.id.choose_hole_local:
                //添加hole
                Bundle local = new Bundle();
                local.putBoolean(MainActivity.RELATE_TYPE, false);
                local.putSerializable(MainActivity.PROJECT, project);
                startActivityForResult(HoleEditActivity.class, local, MainActivity.HOLE_GO_EDIT);
                break;
            case R.id.choose_hole_relate:
                //关联创建
                if (TextUtils.isEmpty(project.getSerialNumber())) {
                    Common.showMessage(this, getString(R.string.hole_list_norelate_project));
                } else {
                    Bundle relate = new Bundle();
                    relate.putInt(MainActivity.RELATE_TYPE, MainActivity.HAVE_SOME);
                    relate.putString(MainActivity.SERIALNUMBER, project.getSerialNumber());
                    startActivityForResult(RelateHoleActivity.class, relate, MainActivity.GO_RELATE_CREATE);
                }
                break;
            case R.id.choose_hole_down:
                //下载数据
                if (TextUtils.isEmpty(project.getSerialNumber())) {
                    Common.showMessage(this, getString(R.string.hole_list_norelate_project));
                } else {
                    Bundle down = new Bundle();
                    down.putInt(MainActivity.RELATE_TYPE, MainActivity.HAVE_ALL);
                    down.putString(MainActivity.SERIALNUMBER, project.getSerialNumber());
                    startActivityForResult(RelateHoleActivity.class, down, MainActivity.GO_DOWNLOAD_CREATE);
                }
                break;
        }
        if (chooseDialog.isShowing()) {
            chooseDialog.dismiss();
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
        L.e(throwable.toString());
        ToastUtil.showToastL(this, throwable.toString());
    }

    @Override
    public void onSuccessAddOrUpdate() {

    }


    @Override
    public void onSuccessDelete() {
        onRefresh();
    }

    @Override
    public void onSuccessList(PageBean<Hole> pageBean) {
        total = pageBean.getTotleSize();
        if (page == 0) {
            dataList.clear();
            dataList.addAll(pageBean.getList());
            holeAdapter.notifyDataSetChanged();
            holeAdapter.openFrist();
            refresh.setRefreshing(false);
        } else if (page > 0) {
            dataList.addAll(pageBean.getList());
            holeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSuccessRelate(BaseObjectBean<String> bean) {
        if (bean.isStatus()) {
            Hole h = JsonUtils.getInstance().fromJson(bean.getResult(), Hole.class);
            uploadHole.setRelateCode(h.getCode());
            uploadHole.setRelateID(h.getId());
            if (!TextUtils.isEmpty(h.getUploadID())) {
                uploadHole.setUploadID(h.getUploadID());
            }
            if (!TextUtils.isEmpty(h.getDepth())) {
                uploadHole.setDepth(h.getDepth());
            }
            if (!TextUtils.isEmpty(h.getElevation())) {
                uploadHole.setElevation(h.getElevation());
            }
            if (!TextUtils.isEmpty(h.getDescription())) {
                uploadHole.setDescription(h.getDescription());
            } else {
                uploadHole.setDescription("");
            }
            if (!TextUtils.isEmpty(h.getType())) {
                uploadHole.setType(h.getType());
            }
            uploadHole.setState("1");
            uploadHole.setStateGW("1");
            HoleDao.getInstance().addOrUpdate(uploadHole);
            project.setUpdateTime(DateUtil.date2Str(new Date()) + "");
            ProjectDao.getInstance().addOrUpdate(project);
            onRefresh();
        } else {
            Common.showMessage(HoleListActivity.this, bean.getMessage());
        }
    }

    @Override
    public void onSuccessRelateMore(BaseObjectBean<String> bean, Hole newHole) {
        if (bean.isStatus()) {
            HoleDao.getInstance().add(newHole);
            onRefresh();
        } else {
            Common.showMessage(HoleListActivity.this, bean.getMessage());
        }
    }

    @Override
    public void onSuccessNoRelateList(List<Hole> noRelateList) {
        if (noRelateList != null && noRelateList.size() > 0) {
            StringBuffer sb = new StringBuffer();
            sb.append("(");
            for (Hole hole : noRelateList) {
                sb.append(hole.getRelateCode());
                sb.append("、");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append(")");
            Common.showMessage(HoleListActivity.this, getString(R.string.hole_list_no_relate_hint1) + sb.toString() + getString(R.string.hole_list_no_relate_hint2));
        }
    }

    @Override
    public void onSuccessDownloadHole(BaseObjectBean<String> bean, LocalUser localUser) {
        Hole hole = JsonUtils.getInstance().fromJson(bean.getResult(), Hole.class);
        //查询userid是否相同，查询项目下钻孔的relateID是否存在
        //判断该项目下是否存在关联的勘探点 projectID、relateID
        if (HoleDao.getInstance().checkRelated(hole.getRelateID(), project.getId())) {
            Common.showMessage(HoleListActivity.this, hole.getRelateCode() + "(" + hole.getCode() + ")关联孔本地已经存在");
            return;
        } else {
            hole.setId(Common.getUUID());
            addOrUpdate(hole, localUser);
            onRefresh();
        }
    }

    @Override
    public void onSuccessRelateList(BaseObjectBean<String> bean) {

    }

    @Override
    public void onSuccessDownloadList(BaseObjectBean<String> bean) {

    }


    @Override
    public void onSuccessCheckUser(BaseObjectBean<String> bean) {
        if (bean.isStatus()) {
            mPresenter.uploadHole(this, project, uploadHole);
        } else {
            Common.showMessage(HoleListActivity.this, bean.getMessage());
        }
    }

    @Override
    public void onSuccessGetScene(List<Record> recordList) {

    }


    @Override
    public void onSuccessUpload(BaseObjectBean<Integer> bean) {
        if (bean.isStatus()) {
            onRefresh();
        } else {
            Common.showMessage(HoleListActivity.this, bean.getMessage());
        }
    }
}
