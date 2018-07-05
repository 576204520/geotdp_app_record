package com.cj.record.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
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
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.model.NaviLatLng;
import com.cj.record.R;
import com.cj.record.activity.base.BaseActivity;
import com.cj.record.adapter.ReleteLocationAdapter;
import com.cj.record.baen.Hole;
import com.cj.record.baen.JsonResult;
import com.cj.record.baen.Record;
import com.cj.record.utils.ToastUtil;
import com.cj.record.utils.Urls;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2018/7/3.
 */

public class ReleteLocationActivity extends BaseActivity implements AMapLocationListener, ReleteLocationAdapter.OnItemListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.location_map)
    MapView locationMap;
    @BindView(R.id.location_fabu)
    TextView locationFabu;
    @BindView(R.id.location_recycler)
    RecyclerView locationRecycler;
    @BindView(R.id.location_start)
    TextView locationStart;

    private AMap aMap;
    private boolean isOpen = false;//发布点列表是否点开
    private Hole hole;//选择的发布点
    private List<Hole> list;
    private ReleteLocationAdapter releteLocationAdapter;
    private String serialNumber;
    //选定要去的点
    private Marker goMarker;
    //声明AMapLocationClient类对象，定位发起端
    private AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象，定位参数
    public AMapLocationClientOption mLocationOption = null;
    //声明mListener对象，定位监听器
    private LocationSource.OnLocationChangedListener mListener = null;
    private AMapLocation aMapLocation = null;
    private boolean isFirstLoc = true;
    private Drawable drawableBottom;
    private Drawable drawableRight;

    @Override
    public int getLayoutId() {
        return R.layout.activity_relete_location;
    }

    @Override
    public void initMust(Bundle savedInstanceState) {
        super.initMust(savedInstanceState);
        locationMap.onCreate(savedInstanceState);// 此方法必须重写
    }


    @Override
    public void initView() {
        toolbar.setTitle("导航");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_clear_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        serialNumber = getIntent().getExtras().getString(MainActivity.SN);
        drawableBottom = getResources().getDrawable(R.mipmap.ai_icon_bottom);
        drawableRight = getResources().getDrawable(R.mipmap.ai_icon_right);
        initMap();
    }


    private void initMap() {
        if (aMap == null) {
            aMap = locationMap.getMap();
        }
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.interval(2000);
        //设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);
        //是否顯示定位按鈕
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        //设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setMyLocationEnabled(true);
        //默认缩放比例
        aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
        getReleteList();
        doLocation();
    }

    private void doLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(this);
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
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    /**
     * 获取勘探点列表
     */
    private void getReleteList() {
        showPPW();
        Map<String, String> map = new HashMap<>();
        map.put("serialNumber", serialNumber);
        OkGo.<String>post(Urls.GET_RELATE_HOLE)
                .params(map)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String data = response.body();//这个就是返回来的结果
                        Gson gson = new Gson();
                        JsonResult jsonResult = gson.fromJson(data, JsonResult.class);
                        if (jsonResult.getStatus()) {
                            list = gson.fromJson(jsonResult.getResult(), new TypeToken<List<Hole>>() {
                            }.getType());
                            if (list != null && list.size() > 0) {
                                addMarkersToMap();
                                createListView();
                            } else {
                                ToastUtil.showToastS(ReleteLocationActivity.this, "服务端未创建勘察点，无法获取");
                            }
                        } else {
                            ToastUtil.showToastS(ReleteLocationActivity.this, jsonResult.getMessage());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ToastUtil.showToastS(ReleteLocationActivity.this, "获取勘察点失败");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dismissPPW();
                    }
                });
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        for (Hole hole : list) {
            if (hole.getLatitude() != null && !"".equals(hole.getLatitude()) && hole.getLongitude() != null && !"".equals(hole.getLongitude())) {
                MarkerOptions options = new MarkerOptions();
                options.anchor(0.5f, 0.5f);
                options.position(new LatLng(Double.valueOf(hole.getLatitude()), Double.valueOf(hole.getLongitude())));
                options.title(hole.getCode());
                options.snippet("经度:" + hole.getLongitude() + ",纬度:" + hole.getLatitude());
                options.draggable(false);
                aMap.addMarker(options);
                aMap.setOnMarkerClickListener(listener);
            }
        }
    }

    /**
     * 覆盖物点击事件
     */
    AMap.OnMarkerClickListener listener = new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            marker.showInfoWindow();
            goMarker = marker;
            return true;
        }
    };

    /**
     * 获取数据后创建列表
     */
    private void createListView() {
        releteLocationAdapter = new ReleteLocationAdapter(this,list);
        locationRecycler.setLayoutManager(new LinearLayoutManager(this));
        locationRecycler.setAdapter(releteLocationAdapter);
        releteLocationAdapter.setOnItemListener(this);
    }

    @Override
    public void onClick(int position) {
        if (list.get(position).getLatitude() != null && list.get(position).getLongitude() != null) {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(list.get(position).getLatitude()), Double.parseDouble(list.get(position).getLongitude())), 19));
        } else {
            ToastUtil.showToastS(ReleteLocationActivity.this, "没有位置信息");
        }
    }


    @OnClick({R.id.location_fabu, R.id.location_start})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.location_fabu:
                if (isOpen) {
                    isOpen = false;
                    locationRecycler.setVisibility(View.GONE);
                    locationFabu.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
                } else {
                    isOpen = true;
                    locationRecycler.setVisibility(View.VISIBLE);
                    locationFabu.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableBottom, null);
                }
                break;
            case R.id.location_start:
                if (goMarker != null) {
                    Intent intent = new Intent(this, RouteNaviActivity.class);
                    intent.putExtra("gps", false);
                    intent.putExtra("start", new NaviLatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
                    intent.putExtra("end", new NaviLatLng(goMarker.getPosition().latitude, goMarker.getPosition().longitude));
                    startActivity(intent);
                } else {
                    ToastUtil.showToastS(this, "请选择要去的位置");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        locationMap.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        locationMap.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        locationMap.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        locationMap.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void stop() {
        mLocationClient.stopLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                this.aMapLocation = aMapLocation;
                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    isFirstLoc = false;
                    stop();
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                ToastUtil.showToastS(this, "信号弱,请耐心等待");
                this.aMapLocation = null;
            }
        }
    }
}
