package com.cj.record.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.baen.Hole;
import com.cj.record.baen.Project;
import com.cj.record.db.HoleDao;
import com.cj.record.slide.AbstractSlideExpandableListAdapter;
import com.cj.record.utils.L;

import net.qiujuer.genius.ui.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/8/24.
 */
public class ProjectAdapter extends AbstractSlideExpandableListAdapter<ProjectAdapter.MyHolder> implements AbstractSlideExpandableListAdapter.OnItemExpandCollapseListener {


    private List<Project> list;
    private Context mContext;
    private LayoutInflater inflater;
    private List<MyHolder> holderList;
    private HoleDao holeDao;
    public View view;

    public ProjectAdapter(Context context, List<Project> list) {
        this.mContext = context;
        this.list = list;
        inflater = LayoutInflater.from(mContext);
        holderList = new ArrayList<>();
        setItemExpandCollapseListener(this);
        holeDao = new HoleDao(context);
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_project, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        Project project = list.get(position);
        MyHolder myHolder = (MyHolder) holder;
        holderList.add(myHolder);
        myHolder.projectFullName.setText(project.getFullName());
        myHolder.projectUpdateTime.setText("创建时间:" + project.getCreateTime());
        List<Hole> holes = holeDao.getHoleListByProjectID(project.getId());
        myHolder.projectHoleNumber.setText("钻孔数:" + holes.size());
        //钻孔编号
        if (TextUtils.isEmpty(project.getCode())) {
            myHolder.projectCode.setText("");
        } else {
            myHolder.projectCode.setText("(" + project.getCode() + ")");
        }
        //关联状态
        if (TextUtils.isEmpty(project.getSerialNumber())) {
            myHolder.projectState.setText("关联状态:未关联");
            myHolder.projectState.setTextColor(mContext.getResources().getColor(R.color.colorTexthintGrey2));
            myHolder.projectHoleNumber.setTextColor(mContext.getResources().getColor(R.color.colorTexthintGrey2));
        } else {
            myHolder.projectState.setText("关联状态:已关联");
            myHolder.projectState.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            myHolder.projectHoleNumber.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        }
        //按钮添加监听
        myHolder.projectDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemListener.detailClick(position);
            }
        });
        myHolder.projectEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemListener.editClick(position);
            }
        });
        myHolder.projectHoleList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemListener.holeListClick(position);
            }
        });
        myHolder.projectNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemListener.navClick(position);
            }
        });
        myHolder.projectUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemListener.uploadClick(position);
            }
        });
        myHolder.projectDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemListener.deleteClick(position);
            }
        });
    }

    private OnItemListener mOnItemListener;

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public interface OnItemListener {
        void detailClick(int position);

        void editClick(int position);

        void holeListClick(int position);

        void navClick(int position);

        void uploadClick(int position);

        void deleteClick(int position);
    }

    public void delete(int position) {
        list.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public View getExpandToggleButton(View parent) {
        return parent.findViewById(R.id.project_ll);
    }

    @Override
    public View getExpandableView(View parent) {
        return parent.findViewById(R.id.project_btn_ll);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onExpand(View itemView, int position) {
        //打开状态
//        MyHolder myHolder = holderList.get(position);
//        myHolder.projectConRl.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
//        myHolder.projectLogo.setBackgroundResource(R.mipmap.project_icon_select);
//        myHolder.projectFullName.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
//        myHolder.projectCode.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
//        myHolder.projectHoleNumber.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
//        myHolder.projectState.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
//        myHolder.projectUpdateTime.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
    }

    @Override
    public void onCollapse(View itemView, int position) {
        //闭合状态
//        MyHolder myHolder = holderList.get(position);
//        myHolder.projectConRl.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
//        myHolder.projectLogo.setBackgroundResource(R.mipmap.project_icon);
//        myHolder.projectFullName.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
//        myHolder.projectCode.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
//        myHolder.projectHoleNumber.setTextColor(mContext.getResources().getColor(R.color.colorTexthintGrey2));
//        myHolder.projectState.setTextColor(mContext.getResources().getColor(R.color.colorTexthintGrey2));
//        myHolder.projectUpdateTime.setTextColor(mContext.getResources().getColor(R.color.colorTexthintGrey2));
    }

    class MyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.project_logo)
        ImageView projectLogo;
        @BindView(R.id.project_right)
        ImageView projectRight;
        @BindView(R.id.project_fullName)
        TextView projectFullName;
        @BindView(R.id.project_code)
        TextView projectCode;
        @BindView(R.id.project_holeNumber)
        TextView projectHoleNumber;
        @BindView(R.id.project_state)
        TextView projectState;
        @BindView(R.id.project_updateTime)
        TextView projectUpdateTime;
        @BindView(R.id.project_detail)
        Button projectDetail;
        @BindView(R.id.project_edit)
        Button projectEdit;
        @BindView(R.id.project_holeList)
        Button projectHoleList;
        @BindView(R.id.project_nav)
        Button projectNav;
        @BindView(R.id.project_upload)
        Button projectUpload;
        @BindView(R.id.project_delete)
        Button projectDelete;
        @BindView(R.id.project_btn_ll)
        LinearLayout projectBtnLl;
        @BindView(R.id.project_ll)
        LinearLayout projectLl;
        @BindView(R.id.project_con_rl)
        RelativeLayout projectConRl;

        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
