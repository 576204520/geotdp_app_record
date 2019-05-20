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
import com.cj.record.mvp.base.App;
import com.cj.record.baen.Message;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.Urls;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2019/4/15.
 */

public class ChatTalkAdapter extends RecyclerView.Adapter<ChatTalkAdapter.MyViewHolder> {


    private List<Message> list;
    private Context context;
    private LayoutInflater mInflater;

    public ChatTalkAdapter(Context context, List<Message> list) {
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.item_chat_talk, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Message message = list.get(position);
        if (message.getSenderIds().equals(SPUtils.get(context, Urls.SPKey.USER_ID, ""))) {
            holder.itemTalkLeft.setVisibility(View.GONE);
            holder.itemTalkRight.setVisibility(View.VISIBLE);
            holder.itemTalkMsgRight.setText(message.getMessage());
            if (TextUtils.isEmpty(message.getReceiverTime())) {
                holder.itemTalkStateRight.setText("未读");
            } else {
                holder.itemTalkStateRight.setText("已读");
            }
        } else {
            holder.itemTalkLeft.setVisibility(View.VISIBLE);
            holder.itemTalkRight.setVisibility(View.GONE);
            holder.itemTalkMsgLeft.setText(message.getMessage());
            holder.itemTalkStateLeft.setText("已读");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_talk_msg_left)
        TextView itemTalkMsgLeft;
        @BindView(R.id.item_talk_state_left)
        TextView itemTalkStateLeft;
        @BindView(R.id.item_talk_msg_right)
        TextView itemTalkMsgRight;
        @BindView(R.id.item_talk_state_right)
        TextView itemTalkStateRight;
        @BindView(R.id.item_talk_Left)
        LinearLayout itemTalkLeft;
        @BindView(R.id.item_talk_right)
        LinearLayout itemTalkRight;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
