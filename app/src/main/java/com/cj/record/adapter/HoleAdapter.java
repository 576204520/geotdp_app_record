package com.cj.record.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.baen.Hole;
import com.cj.record.baen.Project;
import com.cj.record.slide.AbstractSlideExpandableListAdapter;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.Urls;

import net.qiujuer.genius.ui.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/6/4.
 */

public class HoleAdapter extends AbstractSlideExpandableListAdapter<HoleAdapter.MyHolder> implements AbstractSlideExpandableListAdapter.OnItemExpandCollapseListener {


    private List<Hole> list;
    private Context mContext;
    private LayoutInflater inflater;
    private List<MyHolder> holderList;
    private Project project;

    public HoleAdapter(Context context, List<Hole> list, Project project) {
        this.mContext = context;
        this.list = list;
        inflater = LayoutInflater.from(mContext);
        holderList = new ArrayList<>();
        this.project = project;
//        setItemExpandCollapseListener(this);
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(inflater.inflate(R.layout.item_hole, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        MyHolder myHolder = (MyHolder) holder;
        Hole hole = list.get(position);
        boolean isRelate;
        boolean isLocation;
        //判断是否关联
        if (TextUtils.isEmpty(hole.getRelateCode()) || TextUtils.isEmpty(hole.getRelateID())) {
            isRelate = false;
        } else {
            isRelate = true;
        }
        if(isRelate){
            myHolder.holeCode.setText(hole.getRelateCode());
            myHolder.holeCodeRelate.setText("(" + hole.getCode() + ")");
            myHolder.holeRelate.setVisibility(View.VISIBLE);
        }else{
            myHolder.holeCode.setText(hole.getCode());
            myHolder.holeCodeRelate.setText("");
            myHolder.holeRelate.setVisibility(View.GONE);
        }
        //判断是否定位
        if (TextUtils.isEmpty(hole.getMapLatitude()) || TextUtils.isEmpty(hole.getMapLongitude())) {
            myHolder.holeLocation.setVisibility(View.GONE);
            myHolder.holeMapTime.setVisibility(View.GONE);
            isLocation = false;
        } else {
            myHolder.holeLocation.setVisibility(View.VISIBLE);
            myHolder.holeMapTime.setVisibility(View.VISIBLE);
            myHolder.holeMapTime.setText("定位时间:" + hole.getMapTime());
            isLocation = true;
        }
        //判断是否上传
        if (isRelate && isLocation) {
            if (!TextUtils.isEmpty(project.getProjectID())&&project.isUpload()) {
                if ("2".equals(hole.getState()) && "2".equals(hole.getStateGW())) {
                    myHolder.holeRight.setVisibility(View.VISIBLE);
                } else {
                    myHolder.holeRight.setVisibility(View.GONE);
                }
            } else {
                if ("2".equals(hole.getState())) {
                    myHolder.holeRight.setVisibility(View.VISIBLE);
                } else {
                    myHolder.holeRight.setVisibility(View.GONE);
                }
            }
        } else {
            myHolder.holeRight.setVisibility(View.GONE);
        }

        //获取到的深度
        if (null != hole.getCurrentDepth() && !"null".equals(hole.getCurrentDepth())) {
            myHolder.holeDepth.setText("深度:" + hole.getCurrentDepth() + "m");
        } else {
            myHolder.holeDepth.setText("深度:0m");
        }
        myHolder.holeType.setText("类型:" + hole.getType());
//        myHolder.holeCreateTime.setText("开始时间:" + hole.getCreateTime());//同定位时间
        myHolder.holeUpdateTime.setText("修改时间:" + hole.getUpdateTime());
        //判断是否是获取的数据
        if (TextUtils.isEmpty(hole.getUserID())) {
            myHolder.holeGetData.setVisibility(View.GONE);
        } else {
            if (hole.getUserID().equals(SPUtils.get(mContext, Urls.SPKey.USER_ID, ""))) {
                myHolder.holeGetData.setText("获取的本人数据，可以编辑");
            } else {
                myHolder.holeGetData.setText("获取的他人数据，不可以编辑，只能查看");
            }
            myHolder.holeGetData.setVisibility(View.VISIBLE);
        }
        myHolder.holeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemListener.detailClick(position);
            }
        });
        myHolder.holeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemListener.editClick(position);
            }
        });
        myHolder.holeRecordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemListener.recordListClick(position);
            }
        });
        myHolder.holeCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemListener.checkClick(position);
            }
        });
        myHolder.holeUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemListener.uploadClick(position);
            }
        });
        myHolder.holeDelete.setOnClickListener(new View.OnClickListener() {
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

        void recordListClick(int position);

        void checkClick(int position);

        void uploadClick(int position);

        void deleteClick(int position);
    }

    public void delete(int position) {
        list.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public View getExpandToggleButton(View parent) {
        return parent.findViewById(R.id.hole_ll);
    }

    @Override
    public View getExpandableView(View parent) {
        return parent.findViewById(R.id.hole_btn_ll);
    }

    @Override
    public int getItemCount() {
        return list.size() == 0 ? 0 : list.size();
    }

    @Override
    public void onExpand(View itemView, int position) {

    }

    @Override
    public void onCollapse(View itemView, int position) {

    }

    class MyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.hole_logo)
        ImageView holeLogo;
        @BindView(R.id.hole_right)
        ImageView holeRight;
        @BindView(R.id.hole_code)
        TextView holeCode;
        @BindView(R.id.hole_code_relate)
        TextView holeCodeRelate;
        @BindView(R.id.hole_type)
        TextView holeType;
        @BindView(R.id.hole_depth)
        TextView holeDepth;
        @BindView(R.id.hole_location)
        TextView holeLocation;
        @BindView(R.id.hole_relate)
        TextView holeRelate;
        @BindView(R.id.hole_createTime)
        TextView holeCreateTime;
        @BindView(R.id.hole_updateTime)
        TextView holeUpdateTime;
        @BindView(R.id.hole_mapTime)
        TextView holeMapTime;
        @BindView(R.id.hole_detail)
        Button holeDetail;
        @BindView(R.id.hole_edit)
        Button holeEdit;
        @BindView(R.id.hole_recordList)
        Button holeRecordList;
        @BindView(R.id.hole_check)
        Button holeCheck;
        @BindView(R.id.hole_upload)
        Button holeUpload;
        @BindView(R.id.hole_delete)
        Button holeDelete;
        @BindView(R.id.hole_btn_ll)
        LinearLayout holeBtnLl;
        @BindView(R.id.hole_ll)
        LinearLayout holeLl;
        @BindView(R.id.hole_getData)
        TextView holeGetData;

        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
