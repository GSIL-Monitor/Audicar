package com.beautyyan.beautyyanapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.admaster.jicesdk.api.JiceConfig;
import com.admaster.jicesdk.api.JiceSDK;
import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.YuYuanApp;
import com.beautyyan.beautyyanapp.activity.ChooseCityActivity;
import com.beautyyan.beautyyanapp.activity.LoginActivity;
import com.beautyyan.beautyyanapp.activity.MainActivity;
import com.beautyyan.beautyyanapp.activity.ServiceProActivity;
import com.beautyyan.beautyyanapp.activity.TwoYearsActivity;
import com.beautyyan.beautyyanapp.activity.WebViewActivity;
import com.beautyyan.beautyyanapp.activity.YanChangActivity;
import com.beautyyan.beautyyanapp.http.HttpResponseListener;
import com.beautyyan.beautyyanapp.http.RequestHelp;
import com.beautyyan.beautyyanapp.http.ToBeanHelp;
import com.beautyyan.beautyyanapp.http.bean.Banner;
import com.beautyyan.beautyyanapp.http.bean.Car;
import com.beautyyan.beautyyanapp.http.bean.CarType;
import com.beautyyan.beautyyanapp.http.bean.Dealer;
import com.beautyyan.beautyyanapp.http.bean.User;
import com.beautyyan.beautyyanapp.listener.BannerOnClickListener;
import com.beautyyan.beautyyanapp.listener.PriceRangeDiaListener;
import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.utils.LogUtil;
import com.beautyyan.beautyyanapp.utils.ToastUtils;
import com.beautyyan.beautyyanapp.utils.Util;
import com.beautyyan.beautyyanapp.view.HorizontalListView;
import com.beautyyan.beautyyanapp.view.LoadingView;
import com.beautyyan.beautyyanapp.view.MyGridView;
import com.beautyyan.beautyyanapp.view.MyHorizontalScrollView;
import com.beautyyan.beautyyanapp.view.MyScrollView;
import com.beautyyan.beautyyanapp.view.MyViewPager;
import com.beautyyan.beautyyanapp.view.PriceRangeDialog;
import com.beautyyan.beautyyanapp.view.ViewPagerAdapter;
import com.beautyyan.beautyyanapp.view.ViewPagerScroller;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import xmpp.activites.MsgActivity;
import xmpp.constant.Constants;
import xmpp.util.HideSoftInputHelperTool;
import xmpp.xmpp.XmppConnection;

/**
 * Created by xuelu on 2017/3/22.
 */

public class MainFragment extends Fragment implements View.OnClickListener {
    @Bind(R.id.viewpager)
    MyViewPager vpPager;
    @Bind(R.id.my_scrollview)
    MyScrollView myScrollview;
    @Bind(R.id.msg_btn)
    ImageView msgBtn;
    @Bind(R.id.boutique_head_points)
    LinearLayout boutiqueHeadPoints;
    @Bind(R.id.choose_city_txt)
    TextView chooseCityTxt;
    @Bind(R.id.point_img)
    ImageView pointImg;
    @Bind(R.id.view_line)
    View viewLine;
    @Bind(R.id.h_listview_newest)
    HorizontalListView hListviewNewest;
    @Bind(R.id.h_listview_recommend)
    HorizontalListView hListviewRecommend;
    @Bind(R.id.expand_price_rl)
    RelativeLayout expandPriceRl;
    @Bind(R.id.price_tv)
    TextView priceTv;
    @Bind(R.id.expand_all_cartype)
    TextView expandAllCartype;
    @Bind(R.id.ll_anim)
    LinearLayout llAnim;
    @Bind(R.id.rl_anim)
    RelativeLayout rlAnim;
    @Bind(R.id.ll_search)
    LinearLayout llSearch;
    @Bind(R.id.ll_top)
    LinearLayout llTop;
    @Bind(R.id.check_img)
    ImageView checkImg;
    @Bind(R.id.distance_img)
    ImageView distanceImg;
    @Bind(R.id.finance_img)
    ImageView financeImg;
    @Bind(R.id.give_message_img)
    ImageView giveMessageImg;
    @Bind(R.id.service_img)
    ImageView serviceImg;
    @Bind(R.id.find_car_img)
    ImageView findCarImg;
    @Bind(R.id.buycar_img)
    ImageView buycarImg;
    @Bind(R.id.cartype_gv12)
    MyGridView cartypeGv12;
    @Bind(R.id.cartype_gv4)
    MyGridView cartypeGv4;
    @Bind(R.id.view)
    View view;
    @Bind(R.id.view_blank)
    View viewBlank;
    @Bind(R.id.newest_car_txt)
    TextView newestCarTxt;
    @Bind(R.id.popular_car_txt)
    TextView popularCarTxt;
    @Bind(R.id.newest_sc)
    MyHorizontalScrollView newestSc;
    @Bind(R.id.h_listview_popular)
    HorizontalListView hListviewPopular;
    @Bind(R.id.popular_sc)
    MyHorizontalScrollView popularSc;
    @Bind(R.id.input_car_type_ed)
    EditText inputCarTypeEd;
    @Bind(R.id.salecar_txt)
    TextView salecarTxt;
    @Bind(R.id.guess_price_txt)
    TextView guessPriceTxt;
    @Bind(R.id.one_key_message_rl)
    RelativeLayout oneKeyMessageRl;
    @Bind(R.id.search_btn)
    Button searchBtn;
    @Bind(R.id.valuable_car_img)
    ImageView valuableCarImg;
    @Bind(R.id.worry_sale_img)
    ImageView worrySaleImg;
    @Bind(R.id.two_year_zero_rate)
    ImageView twoYearZeroRate;
    @Bind(R.id.newest_more_cars_txt)
    TextView newestMoreCarsTxt;
    @Bind(R.id.pop_more_cars_txt)
    TextView popMoreCarsTxt;
    @Bind(R.id.recommend_more_cars_txt)
    TextView recommendMoreCarsTxt;
    @Bind(R.id.more_stores_txt)
    TextView moreStoresTxt;
    @Bind(R.id.help_me_findcar_rl)
    RelativeLayout helpMeFindcarRl;
    @Bind(R.id.buy_car_tips_txt)
    RelativeLayout buyCarTipsTxt;
    @Bind(R.id.other_brand_btn)
    Button otherBrandBtn;
    @Bind(R.id.store_name_txt)
    TextView storeNameTxt;
    @Bind(R.id.stars_ll)
    LinearLayout starsLl;
    @Bind(R.id.store_address_txt)
    TextView storeAddressTxt;
    @Bind(R.id.on_sale_car_num_txt)
    TextView onSaleCarNumTxt;
    @Bind(R.id.excellent_store_img)
    ImageView excellentStoreImg;
    @Bind(R.id.loadview)
    LoadingView loadview;
    @Bind(R.id.buycar_btn)
    Button buycarBtn;
    @Bind(R.id.main_frag_root)
    FrameLayout mainFragRoot;
    @Bind(R.id.service_pro_rl)
    RelativeLayout serviceProRl;
    @Bind(R.id.go_to_store_detail_txt)
    TextView goToStoreDetailTxt;
    @Bind(R.id.rl_banner)
    View rlBanner;
    @Bind(R.id.view_auto)
    View viewAuto;
    @Bind(R.id.one_one_zero_rl)
    RelativeLayout oneOneZeroRl;
    @Bind(R.id.yan_chang_rl)
    RelativeLayout yanChangRl;
    @Bind(R.id.two_year_rl)
    RelativeLayout twoYearRl;
    @Bind(R.id.price_tip)
    TextView priceTip;
    @Bind(R.id.bao_img)
    ImageView baoImg;
    @Bind(R.id.excellent_store_ll)
    LinearLayout excellentStoreLl;
    @Bind(R.id.take_car_operation_rl)
    RelativeLayout takeCarOperationRl;
    @Bind(R.id.newest_no_data_layout)
    RelativeLayout newestNoDataLayout;
    @Bind(R.id.popular_no_data_layout)
    RelativeLayout popularNoDataLayout;
    @Bind(R.id.recommend_no_data_layout)
    RelativeLayout recommendNoDataLayout;
    @Bind(R.id.recommend_sc)
    MyHorizontalScrollView recommendSc;
    @Bind(R.id.tv_store_rank)
    TextView tvStoreRank;

    private View rootView;
    private ViewGroup vgPoints;
    private List<Banner> banners = new ArrayList<>();
    private Context context;
    private ViewPagerAdapter pagerAdapter;
    private HashMap<Integer, View> views;
    private Timer timer;
    private TimerTask timerTask;
    private static MainFragment instance;
    private PriceRangeDialog dialog;
    private boolean isFistCome = true;
    private List<Dealer> dealers = new ArrayList<>();
    private Dealer dealer;
    private int bannerSize = 0;


    private List<Car> newestCarList = new ArrayList<>();
    private List<Car> hotCarList = new ArrayList<>();
    private List<Car> recommendCarList = new ArrayList<>();


    //价格区间类型：0 不限，1 10才万以下， 2 10-15， 3 15-20， 4 20-30， 5 30-50， 6 50以上
    private int priceType = 0;

    //车型数
    private final int CARTYPE_NUM = 12;

    //车型列表行数，影响动画计算
    private final int hang_num = 3;

    //单个车型高度excell
    private int carTypeHeight;

    //查看全部车型的y坐标
    private int carTypeY;

    //车型列表
    private List<CarType> listCarType12 = new ArrayList<>();
    private List<CarType> listCarType4 = new ArrayList<>();
    //车系是否展开
    private boolean isOpen = false;

    //是否正处于动画中
    private boolean isAniming = false;

    //动画每次移动的像素
    private int dis;

    //动画块的布局参数
    private ViewGroup.LayoutParams p;

    //网络接口数
    private int INTERFACE_COUNT = 4;

    //网络接口完成数
    private int countRequest = 0;

    //viewblank的高度，用来调节动画
    private int heightBlank;


    public static MainFragment getInstance() {
        if (instance == null) {
            instance = new MainFragment();
        }
        return instance;
    }

    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case Constant.MESSAGE.SHOW_POINT:
                    boolean isFromSend = (Boolean) msg.obj;
                    if (isFromSend) {
                        return;
                    }
                    LogUtil.i("show point");
                    if (pointImg.getVisibility() == View.GONE) {
                        pointImg.setVisibility(View.VISIBLE);
                    }
                    //并不是真正未读消息数为1，只是为了记录此时是有未读消息的
                    YuYuanApp.getIns().getSp().setUnreadMsgCount(1);
                    MainActivity.getInstance().mineFragment.showRedPoint(1);
                    break;
                case Constant.MESSAGE.CHANGE_LOCATED_CITY:
                    Constant.getInstance().isActivityShow(getActivity());
                    String obj = (String) msg.obj;
                    if (chooseCityTxt.getText().toString().equals(obj)) {
                        return;
                    }
                    MainActivity.getInstance().setReloadTag();
                    MainActivity.getInstance().refreshByCityChanged();
                    chooseCityTxt.setText(obj);
                    setCarTypeList();
                    doRequest();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_lmain, null);
            ButterKnife.bind(this, rootView);
            context = getActivity();
            initView();
            YuYuanApp.getIns().setStatusBar(getActivity(), viewAuto);
//            initPriceRangePopupWindow();噢
            initListener();
        }
        instance = this;
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    public void sendMessage(Message msg) {
        instance.handler.sendMessage(msg);
    }

    private void initView() {
        initCarTypeList();
        vgPoints = (ViewGroup) rootView.findViewById(R.id.boutique_head_points);// banner下面的点
        viewAuto = rootView.findViewById(R.id.view_auto);
        views = new HashMap<>();
        newestCarTxt.setEnabled(false);
        chooseCityTxt.setText(Constant.getInstance().getChoosedCity().getCityName());
        RequestHelp.getBanner(getActivity(), new BannerList());
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rlBanner.getLayoutParams();
        params.height = Constant.getInstance().getDeviceWidth() * 520 / 936;

//        Banner banner1 = new Banner();
//        Banner banner2 = new Banner();
//        Banner banner3 = new Banner();
//        Banner banner4 = new Banner();
//        banner1.setDrawId(R.mipmap.banner1);
//        banner2.setDrawId(R.mipmap.banner2);
//        banner3.setDrawId(R.mipmap.banner3);
//        banner4.setDrawId(R.mipmap.banner4);
//        banners.add(banner1);
//        banners.add(banner2);
//        banners.add(banner1);
//        banners.add(banner2);
        pagerAdapter = new ViewPagerAdapter(context, views, new BannerClick());
        if (!Util.isEmpty(YuYuanApp.getIns().getSp().getBanner())) {
            banners.addAll(ToBeanHelp.bannersBean(YuYuanApp.getIns().getSp().getBanner()));
            bannerSize = banners.size();
            if (banners.size() == 2) {
                banners.add(banners.get(0));
                banners.add(banners.get(1));
            }
            vpPager.setAdapter(pagerAdapter);
            pagerAdapter.setData(banners);
        }
        initBannerView();
        if (!"".equals(Constant.getInstance().getChoosedCity().getCityName())) {
            doRequest();
        }
        if (!"-1".equals(Constant.getInstance().getUser().getUserId())) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", Constant.getInstance().getUser().getPhone());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JiceSDK.getInstance(getActivity().getApplicationContext(), new JiceConfig()).trackLoginEvent(Constant.getInstance().getUser().getUserId(), jsonObject);
            Constant.getInstance().getAudiBi(getActivity());
//            INTERFACE_COUNT = 5;
            loginXmpp(Constant.getInstance().getUser());
            Constants.USER_NAME = Constant.getInstance().getUser().getUserId();
        }
    }

    private void doRequest() {
        initCarListView();
        INTERFACE_COUNT = 4;
        loadview.changeLoadingStatus(0);
        RequestHelp.getCarList(getActivity(), new RecommandCarList(), Constant.getInstance().getChoosedCity().getCityId(), 0, 6, "", "", "", "RECOMMEND", "", "", "");
        RequestHelp.getCarList(getActivity(), new NewestCarList(), Constant.getInstance().getChoosedCity().getCityId(), 0, 6, "", "", "", "NEW", "", "", "");
        RequestHelp.getCarList(getActivity(), new HotCarList(), Constant.getInstance().getChoosedCity().getCityId(), 0, 6, "", "", "", "HOT", "", "", "");
        RequestHelp.getDealerList(getActivity(), new DealerList(), Constant.getInstance().getChoosedCity().getCityId(), 0, 1, "", "", "1");
    }

    private void loginXmpp(final User user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isSuccess = XmppConnection.getInstance().login(String.valueOf(user.getUserId()), Constants.PWD);
                if (isSuccess && getActivity() != null && !getActivity().isFinishing()) {
                    Constant.getInstance().setXmppHasLogined(true);
                    Constants.USER_NAME = user.getUserId();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//                            judgeReqFinish();
                            LogUtil.i("xmpp login succeess");
                        }
                    });
                } else if (getActivity() != null && !getActivity().isFinishing()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final boolean isSuccess = XmppConnection.getInstance().login(String.valueOf(user.getPhone()), Constants.PWD);
//                            judgeReqFinish();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (isSuccess && getActivity() != null && !getActivity().isFinishing()) {
                                        Constant.getInstance().setXmppHasLogined(true);
                                        Constants.USER_NAME = user.getUserId();
                                        LogUtil.i("xmpp login succeess");
                                    }
                                }
                            });
                        }
                    }).start();

                }
            }
        }).start();
    }

    public void setPointGone() {
        pointImg.setVisibility(View.GONE);
    }

    public void setPointVisible() {
        pointImg.setVisibility(View.VISIBLE);
    }

    private class BannerList implements HttpResponseListener {

        @Override
        public void onSuccess(String data, String message) throws JSONException {
//            judgeReqFinish();
            if (ToBeanHelp.bannersBean(data) == null) {
                return;
            }

            YuYuanApp.getIns().getSp().setBanner(data);
            banners.clear();
            banners.addAll(ToBeanHelp.bannersBean(data));
            bannerSize = banners.size();
            if (banners.size() == 2) {
                banners.add(banners.get(0));
                banners.add(banners.get(1));
            }
            vpPager.setAdapter(pagerAdapter);
            pagerAdapter.setData(banners);
            initBannerView();

        }

        @Override
        public void onFail(String data, int errorCode, String message) throws JSONException {
        }
    }

    private class DealerList implements HttpResponseListener {

        @Override
        public void onSuccess(String data, String message) throws JSONException {
            judgeReqFinish();
            if (ToBeanHelp.dealerListBean(data) == null) {
                storeAddressTxt.setText("");
                storeNameTxt.setText("");
                onSaleCarNumTxt.setText("0");
                double score = 0;
                starsLl.removeAllViews();

                for (double i = 0; i < 5; i++) {
                    View view = View.inflate(context, R.layout.img, null);
                    ImageView imageView = (ImageView) view.findViewById(R.id.img);
                    imageView.setImageResource(i < score ? R.mipmap.star_selected : R.mipmap.star_unselected);
                    starsLl.addView(view);
                }
                return;
            }
            YuYuanApp.getIns().getSp().setDealer(data);
            dealers.clear();
            dealers.addAll(ToBeanHelp.dealerListBean(data));
            setExcellentInfo(dealers);
        }

        @Override
        public void onFail(String data, int errorCode, String message) throws JSONException {
            judgeReqFinish();
            if (!Util.isEmpty(YuYuanApp.getIns().getSp().getDealer())) {
                return;
            }
            storeAddressTxt.setText("");
            storeNameTxt.setText("");
//            excellentStoreImg.setImageResource(R.mipmap.img_default_bg);
            onSaleCarNumTxt.setText("0");
            double score = 0;
            starsLl.removeAllViews();

            for (double i = 0; i < 5; i++) {
                View view = View.inflate(context, R.layout.img, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.img);
                imageView.setImageResource(i < score ? R.mipmap.star_selected : R.mipmap.star_unselected);
                starsLl.addView(view);
            }
        }
    }

    private void setExcellentInfo(List<Dealer> dealers) {
        if (dealers.size() > 0) {
            dealer = dealers.get(0);
        }
        storeAddressTxt.setText(dealer.getShopHomeAddress());
        storeNameTxt.setText(dealer.getShopHomeNick());
        Util.picasso(excellentStoreImg, dealer.getHeadImgPath(), R.mipmap.excellent_store_bg);
        onSaleCarNumTxt.setText(dealer.getOnSellCarCount());
        String rankStr = "优质经销商";
        switch (dealer.getRank()) {
            case 1:
                rankStr = "金牌经销商";
                break;
            case 2:
                rankStr = "银牌经销商";
                break;
            case 3:
                rankStr = "铜牌经销商";
                break;
            case 4:
                rankStr = "优质经销商";
                break;
            default:
                break;
        }
        tvStoreRank.setText(rankStr);
        double score = Math.round(dealer.getScore());
        starsLl.removeAllViews();

        for (double i = 0; i < 5; i++) {
            View view = View.inflate(context, R.layout.img, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.img);
            imageView.setImageResource(i < score ? R.mipmap.star_selected : R.mipmap.star_unselected);
            starsLl.addView(view);
        }
    }

    private class RecommandCarList implements HttpResponseListener {

        @Override
        public void onSuccess(String data, String message) throws JSONException {
            judgeReqFinish();
            if (ToBeanHelp.carsBean(data) == null || ToBeanHelp.carsBean(data).size() == 0) {
                recommendCarList.clear();
                recommendNoDataLayout.setVisibility(View.VISIBLE);
                recommendSc.setVisibility(View.GONE);
                return;
            }
            YuYuanApp.getIns().getSp().setRecommand(data);
            recommendCarList.clear();
            recommendCarList.addAll(ToBeanHelp.carsBean(data));
            recommendNoDataLayout.setVisibility(View.GONE);
            recommendSc.setVisibility(View.VISIBLE);
            refreshRecommendCarList(recommendCarList);
        }

        @Override
        public void onFail(String data, int errorCode, String message) throws JSONException {
            judgeReqFinish();
        }
    }

    private class NewestCarList implements HttpResponseListener {

        @Override
        public void onSuccess(String data, String message) throws JSONException {
            judgeReqFinish();
            if (ToBeanHelp.carsBean(data) == null || ToBeanHelp.carsBean(data).size() == 0) {
                newestCarList.clear();
                newestNoDataLayout.setVisibility(View.VISIBLE);
                newestSc.setVisibility(View.GONE);
                return;
            }
            YuYuanApp.getIns().getSp().setNewest(data);
            newestCarList.clear();
            newestCarList.addAll(ToBeanHelp.carsBean(data));
            newestNoDataLayout.setVisibility(View.GONE);
            newestSc.setVisibility(View.VISIBLE);
            refreshNewestCarList(newestCarList);
        }

        @Override
        public void onFail(String data, int errorCode, String message) throws JSONException {
            judgeReqFinish();
        }
    }

    private class HotCarList implements HttpResponseListener {

        @Override
        public void onSuccess(String data, String message) throws JSONException {
            judgeReqFinish();
            if (ToBeanHelp.carsBean(data) == null || ToBeanHelp.carsBean(data).size() == 0) {
                hotCarList.clear();
                popularNoDataLayout.setVisibility(View.VISIBLE);
                popularSc.setVisibility(View.GONE);
                return;
            }
            YuYuanApp.getIns().getSp().setHot(data);
            hotCarList.clear();
            hotCarList.addAll(ToBeanHelp.carsBean(data));
            popularNoDataLayout.setVisibility(View.GONE);
            popularSc.setVisibility(View.VISIBLE);
            refreshHotCarList(hotCarList);
        }

        @Override
        public void onFail(String data, int errorCode, String message) throws JSONException {
            judgeReqFinish();
        }
    }

    private void judgeReqFinish() {
        countRequest++;
        if (countRequest == INTERFACE_COUNT) {
            loadview.changeLoadingStatus(2);
            countRequest = 0;
        }
    }

    /**
     * 车型列表初始化
     */
    private void initCarTypeList() {
        setCarTypeList();
        cartypeGv12.setAdapter(new GvAdapter(listCarType12));

        ViewTreeObserver vto = cartypeGv12.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!isFistCome) {
                    return;
                }
                isFistCome = false;
                carTypeHeight = cartypeGv4.getHeight() + cartypeGv4.getVerticalSpacing();
                carTypeY = 3 * carTypeHeight;
                heightBlank = viewBlank.getHeight();
                p = viewBlank.getLayoutParams();
                LogUtil.i("height:" + carTypeHeight + "y:" + carTypeY);
                llAnim.setY(carTypeHeight);
                if (carTypeY - carTypeHeight < 300) {
                    dis = 10;
                } else if (carTypeY - carTypeHeight < 400) {
                    dis = 15;
                } else if (carTypeY - carTypeHeight < 500) {
                    dis = 20;
                } else {
                    dis = 25;
                }
            }
        });

    }

    private void setCarTypeList() {
        listCarType4.clear();
        listCarType12.clear();
        for (int i = 1; i < (CARTYPE_NUM / hang_num) + 1; i++) {
            listCarType4.add(getCarType(i));
        }
        cartypeGv4.setAdapter(new GvAdapter(listCarType4));
        for (int i = 1; i < CARTYPE_NUM + 1; i++) {
            listCarType12.add(getCarType(i));
        }
    }

    private CarType getCarType(int type) {
        CarType carType = new CarType();
        String name = "";
        int drawId = 0;
        String carKind = "A1";
        switch (type) {
            case 1:
                name = name + "A6";
                drawId = R.mipmap.a6;
                carKind = "A6";
                break;
            case 2:
                name = name + "A4";
                drawId = R.mipmap.a4;
                carKind = "A4";
                break;
            case 3:
                name = name + "Q5";
                drawId = R.mipmap.q5;
                carKind = "Q5";
                break;
            case 4:
                name = name + "Q3";
                drawId = R.mipmap.q3;
                carKind = "Q3";
                break;
            case 5:
                name = name + "A3";
                drawId = R.mipmap.a3;
                carKind = "A3";
                break;
            case 6:
                name = name + "A8";
                drawId = R.mipmap.a8;
                carKind = "A8";
                break;
            case 7:
                name = name + "Q7";
                drawId = R.mipmap.q7;
                carKind = "Q7";
                break;
            case 8:
                name = name + "A5";
                drawId = R.mipmap.a5;
                carKind = "A5";
                break;
            case 9:
                name = name + "A1";
                drawId = R.mipmap.a1;
                carKind = "A1";
                break;
            case 10:
                name = name + "A7";
                drawId = R.mipmap.a7;
                carKind = "A7";
                break;
            case 11:
                name = name + "TT";
                drawId = R.mipmap.tt;
                carKind = "TT";
                break;
            case 12:
                name = name + "R8";
                drawId = R.mipmap.r8;
                carKind = "R8";
                break;
            default:
                break;
        }
        carType.setName(name);
        carType.setDrawId(drawId);
        carType.setCarKind(carKind);
        return carType;
    }

    private void initCarListView() {
        newestCarList.clear();
        hotCarList.clear();
        recommendCarList.clear();
        dealers.clear();
        if (!Util.isEmpty(YuYuanApp.getIns().getSp().getNewest())) {
            String data = YuYuanApp.getIns().getSp().getNewest();
            newestCarList.addAll(ToBeanHelp.carsBean(data));

        }
        if (!Util.isEmpty(YuYuanApp.getIns().getSp().getHot())) {
            hotCarList.addAll(ToBeanHelp.carsBean(YuYuanApp.getIns().getSp().getHot()));
        }
        if (!Util.isEmpty(YuYuanApp.getIns().getSp().getRecommand())) {
            recommendCarList.addAll(ToBeanHelp.carsBean(YuYuanApp.getIns().getSp().getRecommand()));
        }
        if (!Util.isEmpty(YuYuanApp.getIns().getSp().getDealer())) {
            dealers.addAll(ToBeanHelp.dealerListBean(YuYuanApp.getIns().getSp().getDealer()));
            setExcellentInfo(dealers);
        }
        refreshHotCarList(hotCarList);
        refreshRecommendCarList(recommendCarList);
        refreshNewestCarList(newestCarList);
    }

    private void refreshRecommendCarList(List<Car> list) {
        hListviewRecommend.removeAllViews();
        hListviewRecommend.buildView(context, list);
    }

    private void refreshNewestCarList(List<Car> list) {
        hListviewNewest.removeAllViews();
        hListviewNewest.buildView(context, list);
    }

    private void refreshHotCarList(List<Car> list) {
        hListviewPopular.removeAllViews();
        hListviewPopular.buildView(context, list);
    }

    /**
     * 防止动画上移动后底部出现空白
     *
     * @param isHigh
     */
    private void setViewBlankHigh(boolean isHigh) {
        if (isHigh) {
            p.height = heightBlank + carTypeHeight * 2;
        } else {
            p.height = heightBlank;
        }
    }

    private void initListener() {
//        mainFragRoot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        takeCarOperationRl.setOnClickListener(this);
//        oneOneZeroRl.setOnClickListener(this);
//        twoYearRl.setOnClickListener(this);
//        yanChangRl.setOnClickListener(this);
//        serviceProRl.setOnClickListener(this);
        llSearch.setOnClickListener(this);
        helpMeFindcarRl.setOnClickListener(this);
        buyCarTipsTxt.setOnClickListener(this);
        otherBrandBtn.setOnClickListener(this);
        excellentStoreLl.setOnClickListener(this);
        goToStoreDetailTxt.setOnClickListener(this);
        moreStoresTxt.setOnClickListener(this);
        oneKeyMessageRl.setOnClickListener(this);
        newestMoreCarsTxt.setOnClickListener(this);
        popMoreCarsTxt.setOnClickListener(this);
        recommendMoreCarsTxt.setOnClickListener(this);
        twoYearZeroRate.setOnClickListener(this);
        valuableCarImg.setOnClickListener(this);
        worrySaleImg.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        guessPriceTxt.setOnClickListener(this);
        salecarTxt.setOnClickListener(this);
        buycarBtn.setOnClickListener(this);
//        newestCarTxt.setOnClickListener(this);
//        popularCarTxt.setOnClickListener(this);
        expandAllCartype.setOnClickListener(this);
        expandPriceRl.setOnClickListener(this);
        msgBtn.setOnClickListener(this);
        chooseCityTxt.setOnClickListener(this);
        vpPager.setOnPageChangeListener(new PagerChangeListener());
        vpPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    stopTimer();
                } else {
                    startTimer();
                }

                return false;
            }
        });
        inputCarTypeEd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        search();
                        HideSoftInputHelperTool.hideSoftInputBoard(inputCarTypeEd, getActivity());
                        break;
                }
                return false;
            }
        });
        ViewPagerScroller scroller = new ViewPagerScroller(context, new AccelerateInterpolator());
        scroller.setScrollDuration(200);
        scroller.initViewPagerScroll(vpPager);
    }

    private class GvAdapter extends BaseAdapter {

        private List<CarType> list;

        public GvAdapter(List<CarType> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            HolderView holder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_cartype, null);
                holder = new HolderView();
                holder.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
                holder.carImg = (ImageView) convertView.findViewById(R.id.car_img);
                convertView.setTag(holder);
            }
            holder = (HolderView) convertView.getTag();
            holder.nameTv.setText(list.get(position).getName());
            holder.carImg.setImageResource(list.get(position).getDrawId());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.getInstance().searchByCarkind(list.get(position).getCarKind());
                }
            });
            return convertView;
        }
    }

    private class HolderView {
        ImageView carImg;
        TextView nameTv;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_car_operation_rl:
//                ToastUtils.showCenterShort(context, "敬请期待");
                startWebActivity(Constant.URL.SUBMIT_CAR_WORK, "提车作业");
                break;
            case R.id.two_year_rl:
                startActivity(new Intent(context, TwoYearsActivity.class));
                break;
            case R.id.one_one_zero_rl:
//                startActivity(new Intent(context, OneOneZeroActivity.class));
                Intent intent2;
                if (Util.isAvilible(context, "com.autonavi.minimap")) {
                    try {
                        intent2 = Intent.getIntent("amapuri://route/plan/?sid=BGVIS1&slat=39.92848272&slon=116.39560823&sname=A&did=BGVIS2&dlat=39.98848272&dlon=116.47560823&dname=B&dev=0&t=0");
                        context.startActivity(intent2);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, "您尚未安装百度地图", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.yan_chang_rl:
                startActivity(new Intent(context, YanChangActivity.class));
                break;

            case R.id.more_stores_txt:
                Intent intentStoreList = new Intent(context, WebViewActivity.class);
                intentStoreList.putExtra(Constant.WEB_URL, Constant.getInstance().getDealerList());
                intentStoreList.putExtra(Constant.WEB_TITLE, "经销商列表");
                intentStoreList.putExtra(Constant.IS_STORELIST_ACTIVITY, true);
                startActivity(intentStoreList);

                break;
            case R.id.excellent_store_ll:
            case R.id.go_to_store_detail_txt:
                if (dealers.size() == 0) {
                    return;
                }
                if (dealers.get(0).getDealerCode() == null) {
                    ToastUtils.showCenterShort(getActivity(), "参数缺失");
                    return;
                }
                Intent intentEx = new Intent(getActivity(), WebViewActivity.class);
                intentEx.putExtra(Constant.WEB_URL, Constant.getInstance().getStoreDetail() + '&' + "dealerCode=" + dealers.get(0).getDealerCode());
                intentEx.putExtra(Constant.WEB_TITLE, 1 + dealers.get(0).getShopHomeNick());
                intentEx.putExtra("chatName", dealers.get(0).getDealerCode());
                startActivity(intentEx);
                break;
            case R.id.service_pro_rl:
                startActivity(new Intent(context, ServiceProActivity.class));
                break;
            case R.id.ll_search:
//                startWebActivity(Constant.getInstance().getSearch(), "-2");


                Intent intentSearch = new Intent(context, WebViewActivity.class);
                intentSearch.putExtra(Constant.WEB_URL, Constant.getInstance().getSearch());
                intentSearch.putExtra(Constant.WEB_TITLE, "-2");
                startActivityForResult(intentSearch, Constant.RequestCode.CODE2);
//                startWebActivity("https://paytest.beautyyan.cn/pay/gateway.do?info=xpvs814MUhG7vR8MEu0JzekO1i35mKFnHAJYMiN%2F%2B%2Flg9p414%2B2J8FcOxW3xUBovU7j5C5TDhYM8%0D%0AfQP%2BdMKHfJ6UNbGFmczK%2BY20ex5S7qZjW8DkM1UiHfEqBz10RG5QivcQom3D4LdepL4HLs4FI075%0D%0Als5S7gfyCYUCkvIeVJ6Yfuc3zMFAxHLRtCy5Gbs650SkUSvVWv9JgWtgRYM7VcSvvZbgfgCvmqbv%0D%0AmnrqpmyV%2FvGq0IMhQYvrzrULt5sBgPW%2BFz67OEDVA75oDYPHkiAQ3pZJ1KtxKr1sEBJDIui0eKGj%0D%0AJBkqVEebekxOSDkV6uD1NI7s5J4NWKTFI5RaWjfsmd9H47hwHCc%2FbdUDN5x0OszmzAo0UDAEmidb%0D%0AdokspPNC67rppY2KhRf8yO0b3IOTpJ1LvmKq9nv0npvLxj7u98BcnQiZeHl9uyejXTOzaS9JCeAF%0D%0AuW1z%2F0bQUeXJXT%2BPbdzXkPIcTxqz4tLshXBAqbl%2BvpZXjOQVlE2C1UFWqs3oATZ%2F62ZSLBG1GP9z%0D%0Ap5A4nJQCnv8nkeCN8hyRAVu93i0wVNq%2FZDEaSCdw35NXf%2BlSHJERss9ebvs2bALtgv1ELKlqnEd%2F%0D%0ALJ1O2K9m2m%2F414DaaeBI%2FOtl0uqGBhUKYY%2BoT%2FivG5hrsGZK%2BzZrQHYgtNgIZ%2FYgLy%2FRF0YPnhc8%0D%0AcW80%2BaJUTXS4%2FlnoeeVSErtt9BvRu298VUyPzUryneD3ptMgDlc0rYtyYweWXZ6bF5%2F%2BqXoRBiZz%0D%0ACSImx6G%2BvKalbKoraC4TSwj2HG0htGwdHbhjaQmf4cNDvY1RwDSrzDaDvGYLQNSLEs%2BUo2w3CBGY%0D%0AwQS5hZ5K1LyxTESzxNGdOd4la2yYztza%2B9yJCL%2F99kDBLaG%2BTh5KDsQDyCppJR4ogRg6jY5yWir3%0D%0AM0w4LQ%2ByQpXrX%2BqaHCkZvKrW7y6Tsb0BQcnK7b8lqZCMgnCpxGNSrLbQ8jGAgAE2Aev9sxRsNvgO%0D%0An8bNv2s9YwIUqbXH8gAysMx%2BirSgAS3HjQer", "haha");
                break;
            case R.id.buy_car_tips_txt:
                startWebActivity(Constant.URL.BUY_CAR_TIPS, "购车百科");
                break;
            case R.id.help_me_findcar_rl:
                Intent helpIntent = new Intent(context, WebViewActivity.class);
                helpIntent.putExtra(Constant.WEB_URL, Constant.getInstance().getAdvancedSearch());
                helpIntent.putExtra(Constant.WEB_TITLE, "2高级筛选");
                startActivityForResult(helpIntent, Constant.RequestCode.CODE8);
                break;
            case R.id.other_brand_btn:
                ToastUtils.showCenterShort(context, "敬请期待");
                break;
            case R.id.pop_more_cars_txt:
            case R.id.newest_more_cars_txt:
            case R.id.recommend_more_cars_txt:
            case R.id.buycar_btn:
                MainActivity.getInstance().searchAll();
                break;
            case R.id.valuable_car_img:
                MainActivity.getInstance().searchBySaletype(5);
                break;
            case R.id.worry_sale_img:
                MainActivity.getInstance().searchBySaletype(2);
                break;
            case R.id.two_year_zero_rate:
                MainActivity.getInstance().searchBySaletype(3);
                break;
            case R.id.search_btn:
//                UMImage image = new UMImage(getActivity(), R.mipmap.ic_launcher);
//                UMWeb web = new UMWeb("https://www.baidu.com");
//                web.setTitle("This is music title");//标题
//                web.setThumb(image);  //缩略图
//                web.setDescription("my description");//描述
//                new ShareAction(getActivity())
//                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)//传入平台
//                        .withText("hello")//分享内容
//                        .withMedia(web)
//                        .setCallback(shareListener)//回调监听器
//                        .share();
                search();
                break;
            case R.id.one_key_message_rl:
                startWebActivity(Constant.URL.ONE_KEY_MESSAGE, "一键留言");
                break;
            case R.id.guess_price_txt:
                if (!isLogin()) {
                    return;
                }
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra(Constant.WEB_URL, Constant.getInstance().getGuessPriceUrl());
                intent.putExtra(Constant.WEB_TITLE, "在线评估");
                startActivity(intent);
                break;
            case R.id.salecar_txt:
                MainActivity.getInstance().setTabSelected(MainActivity.TAB_SALE);
                break;
            case R.id.newest_car_txt:
                newestCarTxt.setEnabled(false);
                popularCarTxt.setEnabled(true);

                newestCarTxt.setTextColor(getResources().getColor(R.color.red_used));
                popularCarTxt.setTextColor(getResources().getColor(R.color.normal_text_color));
                newestSc.setVisibility(View.VISIBLE);
                popularSc.setVisibility(View.GONE);

                break;
            case R.id.popular_car_txt:
                popularCarTxt.setEnabled(false);
                newestCarTxt.setEnabled(true);

                popularCarTxt.setTextColor(getResources().getColor(R.color.red_used));
                newestCarTxt.setTextColor(getResources().getColor(R.color.normal_text_color));

                popularSc.setVisibility(View.VISIBLE);
                newestSc.setVisibility(View.GONE);
                break;
            case R.id.choose_city_txt:
                Intent intentChoose = new Intent(getActivity(), ChooseCityActivity.class);
//                Intent intent = new Intent(getActivity(), WebViewActivity.class);
//                intent.putExtra(Constant.WEB_TITLE, "title");
//                intent.putExtra(Constant.WEB_URL, "http:60.205.230.31:88/cardetail");
////                intent.putExtra(Constant.WEB_URL, "http://www.baidu.com");

                startActivity(intentChoose);
                break;
            case R.id.msg_btn:
                if (!isLogin()) {
                    return;
                }
                startActivity(new Intent(getActivity(), MsgActivity.class));
                break;
            case R.id.expand_price_rl:
                if (dialog != null && dialog.isShowing()) {
                    return;
                }
                if (dialog == null) {
                    dialog = new PriceRangeDialog(context, priceType, new PriceRangeDiaListener() {
                        @Override
                        public void clickOk(int which, String str) {
                            if (which == -1) {
                                return;
                            }
                            priceType = which;
                            priceTv.setText(str);

                        }
                    });
                }
                else {
                    dialog.setWhich(priceType);
                }
                dialog.show();
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.width = Constant.getInstance().getDeviceWidth();
                dialog.getWindow().setAttributes(lp);
                break;
            case R.id.expand_all_cartype:
                LogUtil.i("click expand cartype!");

                if (isAniming) {
                    return;
                }
                isAniming = true;
                if (isOpen) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = carTypeY * 10; i > carTypeHeight * 10; i = i - dis) {
                                final int y = i / 10;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        llAnim.setY(y);
                                    }
                                });
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            isOpen = false;
                            isAniming = false;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Drawable drawable = getResources().getDrawable(R.mipmap.add);
                                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                    expandAllCartype.setCompoundDrawables(drawable, null, null, null);
                                    expandAllCartype.setText(" 点击查看更多车系");
                                    cartypeGv12.setVisibility(View.GONE);
                                    cartypeGv4.setVisibility(View.VISIBLE);
                                    setViewBlankHigh(false);

                                }
                            });
                        }
                    }).start();
                } else {
                    cartypeGv12.setVisibility(View.VISIBLE);
                    cartypeGv4.setVisibility(View.GONE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = carTypeHeight * 10; i < carTypeY * 10; i = i + dis) {
                                final int y = i / 10;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        llAnim.setY(y);
                                    }
                                });
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            isOpen = true;
                            isAniming = false;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Drawable drawable = getResources().getDrawable(R.mipmap.reduce);
                                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                    expandAllCartype.setCompoundDrawables(drawable, null, null, null);
                                    expandAllCartype.setText(" 点击收起");
                                    setViewBlankHigh(true);
                                }
                            });
                        }
                    }).start();
                }
                break;
            default:
                break;
        }
    }

    private boolean isLogin() {
        if ("-1".equals(Constant.getInstance().getUser().getUserId())) {
            Intent intentMsg = new Intent();
            intentMsg.setClass(getActivity(), LoginActivity.class);
            startActivity(intentMsg);
            return false;
        }
        return true;
    }

    private void search() {
        int priceMin = 0;
        int priceMax = 52;
        switch (priceType) {
            case 0:
                priceMin = 0;
                priceMax = 52;
                break;
            case 1:
                priceMin = 0;
                priceMax = 10;
                break;
            case 2:
                priceMin = 10;
                priceMax = 15;
                break;
            case 3:
                priceMin = 15;
                priceMax = 20;
                break;
            case 4:
                priceMin = 20;
                priceMax = 30;
                break;
            case 5:
                priceMin = 30;
                priceMax = 50;
                break;
            case 6:
                priceMin = 50;
                priceMax = 52;
                break;
            default:
                break;
        }
        MainActivity.getInstance().searchByCartype(inputCarTypeEd.getText().toString().trim(), priceMin, priceMax);
    }

    private void startWebActivity(String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constant.WEB_URL, url);
        intent.putExtra(Constant.WEB_TITLE, title);
        startActivity(intent);
    }

    /**
     * 价格区间popwindow
     */
    private void initPriceRangePopupWindow() {
//        View view = ((LayoutInflater) context
//                .getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(
//                R.layout.price_popupwindow, null);
//        tv0 = (TextView) view.findViewById(R.id.tv0);
//        tv1 = (TextView) view.findViewById(R.id.tv1);
//        tv2 = (TextView) view.findViewById(R.id.tv2);
//        tv3 = (TextView) view.findViewById(R.id.tv3);
//        tv4 = (TextView) view.findViewById(R.id.tv4);
//        tv5 = (TextView) view.findViewById(R.id.tv5);
//        tv6 = (TextView) view.findViewById(R.id.tv6);
//
//        tv0.setOnClickListener(this);
//        tv1.setOnClickListener(this);
//        tv2.setOnClickListener(this);
//        tv3.setOnClickListener(this);
//        tv4.setOnClickListener(this);
//        tv5.setOnClickListener(this);
//        tv6.setOnClickListener(this);
//
//        ppWindow = new PopupWindow(view,
//                ViewGroup.LayoutParams.FILL_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        ppWindow.setAnimationStyle(R.style.popwin_anim_style);
//        ppWindow.setBackgroundDrawable(new BitmapDrawable());
//        ppWindow.setOutsideTouchable(true);
//        ppWindow.setFocusable(true);

    }

    private class BannerClick
            // Banner点击事件
            implements
            BannerOnClickListener {
        @Override
        public void click(Banner info) {
            clickBanner(info);
        }
    }

    void clickBanner(Banner info) {
        if (Util.isEmpty(info.getBannerClickPath())) {
            return;
        }
        String title = "";
        if (!Util.isEmpty(info.getBannerClickPath())
                && info.getBannerClickPath().contains(Constant.URL.AUDIBI_DUIHUAN)) {
            title = "5优惠兑换机";
            MobclickAgent.onEvent(getActivity(), Constant.UMENG_CLICK_BANNER_TO_DUIHUAN);
        }
        startWebActivity(info.getBannerClickPath(), title);
    }

    private class PagerChangeListener
            // Banner的滚动事件
            implements
            ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int i) {
            setViewPoint();
        }
    }

    private void initBannerView() {
        if (banners.size() == 0) {
            return;
        }
        vgPoints.removeAllViews();
        int size = bannerSize == 2 ? banners.size() / 2 : bannerSize;
        for (int i = 0; i < size; i++) {
            View view = View.inflate(context, R.layout.banner_grid_item, null);
            vgPoints.addView(view);
        }
        vpPager.setCurrentItem(0);
        setViewPoint();
        startScroll();
    }

    private void setViewPoint() {
        if (banners.size() == 0) {
            return;
        }
        for (int i = 0; i < vgPoints.getChildCount(); i++) {
            View view = vgPoints.getChildAt(i);
            ImageView img = (ImageView) view.findViewById(R.id.banner_grid_item_image);

            if (i == vpPager.getCurrentItem() % (bannerSize == 2 ? banners.size() / 2 : banners.size())) {
                img.setBackgroundResource(R.drawable.banner_selected);
            } else {
                img.setBackgroundResource(R.drawable.banner_unselected);
            }
        }
    }

    private void startScroll() {
        if (banners.size() <= 1) {
            return;
        }
        startTimer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void clearData() {
        stopTimer();
        ButterKnife.unbind(this);
    }

    public void startTimer() {
        stopTimer();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (vpPager == null) {
                            return;
                        }
                        int currentItem = vpPager.getCurrentItem();
                        ++currentItem;
                        vpPager.setCurrentItem(currentItem);
                    }
                });
            }
        }, 5000, 5000);
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != 0 || data == null) {
            return;
        }
        switch (requestCode) {
            case Constant.RequestCode.CODE2:
                if (data == null) {
                    return;
                }
                String keyword = data.getExtras().getString("keyword");
                MainActivity.getInstance().searchByCartype(keyword, 0, 52);
                break;
//            case Constant.RequestCode.CODE5:
//                MainActivity.getInstance().mineFragment.isLoadFinish = false;
//                break;
            case Constant.RequestCode.CODE8:
                String json = data.getStringExtra("json");
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    json = jsonObject.getString("advancedFilter");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MainActivity.getInstance().searchByAdvanced(json);
                break;

            default:
                break;
        }
    }


}
