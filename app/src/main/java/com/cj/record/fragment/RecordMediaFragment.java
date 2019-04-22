package com.cj.record.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.cj.record.R;
import com.cj.record.activity.MainActivity;
import com.cj.record.activity.VideoPlayerActivity;
import com.cj.record.adapter.MediaAdapter;
import com.cj.record.baen.Gps;
import com.cj.record.baen.Media;
import com.cj.record.baen.Record;
import com.cj.record.base.BaseFragment;
import com.cj.record.db.GpsDao;
import com.cj.record.db.MediaDao;
import com.cj.record.utils.Common;
import com.cj.record.utils.GPSutils;
import com.cj.record.utils.L;
import com.cj.record.utils.ObsUtils;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.Urls;
import com.j256.ormlite.dao.Dao;
import com.mabeijianxi.smallvideorecord2.MediaRecorderActivity;
import com.mabeijianxi.smallvideorecord2.model.MediaRecorderConfig;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import id.zelory.compressor.Compressor;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.iwf.photopicker.PhotoPreview;

/**
 * Created by Administrator on 2018/6/5.
 */

public class RecordMediaFragment extends BaseFragment implements ObsUtils.ObsLinstener, MediaAdapter.OnItemListener {
    @BindView(R.id.recycler_photo)
    RecyclerView recyclerPhoto;
    @BindView(R.id.recycler_video)
    RecyclerView recyclerVideo;

    private MediaAdapter mediaAdapter, mediaAdapter2;
    public List<Media> photoList;
    private List<Media> videoList;
    private ObsUtils obsUtils;
    private Record record;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int PICKER_CODE = 101;
    private AMapLocation amapLocation;
    private RecordLocationFragment locationFragment;
    private File file;
    private List<String> photoPathList;
    private Dialog dialog;

    @Override
    protected void initView(View view) {
        obsUtils = new ObsUtils();
        obsUtils.setObsLinstener(this);
        obsUtils.execute(1);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_record_media;
    }

    private void initRecycleView() {
        LinearLayoutManager mLayoutManager_photo = new LinearLayoutManager(recyclerPhoto.getContext());
        mLayoutManager_photo.setOrientation(LinearLayoutManager.HORIZONTAL);

        LinearLayoutManager mLayoutManager_video = new LinearLayoutManager(recyclerVideo.getContext());
        mLayoutManager_video.setOrientation(LinearLayoutManager.HORIZONTAL);
        mediaAdapter = new MediaAdapter(mActivity, photoList, MediaAdapter.ITEM_MEDIA_TYPE_PHOTO);
        recyclerPhoto.setLayoutManager(mLayoutManager_photo);
        recyclerPhoto.setAdapter(mediaAdapter);
        mediaAdapter.setOnItemListener(this);
        mediaAdapter2 = new MediaAdapter(mActivity, videoList, MediaAdapter.ITEM_MEDIA_TYPE_VIDEO);
        recyclerVideo.setLayoutManager(mLayoutManager_video);
        recyclerVideo.setAdapter(mediaAdapter2);
        mediaAdapter2.setOnItemListener(this);

    }

    private void initRecyclerByType() {
        //记录类型不同，照片或者媒体不显示
        String recordType = record.getType();
        if (recordType.equals(Record.TYPE_SCENE_VIDEO) || recordType.equals(Record.TYPE_SCENE_PRINCIPAL) || recordType.equals(Record.TYPE_SCENE_TECHNICIAN) || recordType.equals(Record.TYPE_SCENE_OPERATEPERSON) || recordType.equals(Record.TYPE_SCENE_OPERATECODE) || recordType.equals(Record.TYPE_SCENE_RECORDPERSON) || recordType.equals(Record.TYPE_SCENE_SCENE)) {
            if (recordType.equals(Record.TYPE_SCENE_VIDEO)) {
                recyclerPhoto.setVisibility(View.GONE);
            } else {
                recyclerVideo.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onSubscribe(int type) {
        switch (type) {
            case 1:
                if (getArguments().containsKey(MainActivity.RECORD)) {
                    record = (Record) getArguments().getSerializable(MainActivity.RECORD);
                }
                photoList = new ArrayList<>();
                videoList = new ArrayList<>();
                photoPathList = new ArrayList<>();
                getMediaList();
                break;
            case 2:
                photoList.clear();
                videoList.clear();
                getMediaList();
                break;
        }
    }

    @Override
    public void onComplete(int type) {
        switch (type) {
            case 1:
                initRecycleView();
                //实例化位置fragment
                FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                locationFragment = (RecordLocationFragment) fragmentManager.findFragmentByTag("locationFragment");
                initRecyclerByType();
                break;
            case 2:
                mediaAdapter.notifyDataSetChanged();
                mediaAdapter2.notifyDataSetChanged();
                break;
        }
    }

    private void refresh() {
        obsUtils.execute(2);
    }

    private void getMediaList() {
        photoPathList.clear();
        List<Media> list = MediaDao.getInstance().getMediaListByRecordID2(record.getId());
        if (list != null && list.size() > 0) {
            for (Media media : list) {
                if (media.getLocalPath().endsWith("jpg")) {
                    photoList.add(media);
                    photoPathList.add(media.getLocalPath());
                } else {
                    videoList.add(media);
                }
            }
        }
    }

    @Override
    public void goVideo(int position) {
        if (!Common.haveGps(mActivity)) {
            return;
        }
        amapLocation = locationFragment.aMapLocation;
        if (null == amapLocation) {
            ToastUtil.showToastS(mActivity, "未获取定位信息");
            return;
        }
        openVideo();
    }


    @Override
    public void goCamera(int position) {
        if (!Common.haveGps(mActivity)) {
            return;
        }
        amapLocation = locationFragment.aMapLocation;
        if (null == amapLocation) {
            ToastUtil.showToastS(mActivity, "未获取定位信息");
            return;
        }
        openCamera();
    }

    @Override
    public void showPhoto(int position) {
        PhotoPreview.builder()
                .setPhotos((ArrayList<String>) photoPathList)
                .setCurrentItem(position)
                .setShowDeleteButton(false)
                .start(mActivity);
    }

    @Override
    public void showVideo(int position) {
        Media media = videoList.get(position);
        Intent intent = new Intent(mActivity, VideoPlayerActivity.class);
        intent.putExtra("mediaId", media.getId());
        intent.putExtra(MediaRecorderActivity.VIDEO_URI, Common.getVideoByDir((media.getLocalPath())));
        intent.putExtra(MediaRecorderActivity.OUTPUT_DIRECTORY, media.getLocalPath());
        startActivity(intent);
    }

    @Override
    public void deletePhoto(int position) {
        hesitate(position);
    }

    /**
     * 打开视频录制
     */
    private void openVideo() {
        MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
                .fullScreen(false)
                .smallVideoWidth(360)
                .smallVideoHeight(480)
                .recordTimeMax(1800 * 1000)
                .maxFrameRate(20)
                .minFrameRate(8)
                .captureThumbnailsTime(1)
                .recordTimeMin((int) (1.5 * 1000))
                .build();
        MediaRecorderActivity.goSmallVideoRecorder(mActivity, VideoPlayerActivity.class.getName(), config);
    }

    private void hesitate(final int position) {
        final Media media = photoList.get(position);
        if (dialog == null) {
            dialog = new AlertDialog.Builder(mActivity)
                    .setTitle(R.string.hint)
                    .setMessage("确定删除该图片吗")
                    .setNegativeButton(R.string.record_camera_cancel_dialog_yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MediaDao.getInstance().delete(media);
                                    ToastUtil.showToastL(getActivity(), "删除图片成功");
                                    photoList.remove(position);
                                    refresh();
                                }
                            })
                    .setPositiveButton(R.string.record_camera_cancel_dialog_no, null)
                    .setCancelable(false)
                    .show();
        } else {
            dialog.show();
        }
    }

    /**
     * 指定Uri
     * 调用系统相机
     */
    private void openCamera() {
        new RxPermissions(mActivity).requestEach(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(@NonNull Permission permission) throws Exception {
                        if (permission.granted) {
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // Denied permission without ask never again
                            ToastUtil.showToastS(mActivity, "取消照相机授权");
                        } else {
                            // Denied permission with ask never again
                            // Need to go to the
                            ToastUtil.showToastS(mActivity, "您已经禁止弹出照相机的授权操作,请在设置中手动开启");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.i("--->>", "onError", throwable);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                            //创建一个拍照照片的临时文件夹
                            File folder = new File(Urls.APP_PATH_CACHE);
                            if (!folder.exists()) {
                                folder.mkdirs();
                            }
                            file = new File(Urls.APP_PATH_CACHE, Common.getUUID() + ".jpg");
                            if (Build.VERSION.SDK_INT >= 24) {
                                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(mActivity, "com.cj.reocrd.fileprovider", file));
                                startActivityForResult(takeIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                            } else {
                                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                                startActivityForResult(intent2, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                            }

                        } else {
                            ToastUtil.showToastS(mActivity, "外部储存没有挂载");
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //调用系统相机
        if (CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
            if (Activity.RESULT_OK == resultCode) {
                try {
                    //拍照之后存在老的文件夹里，压缩之后的存在新的文件夹里，删除老的文件
                    File newFile = new Compressor(mActivity)
                            .setMaxWidth(200)
                            .setMaxHeight(220)
                            .setQuality(93)
                            .setDestinationDirectoryPath(Urls.PIC_PATH)
                            .compressToFile(file);

                    if (file.exists()) {
                        file.delete();
                    }
                    //修改图片的gps属性
                    GPSutils.setNewThumbnail(newFile.getAbsolutePath(), amapLocation);
                    //创建新的对象
                    Media media = new Media(mActivity, record, newFile.getAbsolutePath(), amapLocation);

                    MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), newFile.getAbsolutePath(), "title", "description");
                    mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(newFile.getAbsolutePath()))));

                    Gps gps = new Gps(media, amapLocation, "照片");
                    media.setGpsID(gps.getId());
                    //写入数据库
                    MediaDao.getInstance().addOrUpdate(media);
                    GpsDao.getInstance().addOrUpdate(gps);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                refresh();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                L.e("TAG", "User cancelled the image capture");
            } else {
                L.e("TAG", "UImage capture failed, advise user");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        L.e("onResume-----" + SPUtils.get(mActivity, "directoryPath", "") + "");
        String dirPath = (String) SPUtils.get(mActivity, "directoryPath", "");
        //判断是否是录像
        if (!TextUtils.isEmpty(dirPath)) {
            if (dirPath.equals("delete")) {
                L.e("onResume-----delete");
            } else {
                L.e("onResume-----path");
                //获取位置信息，经纬度
                amapLocation = locationFragment.aMapLocation;
                try {
                    //这里保存的是文件夹路径，不修改属性，创建新的对象
                    Media media = new Media(mActivity, record, dirPath, amapLocation);
                    Gps gps = new Gps(media, amapLocation, "录像");
                    media.setGpsID(gps.getId());
                    //写入数据库
                    MediaDao.getInstance().addOrUpdate(media);
                    GpsDao.getInstance().addOrUpdate(gps);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            SPUtils.put(mActivity, "directoryPath", "");
        }
        refresh();
    }
}
