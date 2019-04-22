package com.cj.record.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.cj.record.R;
import com.cj.record.activity.HoleListActivity;
import com.cj.record.activity.MainActivity;
import com.cj.record.activity.ProjectEditActiity;
import com.cj.record.activity.ReleteLocationActivity;
import com.cj.record.base.App;
import com.cj.record.adapter.ProjectAdapter;
import com.cj.record.adapter.SpacesItemDecoration;
import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.PageBean;
import com.cj.record.baen.Project;
import com.cj.record.base.BaseMvpFragment;
import com.cj.record.contract.ProjectContract;
import com.cj.record.presenter.ProjectPresenter;
import com.cj.record.utils.Common;
import com.cj.record.utils.ToastUtil;
import com.cj.record.views.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Administrator on 2018/5/24.
 */

public class ProjectListFragment extends BaseMvpFragment<ProjectPresenter> implements ProjectContract.View, SwipeRefreshLayout.OnRefreshListener, ProjectAdapter.OnItemListener {
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refresh;
    @BindView(R.id.project_search_iv)
    ImageView projectSearchIv;
    @BindView(R.id.project_search_et)
    EditText projectSearchEt;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ProjectAdapter projectAdapter;
    private List<Project> dataList;
    private int page = 0;
    private int size = 20;
    private int total = 0;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_project_list;
    }

    @Override
    protected void initView(View view) {
        mPresenter = new ProjectPresenter();
        mPresenter.attachView(this);
        dataList = new ArrayList<>();
        projectSearchEt.addTextChangedListener(textWatcher);
        initRecycleView();
        onRefresh();
    }

    @Override
    public void inCreate() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.act_add:
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(MainActivity.FROMTYPE, false);
                    startActivityForResult(ProjectEditActiity.class, bundle, MainActivity.PROJECT_GO_EDIT);
                    return true;
            }
            return true;
        }
    };


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
        page = 0;
        mPresenter.loadData(App.userID, page, size, projectSearchEt.getText().toString().trim());
    }

    public void onLoadMore() {
        page++;
        mPresenter.loadData(App.userID, page, size, projectSearchEt.getText().toString().trim());
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
            Common.showMessage(mActivity, getString(R.string.project_nav_norelate));
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
                                mPresenter.delete(mActivity, dataList.get(position));
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

    @Override
    public void showLoading() {
        ProgressDialog.getInstance().show(mActivity);
    }

    @Override
    public void hideLoading() {
        ProgressDialog.getInstance().dismiss();
    }

    @Override
    public void onError(Throwable throwable) {
        refresh.setRefreshing(false);
        ToastUtil.showToastS(mActivity, throwable.toString());
    }

    @Override
    public void onSuccessAddOrUpdate() {

    }

    @Override
    public void onSuccessDelete() {
        onRefresh();
    }

    @Override
    public void onSuccessList(PageBean<Project> pageBean) {
        total = pageBean.getTotleSize();
        if (page == 0) {
            dataList.clear();
            dataList.addAll(pageBean.getList());
            projectAdapter.notifyDataSetChanged();
            projectAdapter.openFrist();
            refresh.setRefreshing(false);
        } else if (page > 0) {
            dataList.addAll(pageBean.getList());
            projectAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSuccessRelate(BaseObjectBean<String> bean) {

    }
}
