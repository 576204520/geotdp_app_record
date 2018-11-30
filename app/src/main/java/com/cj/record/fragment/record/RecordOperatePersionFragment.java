package com.cj.record.fragment.record;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.cj.record.views.MaterialEditTextNoEmoji;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;

/**
 * 机长 改成司钻员
 */
public class RecordOperatePersionFragment extends RecordBaseFragment implements ObsUtils.ObsLinstener {
    @BindView(R.id.operateperson_name)
    MaterialBetterSpinner operatepersonName;
    @BindView(R.id.operateperson_code)
    MaterialEditTextNoEmoji operatepersonCode;

    private List<Record> recordList;
    private ObsUtils obsUtils;

    @Override
    public int getLayoutId() {
        return R.layout.frt_scene_operateperson;
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
        obsUtils.execute(1);
    }

    @Override
    public void onSubscribe(int type) {
        recordList = new RecordDao(getActivity()).getRecordListByProject(record.getProjectID(), Record.TYPE_SCENE_OPERATEPERSON);
    }

    @Override
    public void onComplete(int type) {
        if (recordList != null) {
            //新建的记录机长信息是空的，删除掉
            Iterator<Record> ir = recordList.iterator();
            while (ir.hasNext()) {
                Record record = ir.next();
                if ("".equals(record.getOperatePerson())) {
                    ir.remove();
                }
            }
            //去掉机长和机长编号相同的数据
            for (int i = 0; i < recordList.size(); i++) {
                for (int j = i + 1; j < recordList.size(); j++) {
                    if (recordList.get(i).getOperatePerson().equals(recordList.get(j).getOperatePerson()) && recordList.get(i).getTestType().equals(recordList.get(j).getTestType())) {
                        recordList.remove(j);
                        j--;
                    }
                }
            }
            final List<DropItemVo> list = new ArrayList<>();
            for (int i = 1; i <= recordList.size(); i++) {
                DropItemVo dropItemVo = new DropItemVo();
                dropItemVo.setId(i + "");
                dropItemVo.setName("姓名:" + recordList.get(i - 1).getOperatePerson() + "  编号:" + recordList.get(i - 1).getTestType());
                dropItemVo.setValue(recordList.get(i - 1).getOperatePerson() + "  " + recordList.get(i - 1).getTestType());
                list.add(dropItemVo);
            }
            operatepersonName.setText(record.getOperatePerson());
            operatepersonCode.setText(record.getTestType());
            operatepersonName.setAdapter(mActivity, list, MaterialBetterSpinner.MODE_CUSTOM);
            operatepersonName.setOnItemClickListener(new MaterialBetterSpinner.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i + 1 < list.size()) {
                        operatepersonCode.setText(recordList.get(i).getTestType());
                    }
                }
            });
        }
    }


    @Override
    public Record getRecord() {
        record.setOperatePerson(operatepersonName.getText().toString().trim());
        record.setTestType(operatepersonCode.getText().toString().trim());
        return record;
    }

    @Override
    public boolean validator() {
        boolean validator = true;
        if (TextUtils.isEmpty(operatepersonName.getText().toString().trim())) {
            operatepersonName.setError("姓名不能为空");
            validator = false;
        }
        if (TextUtils.isEmpty(operatepersonCode.getText().toString().trim())) {
            operatepersonCode.setError("编号不能为空");
            validator = false;
        }
        return validator;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_SCENE_OPERATEPERSON;
    }

}
