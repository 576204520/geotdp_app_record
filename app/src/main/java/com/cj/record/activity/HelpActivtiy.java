package com.cj.record.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/5/12.
 */
public class HelpActivtiy extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.help_webView)
    WebView helpWebView;
    private String action;

    @Override
    public int getLayoutId() {
        return R.layout.act_help;
    }

    @Override
    public void initData() {
        super.initData();
        action = getIntent().getAction();
        if ("RecordListActivity".equals(action)) {
            helpWebView.loadUrl("file:///android_asset/record_list.html");
        }
        if ("回次".equals(action)) {
            helpWebView.loadUrl("file:///android_asset/record_edit_hc.html");
        }
        if ("岩土".equals(action)) {
            helpWebView.loadUrl("file:///android_asset/record_edit_yt.html");
        }
        if ("取土".equals(action)) {
            helpWebView.loadUrl("file:///android_asset/record_edit_qt.html");
        }
        if ("取水".equals(action)) {
            helpWebView.loadUrl("file:///android_asset/record_edit_qs.html");
        }
        if ("动探".equals(action)) {
            helpWebView.loadUrl("file:///android_asset/record_edit_dt.html");
        }
        if ("标贯".equals(action)) {
            helpWebView.loadUrl("file:///android_asset/record_edit_bg.html");
        }
        if ("水位".equals(action)) {
            helpWebView.loadUrl("file:///android_asset/record_edit_sw.html");
        }
        if ("MainActivity".equals(action)) {
            helpWebView.loadUrl("file:///android_asset/help.html");
        }
        helpWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = helpWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    public void initView() {
        toolbar.setTitle("使用帮助");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}
