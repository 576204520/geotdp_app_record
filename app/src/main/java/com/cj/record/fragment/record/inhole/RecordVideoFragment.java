package com.cj.record.fragment.record.inhole;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cj.record.R;
import com.cj.record.baen.Record;
import com.cj.record.fragment.record.RecordBaseFragment;

/**
 * 提钻录像
 */
public class RecordVideoFragment extends RecordBaseFragment {
    @Override
    public int getLayoutId() {
        return R.layout.frt_scene_video;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
    }

    @Override
    public Record getRecord() {
        return record;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_SCENE_VIDEO;
    }

}
