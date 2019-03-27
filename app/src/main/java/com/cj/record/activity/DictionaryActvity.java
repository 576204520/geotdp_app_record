package com.cj.record.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.adapter.DictionaryAdapter;
import com.cj.record.baen.Dictionary;
import com.cj.record.baen.JsonResult;
import com.cj.record.db.DictionaryDao;
import com.cj.record.utils.Common;
import com.cj.record.utils.JsonUtils;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.UpdateUtil;
import com.cj.record.utils.Urls;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 字典管理
 */
public class DictionaryActvity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.dictionary_recycler)
    RecyclerView dictionaryRecycler;
    @BindView(R.id.dictionary_delete)
    Button dictionaryDelete;
    @BindView(R.id.dictionary_all)
    Button dictionaryAll;
    @BindView(R.id.dictionary_download)
    Button dictionaryDownload;
    @BindView(R.id.dictionary_upload)
    Button dictionaryUpload;
    private List<Dictionary> dictionaryList;
    private DictionaryAdapter adapter;
    private List<Dictionary> list;

    private boolean isSelectAll;

    private Map<String, String> map;

    @Override
    public int getLayoutId() {
        return R.layout.act_setting_dictionary;
    }

    @Override
    public void initData() {
        super.initData();
        toolbar.setTitle("字典库管理");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dictionaryRecycler.setLayoutManager(new LinearLayoutManager(this));
        map = new HashMap<>();
        dictionaryList = new ArrayList<>();
        list = new ArrayList<>();
        initList();
    }

    @Override
    public void initView() {
    }


    private void initList() {
        isSelectAll = false;
        dictionaryList.clear();
        list.clear();
        dictionaryList = DictionaryDao.getInstance().getDictionary();
        if (dictionaryList != null) {
            adapter = new DictionaryAdapter(this, dictionaryList);
            dictionaryRecycler.setAdapter(adapter);
            setListener();
        }
    }

    private void setListener() {
        adapter.setOnItemListener(new DictionaryAdapter.OnItemListener() {
            @Override
            public void checkBoxClick(int position) {
                addOrRemove(position);
            }

            @Override
            public void onItemClick(int position) {
                addOrRemove(position);
            }
        });
    }

    private void addOrRemove(int position) {
        if (list.contains(adapter.getItem(position))) {
            list.remove(adapter.getItem(position));
        } else {
            list.add(adapter.getItem(position));
        }
    }

    private void delete() {
        if (list != null && list.size() > 0) {
            deleteDialog();
        } else {
            ToastUtil.showToastS(this, "当前没有自定义词库");
        }
    }

    private void selectAll() {
        if (dictionaryList != null && dictionaryList.size() > 0) {
            if (!isSelectAll) {
                isSelectAll = true;
                for (int i = 0; i < dictionaryList.size(); i++) {
                    addOrRemove(i);
                    dictionaryList.get(i).isSelect = true;
                }
                adapter.notifyDataSetChanged();
            } else {
                isSelectAll = false;
                for (int i = 0; i < dictionaryList.size(); i++) {
                    addOrRemove(i);
                    dictionaryList.get(i).isSelect = false;
                }
                adapter.notifyDataSetChanged();
            }
        } else {
            ToastUtil.showToastS(this, "当前没有自定义词库");
        }
    }

    public void upload() {
        if (list != null && list.size() > 0) {
            uploadDialog();
        } else {
            ToastUtil.showToastS(this, "还未选择需要上传的字段");
        }
    }

    public void download() {
        downloadDialog();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dictionary_delete:
                delete();
                break;
            case R.id.dictionary_all:
                selectAll();
                break;
            case R.id.dictionary_download:
                download();
                break;
            case R.id.dictionary_upload:
                upload();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 删除确认
     */
    private void deleteDialog() {
        new MaterialDialog.Builder(this).content("确认删除选中词库").positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                DictionaryDao.getInstance().deleteByDicList(list);
                initList();
            }
        }).show();
    }

    /**
     * 下载词库确认
     */

    private void downloadDialog() {
        if (!TextUtils.isEmpty(userID)) {
            new MaterialDialog.Builder(this).content("下载关联词库，将删除本地词库，是否下载？").positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    if (TextUtils.isEmpty(userID)) {
                        ToastUtil.showToastS(mContext, "用户信息丢失，请尝试重新登陆");
                        return;
                    }
                    downloadDictionary(userID);
                }
            }).show();
        } else {
            ToastUtil.showToastS(this, "请先登陆");
        }
    }

    /**
     * 上传词库确认
     */
    private void uploadDialog() {
        new MaterialDialog.Builder(this).content("上传本地词库，将覆盖云端备份，是否上传？").positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                uploadDictionary(list);
            }
        }).show();
    }


    public void downloadDictionary(final String userID) {
        if (TextUtils.isEmpty(userID)) {
            ToastUtil.showToastS(mContext, "用户信息丢失，请尝试重新登陆");
            return;
        }
        showPPW();
        map.clear();
        map.put("relateID", userID);
        map.put("verCode", UpdateUtil.getVerCode(this) + "");
        OkGo.<String>post(Urls.DICTIONARY_DOWNLOAD).params(map).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                String data = response.body();
                if (JsonUtils.isGoodJson(data)) {
                    Gson gson = new Gson();
                    JsonResult jsonResult = gson.fromJson(data.toString(), JsonResult.class);
                    if (jsonResult.getStatus()) {
                        try {
                            List<Dictionary> list = gson.fromJson(jsonResult.getResult(), new TypeToken<List<Dictionary>>() {
                            }.getType());
                            DictionaryDao.getInstance().deleteAll();
                            DictionaryDao.getInstance().addDictionaryList(list);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        ToastUtil.showToastS(DictionaryActvity.this, "数据为空");
                    }
                    initList();
                } else {
                    ToastUtil.showToastS(DictionaryActvity.this, "服务器异常，请联系客服");
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
                ToastUtil.showToastS(DictionaryActvity.this, "下载失败");
            }
        });
    }

    public void uploadDictionary(List<Dictionary> list) {
        if (TextUtils.isEmpty(userID)) {
            ToastUtil.showToastS(mContext, "用户信息丢失，请尝试重新登陆");
            return;
        }
        map.clear();
        map = getMap(list);
        map.put("userID", userID);
        map.put("verCode", UpdateUtil.getVerCode(this) + "");
        showPPW();
        OkGo.<String>post(Urls.DICTIONARY_UPLOAD).params(map).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                ToastUtil.showToastS(DictionaryActvity.this, "上传成功");
                initList();
            }

            @Override
            public void onFinish() {
                dismissPPW();
                super.onFinish();
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                ToastUtil.showToastS(DictionaryActvity.this, "上传失败");
            }

        });
    }


    private Map<String, String> getMap(List<Dictionary> list) {
        for (int i = 0; i < list.size(); i++) {
            map.put("dictionary[" + i + "].name", list.get(i).getName());
            map.put("dictionary[" + i + "].relateID", list.get(i).getRelateID());
            map.put("dictionary[" + i + "].type", list.get(i).getType());
            map.put("dictionary[" + i + "].sort", list.get(i).getSort());
            map.put("dictionary[" + i + "].sortNo", list.get(i).getSortNo());
            map.put("dictionary[" + i + "].form", list.get(i).getForm());
        }
        return map;
    }
}
