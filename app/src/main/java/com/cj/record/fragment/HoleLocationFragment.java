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

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.cj.record.R;
import com.cj.record.activity.MainActivity;
import com.cj.record.baen.BaseObjectBean;
import com.cj.record.baen.Hole;
import com.cj.record.baen.LocalUser;
import com.cj.record.baen.PageBean;
import com.cj.record.baen.Record;
import com.cj.record.base.BaseFragment;
import com.cj.record.base.BaseMvpFragment;
import com.cj.record.contract.HoleContract;
import com.cj.record.presenter.HolePresenter;
import com.cj.record.utils.GPSutils;
import com.cj.record.utils.L;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;


public class HoleLocationFragment extends BaseFragment implements LocationSource, AMapLocationListener {

    @BindView(R.id.hole_map)
    MapView holeMap;
    @BindView(R.id.hole_latitude)
    TextView holeLatitude;
    @BindView(R.id.hole_longitude)
    TextView holeLongitude;
    @BindView(R.id.hole_time)
    TextView holeTime;
    @BindView(R.id.hole_radius)
    TextView holeRadius;

    private AMap aMap;
    public AMapLocation aMapLocation = null;
    //声明AMapLocationClient类对象，定位发起端
    private AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象，定位参数
    public AMapLocationClientOption mLocationOption = null;
    //声明mListener对象，定位监听器
    private OnLocationChangedListener mListener = null;
    private Marker marker;
    private boolean isFirstLoc = true;
    private Hole hole;
    private int firstInt = 1;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_hole_location;
    }

    @Override
    public void initMust(Bundle savedInstanceState) {
        super.initMust(savedInstanceState);
        holeMap.onCreate(savedInstanceState);// 此方法必须重写
    }

    @Override
    protected void initView(View view) {
        hole = (Hole) getArguments().getSerializable(MainActivity.HOLE);
        if (aMap == null) {
            aMap = holeMap.getMap();
            //设置显示定位按钮 并且可以点击
            aMap.setLocationSource(this);//设置了定位的监听
            //Map的一些风格
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
            myLocationStyle.strokeColor(Color.argb(50, 30, 255, 229));// 设置圆形的边框颜色
            myLocationStyle.radiusFillColor(Color.argb(15, 30, 255, 229));// 设置圆形的填充颜色
            // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
            myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
            aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
            //解决ScorllView冲突问题
            aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
                @Override
                public void onTouch(MotionEvent motionEvent) {
                    holeMap.getParent().getParent().requestDisallowInterceptTouchEvent(true);  //scorllview是父控件就用给一个getParent就好了，多个就用多个 getParent
                }
            });
        }
        doLocation();
    }

    //EditActivity按钮点击
    public void location() {
        if (marker != null) {
            marker.destroy();
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
        markerOption.title("当前勘探点");
        markerOption.snippet("经度:" + aMapLocation.getLongitude() + ",纬度" + aMapLocation.getLatitude() + "\n" + "时间:" + String.valueOf(df.format(new Date(aMapLocation.getTime()))));
        markerOption.draggable(true);
        markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        marker = aMap.addMarker(markerOption);
        // marker旋转90度
        marker.showInfoWindow();
    }

    private void doLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(mActivity);
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
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    public void stop() {
        mLocationClient.stopLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        holeMap.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        holeMap.onPause();
    }

    @Override
    public void onDestroyView() {
        //销毁放到super之前，否则binder已经销毁，map会为空
        holeMap.onDestroy();
        super.onDestroyView();
        mLocationClient.stopLocation();//停止定位
        mLocationClient.onDestroy();//销毁定位客户端。
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        holeMap.onSaveInstanceState(outState);
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
                holeLongitude.setText(String.valueOf(aMapLocation.getLongitude()));
                holeLatitude.setText(String.valueOf(aMapLocation.getLatitude()));
                holeTime.setText(GPSutils.utcToTimeZoneDate(aMapLocation.getTime()));
                holeRadius.setText(hole.getRadius());
                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    isFirstLoc = false;
                }
                if (mListener != null) {
                    mListener.onLocationChanged(aMapLocation);
                }
                if (firstInt == 1) {
                    firstInt++;
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
//                ToastUtil.showToastS(context, "信号弱,请耐心等待");
                this.aMapLocation = null;
                holeLatitude.setText("");
                holeLongitude.setText("");
                holeTime.setText("");
                holeRadius.setText("");
            }
        }
    }


}
