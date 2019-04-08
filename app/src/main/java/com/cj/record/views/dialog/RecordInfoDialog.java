package com.cj.record.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cj.record.R;
import com.cj.record.baen.Record;
import com.cj.record.db.MediaDao;
import com.cj.record.db.RecordDao;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Aidan Follestad (afollestad)
 */
public class RecordInfoDialog extends DialogFragment {
    Context context;
    @BindView(R.id.reocrd_hole_code)
    TextView recordHoleCode;
    @BindView(R.id.record_close_ib)
    ImageButton recordCloseIb;
    @BindView(R.id.reocrd_code)
    TextView reocrdCode;
    @BindView(R.id.reocrd_type)
    TextView reocrdType;
    @BindView(R.id.reocrd_depth)
    TextView reocrdDepth;
    @BindView(R.id.reocrd_content)
    TextView reocrdContent;
    @BindView(R.id.reocrd_createTime)
    TextView reocrdCreateTime;
    @BindView(R.id.reocrd_updateTime)
    TextView reocrdUpdateTime;
    @BindView(R.id.reocrd_media)
    TextView reocrdMedia;
    Unbinder unbinder;
    private Record record;
    private RecordDao reocrdDao;
    private MediaDao mediaDao;
    private String projectName;
    private String holeCode;

    public void show(AppCompatActivity context, Record record, String holeCode) {
        this.context = context;
        this.holeCode = holeCode;
        this.record = record;
        mediaDao = new MediaDao(context);
        show(context.getSupportFragmentManager(), "FOLDER_SELECTOR");
    }

    public RecordInfoDialog() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog dialog = new MaterialDialog.Builder(context).customView(R.layout.dlg_record_info, true).build();
        dialog.setCanceledOnTouchOutside(false); // 点背景不会消失
        unbinder = ButterKnife.bind(this,dialog);
        recordCloseIb.setOnClickListener(closeListener);
        recordHoleCode.setText(holeCode);
        reocrdCode.setText(record.getCode());
        reocrdType.setText(record.getType());
        //取水和水位的显示不太一样
        if (record.getType().equals(Record.TYPE_WATER)) {
            String begin = Double.parseDouble(record.getBeginDepth()) > 0 ? record.getBeginDepth() : "-";
            String end = Double.parseDouble(record.getEndDepth()) > 0 ? record.getEndDepth() : "-";
            reocrdDepth.setText(begin + "/" + end);
        } else if (record.getType().equals(Record.TYPE_GET_WATER)) {
            reocrdDepth.setText(record.getBeginDepth());
        } else {
            reocrdDepth.setText(record.getBeginDepth() + "~" + record.getEndDepth());
        }
        reocrdContent.setText(Html.fromHtml(record.getContent()).toString());
        reocrdCreateTime.setText(record.getCreateTime());
        reocrdUpdateTime.setText(record.getUpdateTime());
        reocrdMedia.setText(mediaDao.getMediaCountByrdcordID(record.getId()) + "");
        return dialog;
    }

    View.OnClickListener closeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
