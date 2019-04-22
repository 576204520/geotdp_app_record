package com.cj.record.fragment.record;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cj.record.R;
import com.cj.record.adapter.DropListAdapter;
import com.cj.record.baen.DropItemVo;
import com.cj.record.baen.Record;
import com.cj.record.views.MaterialBetterSpinner;
import com.cj.record.views.MaterialEditTextElevation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 取样(取水) 的片段.
 */
public class RecordEditGetWaterFragment extends RecordBaseFragment {
    @BindView(R.id.edtWaterDepth)
    MaterialEditTextElevation edtWaterDepth;
    @BindView(R.id.sprMode)
    MaterialBetterSpinner sprMode;

    List<DropItemVo> modeList;
    DropListAdapter modeListAdapter;

    StringBuilder str = new StringBuilder();
    MaterialDialog dialog;

    @Override
    public int getLayoutId() {
        return R.layout.frt_record_get_water_edit;

    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        modeListAdapter = new DropListAdapter(mActivity, R.layout.drop_item, getModeList());
        sprMode.setAdapter(modeListAdapter);

        edtWaterDepth.setText(record.getWaterDepth());
        sprMode.setText(record.getGetMode());
    }


    private List<DropItemVo> getModeList() {
        modeList = new ArrayList<DropItemVo>();
        modeList.add(new DropItemVo("1", "无"));
        modeList.add(new DropItemVo("2", "加入大理石粉"));
        return modeList;
    }


    @Override
    public Record getRecord() {
        record.setWaterDepth(edtWaterDepth.getText().toString());
        record.setGetMode(sprMode.getText().toString());
        return record;
    }

    @Override
    public String getTitle() {
        String title = sprMode.getText().toString().trim();
        return title;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_GET_WATER;
    }

    @Override
    public String getBegin() {
        return edtWaterDepth.getText().toString().trim();
    }

    @Override
    public String getEnd() {
        return edtWaterDepth.getText().toString().trim();
    }


    @Override
    public boolean validator() {
        boolean validator = true;
        if ("".equals(edtWaterDepth.getText().toString()) || Double.parseDouble(edtWaterDepth.getText().toString()) <= 0) {
            edtWaterDepth.setError("取水深度必须大于零");
            validator = false;
        }
        return validator;
    }
}