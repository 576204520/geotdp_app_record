package com.cj.record.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.baen.Friends;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2019/4/15.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<Friends> list;
    private Context context;
    private LayoutInflater mInflater;


    public ChatAdapter(Context context, List<Friends> list) {
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.item_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Friends friends = list.get(position);
        holder.itemChatName.setText(friends.getFriendNickname());
        holder.itemChatLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemListener.onItemClick(position);
            }
        });
        int num = Integer.parseInt(friends.getUnreadNum());
        if (num > 0) {
            num = num > 99 ? 99 : num;
            holder.itemChatNumber.setVisibility(View.VISIBLE);
            holder.itemChatNumber.setText(Integer.toString(num));
        } else {
            holder.itemChatNumber.setVisibility(View.GONE);
        }
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
        void onItemClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_chat_name)
        TextView itemChatName;
        @BindView(R.id.item_chat_number)
        TextView itemChatNumber;
        @BindView(R.id.item_chat_ll)
        LinearLayout itemChatLl;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
