package com.cj.record.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cj.record.R;
import com.cj.record.adapter.DictionaryAdapter;
import com.cj.record.base.App;
import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.Dictionary;
import com.cj.record.base.BaseMvpActivity;
import com.cj.record.contract.DictionaryContract;
import com.cj.record.db.DictionaryDao;
import com.cj.record.presenter.DictionaryPresenter;
import com.cj.record.utils.Common;
import com.cj.record.utils.JsonUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.UpdateUtil;
import com.cj.record.views.ProgressDialog;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;

/**
 * 字典管理
 */
public class DictionaryActvity extends BaseMvpActivity<DictionaryPresenter> implements DictionaryContract.View {
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

    @Override
    public int getLayoutId() {
        return R.layout.act_setting_dictionary;
    }


    @Override
    public void initView() {
        mPresenter = new DictionaryPresenter();
        mPresenter.attachView(this);
        toolbar.setTitle(R.string.dictionary_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        isSelectAll = false;
        initRecycleView();
        resetData();
    }

    private void initRecycleView() {
        list = new ArrayList<>();
        dictionaryList = new ArrayList<>();
        dictionaryRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DictionaryAdapter(this, dictionaryList);
        dictionaryRecycler.setAdapter(adapter);
        setListener();
    }

    private void resetData() {
        list.clear();
        dictionaryList.clear();
        dictionaryList.addAll(DictionaryDao.getInstance().getDictionary());
        adapter.notifyDataSetChanged();
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
                resetData();
            }
        }).show();
    }

    /**
     * 下载词库确认
     */

    private void downloadDialog() {
        new MaterialDialog.Builder(this).content(R.string.dictionary_download_please).positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                if (TextUtils.isEmpty(App.userID)) {
                    ToastUtil.showToastS(DictionaryActvity.this, getString(R.string.project_edit_hint_user));
                    return;
                }
                mPresenter.downloadDictionary(App.userID, UpdateUtil.getVerCode(DictionaryActvity.this) + "");
            }
        }).show();
    }

    /**
     * 上传词库确认
     */
    private void uploadDialog() {
        new MaterialDialog.Builder(this).content(R.string.dictionary_upload_please).positiveText(R.string.agree).negativeText(R.string.disagree).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                if (TextUtils.isEmpty(App.userID)) {
                    ToastUtil.showToastS(DictionaryActvity.this, getString(R.string.project_edit_hint_user));
                    return;
                }
                mPresenter.uploadDictionary(getMap(list));
            }
        }).show();
    }


    private Map<String, String> getMap(List<Dictionary> list) {
        Map<String, String> params = new ConcurrentHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            params.put("dictionary[" + i + "].name", list.get(i).getName());
            params.put("dictionary[" + i + "].name", list.get(i).getName());
            params.put("dictionary[" + i + "].relateID", list.get(i).getRelateID());
            params.put("dictionary[" + i + "].type", list.get(i).getType());
            params.put("dictionary[" + i + "].sort", list.get(i).getSort());
            params.put("dictionary[" + i + "].sortNo", list.get(i).getSortNo());
            params.put("dictionary[" + i + "].form", list.get(i).getForm());
        }
        return params;
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
    public void onSuccessUpload(BaseObjectBean<String> bean) {
        Common.showMessage(this, bean.getMessage());
    }

    @Override
    public void onSuccessDownload(BaseObjectBean<String> bean) {
        if (bean.isStatus()) {
            List<Dictionary> list = JsonUtils.getInstance().fromJson(bean.getResult(), new TypeToken<List<Dictionary>>() {
            }.getType());
            if (list != null && list.size() > 0) {
                DictionaryDao.getInstance().deleteAll();
                DictionaryDao.getInstance().addDictionaryList(list);
                dictionaryList.clear();
                dictionaryList.addAll(list);
                adapter.notifyDataSetChanged();
            } else {
                ToastUtil.showToastS(this, getString(R.string.dictionary_download_nodata));
            }

        } else {
            Common.showMessage(this, bean.getMessage());
        }
    }
}
