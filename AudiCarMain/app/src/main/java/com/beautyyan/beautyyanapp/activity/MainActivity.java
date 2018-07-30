package com.beautyyan.beautyyanapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.YuYuanApp;
import com.beautyyan.beautyyanapp.fragment.MainFragment;
import com.beautyyan.beautyyanapp.fragment.WebViewFragment;
import com.beautyyan.beautyyanapp.http.HttpResponseListener;
import com.beautyyan.beautyyanapp.http.RequestHelp;
import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.utils.LogUtil;
import com.beautyyan.beautyyanapp.utils.SharePreferUtil;
import com.beautyyan.beautyyanapp.utils.ToastUtils;
import com.beautyyan.beautyyanapp.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import xmpp.constant.Constants;
import xmpp.xmpp.XmppConnection;

/**
 * Created by xuelu on 2017/3/23.
 */

public class MainActivity extends FragmentActivity
        implements View.OnClickListener {
    @Bind(R.id.tab_main)
    LinearLayout tabMain;
    @Bind(R.id.tab_buy)
    LinearLayout tabBuy;
    @Bind(R.id.tab_sale)
    LinearLayout tabSale;
    @Bind(R.id.tab_calling)
    LinearLayout tabCalling;
    @Bind(R.id.tab_mine)
    LinearLayout tabMine;
    @Bind(R.id.main_frag_layout)
    FrameLayout mainFragLayout;
    @Bind(R.id.buy_frag_layout)
    FrameLayout buyFragLayout;
    @Bind(R.id.sale_frag_layout)
    FrameLayout saleFragLayout;
    @Bind(R.id.call_frag_layout)
    FrameLayout callFragLayout;
    @Bind(R.id.mine_frag_layout)
    FrameLayout mineFragLayout;
    @Bind(R.id.tab_main_img)
    ImageView tabMainImg;
    @Bind(R.id.tab_buy_img)
    ImageView tabBuyImg;
    @Bind(R.id.tab_sale_img)
    ImageView tabSaleImg;
    @Bind(R.id.tab_call_img)
    ImageView tabCallImg;
    @Bind(R.id.tab_mine_img)
    ImageView tabMineImg;
    private MainFragment mainFragment;
    public WebViewFragment buyFragment, saleFragment, callFragment, mineFragment;

    //选中哪个tab的标志位
    public static final int TAB_MAIN = 0;
    public static final int TAB_BUY = 1;
    public static final int TAB_SALE = 2;
    public static final int TAB_CALL = 3;
    public static final int TAB_MINE = 4;

    private boolean isFistTime = true;


    private boolean hasOneClick = false;
    private Context context;
    private NewMsgReceiver newMsgReceiver;
    private static MainActivity instance;


    public Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case Constant.MESSAGE.GO_TO_BUY:
                    setTabSelected(TAB_BUY);
                    break;
                case Constant.MESSAGE.GO_TO_ORDER_LIST:
                    String url = msg.obj.toString();
                    setTabSelected(TAB_BUY);
                    Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                    intent.putExtra(Constant.WEB_TITLE, "订单详情页");
                    intent.putExtra(Constant.WEB_URL, url);
                    startActivity(intent);
                    break;
                case Constant.MESSAGE.CLEAR_LOGININFO:
                    mineFragment.isLoadFinish = false;
                    mineFragment.loadUrl(Constant.getInstance().getMineUrl());
                    mainFragment.setPointGone();
                    MainActivity.getInstance().mineFragment.showRedPoint(0);
                    break;
                case Constant.MESSAGE.REFRESH_MINE:
                    Constant.getInstance().getAudiBi(MainActivity.this);
                    if (mineFragment.hasLoaded) {
                        mineFragment.loadUrl(Constant.getInstance().getMineUrl());
                    }
                    MainFragment.getInstance().onStart();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (!Constant.getInstance().isAppOnForeground()) {
            Constant.getInstance().setActive(false);
        }
        if(buyFragment.getLayerType() != View.LAYER_TYPE_SOFTWARE) {
            buyFragment.setLayerType(View.LAYER_TYPE_SOFTWARE);
            saleFragment.setLayerType(View.LAYER_TYPE_SOFTWARE);
            callFragment.setLayerType(View.LAYER_TYPE_SOFTWARE);
            mineFragment.setLayerType(View.LAYER_TYPE_SOFTWARE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setStartTo(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainFragment.getInstance().stopTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainFragment.getInstance().startTimer();
        if (!"-1".equals(Constant.getInstance().getUser().getUserId())) {
            if (YuYuanApp.getIns().getSp().getUnreadMsgCount() <= 0) {
                mainFragment.setPointGone();
            }
            else {
                mainFragment.setPointVisible();
            }
            MainActivity.getInstance().mineFragment.showRedPoint(YuYuanApp.getIns().getSp().getUnreadMsgCount() == 0 ?
                    0 : 1);
        }
        if (!Constant.getInstance().isActive()) {
            Constant.getInstance().setActive(true);
            Constant.getInstance().getAudiBi(MainActivity.this);
        }
        if (isFistTime) {
            isFistTime = false;
        }
        else if(buyFragment.getLayerType() != View.LAYER_TYPE_HARDWARE) {
            //为防止闪屏，固延迟100毫秒
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    buyFragment.setLayerType(View.LAYER_TYPE_HARDWARE);
                    saleFragment.setLayerType(View.LAYER_TYPE_HARDWARE);
                    callFragment.setLayerType(View.LAYER_TYPE_HARDWARE);
                    mineFragment.setLayerType(View.LAYER_TYPE_HARDWARE);
                }
            }, 100);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            savedInstanceState = null;
        }
        super.onCreate(savedInstanceState);
        setDeviceHW();
        setStartTo(getIntent());
        setContentView(R.layout.activity_main);
        newMsgReceiver = new NewMsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.CHAT_NEW_MSG);
        intentFilter.addAction(Constants.REFRESH_MSGACTIVITY);
        registerReceiver(newMsgReceiver, intentFilter);

        context = this;
        instance = this;
        ButterKnife.bind(this);
        initView();
        initListener();

        checkUpdateVersion();

    }

    private void setStartTo(Intent intent2) {
        boolean isOpenFromShare = intent2.getBooleanExtra(Constant.IS_OPEN_FROM_SHARE, false);
        if ("".equals(Constant.getInstance().getChoosedCity().getCityName())) {
            Intent intent = new Intent(this, ChooseCityActivity.class);
            intent.putExtra("is_need_backbtn", false);
            if (isOpenFromShare) {
                intent.putExtra(Constant.INTENT, intent2.getParcelableExtra(Constant.INTENT));
            }
            startActivity(intent);
        }
        else {
            Constant.getInstance().isActivityShow(this);
            if (isOpenFromShare) {
                Intent intent =  intent2.getParcelableExtra(Constant.INTENT);
                Constant.fromShareSetIntent(this, intent);
            }
        }
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics() );
        return res;
    }

    private void checkUpdateVersion() {
        //判断手机是否有网络
        if(Util.isNetConnected(com.beautyyan.beautyyanapp.activity.MainActivity.this)) {
            /**
             * 1、从后台获取版本信息：是否强制更新、版本号、版本介绍、下载url、安装包大小
             * 2、判断是否有新版本，
             *      无新版本，不处理，结束本流程；
             * 3、有新版本，获取是否强制更新
             *      是，弹出只有"立即更新"按钮的弹框，强制更新
             *      否，弹出"立即更新"、"放弃更新"的弹框，
             * 4、点击"放弃"隐藏弹框，点击"立即更新"下载apk并显示进度
             * 5、下载结束，关闭本app，进行安装，（并重新启动app，待定）
             */
            RequestHelp.getUpdateVersionInfo(this, new HttpResponseListener() {
                @Override
                public void onSuccess(String data, String message) throws JSONException {
                    LogUtil.i(">>onSuccess-data:" + data + ">>message:" + message + ">>>");

                    JSONObject result = new JSONObject(data);
                    if (result != null) {
                        try {
                            JSONObject versionJson = result.getJSONObject("appVersionDetail");
                            if (versionJson != null) {
                                //获取服务端返回的数据
                                String appSize = versionJson.getString("appSize");//版本大小
                                String appReleaseTime = versionJson.getString("appReleaseTime");//版本更新时间
                                String appCategory = versionJson.getString("appCategory");//分类：安卓或者ios
                                String appIntroduction = versionJson.getString("appIntroduction");//版本更新介绍
                                int appForceUpdate = versionJson.getInt("appForceUpdate");//是否强制更新，1是强制更新，0不是强制更新
                                String appVersion = versionJson.getString("appVersion");//App版本号
                                String appDownloadUrl = versionJson.getString("appDownloadUrl");//下载地址
                                //获取平台是否为1-Android  2-ios
                                if(!Util.isEmpty(appCategory) && (Integer.parseInt(appCategory) == 1)) {
                                    //获取本地app版本build号
                                    int versionLocal = getPackageManager().getPackageInfo(getPackageName(),0).versionCode;
                                    int versionServer = 1;
                                    //转化为int类型
                                    if (!Util.isEmpty(appVersion)) {
                                        versionServer = (int)Double.parseDouble(appVersion);
                                    }
                                    //有新版本更新
                                    if (versionServer > versionLocal) {

                                        SharePreferUtil su = new SharePreferUtil(com.beautyyan.beautyyanapp.activity.MainActivity.this);
                                        int versionIgnore = su.getVersionIgnore();

                                        if ( versionIgnore == 1 || (versionIgnore < versionServer) || appForceUpdate == 1) {

                                            Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                                            intent.putExtra(Constant.APP_RELEASE_TIME,appReleaseTime);
                                            intent.putExtra(Constant.APP_CATEGORY,appCategory);
                                            intent.putExtra(Constant.APP_VERSION,appVersion);
                                            intent.putExtra(Constant.APP_INTRODUCTION,appIntroduction);
                                            intent.putExtra(Constant.APP_FORCE_UPDATE,appForceUpdate);
                                            intent.putExtra(Constant.APP_SIZE,appSize);
                                            intent.putExtra(Constant.APP_DOWNLOAD_URL,appDownloadUrl);
                                            startActivity(intent);

                                        }
                                    }
                                }
                            }
                        }catch (Exception e){
                        }
                    }
                }

                @Override
                public void onFail(String data, int errorCode, String message) throws JSONException {
                    LogUtil.i(">>onFail-data:" + data + ">>errorcode:" + errorCode + ">>message: " + message +">");

                }
            });
        }
    }

    // 5.0版本以上
    private void setStatusBarUpperAPI21() {
        Window window = getWindow();
        //设置透明状态栏,这样才能让 ContentView 向上
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
            ViewCompat.setFitsSystemWindows(mChildView, false);
        }
    }

    private void initView() {
        initMainFragment();
        initBuyFragment();
        initSaleFragment();
        initCallFragment();
        initMineFragment();
        setTabSelected(TAB_MAIN);
    }

    private void initMainFragment() {
        if (null != mainFragment) {
            return;
        }
        //获取fragment对象
        mainFragment = MainFragment.getInstance();

        //获取manager
        FragmentManager manager = this.getSupportFragmentManager();
        FragmentTransaction transation = manager.beginTransaction();
        transation.replace(R.id.main_frag_layout, mainFragment);
        try {
            transation.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            LogUtil.e("IllegalStateException error.");
        }
    }

    private void initBuyFragment() {
        buyFragment = new WebViewFragment(Constant.getInstance().getBuyCarUrl());
        //获取manager
        FragmentManager manager = this.getSupportFragmentManager();
        FragmentTransaction transation = manager.beginTransaction();
        transation.replace(R.id.buy_frag_layout, new WebViewFragment(Constant.getInstance().getBuyCarUrl()));
        transation.replace(R.id.buy_frag_layout, buyFragment);
        try {
            transation.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            LogUtil.e("IllegalStateException error.");
        }
    }

    private void initSaleFragment() {
        //获取fragment对象
        saleFragment = new WebViewFragment(Constant.getInstance().getSaleCarUrl());

        //获取manager
        FragmentManager manager = this.getSupportFragmentManager();
        FragmentTransaction transation = manager.beginTransaction();
        transation.replace(R.id.sale_frag_layout, saleFragment);
        try {
            transation.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            LogUtil.e("IllegalStateException error.");
        }
    }

    private void initCallFragment() {
        //获取fragment对象
        callFragment = new WebViewFragment(Constant.getInstance().getCallUrl());

        //获取manager
        FragmentManager manager = this.getSupportFragmentManager();
        FragmentTransaction transation = manager.beginTransaction();
        transation.replace(R.id.call_frag_layout, callFragment);
        try {
            transation.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            LogUtil.e("IllegalStateException error.");
        }
    }

    private void initMineFragment() {
        //获取fragment对象
        mineFragment = new WebViewFragment(Constant.getInstance().getMineUrl());

        //获取manager
        FragmentManager manager = this.getSupportFragmentManager();
        FragmentTransaction transation = manager.beginTransaction();
        transation.replace(R.id.mine_frag_layout, mineFragment);
        try {
            transation.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            LogUtil.e("IllegalStateException error.");
        }
    }

    /**
     * 获取设备高宽
     */
    private void setDeviceHW() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constant.getInstance().setDeviceHeight(dm.heightPixels);
        Constant.getInstance().setDeviceWidth(dm.widthPixels);
    }

    public void tabChanged() {
        setTabSelected(TAB_BUY);
    }


    public void searchByCartype(String carType, int priceMin, int priceMax) {
        clickTab(TAB_BUY);;
        if (buyFragment.isLoadFinish) {
            buyFragment.searchByCartype(Constant.getInstance().getChoosedCity().getCityId(),
                    Constant.getInstance().getChoosedCity().getCityName(), carType, priceMin, priceMax);
        } else {
            String url = Constant.getInstance().getBuyCarUrl() + '&' + "carType=" + carType
                    + '&' + "priceMin=" + priceMin
                    + '&' + "priceMax=" + priceMax;
            buyFragment.loadUrl(url);
            LogUtil.i("click url is " + url);
        }

    }

    public void searchBySaletype(int saleType) {
        clickTab(TAB_BUY);
        if (buyFragment.isLoadFinish) {
            buyFragment.searchBySaletype(Constant.getInstance().getChoosedCity().getCityId(), Constant.getInstance().getChoosedCity().getCityName(), saleType);
        } else {
            String url = Constant.getInstance().getBuyCarUrl() + '&' + "saleType=" + saleType;
            buyFragment.loadUrl(url);
            LogUtil.i("click url is " + url);
        }
    }

    public void searchByCarkind(String carKind) {

        clickTab(TAB_BUY);
        if (buyFragment.isLoadFinish) {
            buyFragment.searchByCarkind(Constant.getInstance().getChoosedCity().getCityId(), Constant.getInstance().getChoosedCity().getCityName(), carKind);
        } else {
            String url = Constant.getInstance().getBuyCarUrl() + '&' + "carKind=" + carKind;
            buyFragment.loadUrl(url);
            LogUtil.i("click url is " + url);
        }

    }

    public void searchByAdvanced(String key) {

        clickTab(TAB_BUY);
        if (buyFragment.isLoadFinish) {
            buyFragment.searchByAdvancedCondition(key);
        } else {
            String url = Constant.getInstance().getBuyCarUrl() + '&' + "advancedFilter=" + key;
            buyFragment.loadUrl(url);
            LogUtil.i("click url is " + url);
        }

    }

    public void searchAll() {

        clickTab(TAB_BUY);
        if (buyFragment.isLoadFinish) {
            buyFragment.searchAll(Constant.getInstance().getChoosedCity().getCityId(), Constant.getInstance().getChoosedCity().getCityName());
        } else {
            String url = Constant.getInstance().getBuyCarUrl();
            buyFragment.loadUrl(url);
            LogUtil.i("click url is " + url);
        }

    }

    public void setReloadTag() {
        buyFragment.isCityChanged = true;
        callFragment.isCityChanged = true;
        mineFragment.isCityChanged = true;
        saleFragment.isCityChanged = true;

        buyFragment.isLoading = true;
        callFragment.isLoading = true;
        mineFragment.isLoading = true;
        saleFragment.isLoading = true;

        callFragment.isLoadFinish = false;
        mineFragment.isLoadFinish = false;
        saleFragment.isLoadFinish = false;
    }

    public static MainActivity getInstance() {
        return instance;
    }

    private void initListener() {
        tabMain.setOnClickListener(this);
        tabBuy.setOnClickListener(this);
        tabSale.setOnClickListener(this);
        tabCalling.setOnClickListener(this);
        tabMine.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        buyFragment.clearPage();
        switch (v.getId()) {
            case R.id.tab_main:
                setTabSelected(TAB_MAIN);
                break;
            case R.id.tab_buy:
                setTabSelected(TAB_BUY);
                break;
            case R.id.tab_sale:
                setTabSelected(TAB_SALE);
                break;
            case R.id.tab_calling:
                setTabSelected(TAB_CALL);
                break;
            case R.id.tab_mine:
                setTabSelected(TAB_MINE);
                break;
            default:
                break;
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void clickTab(int which) {
        setSelect(tabMain, which == TAB_MAIN ? true : false);
        setSelect(tabBuy, which == TAB_BUY ? true : false);
        setSelect(tabSale, which == TAB_SALE ? true : false);
        setSelect(tabCalling, which == TAB_CALL ? true : false);
        setSelect(tabMine, which == TAB_MINE ? true : false);

        tabMainImg.setImageResource(which == TAB_MAIN ? R.mipmap.tab_main_selected : R.mipmap.tab_main_unselected);
        tabBuyImg.setImageResource(which == TAB_BUY ? R.mipmap.tab_buy_selected : R.mipmap.tab_buy_unselected);
        tabSaleImg.setImageResource(which == TAB_SALE ? R.mipmap.tab_sale_selected : R.mipmap.tab_sale_unselected);
        tabCallImg.setImageResource(which == TAB_CALL ? R.mipmap.tab_call_selected : R.mipmap.tab_call_unselected);
        tabMineImg.setImageResource(which == TAB_MINE ? R.mipmap.tab_mine_selected : R.mipmap.tab_mine_unselected);


        mainFragLayout.setVisibility(which == TAB_MAIN ? View.VISIBLE : View.GONE);
        buyFragLayout.setVisibility(which == TAB_BUY ? View.VISIBLE : View.GONE);
        saleFragLayout.setVisibility(which == TAB_SALE ? View.VISIBLE : View.GONE);
        callFragLayout.setVisibility(which == TAB_CALL ? View.VISIBLE : View.GONE);
        mineFragLayout.setVisibility(which == TAB_MINE ? View.VISIBLE : View.GONE);
    }

    public void refreshByCityChanged() {
            if (buyFragment.isLoadFinish) {
                buyFragment.isLoading = false;
                buyFragment.searchAll(Constant.getInstance().getChoosedCity().getCityId(), Constant.getInstance().getChoosedCity().getCityName());
            }
//            else {
//                String url = Constant.getInstance().getBuyCarUrl();
//                buyFragment.loadUrl(url);
//                LogUtil.i("click url is " + url);
//            }
        if (saleFragment.hasLoaded) {
            saleFragment.loadUrl(Constant.getInstance().getSaleCarUrl());
            saleFragment.isCityChanged = false;
        }
        if (callFragment.hasLoaded) {
            callFragment.loadUrl(Constant.getInstance().getCallUrl());
            callFragment.isCityChanged = false;
        }
        if (mineFragment.hasLoaded) {
            mineFragment.loadUrl(Constant.getInstance().getMineUrl());
            mineFragment.isCityChanged = false;
        }

    }

    public void setTabSelected(int which) {
        switch(which) {
            case TAB_MAIN:
                clickTab(TAB_MAIN);
                break;
            case TAB_BUY:
                clickTab(TAB_BUY);
                if (buyFragment.isCityChanged && !buyFragment.isLoadFinish) {
                    buyFragment.loadUrl(Constant.getInstance().getBuyCarUrl());
                    buyFragment.isCityChanged = false;
                    return;
                }
                checkWhetherReload(buyFragment, Constant.getInstance().getBuyCarUrl());
                break;
            case TAB_SALE:
                clickTab(TAB_SALE);
                if (!saleFragment.hasLoaded || saleFragment.isCityChanged) {
                    checkWhetherReload(saleFragment, Constant.getInstance().getSaleCarUrl());
                    saleFragment.isCityChanged = false;
                }
                break;
            case TAB_CALL:
                clickTab(TAB_CALL);
                if (!callFragment.hasLoaded || callFragment.isCityChanged) {
                    checkWhetherReload(callFragment, Constant.getInstance().getCallUrl());
                    callFragment.isCityChanged = false;
                }
                break;
            case TAB_MINE:
                clickTab(TAB_MINE);
                if (!mineFragment.hasLoaded || mineFragment.isCityChanged) {
                    checkWhetherReload(mineFragment, Constant.getInstance().getMineUrl());
                    mineFragment.isCityChanged = false;
                }
                break;
            default:
                break;
        }
    }

    private void checkWhetherReload(WebViewFragment webViewFragment, String url) {
        if (webViewFragment.isLoadFinish || (!webViewFragment.isCityChanged && webViewFragment.isLoading)) {
            return;
        }
        webViewFragment.loadUrl(url);
        LogUtil.i(url);
    }

    private void setSelect(View view, boolean isSelect) {
        view.setSelected(isSelect);
        view.setEnabled(!isSelect);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (newMsgReceiver != null) {
                unregisterReceiver(newMsgReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ButterKnife.unbind(this);
        clearData();
        System.exit(0);

    }

    private void clearData() {
        mainFragment.clearData();
        buyFragment.clearData();
        saleFragment.clearData();
        callFragment.clearData();
        mineFragment.clearData();
        XmppConnection.getInstance().closeConnection();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit2Click();
        }
        return false;
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
        }
    }

    private class NewMsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.CHAT_NEW_MSG)) {
                LogUtil.i("new msg coming!");
                Message msg = handler.obtainMessage();
                msg.what = Constant.MESSAGE.SHOW_POINT;
                msg.obj = intent.getBooleanExtra("isFromsend",false);
                MainFragment.getInstance().sendMessage(msg);

            }
        }
    }
}
