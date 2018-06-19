package com.cj.record.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.utils.UpdateUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/14.
 */
public class SettingActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.setting_dictionary)
    CardView settingDictionary;
    @BindView(R.id.setting_map)
    CardView settingMap;
    @BindView(R.id.setting_update)
    CardView settingUpdate;
    @BindView(R.id.setting_newcode)
    TextView settingNewcode;
    @BindView(R.id.setting_about)
    CardView settingAbout;


    @Override
    public int getLayoutId() {
        return R.layout.act_setting_main;
    }

    @Override
    public void initView() {
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        settingNewcode.setText(UpdateUtil.getVerName(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick({R.id.setting_dictionary, R.id.setting_map, R.id.setting_update, R.id.setting_about})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setting_dictionary:
                startActivity(DictionaryActvity.class);
                break;
            case R.id.setting_map:
                startActivity(new Intent(this.getApplicationContext(), com.amap.api.maps.offlinemap.OfflineMapActivity.class));
                break;
            case R.id.setting_update:
                //检查版本
                UpdateUtil.checkVersion(this, true);
                break;
        }
    }
}
