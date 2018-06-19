package com.cj.record.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.baen.Media;
import com.cj.record.utils.Common;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/6/5.
 */

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MyHolder> {
    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;
    public static final int ITEM_MEDIA_TYPE_PHOTO = 0;
    public static final int ITEM_MEDIA_TYPE_VIDEO = 1;

    private List<Media> list;
    private Context mContext;
    private LayoutInflater inflater;
    private int mediaType;

    public MediaAdapter(Context context, List<Media> list, int mediaType) {
        this.mContext = context;
        this.list = list;
        inflater = LayoutInflater.from(mContext);
        this.mediaType = mediaType;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_media, parent, false);
            return new MyHolder(view, 0);
        }
        view = LayoutInflater.from(mContext).inflate(R.layout.item_media, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        MyHolder myHolder = (MyHolder) holder;
        //区别第一项
        if (getItemViewType(position) == ITEM_VIEW_TYPE_HEADER) {
            //区别录音和拍照
            switch (mediaType) {
                case ITEM_MEDIA_TYPE_PHOTO:
                    myHolder.mediaName.setText("拍摄照片");
                    myHolder.mediaPhotoIcon.setImageResource(R.mipmap.ai_icon_photo);
                    break;
                case ITEM_MEDIA_TYPE_VIDEO:
                    myHolder.mediaName.setText("录制视频");
                    myHolder.mediaPhotoIcon.setImageResource(R.mipmap.ai_icon_video);
                    break;
            }
            myHolder.mediaCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //区别录音和拍照
                    switch (mediaType) {
                        case ITEM_MEDIA_TYPE_PHOTO:
                            mOnItemListener.goCamera(position);
                            break;
                        case ITEM_MEDIA_TYPE_VIDEO:
                            mOnItemListener.goVideo(position);
                            break;
                    }
                }
            });
            myHolder.mediaDelete.setVisibility(View.GONE);
        } else {
            //始终显示header，所以这里-1，getItemCount+1
            Media media = list.get(position - 1);
            Uri uri;
            if (mediaType == ITEM_MEDIA_TYPE_PHOTO) {
                uri = Uri.parse(media.getLocalPath());
            } else {
                uri = Uri.parse(Common.getPicByDir(media.getLocalPath()));
            }
            myHolder.mediaPhoto.setImageURI(uri);
            myHolder.mediaName.setText(media.getName());
            //区别录音和拍照
            switch (mediaType) {
                case ITEM_MEDIA_TYPE_PHOTO:
                    myHolder.mediaPhotoIcon.setImageResource(R.mipmap.ai_icon_havephoto);
                    break;
                case ITEM_MEDIA_TYPE_VIDEO:
                    myHolder.mediaPhotoIcon.setImageResource(R.mipmap.ai_icon_video);
                    break;
            }
            myHolder.mediaCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //区别录音和拍照
                    switch (mediaType) {
                        case ITEM_MEDIA_TYPE_PHOTO:
                            //监听中的position是list的下标，没有header，所以-1
                            mOnItemListener.showPhoto(position - 1);
                            break;
                        case ITEM_MEDIA_TYPE_VIDEO:
                            mOnItemListener.showVideo(position - 1);
                            break;
                    }
                }
            });
            myHolder.mediaDelete.setVisibility(View.VISIBLE);
            myHolder.mediaDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemListener.deletePhoto(position - 1);
                }
            });
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_VIEW_TYPE_HEADER;
        }
        return ITEM_VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    private OnItemListener mOnItemListener;

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public interface OnItemListener {
        void goVideo(int position);

        void goCamera(int position);

        void showPhoto(int position);

        void showVideo(int position);

        void deletePhoto(int position);
    }


    class MyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.media_card)
        CardView mediaCard;
        @BindView(R.id.media_delete)
        ImageView mediaDelete;
        @BindView(R.id.media_photo)
        ImageView mediaPhoto;
        @BindView(R.id.media_photo_icon)
        ImageView mediaPhotoIcon;
        @BindView(R.id.media_name)
        TextView mediaName;

        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public MyHolder(View itemView, int header) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
