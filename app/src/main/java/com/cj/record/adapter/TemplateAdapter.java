package com.cj.record.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.baen.Template;
import com.cj.record.baen.TemplateDetail;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/7/9.
 */

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.MyViewHolder> {

    private List<Template> list;
    private Context context;
    private LayoutInflater mInflater;
    private TemplateDetailAdapter templateDetailAdapter;

    public TemplateAdapter(Context context, List<Template> list) {
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.item_template, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Template template = list.get(position);
        holder.templateName.setText(template.getName());
        holder.templateType.setText(template.getType() + "-" + template.getSolidType());
        List<TemplateDetail> templateDetails = template.getDetailList();
        if (null != templateDetails && templateDetails.size() > 0) {
            holder.templateCheck.setVisibility(View.VISIBLE);
        } else {
            templateDetails = new ArrayList<>();
            holder.templateCheck.setVisibility(View.GONE);
        }
        ViewGroup.LayoutParams layoutParams = holder.templateDetailRecycler.getLayoutParams();
        layoutParams.height = templateDetails.size() * dip2px(50);
        holder.templateDetailRecycler.setLayoutParams(layoutParams);
        holder.templateDetailRecycler.setLayoutManager(new LinearLayoutManager(context));
        templateDetailAdapter = new TemplateDetailAdapter(context, templateDetails);
        holder.templateDetailRecycler.setAdapter(templateDetailAdapter);
        holder.templateCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.templateCheck.isChecked()) {
                    holder.templateDetailRecycler.setVisibility(View.VISIBLE);
                } else {
                    holder.templateDetailRecycler.setVisibility(View.GONE);
                }
            }
        });
        holder.templateItemRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemListener.onItemClick(position);
            }
        });
        holder.templateItemRl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnItemListener.onLongItemClick(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 将dp转化为px
     */
    private int dip2px(float dip) {
        float v = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        return (int) (v + 0.5f);
    }

    private OnItemListener mOnItemListener;

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public interface OnItemListener {
        void onItemClick(int position);

        void onLongItemClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.template_name)
        TextView templateName;
        @BindView(R.id.template_type)
        TextView templateType;
        @BindView(R.id.template_check)
        CheckBox templateCheck;
        @BindView(R.id.template_item_rl)
        RelativeLayout templateItemRl;
        @BindView(R.id.template_detail_recycler)
        RecyclerView templateDetailRecycler;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
