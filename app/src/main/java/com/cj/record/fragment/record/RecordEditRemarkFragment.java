package com.cj.record.fragment.record;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cj.record.R;
import com.cj.record.baen.Record;


/**
 * 备注
 */
public class RecordEditRemarkFragment extends RecordBaseFragment {

    @Override
    public int getLayoutId() {
        return R.layout.frt_record_remark_edit;
    }

    @Override
    public Record getRecord() {
        return record;
    }


    @Override
    public String getTypeName() {
        return Record.TYPE_SCENE;
    }

}
