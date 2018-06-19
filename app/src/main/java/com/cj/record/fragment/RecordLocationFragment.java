/*
 * Copyright (C) 2015 The Android Open Source PowerLog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cj.record.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;
import com.cj.record.R;
import com.cj.record.activity.MainActivity;
import com.cj.record.activity.base.BaseFragment;
import com.cj.record.baen.Hole;
import com.cj.record.utils.Common;
import com.cj.record.views.MaterialEditTextNoEmoji;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecordLocationFragment extends BaseFragment implements LocationSource, AMapLocationListener {


    public AMapLocation aMapLocation = null;
    @BindView(R.id.edtLatitude)
    MaterialEditTextNoEmoji edtLatitude;
    @BindView(R.id.edtLongitude)
    MaterialEditTextNoEmoji edtLongitude;
    @BindView(R.id.edtLocationTime)
    MaterialEditTextNoEmoji edtLocationTime;
    @BindView(R.id.edtAccuracy)
    MaterialEditTextNoEmoji edtAccuracy;
    //声明AMapLocationClient类对象，定位发起端
    private AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象，定位参数
    public AMapLocationClientOption mLocationOption = null;
    //声明mListener对象，定位监听器
    private OnLocationChangedListener mListener = null;
    private Hole hole;
    public Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (getArguments().containsKey(MainActivity.HOLE)) {
            hole = (Hole) getArguments().getSerializable(MainActivity.HOLE);
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_record_location;
    }

    @Override
    public void initView() {
        this.aMapLocation = null;
        edtLongitude.setText("");
        edtLatitude.setText("");
        edtLocationTime.setText("");
        edtAccuracy.setText("");
        location();
    }


    public void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(context);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mLocationClient.stopLocation();//停止定位
        mLocationClient.onDestroy();//销毁定位客户端。
    }


    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                this.aMapLocation = aMapLocation;
                // 定位成功回调信息，设置相关消息
                edtLongitude.setText(String.valueOf(aMapLocation.getLongitude()));
                edtLatitude.setText(String.valueOf(aMapLocation.getLatitude()));
                //定位时间
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                edtLocationTime.setText(df.format(date));
                //偏移值
                if (null != hole) {
                    edtAccuracy.setText(Common.GetDistance(aMapLocation.getLongitude(), aMapLocation.getLatitude(), Double.valueOf(hole.getMapLongitude()), Double.valueOf(hole.getMapLatitude())));
                    aMapLocation.setAccuracy(Float.valueOf(edtAccuracy.getText().toString()));
                }
                if (mListener != null) {
                    mListener.onLocationChanged(aMapLocation);
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
//                ToastUtil.showToastS(context, "信号弱,请耐心等待");
                this.aMapLocation = null;
                edtLongitude.setText("");
                edtLatitude.setText("");
                edtLocationTime.setText("");
                edtAccuracy.setText("");
            }
        }
    }
}
