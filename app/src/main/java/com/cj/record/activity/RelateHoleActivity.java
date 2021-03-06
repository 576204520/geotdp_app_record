package com.cj.record.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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
import android.widget.EditText;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.mvp.base.App;
import com.cj.record.adapter.RelateHoleAdapter;
import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.Hole;
import com.cj.record.baen.LocalUser;
import com.cj.record.baen.PageBean;
import com.cj.record.baen.Record;
import com.cj.record.mvp.base.BaseMvpActivity;
import com.cj.record.mvp.contract.HoleContract;
import com.cj.record.mvp.presenter.HolePresenter;
import com.cj.record.utils.Common;
import com.cj.record.utils.JsonUtils;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.UpdateUtil;
import com.cj.record.utils.Urls;
import com.cj.record.views.ProgressDialog;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/6/12.
 */

public class RelateHoleActivity extends BaseMvpActivity<HolePresenter> implements HoleContract.View {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.relate_hole_search)
    EditText relateHoleSearch;
    @BindView(R.id.relate_hole_recycler)
    RecyclerView relateHoleRecycler;
    @BindView(R.id.prompt)
    TextView prompt;
    private String serialNumber;
    private int relateType;
    private List<Hole> relateList;//获取的勘察点的列表
    private RelateHoleAdapter relateHoleAdapter;
    private List<Hole> checkList;
    private List<LocalUser> localUserList;
    private List<Hole> holeList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_relate_hole;
    }


    @Override
    public void initView() {
        mPresenter = new HolePresenter();
        mPresenter.attachView(this);
        serialNumber = (String) getIntent().getExtras().get(MainActivity.SERIALNUMBER);
        relateType = (int) getIntent().getExtras().get(MainActivity.RELATE_TYPE);
        checkList = new ArrayList<>();
        relateList = new ArrayList<>();
        localUserList = new ArrayList<>();
        holeList = new ArrayList<>();
        switch (relateType) {
            case 1:
                toolbar.setTitle(R.string.hole_relate_title1);
                mPresenter.getRelateList((String) SPUtils.get(this, Urls.SPKey.USER_ID, ""), serialNumber, UpdateUtil.getVerCode(this) + "");
                break;
            case 2:
                toolbar.setTitle(R.string.hole_relate_title2);
                mPresenter.getRelateList((String) SPUtils.get(this, Urls.SPKey.USER_ID, ""), serialNumber, UpdateUtil.getVerCode(this) + "");
                break;
            case 3:
                toolbar.setTitle(R.string.hole_relate_title3);
                mPresenter.getDownLoadList((String) SPUtils.get(this, Urls.SPKey.USER_ID, ""), serialNumber, UpdateUtil.getVerCode(this) + "");
                break;
        }
        relateHoleSearch.addTextChangedListener(textWatcher);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_clear_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        initRecycleView();
    }

    //根据每条hole的userCount进行排序
    private void sort() {
        for (Hole hole : relateList) {
            if (hole.getUserList() == null || hole.getUserList().size() == 0) {
                hole.setUserCount(0);
            } else {
                hole.setUserCount(hole.getUserList().size());
            }
        }
        Collections.sort(relateList, new Comparator<Hole>() {
            @Override
            public int compare(Hole o1, Hole o2) {
                int i = o1.getUserCount() - o2.getUserCount();
                if (i == 0) {
                    if (o1.getCode() != null && o2.getCode() != null) {
                        return o1.getCode().compareTo(o2.getCode());
                    }
                }
                return i;
            }
        });
        Iterator<Hole> it = relateList.iterator();
        while (it.hasNext()) {
            Hole hole = it.next();
            if (!TextUtils.isEmpty(hole.getCheckStatus())) {
                if (hole.getCheckStatus().equals("1") || hole.getCheckStatus().equals("3")) {
                    it.remove();
                }
            }
        }
    }

    private void initRecycleView() {
        relateHoleRecycler.setLayoutManager(new LinearLayoutManager(this));
        relateHoleAdapter = new RelateHoleAdapter(this, relateList, relateType);
        relateHoleRecycler.setNestedScrollingEnabled(false);
        relateHoleRecycler.setAdapter(relateHoleAdapter);
        relateHoleAdapter.setOnItemListener(onItemListener);

    }

    RelateHoleAdapter.OnItemListener onItemListener = new RelateHoleAdapter.OnItemListener() {
        @Override
        public void onItemClick(int position) {
            //hole编辑界面才需要这个动作
            if (relateType == RelateHoleAdapter.HAVE_NOALL) {
                showDialog(holeList.get(position));
            } else {
                addOrRemove(position);
            }
        }

        @Override
        public void checkBoxClick(int position) {
            addOrRemove(position);
        }
    };

    private void showDialog(Hole hole) {
        int num = 0;
        if (hole.getUserList() != null && hole.getUserList().size() > 0) {
            num = hole.getUserList().size();
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.hint)
                .setMessage("确定选择关联(" + hole.getCode() + ")勘探点吗？当前该点已有" + num + "人关联")
                .setNegativeButton(R.string.agree,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(MainActivity.HOLE, hole);
                                intent.putExtras(bundle);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        })
                .setPositiveButton(R.string.disagree, null)
                .setCancelable(false)
                .show();
    }

    private void addOrRemove(int position) {
        if (checkList.contains(relateHoleAdapter.getItem(position))) {
            checkList.remove(relateHoleAdapter.getItem(position));
        } else {
            checkList.add(relateHoleAdapter.getItem(position));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_relate_hole, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.act_save:
                //判断如果是关联创建，返回checkList，如果是获取数据，返回localUserList
                switch (relateType) {
                    case 1:
                        ToastUtil.showToastS(this, "请选择勘探点进行关联");
                        break;
                    case 2:
                        if (checkList.size() > 0) {
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(MainActivity.CHECKLIST, (Serializable) checkList);
                            intent.putExtras(bundle);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            ToastUtil.showToastS(this, "请勾选需要关联的勘探点");
                        }
                        break;
                    case 3:
                        if (relateHoleAdapter.getLocalUserList().size() > 0) {
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(MainActivity.LOCALUSERLIST, (Serializable) relateHoleAdapter.getLocalUserList());
                            intent.putExtras(bundle);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            ToastUtil.showToastS(this, "请勾选需要获取数据的对应描述员");
                        }
                        break;
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            String str = relateHoleSearch.getText().toString();
            if (!TextUtils.isEmpty(str)) {
                holeList.clear();
                holeList.addAll(search(relateList, str));
                if (holeList.size() > 0) {
                    relateHoleRecycler.setVisibility(View.VISIBLE);
                    prompt.setVisibility(View.GONE);
                    relateHoleRecycler.setLayoutManager(new LinearLayoutManager(RelateHoleActivity.this));
                    relateHoleAdapter = new RelateHoleAdapter(RelateHoleActivity.this, holeList, relateType);
                    relateHoleRecycler.setNestedScrollingEnabled(false);
                    relateHoleRecycler.setAdapter(relateHoleAdapter);
                    relateHoleAdapter.setOnItemListener(onItemListener);
                } else {
                    relateHoleRecycler.setVisibility(View.GONE);
                    prompt.setVisibility(View.VISIBLE);
                }
            } else {
                holeList.clear();
                holeList.addAll(relateList);
                relateHoleRecycler.setVisibility(View.VISIBLE);
                prompt.setVisibility(View.GONE);
                relateHoleRecycler.setLayoutManager(new LinearLayoutManager(RelateHoleActivity.this));
                relateHoleAdapter = new RelateHoleAdapter(RelateHoleActivity.this, holeList, relateType);
                relateHoleRecycler.setNestedScrollingEnabled(false);
                relateHoleRecycler.setAdapter(relateHoleAdapter);
                relateHoleAdapter.setOnItemListener(onItemListener);
            }
        }
    };

    /**
     * 筛选list
     *
     * @param list 要筛选的list
     * @param key  筛选的key
     * @param <T>
     * @return
     */
    private <T> List<T> search(List<T> list, String key) {
        //如果查询的值不是空的就走进来然后返回搜索后的值，否则返回原本的值
        if (list != null && list.size() > 0) {
            //new一个新的容器
            List<T> area = new ArrayList<>();
            boolean isok;
            //循环olist集合
            for (T t : list) {
                //判断a里面如果包含了搜索的值，有就添加，没有否则就不添加(会查出属性名)
                //if (t.toString().toUpperCase().indexOf(str) != -1)
                //area.add(t);
                isok = false;
                //遍历实体类，获取属性名和属性值
                for (Field field : t.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    //下面是指定要查的属性
                    //Item_FirstLetter Item_Name  Item_Col1
                    switch (field.getName()) {
                        case "code":
                            try {
                                isok = field.get(t).toString().toLowerCase().contains(key.toLowerCase());
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    if (isok)
                        break;
                }
                if (isok)
                    area.add(t);
            }
            return area;
        } else {
            return list;
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

    }

    @Override
    public void onSuccessDelete() {

    }

    @Override
    public void onSuccessList(PageBean<Hole> pageBean) {

    }

    @Override
    public void onSuccessRelate(BaseObjectBean<String> bean) {

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
        if (bean.isStatus()) {
            relateList.addAll(JsonUtils.getInstance().fromJson(bean.getResult(), new TypeToken<List<Hole>>() {
            }.getType()));
            if (relateList != null && relateList.size() > 0) {
                sort();
                holeList.clear();
                holeList.addAll(relateList);
                relateHoleAdapter.notifyDataSetChanged();
            } else {
                ToastUtil.showToastS(RelateHoleActivity.this, getString(R.string.hole_relate_no_data));
            }
        } else {
            ToastUtil.showToastS(RelateHoleActivity.this, bean.getMessage());
            Common.showMessage(this, bean.getMessage());
        }
    }

    @Override
    public void onSuccessDownloadList(BaseObjectBean<String> bean) {
        if (bean.isStatus()) {
            relateList.addAll(JsonUtils.getInstance().fromJson(bean.getResult(), new TypeToken<List<Hole>>() {
            }.getType()));
            if (relateList != null && relateList.size() > 0) {
                sort();
                holeList.clear();
                holeList.addAll(relateList);
                relateHoleAdapter.notifyDataSetChanged();
            } else {
                Common.showMessage(this, getString(R.string.hole_relate_no_data_download));
            }
        } else {
            Common.showMessage(this, bean.getMessage());
        }
    }

    @Override
    public void onSuccessCheckUser(BaseObjectBean<String> bean) {

    }

    @Override
    public void onSuccessGetScene(List<Record> recordList) {

    }

    @Override
    public void onSuccessUpload(BaseObjectBean<Integer> bean) {

    }


}
