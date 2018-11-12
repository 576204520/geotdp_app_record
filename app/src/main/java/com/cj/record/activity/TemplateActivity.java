package com.cj.record.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.adapter.TemplateAdapter;
import com.cj.record.baen.JsonResult;
import com.cj.record.baen.Record;
import com.cj.record.baen.Template;
import com.cj.record.baen.TemplateDetail;
import com.cj.record.db.TemplateDao;
import com.cj.record.db.TemplateDetialDao;
import com.cj.record.utils.Common;
import com.cj.record.utils.JsonUtils;
import com.cj.record.utils.L;
import com.cj.record.utils.ObsUtils;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.UpdateUtil;
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

public class TemplateActivity extends BaseActivity implements TemplateAdapter.OnItemListener, ObsUtils.ObsLinstener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.prompt)
    TextView prompt;
    @BindView(R.id.template_recycler)
    RecyclerView templateRecycler;

    private TemplateAdapter templateAdapter;
    private List<Template> templateList;
    private ObsUtils obsUtils;
    private TemplateDao templateDao;
    private TemplateDetialDao templateDetialDao;
    private Record record;
    private Template deleteTemplate;

    @Override
    public int getLayoutId() {
        return R.layout.activity_template;
    }

    @Override
    public void initView() {
        toolbar.setTitle("模板");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_clear_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        templateDao = new TemplateDao(this);
        templateDetialDao = new TemplateDetialDao(this);
        obsUtils = new ObsUtils();
        obsUtils.setObsLinstener(this);
        obsUtils.execute(1);
        //recordEdit进入有值，setting进入没有
        if (null != getIntent().getExtras()) {
            record = (Record) getIntent().getExtras().getSerializable(MainActivity.RECORD);
        }
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
        if(TextUtils.isEmpty(userID)){
            ToastUtil.showToastS(mContext, "用户信息丢失，请尝试重新登陆");
            return;
        }
        showPPW();
        Map<String, String> map = new HashMap<>();
        map.put("userID", userID);
        map.put("verCode", UpdateUtil.getVerCode(this) + "");
        OkGo.<String>post(Urls.TEMPLATE_DOWNLOAD)
                .params(map)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        //注意这里已经是在主线程了
                        String data = response.body();//这个就是返回来的结果
                        if (JsonUtils.isGoodJson(data)) {
                            Gson gson = new Gson();
                            JsonResult jsonResult = gson.fromJson(data, JsonResult.class);
                            if (jsonResult.getStatus()) {
                                templateList = gson.fromJson(jsonResult.getResult(), new TypeToken<List<Template>>() {
                                }.getType());
                                if (templateList != null && templateList.size() > 0) {
                                    obsUtils.execute(2);
                                } else {
                                    ToastUtil.showToastS(TemplateActivity.this, "服务端未创建模板");
                                }
                            } else {
                                ToastUtil.showToastS(TemplateActivity.this, jsonResult.getMessage());
                            }
                        } else {
                            ToastUtil.showToastS(TemplateActivity.this, "服务器异常，请联系客服");
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
        Template template = templateList.get(position);
        if (null != record) {
            if (template.getType().equals(record.getType())) {
                Intent intent = new Intent();
                intent.putExtra(MainActivity.TEMPLATE, template);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                ToastUtil.showToastS(this, "模板类型不匹配，请重新选择");
            }
        }
    }

    @Override
    public void onLongItemClick(int position) {
        deleteTemplate = templateList.get(position);
        deleteDialog();
    }

    public void deleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.hint)
                .setMessage("是否删除本地模板？")
                .setNegativeButton(R.string.agree,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                obsUtils.execute(3);
                            }
                        })
                .setPositiveButton(R.string.disagree, null)
                .setCancelable(false)
                .show();
    }

    @Override
    public void onSubscribe(int type) {
        switch (type) {
            case 1:
                //从数据库中获取模板信息
                templateList = templateDao.queryList(userID);
                if (templateList != null && templateList.size() > 0) {
                    for (Template template : templateList) {
                        List<TemplateDetail> detailList = templateDetialDao.queryList(template.getIds());
                        if (detailList != null && detailList.size() > 0) {
                            template.setDetailList(detailList);
                        }
                    }
                }
                break;
            case 2:
                //拿到模板数据，保存到数据库
                for (Template template : templateList) {
                    if (template.getDetailList() != null && template.getDetailList().size() > 0) {
                        templateDetialDao.saveList(template.getDetailList());
                    }
                    //每个模板添加userid以标识
                    template.setUserID(userID);
                }
                templateDao.saveList(templateList);
                break;
            case 3:
                List<TemplateDetail> detailList = deleteTemplate.getDetailList();
                if (detailList != null && detailList.size() > 0) {
                    for (TemplateDetail detail : detailList) {
                        templateDetialDao.delete(detail);
                    }
                }
                templateDao.delete(deleteTemplate);
                break;
        }
    }

    @Override
    public void onComplete(int type) {
        switch (type) {
            case 1:
                if (templateList != null && templateList.size() > 0) {
                    templateRecycler.setVisibility(View.VISIBLE);
                    initRecycleView();
                    prompt.setText("长按可以删除本地模板");
                } else {
                    templateRecycler.setVisibility(View.GONE);
                    prompt.setText("本地没有数据，右上角可以更新数据");
                }
                break;
            case 2:
                obsUtils.execute(1);
                break;
            case 3:
                obsUtils.execute(1);
                break;
        }
    }
}
