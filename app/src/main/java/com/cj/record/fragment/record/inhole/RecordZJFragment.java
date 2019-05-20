package com.cj.record.fragment.record.inhole;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.cj.record.R;
import com.cj.record.baen.DropItemVo;
import com.cj.record.baen.Record;
import com.cj.record.db.RecordDao;
import com.cj.record.fragment.record.RecordBaseFragment;
import com.cj.record.views.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 钻机编号照片
 */
public class RecordZJFragment extends RecordBaseFragment {
    @BindView(R.id.operatecode_code)
    MaterialBetterSpinner operatecodeCode;
    private List<Record> recordList;

    @Override
    public int getLayoutId() {
        return R.layout.frt_scene_operatecode;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        operatecodeCode.setText(record.getTestType());
        recordList = RecordDao.getInstance().getRecordListByProject(record.getProjectID(), Record.TYPE_SCENE_OPERATECODE);
        if (recordList != null && recordList.size() > 0) {
            //新建的记录机长信息是空的，删除掉
            List<DropItemVo> list = new ArrayList<>();
            for (int i = 1; i <= recordList.size(); i++) {
                DropItemVo dropItemVo = new DropItemVo();
                dropItemVo.setId(i + "");
                dropItemVo.setName(recordList.get(i - 1).getTestType());
                dropItemVo.setValue(recordList.get(i - 1).getTestType());
                list.add(dropItemVo);
            }
            operatecodeCode.setAdapter(mActivity, list, MaterialBetterSpinner.MODE_CUSTOM);
        }
    }


    @Override
    public Record getRecord() {
        record.setTestType(operatecodeCode.getText().toString().trim());
        return record;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_SCENE_OPERATECODE;
    }

}
