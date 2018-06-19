package com.cj.record.fragment.record;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.amap.api.location.AMapLocation;
import com.cj.record.R;
import com.cj.record.adapter.DropListAdapter;
import com.cj.record.baen.Dictionary;
import com.cj.record.baen.DropItemVo;
import com.cj.record.baen.Record;
import com.cj.record.fragment.RecordLocationFragment;
import com.cj.record.utils.Common;
import com.cj.record.utils.ToastUtil;
import com.cj.record.views.MaterialBetterSpinner;
import com.cj.record.views.MaterialEditTextElevation;
import com.cj.record.views.MaterialEditTextNoEmoji;
import com.rengwuxian.materialedittext.MaterialEditText;

import net.qiujuer.genius.ui.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 水位编辑
 */
public class RecordEditWaterFragment extends RecordBaseFragment {

    @BindView(R.id.waterType)
    MaterialBetterSpinner waterType;
    @BindView(R.id.edtWaterShow)
    MaterialEditTextElevation edtWaterShow;
    @BindView(R.id.btnAddWaterShow)
    Button btnAddWaterShow;
    @BindView(R.id.edtWaterShowTime)
    MaterialEditTextNoEmoji edtWaterShowTime;
    @BindView(R.id.edtWaterStable)
    MaterialEditTextElevation edtWaterStable;
    @BindView(R.id.btnAddWaterStable)
    Button btnAddWaterStable;
    @BindView(R.id.edtWaterStableTime)
    MaterialEditTextNoEmoji edtWaterStableTime;

    List<DropItemVo> typeList;
    DropListAdapter typeListAdapter;
    private int sortNoType = 0;
    private RecordLocationFragment locationFragment;
    private AMapLocation amapLocation;

    @Override
    public int getLayoutId() {
        return R.layout.frt_record_water_edit;
    }

    @Override
    public void initData() {
        super.initData();
        typeList = dictionaryDao.getDropItemList(getSqlString("地下水类型"));
        typeListAdapter = new DropListAdapter(mActivity, R.layout.drop_item, typeList);
        waterType.setAdapter(typeListAdapter);
        //设置可自定义和可清空
        waterType.setCustom();
        waterType.setOnItemClickListener(typeListener);
        //实例化位置fragment
        FragmentManager fragmentManager = mActivity.getFragmentManager();
        locationFragment = (RecordLocationFragment) fragmentManager.findFragmentByTag("locationFragment");
    }

    @Override
    public void initView() {
        super.initView();
        waterType.setText(record.getWaterType());
        edtWaterShow.setText(record.getShownWaterLevel());
        edtWaterShowTime.setText(record.getShownTime());
        edtWaterStable.setText(record.getStillWaterLevel());
        edtWaterStableTime.setText(record.getStillTime());
    }

    @OnClick({R.id.btnAddWaterShow, R.id.btnAddWaterStable})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAddWaterShow:
                if (isLocation()) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    edtWaterShowTime.setText(df.format(date));
                }
                break;
            case R.id.btnAddWaterStable:
                if (isLocation()) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    edtWaterStableTime.setText(df.format(date));
                }
                break;
        }
    }

    private boolean isLocation() {
        if (!Common.gPSIsOPen(mActivity)) {
            ToastUtil.showToastS(mActivity, "GPS未开启，请开启以提高精度");
            return false;
        }
        amapLocation = locationFragment.aMapLocation;
        if (null == amapLocation) {
            ToastUtil.showToastS(mActivity, "未获取定位信息");
            return false;
        }
        return true;
    }


    MaterialBetterSpinner.OnItemClickListener typeListener = new MaterialBetterSpinner.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            sortNoType = i == adapterView.getCount() - 1 ? i : 0;
        }
    };

    @Override
    public Record getRecord() {
        record.setWaterType(waterType.getText().toString());
        record.setShownWaterLevel(edtWaterShow.getText().toString());
        record.setShownTime(edtWaterShowTime.getText().toString());
        record.setStillWaterLevel(edtWaterStable.getText().toString());
        record.setStillTime(edtWaterStableTime.getText().toString());
        return record;
    }

    @Override
    public boolean validator() {
        boolean validator = true;
        double waterShow = 0.0;
        double waterStable = 0.0;
        String show = edtWaterShow.getText().toString();
        String stable = edtWaterStable.getText().toString();
        if (("".equals(show) || Double.valueOf(show) == 0) && ("".equals(stable) || Double.valueOf(stable) == 0)) {
            validator = false;
            edtWaterShow.setError("至少输入一种水位");
            edtWaterStable.setError("至少输入一种水位");
        } else {
            String sh = edtWaterShow.getText().toString();
            if (sh.equals("")) {
                sh = "0";
            }
            String st = edtWaterStable.getText().toString();
            if (st.equals("")) {
                st = "0";
            }
            waterShow = Double.valueOf(sh);
            waterStable = Double.valueOf(st);
            if (waterShow > 0 && "".equals(edtWaterShowTime.getText().toString())) {
                validator = false;
                edtWaterShowTime.setError("请更新初见水位时间");
            }
            if (waterStable > 0 && "".equals(edtWaterStableTime.getText().toString())) {
                validator = false;
                edtWaterStableTime.setError("请更新稳定水位时间");
            }
            //有时间的，水位为零
            if (!"".equals(edtWaterShowTime.getText().toString()) && waterShow == 0) {
                validator = false;
                edtWaterShow.setError("请输入深度");
            }
            if (!"".equals(edtWaterStableTime.getText().toString()) && waterStable == 0) {
                validator = false;
                edtWaterStable.setError("请输入深度");
            }
        }

        if (sortNoType > 0 && validator) {
            dictionaryDao.addDictionary(new Dictionary("1", "地下水类型", waterType.getText().toString(), "" + sortNoType, userID, Record.TYPE_WATER));
        }
        return validator;
    }

    @Override
    public String getTitle() {
        String title = waterType.getText().toString();
        return title;
    }

    @Override
    public String getTypeName() {
        return Record.TYPE_WATER;
    }

    @Override
    public String getBegin() {
        return edtWaterShow.getText().toString();
    }

    @Override
    public String getEnd() {
        return edtWaterStable.getText().toString();
    }


}
