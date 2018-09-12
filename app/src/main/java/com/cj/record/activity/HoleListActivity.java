package com.cj.record.activity;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageView;

import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.adapter.HoleAdapter;
import com.cj.record.adapter.SpacesItemDecoration;
import com.cj.record.baen.DropItemVo;
import com.cj.record.baen.Gps;
import com.cj.record.baen.Hole;
import com.cj.record.baen.JsonResult;
import com.cj.record.baen.LocalUser;
import com.cj.record.baen.Media;
import com.cj.record.baen.Project;
import com.cj.record.baen.Record;
import com.cj.record.db.DBHelper;
import com.cj.record.db.GpsDao;
import com.cj.record.db.HoleDao;
import com.cj.record.db.MediaDao;
import com.cj.record.db.ProjectDao;
import com.cj.record.db.RecordDao;
import com.cj.record.utils.Common;
import com.cj.record.utils.DateUtil;
import com.cj.record.utils.JsonUtils;
import com.cj.record.utils.L;
import com.cj.record.utils.ObsUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.Urls;
import com.cj.record.views.MaterialBetterSpinner;
import com.cj.record.views.dialog.HoleInfoDialog;
import com.cj.record.views.dialog.RecordInfoDialog;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2018/5/29.
 */

public class HoleListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        HoleAdapter.OnItemListener, ObsUtils.ObsLinstener, View.OnClickListener {
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
    private ProjectDao projectDao;
    private List<Hole> dataList;
    private List<Hole> newList;
    private HoleDao holeDao;
    private HoleAdapter holeAdapter;
    private int total = 0;//总条数
    private int size = 20;
    private int page = 1;//页数 初始值1
    private List<DropItemVo> listSort;
    private LinearLayoutManager linearLayoutManager;
    private String search = "";
    private ObsUtils obsUtils;
    private int deletePosition;
    private List<Hole> checkList;//关联多次的list
    private List<LocalUser> localUserList;//获取数据的userList
    private RecordDao recordDao;
    private GpsDao gpsDao;
    private Hole uploadHole;
    private MediaDao mediaDao;
    private Dialog chooseDialog;
    private HoleInfoDialog holeInfoDialog;//详情

    @Override
    public int getLayoutId() {
        return R.layout.activity_hole_list;
    }

    @Override
    public void initData() {
        super.initData();
        project = (Project) getIntent().getSerializableExtra(MainActivity.PROJECT);
        dataList = new ArrayList<>();
        newList = new ArrayList<>();
        holeDao = new HoleDao(this);
        projectDao = new ProjectDao(this);
        recordDao = new RecordDao(this);
        gpsDao = new GpsDao(this);
        mediaDao = new MediaDao(this);
        obsUtils = new ObsUtils();
        obsUtils.setObsLinstener(this);
        holeInfoDialog = new HoleInfoDialog();
    }

    @Override
    public void onSubscribe(int type) {
        switch (type) {
            case 1:
                total = holeDao.getHoleListByProjectIDUserDelete(project.getId()).size();
                dataList = getList(project.getId(), page, search);
                break;
            case 2:
                search = holeSearchEt.getText().toString().trim();
                page = 1;
                total = holeDao.getHoleListByProjectIDUserDelete(project.getId()).size();
                dataList = getList(project.getId(), page, search);
                break;
            case 3:
                page++;
                newList.clear();
                newList = getList(project.getId(), page, search);
                break;
            case 4:
                Hole hole = dataList.get(deletePosition);
                if (hole.delete(HoleListActivity.this)) {
                    ToastUtil.showToastL(HoleListActivity.this, "删除勘探点成功");
                } else {
                    ToastUtil.showToastL(HoleListActivity.this, "删除勘探点失败");
                }
                break;
            case 5:
                getUploadData();
                break;
        }
    }

    @Override
    public void onComplete(int type) {
        switch (type) {
            case 1:
                initRecycleView();
                break;
            case 2:
                holeAdapter.refresh(dataList);
                holeAdapter.openFrist();
                refresh.setRefreshing(false);
                break;
            case 3:
                holeAdapter.loadMore(newList);
                break;
            case 4:
                onRefresh();
                break;
            case 5:
                onRefresh();
                break;
        }
    }

    @Override
    public void initView() {
        toolbar.setTitle(project.getFullName());
        toolbar.setSubtitle("勘探点列表");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //排序选择
        sprSort.setAdapter(this, initLayerTypeList());
        sprSort.setText(listSort.get(0).getValue());
        sprSort.setTag(listSort.get(0).getId());
        sprSort.setOnItemClickListener(sortListener);
        obsUtils.execute(1);
    }

    private void initRecycleView() {
        holeAdapter = new HoleAdapter(this, dataList);
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
        listSort.add(new DropItemVo("3", "按上传进度排序"));
//        listSort.add(new DropItemVo("4", "按定位时间优先显示"));
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
        obsUtils.execute(2);
    }

    private void onLoadMore() {
        obsUtils.execute(3);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //单个关联回调
        if (requestCode == MainActivity.GO_LOCAL_CREATE && resultCode == RESULT_OK) {
            Hole relateHole = (Hole) data.getSerializableExtra(MainActivity.HOLE);
            if (relateHole != null) {
                relate(relateHole);
            }
        }
        //关联列表回调
        if (requestCode == MainActivity.GO_RELATE_CREATE && resultCode == RESULT_OK) {
            checkList = (List<Hole>) data.getSerializableExtra(MainActivity.CHECKLIST);
            if (checkList != null && checkList.size() > 0) {
                relateMore(checkList);
            }
            return;
        }
        if (requestCode == MainActivity.GO_DOWNLOAD_CREATE && resultCode == RESULT_OK) {
            localUserList = (List<LocalUser>) data.getSerializableExtra(MainActivity.LOCALUSERLIST);
            if (localUserList != null && localUserList.size() > 0) {
                getData(localUserList);
            }
            return;
        }
        if (resultCode == RESULT_OK) {
            onRefresh();
        }
    }

    private void relate(final Hole relateHole) {
        //检查网络
        if(!haveNet()){
            return;
        }
        showPPW();
        //遍历数据库，查找是否关联
        if (holeDao.checkRelated(relateHole.getId(), project.getId())) {
            ToastUtil.showToastS(this, "该发布点本地已经存在关联");
            dismissPPW();
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("userID", userID);
        map.put("relateID", relateHole.getId());
        map.put("holeID", uploadHole.getId());
        OkGo.<String>post(Urls.DO_RELATE_HOLE)
                .params(map)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        //注意这里已经是在主线程了
                        String data = response.body();//这个就是返回来的结果
                        if (JsonUtils.isGoodJson(data)) {
                            Gson gson = new Gson();
                            Hole h = gson.fromJson(data.toString(), Hole.class);
                            //修改界面,就该hole
                            uploadHole.setRelateCode(h.getCode());
                            uploadHole.setRelateID(h.getId());
                            if (h.getDepth() != null) {
                                uploadHole.setDepth(h.getDepth());
                            }
                            if (h.getElevation() != null) {
                                uploadHole.setElevation(h.getElevation());
                            }
                            if (h.getDescription() != null) {
                                uploadHole.setDescription(h.getDescription());
                            } else {
                                uploadHole.setDescription("");
                            }
                            uploadHole.setState("1");
                            holeDao.add(uploadHole);
                            project.setUpdateTime(DateUtil.date2Str(new Date()) + "");
                            projectDao.update(project);
                            ToastUtil.showToastS(HoleListActivity.this, "勘察点关联成功");
                            onRefresh();
                        } else {
                            ToastUtil.showToastS(HoleListActivity.this, "服务器异常，请联系客服");
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
                        ToastUtil.showToastS(mContext, "网络连接错误");
                    }
                });
    }

    private void relateMore(List<Hole> checkList) {
        //检查网络
        if(!haveNet()){
            return;
        }
        for (Hole relateHole : checkList) {
            //遍历数据库，查找是否关联
            if (holeDao.checkRelated(relateHole.getId(), project.getId())) {
                ToastUtil.showToastS(this, relateHole.getCode() + "勘探点本地已经存在关联");
            } else {
                showPPW();
                //每次都新建一个新的勘察点
                final Hole newHole = new Hole(this, project.getId());
                newHole.setRelateCode(relateHole.getCode());
                newHole.setRelateID(relateHole.getId());
                newHole.setDepth(relateHole.getDepth());
                newHole.setDescription(relateHole.getDescription());
                newHole.setElevation(relateHole.getElevation());
                newHole.setState("1");
                holeDao.add(newHole);
                //三个参数项
                Map<String, String> params = new HashMap<>();
                params.put("userID", userID);
                params.put("relateID", relateHole.getId());
                params.put("holeID", newHole.getId());
                OkGo.<String>post(Urls.DO_RELATE_HOLE).params(params).execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String data = response.body();//这个就是返回来的结果
                        if (JsonUtils.isGoodJson(data)) {
                            onRefresh();
                        } else {
                            holeDao.delete(newHole);
                            ToastUtil.showToastS(HoleListActivity.this, "服务器异常，请联系客服");
                        }
                        dismissPPW();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        holeDao.delete(newHole);
                        dismissPPW();
                        ToastUtil.showToastS(HoleListActivity.this, "网络连接错误");
                    }
                });
            }
        }
    }

    private void getData(List<LocalUser> localUserList) {
        //检查网络
        if(!haveNet()){
            return;
        }
        for (LocalUser localUser : localUserList) {
            showPPW();
            Map<String, String> params = new HashMap<>();
            params.put("holeID", localUser.getId());
            OkGo.<String>post(Urls.DOWNLOAD_RELATE_HOLE).params(params).execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    String data = response.body();//这个就是返回来的结果
                    if (JsonUtils.isGoodJson(data)) {
                        Gson gson = new Gson();
                        Hole hole = gson.fromJson(data, Hole.class);
                        //查询userid是否相同，查询项目下钻孔的relateID是否存在
                        L.e(hole.getRelateID());
                        L.e(project.getId());
                        //判断该项目下是否存在关联的勘探点 projectID、relateID
                        if (holeDao.checkRelated(hole.getRelateID(), project.getId())) {
//                            new AlertDialog.Builder(HoleListActivity.this)
//                                    .setTitle(R.string.hint)
//                                    .setMessage(hole.getRelateCode() + "(" + hole.getCode() + ")关联孔本地已经存在，是否合并到本地")
//                                    .setNegativeButton("合并到本地",
//                                            new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    //根据relateID和项目id找到hole的id
//                                                    hole.setId(holeDao.getIDByRelate(hole.getRelateID(), project.getId()).getId());
//                                                    addOrUpdate(hole, localUser);
//                                                    onRefresh();
//                                                }
//                                            })
//                                    .setPositiveButton("放弃获取",
//                                            new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    onRefresh();
//                                                }
//                                            })
//                                    .setCancelable(false)
//                                    .show();
                            ToastUtil.showToastS(HoleListActivity.this, hole.getRelateCode() + "(" + hole.getCode() + ")关联孔本地已经存在");
                            return;
                        } else {
                            hole.setId(Common.getUUID());
                            addOrUpdate(hole, localUser);
                            onRefresh();
                        }

                    } else {
                        ToastUtil.showToastS(HoleListActivity.this, "服务器异常，请联系客服");
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
                    ToastUtil.showToastS(HoleListActivity.this, "网络连接错误");
                }
            });
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
        hole.setIsDelete("0");
        holeDao.add(hole);
        List<Record> recordList = hole.getRecordList();
        if (recordList != null && recordList.size() > 0) {
            for (Record record : recordList) {
                //同勘探点，要判断是否存在
                if (!localUser.getDeptID().equals(userID)) {
                    record.setId(Common.getUUID());
                }
                record.setProjectID(project.getId());
                record.setHoleID(hole.getId());
                record.setState("1");
                record.setIsDelete("0");
                record.setUpdateId(record.getUpdateId() == null ? "" : record.getUpdateId());//这里有历史记录，不能情况updateID
                recordDao.add(record);
                List<Gps> gpsList = record.getGpsList();
                if (gpsList != null && gpsList.size() > 0) {
                    for (Gps gps : gpsList) {
                        if (!localUser.getDeptID().equals(userID)) {
                            gps.setId(Common.getUUID());
                        }
                        gps.setProjectID(project.getId());
                        gps.setHoleID(hole.getId());
                        gps.setRecordID(record.getId());
                        gpsDao.add(gps);
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
        if (TextUtils.isEmpty(hole.getUserID()) || hole.getUserID().equals(userID)) {
            goEdit(hole);
        } else {
            ToastUtil.showToastS(this, "不可以编辑他人数据");
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
            new AlertDialog.Builder(this)
                    .setTitle(R.string.hint)
                    .setMessage("勘探点未定位,是否编辑勘察点，进行定位操作")
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
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.HOLE, dataList.get(position));
        startActivityForResult(RecordListActivity.class, bundle, MainActivity.HOLE_GO_LIST);
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
            ToastUtil.showToastS(this, "该项目还没有序列号，请返回项目列表，关联该项目");
            return;
        }
        //判断是否是自己的项目
        if (uploadHole.getUserID() != null && !uploadHole.getUserID().equals(userID)) {
            ToastUtil.showToastS(this, "不能上传别人的项目");
            return;
        }
        //判断是否定位
        if (TextUtils.isEmpty(uploadHole.getMapLatitude()) || TextUtils.isEmpty(uploadHole.getMapLongitude())) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.hint)
                    .setMessage("勘探点未定位,是否编辑勘察点，进行定位操作")
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
        //判断是否关联勘探点
        if (TextUtils.isEmpty(uploadHole.getRelateID())) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.hint)
                    .setMessage("勘探点未关联,是否获取勘探点列表，进行关联操作")
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
        //检查网络
        if(!haveNet()){
            return;
        }
        if (uploadHole.getNotUploadCount() > 0) {
            //上传操作
            obsUtils.execute(5);
        }
    }



    private void getUploadData() {
        showPPW();
        String strParams = "/" + project.getSerialNumber();
        final Map<String, String> map = new ConcurrentHashMap<>();
        map.putAll(uploadHole.getNameValuePairMap(project.getSerialNumber()));
        //获取record
        final List<Record> recordList = recordDao.getNotUploadListByHoleID(uploadHole.getId());
        List<Gps> resultGpsList = new LinkedList<>();
        if (recordList != null && recordList.size() > 0) {
            map.putAll(Record.getMap(recordList, project.getSerialNumber()));
            //获取gps
            for (Record record : recordList) {
                List<Gps> gpsList = gpsDao.getListGpsByRecord(record.getId());
                if (gpsList != null && gpsList.size() > 0) {
                    resultGpsList.addAll(gpsList);
                }
            }
        }
        //获取media,先确定媒体文件存在，不存在删除media数据
        final List<Media> mediaList = mediaDao.getNotUploadListByHoleID(uploadHole.getId());
        List<Media> realMediaList = new ArrayList<>();
        if (mediaList != null && mediaList.size() > 0) {
            for (Media media : mediaList) {
                File file = new File(media.getLocalPath());
                String localPaht;//文件完整路径
                if (file.isDirectory()) {
                    localPaht = Common.getVideoByDir(media.getLocalPath());
                } else {
                    localPaht = media.getLocalPath();
                }
                //判断媒体文件是否存在
                File f = new File(localPaht);
                if (f.exists()) {
                    //添加媒体参数
                    realMediaList.add(media);
                    //添加对应媒体gps
                    Gps gps = gpsDao.getGpsByMedia(media.getId());
                    resultGpsList.add(gps);
                } else {
                    media.delete(this);
                }
            }
            if (realMediaList.size() > 0) {
                map.putAll(Media.getMap(realMediaList, project.getSerialNumber()));
                strParams += "-file";
            } else {
                strParams += "-noFile";
            }
        }
        //所有的gps数据
        if (resultGpsList.size() > 0) {
            map.putAll(Gps.getMap(resultGpsList, project.getSerialNumber()));
        }
        dismissPPW();
        upload(map, recordList, realMediaList, strParams);
    }

    private void upload(Map<String, String> map, final List<Record> recordList, final List<Media> realMediaList, String strParams) {
        //构建请求
        PostRequest request = OkGo.<String>post(Urls.UPLOAD_HOLE_NEW + strParams);
        request.params(map);
        if (realMediaList.size() > 0) {
            for (Media media : realMediaList) {
                File file = new File(media.getLocalPath());
                String suffix;
                String localPaht;
                if (file.isDirectory()) {
                    localPaht = Common.getVideoByDir(media.getLocalPath());
                    suffix = ".mp4";
                } else {
                    localPaht = media.getLocalPath();
                    suffix = ".jpg";
                }
                request.params("file_upload", new File(localPaht), media.getId() + suffix);
            }
        }
        final ProgressDialog pdialog = new ProgressDialog(HoleListActivity.this, 0);
        // 设置对话框是否可以取消
        pdialog.setCancelable(true);
        pdialog.setMax(100);
        pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pdialog.show();
        request.execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String data = response.body();
                if (JsonUtils.isGoodJson(data)) {
                    Gson gson = new Gson();
                    JsonResult jsonResult = gson.fromJson(data.toString(), JsonResult.class);
                    if (jsonResult.getStatus()) {
                        //修改状态
                        uploadHole.setState("2");
                        holeDao.update(uploadHole);
                        if (recordList != null && recordList.size() > 0) {
                            for (Record record : recordList) {
                                record.setState("2");
                                recordDao.update(record);
                            }
                        }
                        if (realMediaList != null && realMediaList.size() > 0) {
                            for (Media media : realMediaList) {
                                media.setState("2");
                                mediaDao.update(media);
                            }
                        }
                        onRefresh();
                    } else {
                        ToastUtil.showToastS(HoleListActivity.this, jsonResult.getMessage());
                    }
                } else {
                    ToastUtil.showToastS(HoleListActivity.this, "服务器异常，请联系客服");
                }
            }

            @Override
            public void uploadProgress(Progress progress) {
                super.uploadProgress(progress);
                pdialog.setProgress((int) Math.abs(progress.fraction * 100));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pdialog.dismiss();
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                ToastUtil.showToastS(mContext, "网络连接错误");
            }
        });
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
                                deletePosition = position;
                                obsUtils.execute(4);
                            }
                        })
                .setPositiveButton(R.string.record_camera_cancel_dialog_no, null)
                .setCancelable(false)
                .show();
    }

    private List<Hole> getList(String projectID, int page, String code) {
        List<Hole> list = new ArrayList<Hole>();
        DBHelper dbHelper = DBHelper.getInstance(this);
        try {
            Dao<Hole, String> dao = dbHelper.getDao(Hole.class);
            //钻孔的记录数量要 排除机长等基本信息和原始记录信息、机长等信息type不同，原始记录update不为空
            String recordCount = "select count(id) from record r where state <> '0' and r.holeID=h.id and r.type<>'机长' and r.type<>'钻机'and r.type<>'描述员'and r.type<>'场景'and r.type<>'负责人'and r.type<>'工程师'and r.type<>'提钻录像' and r.updateID=''";
            String currentDepth = "select max(r1.endDepth) from record r1 where state <> '0' and updateID='' and r1.holeID=h.id";
            String uploadCountNew = "(select count(id) as uploadedCount from record r2 where state='2'and r2.holeID=h.id) " +
                    "+ (select count(id) as uploadedCount from media m where state='2'and m.holeID=h.id) " +
                    "+ (case h.state when '2' then 1 else 0 end)";
            String notUploadCountNew = "(select count(id) as notUploadCount from record r2 where state='1'and r2.holeID=h.id) " +
                    "+ (select count(id) as notUploadCount from media m where state='1'and m.holeID=h.id) " +
                    "+ (case h.state when '1' then 1 else 0 end)";
            //模糊查询
            String like = "";
            if (!TextUtils.isEmpty(code)) {
                like = "and h.code LIKE'%" + code + "%' ";
            }
            //排序
            String sequence = "h.updateTime desc ";
            if ("1".equals(sprSort.getTag())) {//时间
                sequence = "h.updateTime desc ";
            } else if ("2".equals(sprSort.getTag())) {//名字
                sequence = "h.code asc ";
            } else if ("3".equals(sprSort.getTag())) {//上传
                sequence = "round((uploadedCount/((uploadedCount+notUploadCount)*1.0)),3) desc ";
            } else if ("4".equals(sprSort.getTag())) {//定位
                sequence = "h.mapTime desc,h.updateTime desc ";
            } else if ("5".equals(sprSort.getTag())) {//关联
                sequence = "h.relateID desc,h.updateTime desc ";
            } else if ("6".equals(sprSort.getTag())) {
                sequence = "length(h.relateCode) asc,h.relateCode asc ";
            }
            //用于分页加载的sql 这里的index-1 第二页的时候是跳过一个size  --
            String pageSql = String.format("limit (" + size + ") offset " + size * (page - 1));//size:每页显示条数，index页码
            String sql = "select h.id,h.code,h.type,h.state,h.updateTime,h.mapLatitude,h.mapLongitude,h.mapPic," +
                    "(" + recordCount + ")as recordsCount," +
                    "(" + uploadCountNew + ") as uploadedCount," +
                    "(" + notUploadCountNew + ") as notUploadCount,h.projectID ," +
                    "(" + currentDepth + ")  as currentDepth," +
                    "h.locationState,h.relateID,h.userID,h.relateCode,h.mapTime,h.createTime,h.radius,h.elevation,h.depth " +
                    "from hole h " +
                    "where h.projectID='" + projectID + "' " + like + " order by " + sequence + pageSql;

            GenericRawResults<Hole> results = dao.queryRaw(sql, new RawRowMapper<Hole>() {
                @Override
                public Hole mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                    Hole hole = new Hole();
                    hole.setId(resultColumns[0]);
                    hole.setCode(resultColumns[1]);
                    hole.setType(resultColumns[2]);
                    hole.setState(resultColumns[3]);
                    hole.setUpdateTime(resultColumns[4]);
                    hole.setMapLatitude(resultColumns[5]);
                    hole.setMapLongitude(resultColumns[6]);
                    hole.setMapPic(resultColumns[7]);
                    hole.setRecordsCount(resultColumns[8]);
                    hole.setUploadedCount(Integer.valueOf(resultColumns[9]));
                    hole.setNotUploadCount(Integer.valueOf(resultColumns[10]));
                    hole.setProjectID(resultColumns[11]);
                    hole.setCurrentDepth(resultColumns[12]);
                    hole.setLocationState(resultColumns[13]);
                    hole.setRelateID(resultColumns[14]);
                    hole.setUserID(resultColumns[15]);
                    hole.setRelateCode(resultColumns[16]);
                    hole.setMapTime(resultColumns[17]);
                    hole.setCreateTime(resultColumns[18]);
                    hole.setRadius(resultColumns[19]);
                    hole.setElevation(resultColumns[20]);
                    hole.setDepth(resultColumns[21]);
                    return hole;
                }
            });

            Iterator<Hole> iterator = results.iterator();
            while (iterator.hasNext()) {
                Hole hole = iterator.next();
                list.add(hole);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    @OnClick(R.id.hole_search_iv)
    public void onViewClicked() {
        if (!TextUtils.isEmpty(holeSearchEt.getText().toString().trim())) {
            onRefresh();
        }
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
                    ToastUtil.showToastS(this, "所在项目未关联");
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
                    ToastUtil.showToastS(this, "所在项目未关联");
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
}
