package com.cj.record.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cj.record.R;
import com.cj.record.baen.Hole;
import com.cj.record.baen.Project;
import com.cj.record.baen.Record;
import com.cj.record.db.RecordDao;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

//import com.geotdb.compile.fragment.HoleListFragment;

/**
 * @author Aidan Follestad (afollestad)
 */
public class HoleInfoDialog extends DialogFragment {
    Context context;
    @BindView(R.id.hole_project_name)
    TextView holeProjectName;
    @BindView(R.id.hole_close_ib)
    ImageButton holeCloseIb;
    @BindView(R.id.hole_code)
    TextView holeCode;
    @BindView(R.id.hole_type)
    TextView holeType;
    @BindView(R.id.hole_elevation)
    TextView holeElevation;
    @BindView(R.id.hole_depth)
    TextView holeDepth;
    @BindView(R.id.hole_operatePerson)
    TextView holeOperatePerson;
    @BindView(R.id.hole_operateCode)
    TextView holeOperateCode;
    @BindView(R.id.hole_radius)
    TextView holeRadius;
    @BindView(R.id.hole_mapTime)
    TextView holeMapTime;
    Unbinder unbinder;
    private Project project;
    private Hole hole;
    private Record jRecord, zRecord;

    public HoleInfoDialog() {

    }

    public void show(AppCompatActivity context, Project project, Hole hole) {
        this.hole = hole;
        this.context = context;
        this.project = project;
        jRecord = RecordDao.getInstance().getRecordByType(hole.getId(), Record.TYPE_SCENE_OPERATEPERSON);
        zRecord = RecordDao.getInstance().getRecordByType(hole.getId(), Record.TYPE_SCENE_OPERATECODE);
        show(context.getSupportFragmentManager(), "FOLDER_SELECTOR");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).customView(R.layout.dlg_hole_info, false).build();
        dialog.setCanceledOnTouchOutside(false); // 点背景不会消失
        unbinder = ButterKnife.bind(this, dialog);
        holeProjectName.setText(project.getFullName());
        holeCloseIb.setOnClickListener(closeListener);
        holeCode.setText(hole.getCode());
        holeType.setText(hole.getType());
        holeElevation.setText(hole.getElevation());
        holeDepth.setText(hole.getDepth());
        if (null != jRecord) {
            holeOperatePerson.setText(jRecord.getOperatePerson());
        }
        if (null != zRecord) {
            holeOperateCode.setText(zRecord.getTestType());
        }
        holeRadius.setText(hole.getRadius());
        holeMapTime.setText(hole.getMapTime());
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
