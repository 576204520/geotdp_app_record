package com.cj.record.fragment.record;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.cj.record.R;
import com.cj.record.adapter.DropListAdapter;
import com.cj.record.baen.Dictionary;
import com.cj.record.baen.DropItemVo;
import com.cj.record.baen.Record;
import com.cj.record.db.DictionaryDao;
import com.cj.record.views.MaterialBetterSpinner;
import com.cj.record.views.MaterialEditTextMillimeter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 回次编辑
 */
public class RecordEditFrequencyFragment extends RecordBaseFragment {
    @BindView(R.id.sprMode)
    MaterialBetterSpinner sprMode;
    @BindView(R.id.sprType)
    MaterialBetterSpinner sprType;
    @BindView(R.id.edtAperture)
    MaterialEditTextMillimeter edtAperture;

    List<DropItemVo> typeList;
    DropListAdapter typeListAdapter;
    private int sortNoType = 0;
    List<DropItemVo> modeList;
    DropListAdapter modeListAdapter;
    private int sortNoMode = 0;

    @Override
    public int getLayoutId() {
        return R.layout.frt_record_frequency_edit;
    }

    @Override
    public void initData() {
        super.initData();
        typeListAdapter = new DropListAdapter(mActivity, R.layout.drop_item, getLayerTypeList());
        sprType.setAdapter(typeListAdapter);
        sprType.setOnItemClickListener(typeListener);
        //默认填写
        //sprType.setText(typeList.get(0).getName());
        modeListAdapter = new DropListAdapter(mActivity, R.layout.drop_item, getModeList());
        sprMode.setAdapter(modeListAdapter);
        sprMode.setOnItemClickListener(modeListener);
        //设置可自定义和可清空
        sprType.setCustom().setClear();
        sprMode.setCustom().setClear();
    }

    @Override
    public void initView() {
        super.initView();
        sprType.setText(record.getFrequencyType());
        sprMode.setText(record.getFrequencyMode());
        edtAperture.setText(record.getAperture());
    }

    MaterialBetterSpinner.OnItemClickListener typeListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoType = i == adapterView.getCount() - 1 ? i : 0;
        }
    };
    MaterialBetterSpinner.OnItemClickListener modeListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoMode = i == adapterView.getCount() - 1 ? i : 0;
        }
    };


    private List<DropItemVo> getLayerTypeList() {
        typeList = DictionaryDao.getInstance().getDropItemList(getSqlString("钻进方法"));
        return typeList;
    }

    private List<DropItemVo> getModeList() {
        modeList = DictionaryDao.getInstance().getDropItemList(getSqlString("护壁方法"));
        return modeList;
    }

    @Override
    public Record getRecord() {
        record.setFrequencyType(sprType.getText().toString());
        record.setFrequencyMode(sprMode.getText().toString());
        record.setAperture(edtAperture.getText().toString());
        return record;
    }

    @Override
    public String getTitle() {
        String title = sprType.getText().toString().trim() + "--" + sprMode.getText().toString().trim();
        return title;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_FREQUENCY;
    }

    @Override
    public boolean validator() {
        boolean validator = true;
        if (sortNoType > 0 && validator) {
            DictionaryDao.getInstance().add(new Dictionary("1", "钻进方法", sprType.getText().toString().trim(), "" + sortNoType, userID, Record.TYPE_FREQUENCY));
        }
        if (sortNoMode > 0 && validator) {
            DictionaryDao.getInstance().add(new Dictionary("1", "护壁方法", sprMode.getText().toString().trim(), "" + sortNoMode, userID, Record.TYPE_FREQUENCY));
        }
        return validator;
    }
}
