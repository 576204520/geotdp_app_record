package com.cj.record.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.adapter.ChatAdapter;
import com.cj.record.adapter.ChatTalkAdapter;
import com.cj.record.base.App;
import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.Friends;
import com.cj.record.baen.Message;
import com.cj.record.base.BaseMvpFragment;
import com.cj.record.contract.ChatContract;
import com.cj.record.presenter.ChatPresenter;
import com.cj.record.utils.JsonUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.views.MaterialEditTextNoEmoji;
import com.cj.record.views.ProgressDialog;
import com.google.gson.reflect.TypeToken;

import net.qiujuer.genius.ui.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class ChatFragment extends BaseMvpFragment<ChatPresenter> implements ChatContract.View,
        SwipeRefreshLayout.OnRefreshListener, ChatAdapter.OnItemListener, View.OnClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.refresh)
    SwipeRefreshLayout refresh;

    private ChatAdapter chatAdapter;
    private List<Friends> list;
    private boolean isMyFriends = true;
    public Dialog talkDialog;

    private RecyclerView recyclerView;
    private List<Message> messageList;
    private ChatTalkAdapter chatTalkAdapter;
    private MaterialEditTextNoEmoji msgText;
    private Friends sendFriend;
    private TextView friendName;

    @Override
    protected void initView(View view) {
        mPresenter = new ChatPresenter();
        mPresenter.attachView(this);
        list = new ArrayList<>();
        initRecycleView();
        mPresenter.myFriendList(App.userID);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_chat;
    }

    private void initRecycleView() {
        chatAdapter = new ChatAdapter(mActivity, list);
        chatAdapter.setOnItemListener(this);
        refresh.setOnRefreshListener(this);
        recycler.setLayoutManager(new LinearLayoutManager(mActivity));
        recycler.setAdapter(chatAdapter);
    }

    @Override
    public void inCreate() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar.inflateMenu(R.menu.menu_chat);
        toolbar.setTitle(R.string.chat_title_myfriends);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.change:
                    if (isMyFriends) {
                        mPresenter.myUserForCompanyOrProject(App.userID);
                    } else {
                        mPresenter.myFriendList(App.userID);
                    }
                    return true;
            }
            return true;
        }
    };

    @Override
    public void showLoading() {
        ProgressDialog.getInstance().show(mActivity);
    }

    @Override
    public void hideLoading() {
        ProgressDialog.getInstance().dismiss();
    }

    @Override
    public void onError(Throwable throwable) {
        ToastUtil.showToastS(mActivity, throwable.toString());
    }

    @Override
    public void onSuccessMyUserForCompanyOrProject(BaseObjectBean<String> bean) {
        refresh.setRefreshing(false);
        if (bean.isStatus()) {
            toolbar.setTitle(R.string.chat_title_nofriends);
            isMyFriends = false;
            List<Friends> loadList = JsonUtils.getInstance().fromJson(bean.getResult(), new TypeToken<List<Friends>>() {
            }.getType());
            list.clear();
            list.addAll(loadList);
            chatAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showToastS(mActivity, bean.getMessage());
        }
    }

    @Override
    public void onSuccessMyFriendList(BaseObjectBean<String> bean) {
        refresh.setRefreshing(false);
        if (bean.isStatus()) {
            toolbar.setTitle(R.string.chat_title_myfriends);
            isMyFriends = true;
            List<Friends> loadList = JsonUtils.getInstance().fromJson(bean.getResult(), new TypeToken<List<Friends>>() {
            }.getType());
            list.clear();
            list.addAll(loadList);
            chatAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showToastS(mActivity, bean.getMessage());
        }
    }

    @Override
    public void onSuccessAddUserFriend(BaseObjectBean<String> bean) {
        if (bean.isStatus()) {
            onRefresh();
        } else {
            ToastUtil.showToastS(mActivity, bean.getMessage());
        }
    }

    @Override
    public void onSuccessDeleteUserFriend(BaseObjectBean<String> bean) {
        if (bean.isStatus()) {
            onRefresh();
        } else {
            ToastUtil.showToastS(mActivity, bean.getMessage());
        }
    }

    @Override
    public void onSuccessSendMessage(BaseObjectBean<String> bean) {
        if (bean.isStatus()) {
            messageList.clear();
            mPresenter.myChatRecord(App.userID, sendFriend.getFriendUserIds());
            msgText.setText("");
        } else {
            ToastUtil.showToastS(mActivity, bean.getMessage());
        }
    }

    @Override
    public void onSuccessReadMessageCallBack(BaseObjectBean<String> pageBean) {

    }

    @Override
    public void onSuccessMyChatRecord(BaseObjectBean<String> bean) {
        if (bean.isStatus()) {
            List<Message> loadList = JsonUtils.getInstance().fromJson(bean.getResult(), new TypeToken<List<Message>>() {
            }.getType());
            if (loadList != null && loadList.size() > 0) {
                messageList.addAll(loadList);
                chatTalkAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatTalkAdapter.getItemCount() - 1);
                Message lastMesssage = null;//拿到最后一条 好友发的消息
                for (int i = loadList.size() - 1; i >= 0; i--) {
                    if (!loadList.get(i).getSenderIds().equals(App.userID)) {
                        lastMesssage = loadList.get(i);
                        break;
                    }
                }
                if (lastMesssage != null && TextUtils.isEmpty(lastMesssage.getReceiverTime())) {
                    mPresenter.readMessageCallBack(App.userID, lastMesssage.getIds());
                }
            }
        } else {
            ToastUtil.showToastS(mActivity, bean.getMessage());
        }
    }

    @Override
    public void onRefresh() {
        if (isMyFriends) {
            mPresenter.myFriendList(App.userID);
        } else {
            mPresenter.myUserForCompanyOrProject(App.userID);
        }
    }

    @Override
    public void onItemClick(int position) {
        if (isMyFriends) {
            sendFriend = list.get(position);
            showChatDialog();
        } else {
            new AlertDialog.Builder(mActivity)
                    .setTitle(R.string.hint)
                    .setMessage(R.string.chat_guanzhu)
                    .setNegativeButton(R.string.record_camera_cancel_dialog_yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mPresenter.addUserFriend(App.userID, list.get(position).getFriendUserIds());
                                }
                            })
                    .setPositiveButton(R.string.record_camera_cancel_dialog_no, null)
                    .setCancelable(false)
                    .show();
        }
    }

    private void showChatDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_list_chat, null);
        if (talkDialog == null) {
            talkDialog = new Dialog(mActivity, R.style.transparentFrameWindowStyle);
            talkDialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            Window window = talkDialog.getWindow();
            // 设置显示动画
            if (window != null) {
                window.setWindowAnimations(R.style.main_menu_animstyle);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                WindowManager.LayoutParams wl = window.getAttributes();
                wl.x = 0;
                wl.y = mActivity.getWindowManager().getDefaultDisplay().getHeight();
                // 以下这两句是为了保证按钮可以水平满屏
                wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
                wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

                // 设置显示位置
                talkDialog.onWindowAttributesChanged(wl);
            }
            // 设置点击外围解散
            talkDialog.setCanceledOnTouchOutside(true);
            //获取布局中的控件
            Button send = view.findViewById(R.id.chat_send);
            send.setOnClickListener(this);
            friendName = view.findViewById(R.id.chat_friend_name);
            ImageView back = view.findViewById(R.id.chat_back);
            back.setOnClickListener(this);
            TextView delte = view.findViewById(R.id.chat_friend_delete);
            delte.setOnClickListener(this);
            msgText = view.findViewById(R.id.chat_msg);
            //初始化列表
            recyclerView = view.findViewById(R.id.chat_recycler);
            messageList = new ArrayList<>();
            chatTalkAdapter = new ChatTalkAdapter(mActivity, messageList);
            recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
            recyclerView.setAdapter(chatTalkAdapter);
            dialogShow();
        } else {
            dialogShow();
        }
    }

    private void dialogShow() {
        msgText.setText("");
        friendName.setText(sendFriend.getFriendNickname());
        messageList.clear();
        mPresenter.myChatRecord(App.userID, sendFriend.getFriendUserIds());
        talkDialog.show();
    }

    public void dialogDismiss() {
        talkDialog.dismiss();
        onRefresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_send:
                String msg = msgText.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    ToastUtil.showToastS(mActivity, "不可以发送空的内容");
                    return;
                }
                mPresenter.sendMessage(App.userID, sendFriend.getFriendUserIds(), msg);
                break;
            case R.id.chat_back:
                dialogDismiss();
                break;

            case R.id.chat_friend_delete:
                new AlertDialog.Builder(mActivity)
                        .setTitle(R.string.hint)
                        .setMessage(R.string.chat_friend_delete)
                        .setNegativeButton(R.string.record_camera_cancel_dialog_yes,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mPresenter.deleteUserFriend(App.userID, sendFriend.getFriendUserIds());
                                        dialogDismiss();
                                    }
                                })
                        .setPositiveButton(R.string.record_camera_cancel_dialog_no, null)
                        .setCancelable(false)
                        .show();
                break;
        }
    }
}
