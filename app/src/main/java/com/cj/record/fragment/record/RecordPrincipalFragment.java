package com.cj.record.fragment.record;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.cj.record.R;
import com.cj.record.baen.DropItemVo;
import com.cj.record.baen.Record;
import com.cj.record.db.RecordDao;
import com.cj.record.utils.ObsUtils;
import com.cj.record.views.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 负责人
 */
public class RecordPrincipalFragment extends RecordBaseFragment implements ObsUtils.ObsLinstener {
    @BindView(R.id.principal_name)
    MaterialBetterSpinner principalName;

    private List<Record> recordList;
    private ObsUtils obsUtils;

    @Override
    public int getLayoutId() {
        return R.layout.frt_scene_principal;
    }

    @Override
    public void initData() {
        super.initData();
        obsUtils = new ObsUtils();
        obsUtils.setObsLinstener(this);

    }

    @Override
    public void initView() {
        super.initView();
        principalName.setText(record.getOperatePerson());
        obsUtils.execute(1);
    }


    @Override
    public void onSubscribe(int type) {
        recordList = new RecordDao(getActivity()).getRecordListByProject(record.getProjectID(), Record.TYPE_SCENE_PRINCIPAL);
    }

    @Override
    public void onComplete(int type) {
        if (recordList != null) {
            Iterator<Record> ir = recordList.iterator();
            while (ir.hasNext()) {
                Record record = ir.next();
                if ("".equals(record.getOperatePerson())) {
                    ir.remove();
                }
            }
            for (int i = 0; i < recordList.size(); i++) {
                for (int j = i + 1; j < recordList.size(); j++) {
                    if (recordList.get(i).getOperatePerson().equals(recordList.get(j).getOperatePerson())) {
                        recordList.remove(j);
                        j--;
                    }
                }
            }

            final List<DropItemVo> list = new ArrayList<>();
            for (int i = 1; i <= recordList.size(); i++) {
                DropItemVo dropItemVo = new DropItemVo();
                dropItemVo.setId(i + "");
                dropItemVo.setName(recordList.get(i - 1).getOperatePerson());
                dropItemVo.setValue(recordList.get(i - 1).getOperatePerson());
                list.add(dropItemVo);
            }
            principalName.setText(record.getOperatePerson());
            principalName.setAdapter(mActivity, list, MaterialBetterSpinner.MODE_CUSTOM);
            principalName.setOnItemClickListener(new MaterialBetterSpinner.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                }
            });

        }
    }

    @Override
    public Record getRecord() {
        record.setOperatePerson(principalName.getText().toString());
        return record;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_SCENE_PRINCIPAL;
    }

}
