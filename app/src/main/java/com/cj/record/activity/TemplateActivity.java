package com.cj.record.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.adapter.TemplateAdapter;
import com.cj.record.baen.JsonResult;
import com.cj.record.baen.Template;
import com.cj.record.baen.TemplateDetail;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.Urls;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/7/9.
 */

public class TemplateActivity extends BaseActivity implements TemplateAdapter.OnItemListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.prompt)
    TextView prompt;
    @BindView(R.id.template_recycler)
    RecyclerView templateRecycler;

    private TemplateAdapter templateAdapter;
    private List<Template> templateList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_template;
    }

    @Override
    public void initView() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_clear_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_template, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.act_down:
                getDateByNet();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDateByNet() {
        showPPW();
        Map<String, String> map = new HashMap<>();
        map.put("userID", userID);
        OkGo.<String>post(Urls.TEMPLATE_DOWNLOAD)
                .params(map)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        //注意这里已经是在主线程了
                        String data = response.body();//这个就是返回来的结果
                        Gson gson = new Gson();
                        JsonResult jsonResult = gson.fromJson(data, JsonResult.class);
                        if (jsonResult.getStatus()) {
                            templateList = gson.fromJson(jsonResult.getResult(), new TypeToken<List<Template>>() {
                            }.getType());
                            if (templateList != null && templateList.size() > 0) {
                                initRecycleView();
                            } else {
                                ToastUtil.showToastS(TemplateActivity.this, "服务端未创建模板");
                            }
                        } else {
                            ToastUtil.showToastS(TemplateActivity.this, jsonResult.getMessage());
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

    private void initRecycleView() {
        templateRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        templateAdapter = new TemplateAdapter(mContext, templateList);
        templateRecycler.setNestedScrollingEnabled(false);
        templateRecycler.setAdapter(templateAdapter);
        templateAdapter.setOnItemListener(this);
    }

    @Override
    public void onItemClick(int position) {
        ToastUtil.showToastS(this, position + "");
    }
}
