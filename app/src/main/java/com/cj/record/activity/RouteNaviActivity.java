package com.cj.record.activity;

import android.content.Intent;
import android.os.Bundle;

import com.alibaba.idst.nls.internal.utils.L;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.cj.record.R;
import com.cj.record.base.BaseActivity;
import com.cj.record.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/7/3.
 */

public class RouteNaviActivity extends BaseActivity implements AMapNaviListener, AMapNaviViewListener {
    @BindView(R.id.navi_view)
    AMapNaviView mAMapNaviView;
    AMapNavi mAMapNavi;
    boolean mIsGps;
    NaviLatLng start;
    NaviLatLng end;

    @Override
    public int getLayoutId() {
        return R.layout.activity_navi;
    }

    @Override
    public void initMust(Bundle savedInstanceState) {
        super.initMust(savedInstanceState);
        mAMapNaviView.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        mAMapNaviView.setAMapNaviViewListener(this);
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.setEmulatorNaviSpeed(60);
        getNaviParam();
    }

    /**
     * 获取intent参数并计算路线
     */
    private void getNaviParam() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mIsGps = intent.getBooleanExtra("gps", false);
        start = intent.getParcelableExtra("start");
        end = intent.getParcelableExtra("end");
        calculateDriveRoute(start, end, true);
    }

    /**
     * 驾车路径规划计算,计算单条路径
     */
    private void calculateDriveRoute(NaviLatLng start, NaviLatLng end, boolean isWalk) {
        int strategyFlag = 0;
        List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
        /**
         * 途径点坐标集合
         */
        List<NaviLatLng> wayList = new ArrayList<NaviLatLng>();
        /**
         * 终点坐标集合［建议就一个终点］
         */
        List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
        try {
            strategyFlag = mAMapNavi.strategyConvert(true, false, false, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startList.add(start);
        endList.add(end);
        //步行
        if (isWalk) {
            mAMapNavi.calculateWalkRoute(start, end);
        } else {
            mAMapNavi.calculateDriveRoute(startList, endList, wayList, strategyFlag);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mAMapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAMapNaviView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
        mAMapNavi.stopNavi();
        mAMapNavi.destroy();
    }
    @Override
    public void onInitNaviFailure() {
        ToastUtil.showToastS(this, "导航计算失败");
    }

    @Override
    public void onInitNaviSuccess() {
        L.e("onInitNaviSuccess");
    }

    @Override
    public void onStartNavi(int i) {
        L.e("onStartNavi");
    }

    @Override
    public void onTrafficStatusUpdate() {
        L.e("onTrafficStatusUpdate");
    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        L.e("onLocationChange");
    }

    @Override
    public void onGetNavigationText(int i, String s) {
        L.e("onGetNavigationText");
    }

    @Override
    public void onGetNavigationText(String s) {
        L.e("onGetNavigationText");
    }

    @Override
    public void onEndEmulatorNavi() {
        L.e("onEndEmulatorNavi");
    }

    @Override
    public void onArriveDestination() {
        L.e("onPlayRing");
    }


    @Override
    public void onCalculateRouteFailure(int i) {
        calculateDriveRoute(start, end, false);
    }

    @Override
    public void onReCalculateRouteForYaw() {
        L.e("onReCalculateRouteForYaw");
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        L.e("onReCalculateRouteForTrafficJam");
    }

    @Override
    public void onArrivedWayPoint(int i) {
        L.e("onArrivedWayPoint");
    }

    @Override
    public void onGpsOpenStatus(boolean b) {
        L.e("onGpsOpenStatus");
    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
        L.e("onNaviInfoUpdate");
    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {
        L.e("onNaviInfoUpdated");
    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {
        L.e("updateCameraInfo");
    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {
        L.e("onServiceAreaUpdate");
    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {
        L.e("showCross");
    }

    @Override
    public void hideCross() {
        L.e("hideCross");
    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {
        L.e("showModeCross");
    }

    @Override
    public void hideModeCross() {
        L.e("hideModeCross");
    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {
        L.e("showLaneInfo");
    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    @Override
    public void hideLaneInfo() {
        L.e("hideLaneInfo");
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        mAMapNavi.startNavi(NaviType.GPS);
    }

    @Override
    public void notifyParallelRoad(int i) {
        L.e("notifyParallelRoad");
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {
        L.e("OnUpdateTrafficFacility");
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
        L.e("OnUpdateTrafficFacility");
    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {
        L.e("OnUpdateTrafficFacility");
    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {
        L.e("updateAimlessModeStatistics");
    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {
        L.e("updateAimlessModeCongestionInfo");
    }

    @Override
    public void onPlayRing(int i) {
        L.e("onPlayRing");
    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {

    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }

    @Override
    public void onNaviSetting() {
        L.e("onNaviSetting");
    }

    @Override
    public void onNaviCancel() {
        finish();
    }

    @Override
    public boolean onNaviBackClick() {
        L.e("onNaviBackClick");
        return false;
    }

    @Override
    public void onNaviMapMode(int i) {
        L.e("onNaviMapMode");
    }

    @Override
    public void onNaviTurnClick() {
        L.e("onNaviTurnClick");
    }

    @Override
    public void onNextRoadClick() {
        L.e("onNextRoadClick");
    }

    @Override
    public void onScanViewButtonClick() {
        L.e("onScanViewButtonClick");
    }

    @Override
    public void onLockMap(boolean b) {
        L.e("onLockMap");
    }

    @Override
    public void onNaviViewLoaded() {
        L.e("onNaviViewLoaded");
    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public void onNaviViewShowMode(int i) {

    }
}
