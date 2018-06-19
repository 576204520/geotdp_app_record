package com.cj.record.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cj.record.R;
import com.cj.record.adapter.MyViewPagerAdapter;
import com.cj.record.baen.Hole;
import com.cj.record.baen.Project;
import com.cj.record.db.HoleDao;
import com.cj.record.db.ProjectDao;
import com.cj.record.fragment.preview.PreviewCountFragment;
import com.cj.record.fragment.preview.PreviewShowFragment;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.Urls;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 勘探点记录预览
 */
public class PreviewActivity extends AppCompatActivity {
    public static final String EXTRA_HOLE = "hole";
    private Hole hole;
    private HoleDao holeDao;
    private PreviewCountFragment CountFragment;
    private PreviewShowFragment ShowFragment;

    //    private Button button;
    boolean isUpload = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //接收传递的信息
        Bundle bundle = this.getIntent().getExtras();
        hole = (Hole) bundle.getSerializable(EXTRA_HOLE);
        holeDao = new HoleDao(this);
        //由于传来的hole不完整，所有再次查询完整的hole
        hole = holeDao.queryForId(hole.getId());
        setContentView(R.layout.act_preview);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.preview_toolBar);
        mToolbar.setTitle("预览相关");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //向两个fragmnent都传递hole
        Bundle bh = new Bundle();
        bh.putSerializable(EXTRA_HOLE, hole);
        CountFragment = PreviewCountFragment.newInstance();
        CountFragment.setArguments(bh);
        ShowFragment = PreviewShowFragment.newInstance();
        ShowFragment.setArguments(bh);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.preview_viewpager);
        MyViewPagerAdapter viewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(CountFragment, "统计");//添加Fragment
        viewPagerAdapter.addFragment(ShowFragment, "展示");
        mViewPager.setAdapter(viewPagerAdapter);//设置适配器

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.preview_tabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText("TabOne"));//给TabLayout添加Tab
        mTabLayout.addTab(mTabLayout.newTab().setText("TabTwo"));
        mTabLayout.setupWithViewPager(mViewPager);//给TabLayout设置关联ViewPager，如果设置了ViewPager，那么ViewPagerAdapter中的getPageTitle()方法返回的就是Tab上的标题
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
