package com.beautyyan.beautyyanapp.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.fragment.MainFragment;
import com.beautyyan.beautyyanapp.http.HttpResponseListener;
import com.beautyyan.beautyyanapp.http.RequestHelp;
import com.beautyyan.beautyyanapp.http.ToBeanHelp;
import com.beautyyan.beautyyanapp.http.bean.City;
import com.beautyyan.beautyyanapp.listener.LettersBgListener;
import com.beautyyan.beautyyanapp.utils.CitiesSort;
import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.utils.LogUtil;
import com.beautyyan.beautyyanapp.utils.PinyinUtil;
import com.beautyyan.beautyyanapp.utils.ToastUtils;
import com.beautyyan.beautyyanapp.utils.Util;
import com.beautyyan.beautyyanapp.view.LoadingView;
import com.beautyyan.beautyyanapp.view.MyGridView;
import com.beautyyan.beautyyanapp.view.SideBar;
import com.beautyyan.beautyyanapp.view.stickylistview.ChooseCityAdapter;
import com.beautyyan.beautyyanapp.view.stickylistview.ExpandableStickyListHeadersListView;
import com.beautyyan.beautyyanapp.view.stickylistview.StickyListHeadersListView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xuelu on 2017/3/28.
 */

public class ChooseCityActivity extends BaseActivity implements HttpResponseListener {
    @Bind(R.id.cities_gv)
    GridView citiesGv;
    @Bind(R.id.listview)
    StickyListHeadersListView listview;
    @Bind(R.id.ll_root_view)
    LinearLayout llRootView;
    @Bind(R.id.loadingview)
    LoadingView loadingview;
    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.tv_dialog)
    TextView tvDialog;
    @Bind(R.id.sideBar)
    SideBar sideBar;
    @Bind(R.id.view_bar)
    View viewBar;

    private TextView tvLocated;
    private RelativeLayout locatedRl;
    private Context context;
    private Handler handler = new Handler();
    //城市列表
    private List<City> cityList = new ArrayList<>();

    WeakHashMap<View, Integer> mOriginalViewHeightPool = new WeakHashMap<View, Integer>();

    //首字母列表
    private ArrayList<String> zimuList = new ArrayList<>();


    private HashMap<String, List<City>> map = new HashMap<>();

    private boolean isNeedBackBtn = true;

    private boolean hasOneClick = false;

    private boolean isFromH5 = false;

    private LocationClient mLocationClient;

    private boolean isHttpReFinish = false;

    //百度定位超时时间
    private final int TIME_OUT = 5000;
    public BDLocationListener myListener = new MyLocationListener();
    public boolean isInOpenCities = false;

    private ChooseCityAdapter adapter;
    private static ChooseCityActivity instance;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        instance = null;
    }

    @Override
    protected void bindButterKnife() {
        ButterKnife.bind(this);
    }

    public static ChooseCityActivity getIns() {
        return instance;
    }

    @Override
    protected void initView() {
        if (getIntent().getParcelableExtra(Constant.INTENT) != null) {
            Intent intent =  getIntent().getParcelableExtra(Constant.INTENT);
            Constant.fromShareSetIntent(this, intent);
        }
        context = this;
        instance = this;
        setContentView(R.layout.activity_choose_city);
        View headView = View.inflate(context, R.layout.head_view, null);
        View footView = View.inflate(context, R.layout.root_view, null);
        listview.addFooterView(footView);
        tvLocated = (TextView) headView.findViewById(R.id.tv_located);
        locatedRl = (RelativeLayout) headView.findViewById(R.id.located_rl);
        refreshLayout.setEnabled(false);
        sideBar.setTextView(tvDialog);
        if ("".equals(Constant.getInstance().getLocatedCity().getCityName())) {
            initLocation();
        } else {
            tvLocated.setText(Constant.getInstance().getLocatedCity().getCityName());
        }
        loadingview.changeLoadingStatus(0);
        setTitleName(getResources().getString(R.string.choose_city));

        isNeedBackBtn = getIntent().getBooleanExtra("is_need_backbtn", true);
        if (!isNeedBackBtn) {
            setBackBtnGone();
        }
    }

    private void initLocation() {
        tvLocated.setText(getResources().getString(R.string.located_failed));
        LocationClient mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
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
        mLocationClient.start();
        this.mLocationClient = mLocationClient;
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

            if (location.getLocType() == BDLocation.TypeGpsLocation) {

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
                tvLocated.setText(Constant.getInstance().getLocatedCity().getCityName());


            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {

                // 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\noperationers : ");
                sb.append(location.getOperators());    //获取运营商信息

                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
                String city = location.getCity();
                setCity(city, location.getCityCode());
                tvLocated.setText(Constant.getInstance().getLocatedCity().getCityName());

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

            if (isHttpReFinish && !"".equals(Constant.getInstance().getLocatedCity().getCityName())) {
                locatedRl.setEnabled(true);
                for (City city : cityList) {
                    if (city.getCityName().trim().equals(Constant.getInstance().getLocatedCity().getCityName())) {
                        Constant.getInstance().getLocatedCity().setCityId(city.getCityId());
                        isInOpenCities = true;
                    }
                }

                if (adapter != null) {
                    List<City> listLoca = new ArrayList<>();
                    listLoca.add(Constant.getInstance().getLocatedCity());
                    map.put("定位城市", listLoca);
                    adapter.setEnable("".equals(Constant.getInstance().getLocatedCity().getCityName()) ? false : true);
                    adapter.notifyDataSetChanged();
                }
            } else if ("".equals(Constant.getInstance().getLocatedCity().getCityName())) {
                locatedRl.setEnabled(false);
            }
            LogUtil.i(sb.toString());
            mLocationClient = null;
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    private void setCity(String city, String id) {
        LogUtil.i("cityId:" + id);
        if (city != null && city.contains("市")) {
            city = city.substring(0, city.length() - 1);
        }
        Constant.getInstance().setLocatedCity(new City(city, id));
    }


    @Override
    protected void initData() {
        isFromH5 = getIntent().getBooleanExtra("from_h5", false);
        RequestHelp.getOpenCities(this, this);
    }

    @Override
    protected void initListener() {
        loadingview.setFailClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(Constant.getInstance().getLocatedCity().getCityName())) {
                    initLocation();
                }
                loadingview.changeLoadingStatus(0);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RequestHelp.getOpenCities(ChooseCityActivity.this, ChooseCityActivity.this);
                    }
                }, 500);

            }
        });
        locatedRl.setEnabled(false);
        locatedRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyLocatedCityChanged(Constant.getInstance().getLocatedCity());
            }
        });
    }

    //通知定位城市改变
    public void notifyLocatedCityChanged(City city) {
        if (city == null) {
            LogUtil.e("return");
            return;
        }
        if (isFromH5) {
            LogUtil.i("h5----cityId" + city.getCityId());
            Intent intent = new Intent();
            intent.putExtra("cityId", city.getCityId());
            intent.putExtra("cityName", city.getCityName());
            setResult(0, intent);
            finish();
            return;
        }
        if (city.getCityName().equals("") || city.getCityId().equals("")) {
            return;
        }
        Message msg = new Message();
        msg.what = Constant.MESSAGE.CHANGE_LOCATED_CITY;
        msg.obj = city.getCityName();
        MainFragment.getInstance().sendMessage(msg);
        Constant.getInstance().setChoosedCity(city);
        LogUtil.i("setCity id:" + city.getCityId());
        finish();
    }

    @Override
    public void onSuccess(String result, String message) {
        String beiJingCityId = "";
        String shangHaiCityId = "";
        String hangZhouCityId = "";
        String guangZhouCityId = "";
        String chengDuCityId = "";
        isInOpenCities = false;
        locatedRl.setEnabled(true);
        List<City> listCity = new ArrayList<>();
        listCity.addAll(ToBeanHelp.citiesBean(result));
        for (City city : listCity) {
            cityList.add(city);
            if (city.getCityName().trim().equals(Constant.getInstance().getLocatedCity().getCityName())) {
                Constant.getInstance().getLocatedCity().setCityId(city.getCityId());
                isInOpenCities = true;
            }
            if (city.getCityName().contains("北京")) {
                beiJingCityId = city.getCityId();
            } else if (city.getCityName().contains("上海")) {
                shangHaiCityId = city.getCityId();
            } else if (city.getCityName().contains("杭州")) {
                hangZhouCityId = city.getCityId();
            } else if (city.getCityName().contains("广州")) {
                guangZhouCityId = city.getCityId();
            } else if (city.getCityName().contains("成都")) {
                chengDuCityId = city.getCityId();
            }
        }

        //按照字母排序
        List<City> cityList1 = new ArrayList<>();
        cityList1.addAll(CitiesSort.getSortCities(cityList));
        cityList.clear();
        cityList.addAll(cityList1);

        int count = cityList.size();
        for (int i = 0; i < count; i++) {
            if (i == 0 || (i > 0 && !PinyinUtil.getFirstSpell(cityList.get(i).getCityName()).equals(zimuList.get(zimuList.size() - 1)))) {

                String zimu = PinyinUtil.getFirstSpell(cityList.get(i).getCityName());
                zimuList.add(zimu);
                List<City> list = new ArrayList<>();
                list.add(cityList.get(i));
                map.put(zimu, list);
            } else {
                String zimu = PinyinUtil.getFirstSpell(cityList.get(i).getCityName());
                List<City> list = map.get(zimu);
                list.add(cityList.get(i));
            }
        }
        City cityAll = new City("全国城市", "100001");
        List<City> list = new ArrayList<>();
        if (!Util.isEmpty(getIntent().getStringExtra("path"))
                && "buycar".equals(getIntent().getStringExtra("path"))) {
            list.add(cityAll);
        }
        City cityBj = new City("北京", beiJingCityId);
        City citySh = new City("上海", shangHaiCityId);
        City cityHz = new City("杭州", hangZhouCityId);
        City cityGz = new City("广州", guangZhouCityId);
        City cityCd = new City("成都", chengDuCityId);
        list.add(cityBj);
        list.add(citySh);
        list.add(cityHz);
        list.add(cityGz);
        list.add(cityCd);
        zimuList.add(0, "热门城市");
        zimuList.add(0, "定位城市");
        List<City> listLoca = new ArrayList<>();
        listLoca.add(Constant.getInstance().getLocatedCity());
        map.put("热门城市", list);
        map.put("定位城市", listLoca);
        adapter = new ChooseCityAdapter(this, cityList, map, zimuList);
        adapter.setEnable(true);
        listview.setAdapter(adapter);
        loadingview.changeLoadingStatus(2);
        llRootView.setVisibility(View.VISIBLE);
        ArrayList<String> zList = new ArrayList<>();
        zList.addAll(zimuList);
        zList.remove(0);
        zList.set(0, "定");
        zList.add(1, "热");
        sideBar.setIndexText(zList);
        sideBar.setVisibility(View.VISIBLE);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                if ("定".equals(s)) {
                    listview.setSelection(0);
                    return;
                } else if ("热".equals(s)) {
                    listview.setSelection(1);
                    return;
                }
                listview.setSelection(zimuList.indexOf(s));
            }
        });
        sideBar.setLettersBgListener(new LettersBgListener() {
            @Override
            public void gone() {
                viewBar.setVisibility(View.GONE);
            }

            @Override
            public void visible() {
                viewBar.setVisibility(View.VISIBLE);
            }
        });
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewBar.getLayoutParams();
        lp.height = sideBar.getHeight() / sideBar.RATE * (zList.size() + 1);
        LogUtil.i("height is " + lp.height);
        isHttpReFinish = true;
//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) sideBar.getLayoutParams();
//        layoutParams.height = sideBar.singleHeight * zimuList.size()*2;

    }

    @Override
    public void onFail(String data, int errorCode, String message) throws JSONException {
        loadingview.changeLoadingStatus(-1);
    }

    private class GvAdapter extends BaseAdapter {
        private List<City> cityList;

        public GvAdapter(List<City> cityList) {
            this.cityList = cityList;
        }

        @Override
        public int getCount() {
            return cityList.size();
        }

        @Override
        public Object getItem(int position) {
            return cityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CityHolderView holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.choose_city_gv_item, null);
                holder = new CityHolderView();
                holder.cityTv = (TextView) convertView.findViewById(R.id.city_tv);
                convertView.setTag(holder);
            }

            holder = (CityHolderView) convertView.getTag();
            holder.cityTv.setText(cityList.get(position).getCityName());

            return convertView;
        }
    }

    private class MylistAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return zimuList.size();
        }

        @Override
        public Object getItem(int position) {
            return zimuList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ZimuHolderView holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.choose_city_listview_item, null);
                holder = new ZimuHolderView();
                holder.gv = (MyGridView) convertView.findViewById(R.id.gridview);
                holder.zimuTv = (TextView) convertView.findViewById(R.id.zimu_tv);
                convertView.setTag(holder);
            }
            holder = (ZimuHolderView) convertView.getTag();
            holder.zimuTv.setText(zimuList.get(position));
            holder.gv.setAdapter(new GvAdapter(map.get(zimuList.get(position))));
            holder.gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                    notifyLocatedCityChanged(map.get(zimuList.get(position)).get(pos));
                }
            });
            return convertView;
        }
    }

    private class CityHolderView {
        TextView cityTv;
    }

    private class ZimuHolderView {
        TextView zimuTv;
        MyGridView gv;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!isNeedBackBtn && keyCode == KeyEvent.KEYCODE_BACK) {
            exit2Click();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit2Click() {
        if (!hasOneClick) {
            ToastUtils.showShort(context, "再按一次退出应用");
            hasOneClick = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hasOneClick = false;
                }
            }, 2000);
        } else {
            finish();
            MainActivity.getInstance().finish();
        }
    }

    //animation executor
    class AnimationExecutor implements ExpandableStickyListHeadersListView.IAnimationExecutor {

        @Override
        public void executeAnim(final View target, final int animType) {
            if (ExpandableStickyListHeadersListView.ANIMATION_EXPAND == animType && target.getVisibility() == View.VISIBLE) {
                return;
            }
            if (ExpandableStickyListHeadersListView.ANIMATION_COLLAPSE == animType && target.getVisibility() != View.VISIBLE) {
                return;
            }
            if (mOriginalViewHeightPool.get(target) == null) {
                mOriginalViewHeightPool.put(target, target.getHeight());
            }
            final int viewHeight = mOriginalViewHeightPool.get(target);
            float animStartY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? 0f : viewHeight;
            float animEndY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? viewHeight : 0f;
            final ViewGroup.LayoutParams lp = target.getLayoutParams();
            ValueAnimator animator = ValueAnimator.ofFloat(animStartY, animEndY);
            animator.setDuration(200);
            target.setVisibility(View.VISIBLE);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND) {
                        target.setVisibility(View.VISIBLE);
                    } else {
                        target.setVisibility(View.GONE);
                    }
                    target.getLayoutParams().height = viewHeight;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    lp.height = ((Float) valueAnimator.getAnimatedValue()).intValue();
                    target.setLayoutParams(lp);
                    target.requestLayout();
                }
            });
            animator.start();

        }
    }
}
