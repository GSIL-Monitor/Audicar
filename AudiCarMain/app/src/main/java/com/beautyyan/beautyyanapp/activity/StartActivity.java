package com.beautyyan.beautyyanapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;

import com.admaster.jicesdk.api.JiceConfig;
import com.admaster.jicesdk.api.JiceSDK;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.beautyyan.beautyyanapp.YuYuanApp;
import com.beautyyan.beautyyanapp.http.bean.City;
import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by xuelu on 2017/3/29.
 * 启动页
 */

public class StartActivity extends BaseActivity {

    private Context context;
    //启动页停留时间
    private final int TIME = 1000;

   //百度定位超时时间
    private final int TIME_OUT = 7000;
    private String myUri;
    private String myTitle;
    private String shareTitle;
    private String shareUrl;
    private String shareImg;
    private String shareInfo;
    private final int REQUEST_LOCATION_ABLED = 1;
    private int requestTimes = 0;

    /**
     * 定位SDK的核心类
     */
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();


    @Override
    protected void bindButterKnife() {
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {
        context = this;
        Uri uridata = this.getIntent().getData();
        if (uridata != null) {
            myUri = uridata.toString().substring(uridata.toString().indexOf("uri=") + 4, uridata.toString().indexOf("shareUrl=") - 1);
            myTitle = uridata.getQueryParameter("title");

            shareUrl = uridata.toString().substring(uridata.toString().indexOf("shareUrl=") + 9, uridata.toString().length());
            shareTitle = uridata.getQueryParameter("shareTitle");
            shareImg = uridata.getQueryParameter("shareImg");
            shareInfo = uridata.getQueryParameter("shareInfo");
        }
        else if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_start);
        setTitleBarGone();
        initLocation();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestTimes++;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_ABLED);
        }
//        else if (ActivityCompat.checkSelfPermission(StartActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
////            toast("需要动态获取权限");
//            requestTimes++;
//            ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_STATE);
//        }
        else {
            mLocationClient.start();
            startAct();
        }

//        Drawable drawable = getResources().getDrawable(R.mipmap.start);
//        imgStart.setImageDrawable(drawable);
    }

    @Override
    protected void initData() {
        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        JiceSDK.getInstance(getApplicationContext(), new JiceConfig());
        Config.DEBUG = true;
        PlatformConfig.setWeixin(Constant.WEIXIN_ID, Constant.WEIXIN_KEY);
        PlatformConfig.setQQZone(Constant.QQ_ID, Constant.QQ_KEY);
        PlatformConfig.setSinaWeibo(Constant.SINA_ID, Constant.SINA_KEY, "http://sns.whalecloud.com/sina2/callback");
    }

    @Override
    protected void initListener() {

    }

    private void initLocation(){
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

//        int span=1000;
//        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        //定位超时时限
        option.setTimeOut(TIME_OUT);

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        mLocationClient.setLocOption(option);
    }


    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            mLocationClient.stop();
            //获取定位结果
            StringBuffer sb = new StringBuffer(256);

            sb.append("time : ");
            sb.append(location.getTime());    //获取定位时间

            sb.append("\nerror code : ");
            sb.append(location.getLocType());    //获取类型类型

            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());    //获取纬度信息

            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());    //获取经度信息

            sb.append("\nradius : ");
            sb.append(location.getRadius());    //获取定位精准度

            if (location.getLocType() == BDLocation.TypeGpsLocation){

                // GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());    // 单位：公里每小时

                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());    //获取卫星数

                sb.append("\nheight : ");
                sb.append(location.getAltitude());    //获取海拔高度信息，单位米

                sb.append("\ndirection : ");
                sb.append(location.getDirection());    //获取方向信息，单位度

                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
                String city = location.getCity();

                setCity(city, location.getCityCode());

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){

                // 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\noperationers : ");
                sb.append(location.getOperators());    //获取运营商信息

                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
                String city = location.getCity();

                setCity(city, location.getCityCode());

            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

                // 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");

            } else if (location.getLocType() == BDLocation.TypeServerError) {

                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");

            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");

            }

            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());    //位置语义化信息

            List<Poi> list = location.getPoiList();    // POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }

            LogUtil.i(sb.toString());
            mLocationClient = null;
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return false;
    }

    private void setCity(String city, String id) {
        LogUtil.i("cityId:" + id);
        if (city != null && city.contains("市")) {
            city = city.substring(0, city.length() - 1);
        }
        Constant.getInstance().setLocatedCity(new City(city, id));
    }

    private void startAct() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                if (!YuYuanApp.getIns().getSp().isFirstOpen() && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
//                    Constant.getInstance().showStatusBar(StartActivity.this, true);
//                }
                Intent intent = new Intent();
                if (YuYuanApp.getIns().getSp().isFirstOpen()) {
                    intent.setClass(context, WelcomeActivity.class);
                }
                else {
                    intent.setClass(context, MainActivity.class);
                    if (myUri != null) {
                        intent.putExtra(Constant.IS_OPEN_FROM_SHARE, true);
                        Intent intent2 = new Intent();
                        intent2.putExtra(Constant.IS_SHARE, true);
                        intent2.putExtra(Constant.WEB_URL, myUri);
                        intent2.putExtra(Constant.WEB_TITLE, myTitle);
                        intent2.putExtra(Constant.SHARE_TITLE, shareTitle);
                        intent2.putExtra(Constant.SHARE_URL, shareUrl);
                        intent2.putExtra(Constant.SHARE_IMG, shareImg);
                        intent2.putExtra(Constant.SHARE_INFO, shareInfo);
                        intent.putExtra(Constant.INTENT, intent2);
                    }
                }
//                overridePendingTransition(R.anim.bottom_to_top, R.anim.do_nothing);
                if (YuYuanApp.getIns().getSp().isFirstOpen()) {
                    YuYuanApp.getIns().getSp().setFirstOpenFalse();
                }
                startActivity(intent);
                finish();
            }
        },TIME);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_ABLED && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationClient.start();
        }
        requestTimes--;
        if (requestTimes <= 0) {
            startAct();
        }
    }
}
