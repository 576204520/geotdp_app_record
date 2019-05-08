package com.cj.record.activity;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.mvp.base.BaseActivity;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.UpdateUtil;

import butterknife.BindView;

/**
 * Created by Administrator on 2019/4/8.
 */

public class AboutActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.about_name)
    TextView aboutName;
    @BindView(R.id.about_help)
    TextView aboutHelp;
    @BindView(R.id.about_company)
    TextView aboutCompany;
    @BindView(R.id.about_code)
    TextView aboutCode;

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void initView() {
        toolbar.setTitle("关于");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        aboutName.setText(R.string.app_name);
        aboutHelp.setText("技术支持：010-84025805 QQ群：224576759");
        aboutCompany.setText("北京综建信息技术有限公司2018年版权所有");
        aboutCode.setText("当前版本：" + UpdateUtil.getVerName(this));
        aboutName.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View v) {
                ToastUtil.showToastS(AboutActivity.this, "欢迎，祝使用愉快");
            }
        });
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

    abstract class DoubleClickListener implements View.OnClickListener {
        private final long DOUBLE_TIME = 1000;
        private long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - lastClickTime < DOUBLE_TIME) {
                onDoubleClick(v);
            }
            lastClickTime = currentTimeMillis;
        }

        public abstract void onDoubleClick(View v);
    }
}