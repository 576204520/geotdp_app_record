package com.cj.record.fragment.record;

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
import com.cj.record.utils.ObsUtils;
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
public class RecordOperateCodeFragment extends RecordBaseFragment implements ObsUtils.ObsLinstener {
    @BindView(R.id.operatecode_code)
    MaterialBetterSpinner operatecodeCode;
    private List<Record> recordList;
    private ObsUtils obsUtils;

    @Override
    public int getLayoutId() {
        return R.layout.frt_scene_operatecode;
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
        operatecodeCode.setText(record.getTestType());
        obsUtils.execute(1);
    }

    @Override
    public void onSubscribe(int type) {
        recordList = RecordDao.getInstance().getRecordListByProject(record.getProjectID(), Record.TYPE_SCENE_OPERATECODE);
    }

    @Override
    public void onComplete(int type) {
        if (recordList != null) {
            //新建的记录机长信息是空的，删除掉
            Iterator<Record> ir = recordList.iterator();
            while (ir.hasNext()) {
                Record record = ir.next();
                if ("".equals(record.getTestType())) {
                    ir.remove();
                }
            }
            //去掉机长和机长编号相同的数据
            for (int i = 0; i < recordList.size(); i++) {
                for (int j = i + 1; j < recordList.size(); j++) {
                    if (recordList.get(i).getTestType().equals(recordList.get(j).getTestType())) {
                        recordList.remove(j);
                        j--;
                    }
                }
            }

            final List<DropItemVo> list = new ArrayList<>();
            for (int i = 1; i <= recordList.size(); i++) {
                DropItemVo dropItemVo = new DropItemVo();
                dropItemVo.setId(i + "");
                dropItemVo.setName(recordList.get(i - 1).getTestType());
                dropItemVo.setValue(recordList.get(i - 1).getTestType());
                list.add(dropItemVo);
            }
            operatecodeCode.setText(record.getTestType());
            operatecodeCode.setAdapter(mActivity, list, MaterialBetterSpinner.MODE_CUSTOM);
            operatecodeCode.setOnItemClickListener(new MaterialBetterSpinner.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                }
            });

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
