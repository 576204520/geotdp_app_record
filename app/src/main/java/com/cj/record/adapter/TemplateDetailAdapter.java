package com.cj.record.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.baen.TemplateDetail;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/7/9.
 */

public class TemplateDetailAdapter extends RecyclerView.Adapter<TemplateDetailAdapter.MyViewHolder> {

    private List<TemplateDetail> list;
    private Context context;
    private LayoutInflater mInflater;


    public TemplateDetailAdapter(Context context, List<TemplateDetail> list) {
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.item_template_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TemplateDetail detail = list.get(position);
        holder.templateDetailKey.setText(detail.getFieldKey());
        holder.templateDetailValue.setText(detail.getFieldValue());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.template_detail_key)
        TextView templateDetailKey;
        @BindView(R.id.template_detail_value)
        EditText templateDetailValue;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
