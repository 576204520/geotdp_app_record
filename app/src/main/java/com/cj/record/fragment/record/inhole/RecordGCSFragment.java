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
 * 工程师
 */
public class RecordGCSFragment extends RecordBaseFragment {
    @BindView(R.id.technician_name)
    MaterialBetterSpinner technicianName;
    private List<Record> recordList;

    @Override
    public int getLayoutId() {
        return R.layout.frt_scene_technician;
    }


    @Override
    protected void initView(View view) {
        super.initView(view);
        technicianName.setText(record.getOperatePerson());
        recordList = RecordDao.getInstance().getRecordListByProject(record.getProjectID(), Record.TYPE_SCENE_TECHNICIAN);
        if (recordList != null && recordList.size() > 0) {
            List<DropItemVo> list = new ArrayList<>();
            for (int i = 1; i <= recordList.size(); i++) {
                DropItemVo dropItemVo = new DropItemVo();
                dropItemVo.setId(i + "");
                dropItemVo.setName(recordList.get(i - 1).getOperatePerson());
                dropItemVo.setValue(recordList.get(i - 1).getOperatePerson());
                list.add(dropItemVo);
            }
            technicianName.setAdapter(mActivity, list, MaterialBetterSpinner.MODE_CUSTOM);
        }
    }

    @Override
    public Record getRecord() {
        record.setOperatePerson(technicianName.getText().toString().trim());
        return record;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_SCENE_TECHNICIAN;
    }

}
