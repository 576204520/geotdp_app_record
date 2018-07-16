package com.cj.record.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cj.record.R;
import com.cj.record.activity.HoleListActivity;
import com.cj.record.activity.MainActivity;
import com.cj.record.activity.ProjectEditActiity;
import com.cj.record.activity.ReleteLocationActivity;
import com.cj.record.activity.base.BaseFragment;
import com.cj.record.adapter.ProjectAdapter;
import com.cj.record.adapter.SpacesItemDecoration;
import com.cj.record.baen.Project;
import com.cj.record.db.ProjectDao;
import com.cj.record.utils.ObsUtils;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.Urls;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Administrator on 2018/5/24.
 */

public class ProjectListFragment extends BaseFragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, ProjectAdapter.OnItemListener, ObsUtils.ObsLinstener {
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refresh;
    @BindView(R.id.project_search_iv)
    ImageView projectSearchIv;
    @BindView(R.id.project_search_et)
    EditText projectSearchEt;

    private ProjectDao projectDao;
    private ProjectAdapter projectAdapter;
    private List<Project> dataList;
    private int page = 0;
    private int size = 20;
    private int total = 0;
    private String userID;
    private Dialog dialog;
    private int longClickPosition;
    private LinearLayoutManager linearLayoutManager;
    private ObsUtils obsUtils;
    private List<Project> newList;
    private int deletePosition;
    private String search = "";

    @Override
    public int getLayoutId() {
        return R.layout.fragment_project_list;
    }

    @Override
    public void initData() {
        super.initData();
        userID = (String) SPUtils.get(mActivity, Urls.SPKey.USER_ID, "");
        projectDao = new ProjectDao(mActivity);
        dataList = new ArrayList<>();
        newList = new ArrayList<>();
        obsUtils = new ObsUtils();
        obsUtils.setObsLinstener(this);
    }

    @Override
    public void initView() {
        obsUtils.execute(1);
    }

    private void initRecycleView() {
        projectAdapter = new ProjectAdapter(mActivity, dataList);
        projectAdapter.setOnItemListener(this);
        refresh.setOnRefreshListener(this);
        recycler.addItemDecoration(new SpacesItemDecoration(6));
        linearLayoutManager = new LinearLayoutManager(mActivity);
        recycler.setLayoutManager(linearLayoutManager);
        //addOnScrollListener到不到效果不知为什么
        recycler.addOnScrollListener(onScrollListener);
        recycler.setAdapter(projectAdapter);
        //默认打开第一条数据的按钮
        projectAdapter.openFrist();
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
    public void onRefresh() {
        obsUtils.execute(2);
    }

    public void onLoadMore() {
        obsUtils.execute(3);
    }


    @Override
    public void onSubscribe(int type) {
        switch (type) {
            case 1:
                total = projectDao.getAllCount(userID);
                dataList = projectDao.getAll(userID, page, size, search);
                break;
            case 2:
                search = projectSearchEt.getText().toString().trim();
                page = 0;
                total = projectDao.getAllCount(userID);
                dataList = projectDao.getAll(userID, page, size, search);
                break;
            case 3:
                page++;
                newList.clear();
                newList = projectDao.getAll(userID, page, size, search);
                break;
            case 4:
                showPPW();
                projectDao.delete(dataList.get(deletePosition));
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
                projectAdapter.refresh(dataList);
                projectAdapter.openFrist();
                refresh.setRefreshing(false);
                break;
            case 3:
                projectAdapter.loadMore(newList);
                break;
            case 4:
                dismissPPW();
                onRefresh();
                break;
        }
    }

    @Override
    public void detailClick(int position) {

    }

    @Override
    public void editClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(MainActivity.FROMTYPE, true);
        bundle.putSerializable(MainActivity.PROJECT, dataList.get(position));
        startActivityForResult(ProjectEditActiity.class, bundle, MainActivity.PROJECT_GO_EDIT);
    }

    @Override
    public void holeListClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(MainActivity.PROJECT, dataList.get(position));
        startActivityForResult(HoleListActivity.class, bundle, MainActivity.PROJECT_GO_LIST);
    }

    @Override
    public void navClick(int position) {
        String sn = dataList.get(position).getSerialNumber();
        if (!TextUtils.isEmpty(sn)) {
            Bundle bundle = new Bundle();
            bundle.putString(MainActivity.SN, sn);
            startActivity(ReleteLocationActivity.class, bundle);
        } else {
            ToastUtil.showToastS(mActivity, "该项目未关联，请先关联项目信息");
        }
    }

    @Override
    public void uploadClick(int position) {

    }

    @Override
    public void deleteClick(int position) {
        deleteDialog(position);
    }

    private void deleteDialog(final int position) {
        final Project project = dataList.get(position);
        int con = project.getNotUploadCount() == 0 ? R.string.confirmDelete : R.string.confirmDelete3;
        new AlertDialog.Builder(mActivity)
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            onRefresh();
        }
    }

    private void showDialog() {
        View view = mActivity.getLayoutInflater().inflate(R.layout.project_list_choose, null);
        Button butDetail = (Button) view.findViewById(R.id.but_detail);
        Button butNav = (Button) view.findViewById(R.id.but_nav);
        Button butDelete = (Button) view.findViewById(R.id.but_delete);
        Button butCancel = (Button) view.findViewById(R.id.but_cancel);
        butDetail.setOnClickListener(this);
        butNav.setOnClickListener(this);
        butDelete.setOnClickListener(this);
        butCancel.setOnClickListener(this);
        dialog = new Dialog(mActivity, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        if (window != null) {
            window.setWindowAnimations(R.style.main_menu_animstyle);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0;
            wl.y = mActivity.getWindowManager().getDefaultDisplay().getHeight();
            // 以下这两句是为了保证按钮可以水平满屏
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            // 设置显示位置
            dialog.onWindowAttributesChanged(wl);
        }
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.but_detail:
                dialog.dismiss();
                break;
            case R.id.but_nav:
                dialog.dismiss();
                break;
            case R.id.but_delete:
                dialog.dismiss();
                break;
            case R.id.but_cancel:
                dialog.dismiss();
                break;
        }
    }

    @OnClick(R.id.project_search_iv)
    public void onViewClicked() {
        if (!TextUtils.isEmpty(projectSearchEt.getText().toString().trim())) {
            onRefresh();
        }
    }
}
