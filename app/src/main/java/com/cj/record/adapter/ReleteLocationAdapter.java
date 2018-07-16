package com.cj.record.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.baen.Hole;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/6/21.
 */
public class ReleteLocationAdapter extends RecyclerView.Adapter<ReleteLocationAdapter.ViewHolder> {

    private List<Hole> data;
    private Context mContext;

    public ReleteLocationAdapter(Context mContext, List<Hole> data) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //绑定布局
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_relate_location, parent, false);
        //创建ViewHolder
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Hole hole = data.get(position);
        if (hole.getLatitude() != null && !"".equals(hole.getLatitude()) && hole.getLongitude() != null && !"".equals(hole.getLongitude())) {
            holder.relateLocationItemName.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
        } else {
            holder.relateLocationItemName.setTextColor(mContext.getResources().getColor(R.color.colorTexthintGrey));
        }
        holder.relateLocationItemName.setText(hole.getCode());
        holder.relateLocationItemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemListener.onClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private OnItemListener mOnItemListener;

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public interface OnItemListener {
        void onClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.relate_location_item_name)
        TextView relateLocationItemName;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }
    }
}
