package com.cj.record.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.baen.Hole;
import com.cj.record.baen.Record;
import com.cj.record.slide.AbstractSlideExpandableListAdapter;
import com.cj.record.utils.ToastUtil;

import net.qiujuer.genius.ui.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cj.record.activity.base.BaseActivity.userID;

/**
 * Created by Administrator on 2018/6/7.
 */

public class RecordAdapter extends AbstractSlideExpandableListAdapter<RecordAdapter.MyHolder> implements AbstractSlideExpandableListAdapter.OnItemExpandCollapseListener {

    private List<Record> list;
    private Context mContext;
    private LayoutInflater inflater;
    private List<HoleAdapter.MyHolder> holderList;

    public RecordAdapter(Context context, List<Record> list) {
        this.mContext = context;
        this.list = list;
        inflater = LayoutInflater.from(mContext);
        holderList = new ArrayList<>();
//        setItemExpandCollapseListener(this);
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(inflater.inflate(R.layout.item_record, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        final RecordAdapter.MyHolder myHolder = (RecordAdapter.MyHolder) holder;
        Record record = list.get(position);
        myHolder.recordBeginDepth.setText(record.getBeginDepth() + "m");
        myHolder.recordEndDepth.setText(record.getEndDepth() + "m");
        myHolder.recordType.setText(record.getType());
        myHolder.recordName.setText(record.getTitle());
        myHolder.recordUpdateTime.setText("修改时间:" + record.getUpdateTime());
        if (record.getType().equals(Record.TYPE_GET_WATER)) {
            myHolder.recordBeginDepth.setVisibility(View.VISIBLE);
            myHolder.recordEb.setVisibility(View.GONE);
            myHolder.recordEndDepth.setVisibility(View.GONE);
            myHolder.recordEb.setText("~");
        } else if (record.getType().equals(Record.TYPE_WATER)) {
            myHolder.recordBeginDepth.setVisibility(View.VISIBLE);
            myHolder.recordEb.setVisibility(View.VISIBLE);
            myHolder.recordEndDepth.setVisibility(View.VISIBLE);
            myHolder.recordEb.setText("/");
            myHolder.recordEndDepth.setText(record.getEndDepth() + "m");
        } else {
            myHolder.recordBeginDepth.setVisibility(View.VISIBLE);
            myHolder.recordEb.setVisibility(View.VISIBLE);
            myHolder.recordEndDepth.setVisibility(View.VISIBLE);
            myHolder.recordEb.setText("~");
        }
        //岩土顯示分層編號
//        if (record.getType().equals(Record.TYPE_LAYER)) {
//            myHolder.recordLayerCode.setVisibility(View.VISIBLE);
//            myHolder.recordLayerCode.setText("土层编号:" + record.getMainLayerCode() + "-" + record.getSubLayerCode() + "-" + record.getSecondSubLayerCode());
//        } else {
//            myHolder.recordLayerCode.setVisibility(View.GONE);
//        }
        myHolder.recordDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemListener.detailClick(position);
            }
        });
        myHolder.recordEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemListener.editClick(position);
            }
        });
        myHolder.recordDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemListener.deleteClick(position);
            }
        });

    }

    @Override
    public View getExpandToggleButton(View parent) {
        return parent.findViewById(R.id.record_ll);
    }

    @Override
    public View getExpandableView(View parent) {
        return parent.findViewById(R.id.record_btn_ll);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private OnItemListener mOnItemListener;

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public interface OnItemListener {
        void detailClick(int position);

        void editClick(int position);

        void deleteClick(int position);
    }

    public void delete(int position) {
        list.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public void onExpand(View itemView, int position) {

    }

    @Override
    public void onCollapse(View itemView, int position) {

    }

    class MyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.record_beginDepth)
        TextView recordBeginDepth;
        @BindView(R.id.record_eb)
        TextView recordEb;
        @BindView(R.id.record_endDepth)
        TextView recordEndDepth;
        @BindView(R.id.record_type)
        TextView recordType;
        @BindView(R.id.record_tn)
        TextView recordTn;
        @BindView(R.id.record_name)
        TextView recordName;
        @BindView(R.id.record_updateTime)
        TextView recordUpdateTime;
        @BindView(R.id.record_detail)
        Button recordDetail;
        @BindView(R.id.record_edit)
        Button recordEdit;
        @BindView(R.id.record_delete)
        Button recordDelete;
        @BindView(R.id.record_btn_ll)
        LinearLayout recordBtnLl;
        @BindView(R.id.record_ll)
        LinearLayout recordLl;
        @BindView(R.id.record_layerCode)
        TextView recordLayerCode;

        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
