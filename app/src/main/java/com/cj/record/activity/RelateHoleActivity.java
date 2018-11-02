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

import com.alibaba.idst.nls.internal.utils.L;
import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.adapter.RelateHoleAdapter;
import com.cj.record.baen.Hole;
import com.cj.record.baen.JsonResult;
import com.cj.record.baen.LocalUser;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.Urls;
import com.cj.record.views.MaterialEditTextNoEmoji;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/6/12.
 */

public class RelateHoleActivity extends BaseActivity {
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
    private String path;

    @Override
    public int getLayoutId() {
        return R.layout.activity_relate_hole;
    }

    @Override
    public void initData() {
        super.initData();
        serialNumber = (String) getIntent().getExtras().get(MainActivity.SERIALNUMBER);
        relateType = (int) getIntent().getExtras().get(MainActivity.RELATE_TYPE);
        checkList = new ArrayList<>();
        relateList = new ArrayList<>();
        localUserList = new ArrayList<>();
        holeList = new ArrayList<>();
    }

    @Override
    public void initView() {
        switch (relateType) {
            case 1:
                path = Urls.GET_RELATE_HOLE;
                toolbar.setTitle("选择关联勘探点");
                break;
            case 2:
                path = Urls.GET_RELATE_HOLE;
                toolbar.setTitle("选择关联勘探点");
                break;
            case 3:
                path = Urls.GET_RELATE_HOLEWITHRECORD;
                toolbar.setTitle("选择获取数据");
                break;
        }
        relateHoleSearch.addTextChangedListener(textWatcher);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_clear_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        getRelateList();
    }

    private void getRelateList() {
        //检查网络
        if(!haveNet()){
            return;
        }
        if(TextUtils.isEmpty(userID)){
            ToastUtil.showToastS(mContext, "用户信息丢失，请尝试重新登陆");
            return;
        }
        showPPW();
        Map<String, String> map = new HashMap<>();
        map.put("serialNumber", serialNumber);
        map.put("userID", userID);
        OkGo.<String>post(path)
                .params(map)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        //注意这里已经是在主线程了
                        String data = response.body();//这个就是返回来的结果
                        Gson gson = new Gson();
                        JsonResult jsonResult = gson.fromJson(data, JsonResult.class);
                        if (jsonResult.getStatus()) {
                            relateList.addAll(gson.fromJson(jsonResult.getResult(), new TypeToken<List<Hole>>() {}.getType()));
                            if (relateList != null && relateList.size() > 0) {
                                sort();
                                holeList.clear();
                                holeList.addAll(relateList);
                                initRecycleView();
                            } else {
                                ToastUtil.showToastS(RelateHoleActivity.this, "服务端未创建勘察点，无法关联");
                            }
                        } else {
                            ToastUtil.showToastS(RelateHoleActivity.this, jsonResult.getMessage());
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
    }

    private void initRecycleView() {
        relateHoleRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        relateHoleAdapter = new RelateHoleAdapter(mContext, relateList, relateType);
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
                    relateHoleRecycler.setLayoutManager(new LinearLayoutManager(mContext));
                    relateHoleAdapter = new RelateHoleAdapter(mContext, holeList, relateType);
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
                relateHoleRecycler.setLayoutManager(new LinearLayoutManager(mContext));
                relateHoleAdapter = new RelateHoleAdapter(mContext, holeList, relateType);
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

}
