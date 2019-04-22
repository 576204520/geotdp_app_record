package com.cj.record.activity;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.amap.api.location.AMapLocation;
import com.cj.record.R;
import com.cj.record.base.BaseActivity;
import com.cj.record.baen.Media;
import com.cj.record.db.MediaDao;
import com.cj.record.fragment.RecordLocationFragment;
import com.cj.record.utils.Common;
import com.cj.record.utils.SPUtils;
import com.cj.record.utils.ToastUtil;
import com.mabeijianxi.smallvideorecord2.FileUtils;
import com.mabeijianxi.smallvideorecord2.MediaRecorderActivity;
import com.mabeijianxi.smallvideorecord2.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by Administrator on 2018/6/11.
 */

public class VideoPlayerActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.videoplayer)
    JCVideoPlayerStandard videoplayer;
    private String videoUri;
    private String directoryPath;
    private AlertDialog dialog;

    private String mediaId;
    private Media media;
    private MediaDao mediaDao;
    private boolean isLook = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_player;
    }

    @Override
    public void initView() {
        toolbar.setTitle("录像");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_clear_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        videoUri = intent.getStringExtra(MediaRecorderActivity.VIDEO_URI);
        directoryPath = intent.getStringExtra(MediaRecorderActivity.OUTPUT_DIRECTORY);
        mediaId = intent.getStringExtra("mediaId");

        if (StringUtils.isEmpty(videoUri)) {
            finish();
            return;
        }
        if (StringUtils.isEmpty(directoryPath)) {
            finish();
            return;
        }
        if (!StringUtils.isEmpty(mediaId)) {
            media = MediaDao.getInstance().getMediaByID(mediaId);
            isLook = true;
        }
        videoplayer.setUp(videoUri, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_record_edit, menu);
        if (isLook) {
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(false);
                menu.getItem(i).setEnabled(false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                hesitate();
                return true;
            case R.id.act_save:
                setResult(RESULT_OK);
                save();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        if (StringUtils.isEmpty(mediaId)) {
            SPUtils.put(this, "directoryPath", directoryPath);
        } else {
            SPUtils.put(this, "directoryPath", "");
        }
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (isLook) {
            SPUtils.put(this, "directoryPath", "");
            finish();
        } else {
            hesitate();
        }
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    private void hesitate() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.hint)
                    .setMessage(R.string.record_camera_exit_dialog_message)
                    .setNegativeButton(R.string.record_camera_cancel_dialog_yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SPUtils.put(VideoPlayerActivity.this, "directoryPath", "delete");
                                    FileUtils.deleteDir(getIntent().getStringExtra(MediaRecorderActivity.OUTPUT_DIRECTORY));
                                    if (!StringUtils.isEmpty(mediaId)) {
                                        media.delete(VideoPlayerActivity.this);
                                    }
                                    try {
                                        MediaStore.Images.Media.insertImage(getContentResolver(), videoUri, "title", "description");
                                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(videoUri))));
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    finish();
                                }
                            })
                    .setPositiveButton(R.string.record_camera_cancel_dialog_no, null)
                    .setCancelable(false)
                    .show();
        } else {
            dialog.show();
        }
    }
}
