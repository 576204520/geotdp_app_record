package com.cj.record.fragment.record;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cj.record.R;
import com.cj.record.baen.Record;
import com.cj.record.views.MaterialEditTextNoEmoji;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 描述员
 */
public class RecordPersonFragment extends RecordBaseFragment {
    @BindView(R.id.person_name)
    MaterialEditTextNoEmoji personName;

    @Override
    public int getLayoutId() {
        return R.layout.frt_scene_recordperson;
    }


    @Override
    public void initView() {
        super.initView();
        personName.setText(userName);
    }

    @Override
    public Record getRecord() {
        record.setRecordPerson(personName.getText().toString());
        return record;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_SCENE_RECORDPERSON;
    }
}
