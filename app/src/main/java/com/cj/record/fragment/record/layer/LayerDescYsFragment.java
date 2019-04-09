package com.cj.record.fragment.record.layer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.cj.record.R;
import com.cj.record.baen.Dictionary;
import com.cj.record.baen.DropItemVo;
import com.cj.record.baen.Record;
import com.cj.record.db.DictionaryDao;
import com.cj.record.fragment.record.RecordBaseFragment;
import com.cj.record.views.MaterialBetterSpinner;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 地层分类(岩石) 的片段.
 */
public class LayerDescYsFragment extends RecordBaseFragment {

    @BindView(R.id.sprYs)
    MaterialBetterSpinner sprYs;
    @BindView(R.id.sprJycd)
    MaterialBetterSpinner sprJycd;
    @BindView(R.id.sprWzcd)
    MaterialBetterSpinner sprWzcd;
    @BindView(R.id.sprJbzldj)
    MaterialBetterSpinner sprJbzldj;
    @BindView(R.id.sprFhcd)
    MaterialBetterSpinner sprFhcd;
    @BindView(R.id.sprKwx)
    MaterialBetterSpinner sprKwx;
    @BindView(R.id.sprJglx)
    MaterialBetterSpinner sprJglx;

    private List<DropItemVo> sprYsList;
    private List<DropItemVo> sprJycdList;
    private List<DropItemVo> sprWzcdList;
    private List<DropItemVo> sprJbzldjList;
    private List<DropItemVo> sprFhcdList;
    private List<DropItemVo> sprKwxList;
    private List<DropItemVo> sprJglxList;

    private int sortNoYs = 0;
    private int sortNOKwx = 0;


    @Override
    public int getLayoutId() {
        return R.layout.frt_dcms_ys;
    }

    @Override
    public void initData() {
        super.initData();
        sprYsList = DictionaryDao.getInstance().getDropItemList(getSqlString("岩石_颜色"));
        sprJycdList = DictionaryDao.getInstance().getDropItemList(getSqlString("岩石_坚硬程度"));
        sprWzcdList = DictionaryDao.getInstance().getDropItemList(getSqlString("岩石_完整程度"));
        sprJbzldjList = DictionaryDao.getInstance().getDropItemList(getSqlString("岩石_基本质量等级"));
        sprFhcdList = DictionaryDao.getInstance().getDropItemList(getSqlString("岩石_风化程度"));
        sprKwxList = DictionaryDao.getInstance().getDropItemList(getSqlString("岩石_可挖性"));
        sprJglxList = DictionaryDao.getInstance().getDropItemList(getSqlString("岩石_结构类型"));

        sprYs.setAdapter(mActivity, sprYsList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprYs.setOnItemClickListener(ysListener);
        sprJycd.setAdapter(mActivity, sprJycdList, MaterialBetterSpinner.MODE_CLEAR);
        sprWzcd.setAdapter(mActivity, sprWzcdList, MaterialBetterSpinner.MODE_CLEAR);
        sprJbzldj.setAdapter(mActivity, sprJbzldjList, MaterialBetterSpinner.MODE_CLEAR);
        sprFhcd.setAdapter(mActivity, sprFhcdList, MaterialBetterSpinner.MODE_CLEAR);
        sprKwx.setAdapter(mActivity, sprKwxList, MaterialBetterSpinner.MODE_CLEAR_CUSTOM);
        sprKwx.setOnItemClickListener(kwxListener);
        sprJglx.setAdapter(mActivity, sprJglxList, MaterialBetterSpinner.MODE_CLEAR);
    }

    @Override
    public void initView() {
        super.initView();
        sprYs.setText(record.getYs());
        sprJycd.setText(record.getJycd());
        sprWzcd.setText(record.getWzcd());
        sprJbzldj.setText(record.getJbzldj());
        sprFhcd.setText(record.getFhcd());
        sprKwx.setText(record.getKwx());
        sprJglx.setText(record.getJglx());
    }


    MaterialBetterSpinner.OnItemClickListener ysListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoYs = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener kwxListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNOKwx = i == adapterView.getCount() - 1 ? i : 0;
        }
    };


    @Override
    public Record getRecord() {
        record.setYs(sprYs.getText().toString().trim());
        record.setJycd(sprJycd.getText().toString().trim());
        record.setWzcd(sprWzcd.getText().toString().trim());
        record.setJbzldj(sprJbzldj.getText().toString().trim());
        record.setFhcd(sprFhcd.getText().toString().trim());
        record.setKwx(sprKwx.getText().toString().trim());
        record.setJglx(sprJglx.getText().toString().trim());
        return record;
    }

    @Override
    public boolean layerValidator() {
        if (sortNoYs > 0) {
            DictionaryDao.getInstance().add(new Dictionary("1", "岩石_颜色", sprYs.getText().toString().trim(), "" + sortNoYs, userID, Record.TYPE_LAYER));
        }
        if (sortNOKwx > 0) {
            DictionaryDao.getInstance().add(new Dictionary("1", "岩石_可挖性", sprKwx.getText().toString().trim(), "" + sortNOKwx, userID, Record.TYPE_LAYER));
        }
        return true;
    }
}

