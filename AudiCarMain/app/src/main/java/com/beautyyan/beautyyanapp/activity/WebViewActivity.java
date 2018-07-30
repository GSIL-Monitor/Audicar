package com.beautyyan.beautyyanapp.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.UMHybrid;
import com.beautyyan.beautyyanapp.YuYuanApp;
import com.beautyyan.beautyyanapp.dsbridge.CompletionHandler;
import com.beautyyan.beautyyanapp.dsbridge.DWebView;
import com.beautyyan.beautyyanapp.http.HttpResponseListener;
import com.beautyyan.beautyyanapp.http.RequestHelp;
import com.beautyyan.beautyyanapp.http.ToBeanHelp;
import com.beautyyan.beautyyanapp.listener.MapChooseListener;
import com.beautyyan.beautyyanapp.listener.ShareListener;
import com.beautyyan.beautyyanapp.pay.PayResult;
import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.utils.DataClearUtil;
import com.beautyyan.beautyyanapp.utils.LogUtil;
import com.beautyyan.beautyyanapp.utils.ToastUtils;
import com.beautyyan.beautyyanapp.utils.Util;
import com.beautyyan.beautyyanapp.view.LoadingView;
import com.beautyyan.beautyyanapp.view.MapChooseDialog;
import com.beautyyan.beautyyanapp.view.RegulationIntroDialog;
import com.beautyyan.beautyyanapp.view.ShareDialog;
import com.google.gson.JsonObject;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import xmpp.activites.ChatActivity;
import xmpp.util.HideSoftInputHelperTool;

import static com.beautyyan.beautyyanapp.utils.Util.getValueByName;


/**
 * Created by xuelu on 2017/4/17.
 */
@SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
public class WebViewActivity extends AppCompatActivity implements IWXAPIEventHandler {
    @Bind(R.id.webview_web)
    DWebView webWeb;
    @Bind(R.id.reset_search)
    TextView resetSearch;
    @Bind(R.id.choose_select_img)
    ImageView chooseSelectImg;
    @Bind(R.id.share_icon)
    ImageView shareIcon;
    @Bind(R.id.share_img)
    ImageView shareImg;
    @Bind(R.id.detail_back_btn_zero)
    ImageView detailBackBtnZero;
    @Bind(R.id.activity_intro_txt)
    TextView activityIntroTxt;
    @Bind(R.id.loadview)
    LoadingView loadview;
    @Bind(R.id.webview_flay)
    RelativeLayout webviewFlay;
    @Bind(R.id.back_btn)
    ImageView backBtn;
    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.detail_rl_title)
    RelativeLayout detailRlTitle;
    @Bind(R.id.detail_view_bg)
    View detailViewBg;
    @Bind(R.id.detail_back_btn)
    ImageView detailBackBtn;
    @Bind(R.id.detail_collect_img)
    ImageView detailCollectImg;
    @Bind(R.id.rl_title)
    RelativeLayout rlTitle;
    @Bind(R.id.rl_t)
    RelativeLayout rlT;
    @Bind(R.id.title_detail)
    TextView titleDetail;
    @Bind(R.id.choose_city_txt)
    TextView chooseCityTxt;
    @Bind(R.id.view_auto)
    View viewAuto;
    @Bind(R.id.view_detail_auto)
    View viewDetailAuto;

    private DWebView webviewWeb;
    private String mTitle;
    private String mUrl;
    private Handler handler = new Handler();
    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;

    //用来统计优惠兑换机页面停留时长
    private long startTime;

    //UMENG跳出率 3秒内算跳出
    private final int TIAOCHU_TIME = 3;

    //针对车源详情页的渐变效果
    private final float rate = 578 / 750;

    //是否是车源详情页面
    private boolean isDetailActivity;

    //是否是经销商列表页面
    private boolean isStoreListActivity;

    //车源id
    private String carId;

    //渐变滑动距离
    private float dis;
    private CompletionHandler h5Handler;

    /**
     * 支付宝支付业务
     */
    private static final int SDK_ALIPAY_FLAG = 99999;

    private boolean isError = false;

    //活动页面
    private boolean isActivities = false;
    private String shareTitle;
    private String shareUrl;
    private String shareImgUrl;
    private String shareInfo;


    /**
     * 异步消息回调机制
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_ALIPAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知
                     */
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    String resultInfo = payResult.getResult();
                    Log.i("Pay", "Pay:" + resultInfo);
                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, "9000")) {
                        h5Handler.complete("0");
                    } else {
                        h5Handler.complete("1");
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            savedInstanceState = null;
        }
        super.onCreate(savedInstanceState);
        MobclickAgent.setScenarioType(WebViewActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        setContentView(R.layout.acti_webview_lay);
        Constant.getInstance().addToWebList(this);
        ButterKnife.bind(this);
        webWeb.setVisibility(View.GONE);
        webviewWeb = new DWebView(this);
        webviewWeb.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT));
        webviewFlay.addView(webviewWeb);
        if (getIntent().getBooleanExtra(Constant.IS_SHARE, false)
                && (Util.isWeiboInstalled(this)
                || Util.isWeixinInstalled(this)
                || Util.isQQClientInstalled(this))) {
            shareIcon.setVisibility(View.VISIBLE);
            shareIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    share();
                }
            });

        }
        initData();

        if (isDetailActivity)
        {
            initCarDetail();
        }
        else if (isStoreListActivity)
        {
            initStoreList();
        }

        switch (!Util.isEmpty(mTitle) ? mTitle.substring(0, 1) : "-9999")
        {
            case "1":
                initStoreDetail();
                break;
            case "2":
                initAdvancedFilters();
                break;
            case "3":
                initCoupon();
                break;
            case "4":
                initTichezuoye();
                break;
            case "5":
                initYouhuiduihuan();
                break;
            default:
                if ("-2".equals(mTitle))
                {
                    initSearchOrMap();
                }
                else
                {
                    YuYuanApp.getIns().setStatusBarForDetail(this, viewAuto);
                }
                break;

        }
        titleText.setText(mTitle);
        setListener();
        setWebView();
        loadview.changeLoadingStatus(0);
        loadUrl(mUrl);
//        shareImg.setVisibility(View.GONE);
//        shareIcon.setVisibility(View.GONE);
    }

    private void initData() {
        mTitle = getIntent().getExtras().getString(Constant.WEB_TITLE);
        mUrl = getIntent().getExtras().getString(Constant.WEB_URL);
        shareTitle = getIntent().getExtras().getString(Constant.SHARE_TITLE);
        shareUrl = getIntent().getExtras().getString(Constant.SHARE_URL);
//        if (!Util.isEmpty(shareUrl) && shareUrl.contains("shareInfo=")
//                && shareUrl.contains("shareImg=")
//                && shareUrl.indexOf("shareImg=") > shareUrl.indexOf("shareInfo=")) {
//            shareUrl = shareUrl.substring(0, shareUrl.indexOf("shareImg=") - 1);
//        }
        shareImgUrl = getIntent().getExtras().getString(Constant.SHARE_IMG);
        shareInfo = getIntent().getExtras().getString(Constant.SHARE_INFO);
        LogUtil.i("url1 is " + mUrl);
        isDetailActivity = getIntent().getBooleanExtra(Constant.IS_DETAIL_ACTIVITY, false);
        isActivities = getIntent().getBooleanExtra(Constant.IS_ACTIVITIES, false);
        isStoreListActivity = getIntent().getBooleanExtra(Constant.IS_STORELIST_ACTIVITY, false);
    }

    /**
     * 车源详情页初始化
     */
    private void initCarDetail() {
        YuYuanApp.getIns().setStatusBarForDetail(this, viewDetailAuto);
        viewDetailAuto.setBackgroundColor(getResources().getColor(R.color.title_bg));
        viewDetailAuto.setVisibility(View.VISIBLE);
        viewDetailAuto.setAlpha(0);
        shareImg.setImageResource(R.mipmap.share_icon_detail);
        detailCollectImg.setImageResource(R.drawable.collect_bg_detail);
        detailBackBtn.setVisibility(View.INVISIBLE);
        detailBackBtnZero.setVisibility(View.VISIBLE);
        YuYuanApp.getIns().setStatusBar(this, viewDetailAuto);
        carId = getIntent().getExtras().getString(Constant.CAR_DETAIL_ID);
        if (!"-1".equals(Constant.getInstance().getUser().getUserId())) {
            RequestHelp.isCollect(WebViewActivity.this, new IsCollectedListener(), Constant.getInstance().getUser().getUserId(), carId);
            LogUtil.i("iscollect : " + "userId : " + Constant.getInstance().getUser().getUserId() + "carId : " + carId);
        }
        LogUtil.i("width is" + Constant.getInstance().getDeviceWidth() * 3 / 4);
        rlTitle.setVisibility(View.GONE);
        detailRlTitle.setVisibility(View.VISIBLE);
        if (Util.isWeiboInstalled(this)
                || Util.isWeixinInstalled(this)
                || Util.isQQClientInstalled(this)) {
            shareImg.setVisibility(View.VISIBLE);
            shareImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    share();
                }
            });
        }
        detailRlTitle.post(new Runnable() {
            @Override
            public void run() {
                dis = Constant.getInstance().getDeviceWidth() * 3 / 4 - detailRlTitle.getHeight();
            }
        });
        webviewWeb.setOnScrollListener(new DWebView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int y) {
                if (y <= dis) {
//                        tintManager.setStatusBarAlpha(y / dis);
                    if (y / dis <= 0.2) {
                        detailBackBtn.setVisibility(View.INVISIBLE);
                        detailBackBtnZero.setVisibility(View.VISIBLE);
                        shareImg.setImageResource(R.mipmap.share_icon_detail);
                        detailCollectImg.setImageResource(R.drawable.collect_bg_detail);
                    } else {
                        detailBackBtn.setVisibility(View.VISIBLE);
                        detailBackBtnZero.setVisibility(View.INVISIBLE);
                        shareImg.setImageResource(R.mipmap.share_icon);
                        detailCollectImg.setImageResource(R.drawable.collect_bg);
                    }
                    detailViewBg.setAlpha(y / dis);
                    viewDetailAuto.setAlpha(y / dis);
                } else if (detailViewBg.getAlpha() != 1) {
                    detailViewBg.setAlpha(1);
                    viewDetailAuto.setAlpha(1);
//                        tintManager.setStatusBarAlpha(1);
                }
            }
        });
        detailCollectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("-1".equals(Constant.getInstance().getUser().getUserId())) {
                    Intent intent = new Intent(WebViewActivity.this, LoginActivity.class);
                    startActivityForResult(intent, Constant.RequestCode.CODE11);
                    return;
                }
                LogUtil.i("detailCollectImg.isSelected()" + detailCollectImg.isSelected());
                RequestHelp.collect(WebViewActivity.this, new CollectListener(), Constant.getInstance().getUser().getUserId(), carId, isActivities ? "2" : "1", "0", detailCollectImg.isSelected() ? "del" : "add");
                LogUtil.i("doCollect : " + "userId : " + Constant.getInstance().getUser().getUserId() + "carId : " + carId);
                detailCollectImg.setEnabled(false);
            }
        });
    }

    /**
     * 店铺列表页初始化
     */
    private void initStoreList() {
        viewAuto.setAlpha(1);
        YuYuanApp.getIns().setStatusBar(this, viewAuto);
        chooseCityTxt.setVisibility(View.VISIBLE);
        chooseCityTxt.setText(Constant.getInstance().getChoosedCity().getCityName());
        chooseCityTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WebViewActivity.this, ChooseCityActivity.class);
                intent.putExtra("from_h5", true);
                startActivityForResult(intent, Constant.RequestCode.CODE4);
            }
        });
    }

    /**
     * 搜索页和地图页初始化
     */
    private void initSearchOrMap() {
        rlT.setVisibility(View.GONE);
        viewAuto.setVisibility(View.GONE);
        YuYuanApp.getIns().setStatusBar(this, viewAuto);
    }

    /**
     * 经销商详情页初始化
     */
    private void initStoreDetail() {
        String dealerCode = Util.getValueByName(mUrl, "dealerCode");
        if (!Util.isEmpty(dealerCode)) {
            MobclickAgent.onEvent(WebViewActivity.this, dealerCode);
        }
        final String chatName = getIntent().getStringExtra("chatName");
        viewDetailAuto.setAlpha(0);
        YuYuanApp.getIns().setStatusBar(this, viewDetailAuto);
        rlTitle.setVisibility(View.GONE);
        detailRlTitle.setVisibility(View.VISIBLE);
        detailCollectImg.setImageResource(R.mipmap.message);
        mTitle = (mTitle.substring(1, mTitle.length()));
        titleDetail.setText(mTitle);
        detailCollectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("-1".equals(Constant.getInstance().getUser().getUserId())) {
                    Intent intentMsg = new Intent();
                    intentMsg.setClass(WebViewActivity.this, LoginActivity.class);
                    startActivityForResult(intentMsg, Constant.RequestCode.CODE5);
                    return;
                }
                if (chatName == null) {
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(WebViewActivity.this, ChatActivity.class);
                intent.putExtra("chatName", chatName);
                intent.putExtra("chatType", 0);
                intent.putExtra("name", mTitle);
                startActivity(intent);
                MobclickAgent.onEvent(WebViewActivity.this, Constant.UMENG_CLICK_CHAT);
            }
        });
    }

    /**
     * 高级筛选页初始化
     */
    private void initAdvancedFilters() {
        viewAuto.setAlpha(1);
        YuYuanApp.getIns().setStatusBar(this, viewAuto);
        mTitle = (mTitle.substring(1, mTitle.length()));
        resetSearch.setVisibility(View.VISIBLE);
        resetSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webviewWeb.callHandler("resetAdvancedSearch", new Object[]{}, new CompletionHandler() {
                    @Override
                    public void complete(String retValue) {

                    }
                });
//                    loadUrl(mUrl);
//                    LogUtil.i("url2 is " + mUrl);
            }
        });
    }

    /**
     * 优惠券选择页初始化
     */
    private void initCoupon() {
        viewAuto.setAlpha(1);
        YuYuanApp.getIns().setStatusBar(this, viewAuto);
        mTitle = (mTitle.substring(1, mTitle.length()));
        chooseSelectImg.setVisibility(View.VISIBLE);
        chooseSelectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webviewWeb.callHandler("getCouponInfo", new Object[]{}, new CompletionHandler() {
                    @Override
                    public void complete(String retValue) {
                        Intent intent = getIntent();
                        intent.putExtra("coupon_info", retValue);
                        setResult(0, intent);
                        finish();
                    }
                });
            }
        });
    }

    /**
     *提车作业初始化
     */
    private void initTichezuoye() {
        viewAuto.setAlpha(1);
        YuYuanApp.getIns().setStatusBar(this, viewAuto);
        mTitle = (mTitle.substring(1, mTitle.length()));
        resetSearch.setVisibility(View.VISIBLE);
        resetSearch.setText("发表");
        resetSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webviewWeb.callHandler("imageUpload", new Object[]{}, new CompletionHandler() {
                    @Override
                    public void complete(String retValue) {
                    }
                });
            }
        });
    }

    /**
     * 优惠兑换机页初始化
     */
    private void initYouhuiduihuan() {
        MobclickAgent.onEvent(WebViewActivity.this, Constant.UMENG_DUIHUAN_PV);
        startTime = System.currentTimeMillis();
        viewAuto.setAlpha(1);
        YuYuanApp.getIns().setStatusBar(this, viewAuto);
        mTitle = (mTitle.substring(1, mTitle.length()));
        activityIntroTxt.setVisibility(View.VISIBLE);
        activityIntroTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(WebViewActivity.this, Constant.UMENG_CLICK_ACTIVITY_INTRO);
                RegulationIntroDialog dialog = new RegulationIntroDialog(WebViewActivity.this);
                dialog.show();
            }
        });
    }

    private void clickDetailBackBtn() {
        hideSoftInput();
        if ("支付成功".equals(mTitle) || "订单已超时".equals(mTitle)) {
            for (int i = 0; i < Constant.getInstance().getListWeb().size(); i++) {
                Constant.getInstance().getListWeb().get(i).finish();
            }
            return;
        }

        final String[] result = {null};
        webviewWeb.callHandler("isH5BigPicStatus", new Object[]{}, new CompletionHandler() {
            @Override
            public void complete(String retValue) {
                result[0] = retValue;
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (result.length > 0 && "0".equals(result[0])) {
                    return;
                }
                finish();
            }
        }, 200);
    }

    private void loadUrl(String url) {
        webviewWeb.loadUrl(url);
        isError = false;
    }

    public void sendNative() {
        webviewWeb.callHandler("test", new Object[]{1, "hello"}, new CompletionHandler() {
            @Override
            public void complete(String retValue) {
                Log.i("jsbridge", "call succeed,return value is " + retValue);
                Toast.makeText(WebViewActivity.this, "from h5" + retValue, Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * 支付宝调用方法
     */
    private void beginAlipayPayWithOrderInfo(final String orderInfo) {
        /**
         *  验证输入参数的有效性
         */
        if (orderInfo != null && orderInfo.length() > 0) {
            //创建一个支付的任务
            Runnable payRunnable = new Runnable() {
                @Override
                public void run() {
                    //创建支付
                    PayTask alipay = new PayTask(WebViewActivity.this);
                    //执行支付
                    Map<String, String> result = alipay.payV2(orderInfo, true);
                    //向主线程发送消息
                    Message msg = new Message();
                    msg.what = SDK_ALIPAY_FLAG;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            };
            //开启线程执行支付任务
            Thread payThread = new Thread(payRunnable);
            payThread.start();
        } else {
            /**
             *   提示用户订单数据出错
             */
            ToastUtils.showShort(WebViewActivity.this, "订单数据出错");
        }
    }

    /**
     * 请求微信app，发起支付流程
     */
    private void startWXPay(JSONObject reqJson) {
        // TODO: 2017/6/16  微信支付


        String orderStr = null;
        String appid = null;
        try {
            orderStr = reqJson.getString("orderInfo");
            reqJson = new JSONObject(orderStr);
            appid = reqJson.getString("appid");
            Constant.getInstance().setWxPayId(appid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IWXAPI api = WXAPIFactory.createWXAPI(this, appid,true);
        //检查微信是否已被用户安装
        if (api.isWXAppInstalled()) {
            //判断当前微信的版本是否支持OpenApi
            if (api.isWXAppSupportAPI()) {
                if (reqJson != null) {

                    try {
                        String nonceStr = reqJson.getString("noncestr");
                        String partnerId = reqJson.getString("partnerid");
                        String prepayId = reqJson.getString("prepayid");
                        String sign = reqJson.getString("sign");
                        String timestamp = reqJson.getString("timestamp");

                        if (prepayId != null && sign != null && appid != null) {
                            api.registerApp(appid);

                            PayReq req = new PayReq();
                            req.appId = appid;
                            LogUtil.i("appid : " + appid);
                            req.nonceStr = nonceStr;
                            req.packageValue = "Sign=WXPay";
                            req.partnerId = partnerId;
                            req.prepayId = prepayId;
                            req.sign = sign;
                            req.timeStamp = timestamp;
                            api.sendReq(req);
                            registerReceiver(broadcastReceiver, new IntentFilter(Constant.BroadCastCode.code1));

                            return;
                        }
                        //订单数据异常
                        ToastUtils.showCenterShort(WebViewActivity.this, "订单数据异常");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //订单数据异常
                    ToastUtils.showCenterShort(WebViewActivity.this, "订单数据异常");
                }
            } else {
                //提示用户微信版本不支持此功能
                ToastUtils.showCenterShort(WebViewActivity.this, "您的微信版本不支持此功能!");//根据实际情况引导用户进行其他操作
            }
        } else {
            //提示用户请安装微信!
            ToastUtils.showCenterShort(WebViewActivity.this, "请安装微信!");//根据实际情况引导用户进行其他操作
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int result = intent.getIntExtra("result", -1);
            if (h5Handler != null) {
                h5Handler.complete(result == 0 ? "0" : "1");
            }
        }
    };

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        int result = 0;

        String msg = "支付失败";

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                msg = "支付成功";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                msg = "已取消";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                msg = "授权失败";
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                msg = "微信不支持";
                break;
            default:
                break;
        }

        if (msg.length() > 0) {
            ToastUtils.showCenterLong(this, msg);

        }
    }

    /**
     * h5
     */
    private class JsApi {

        @JavascriptInterface
        void goToAdvancedSearch(JSONObject jsonObject) throws JSONException {
            String json = jsonObject.toString();
            Intent intent = getIntent();
            intent.putExtra("json", json);
            setResult(0, intent);
            finish();
        }


        @JavascriptInterface
        void startChooseCityActivity(JSONObject jsonObject, CompletionHandler handler) throws JSONException {

            h5Handler = handler;
            Intent intent = new Intent(WebViewActivity.this, ChooseCityActivity.class);
            intent.putExtra("from_h5", true);
            startActivityForResult(intent, Constant.RequestCode.CODE10);
        }

        //for synchronous invocation
        @JavascriptInterface
        String testSyn(JSONObject jsonObject) throws JSONException {
            return jsonObject.getString("msg") + "［syn call］";
        }

        //for asynchronous invocation
        @JavascriptInterface
        void testAsyn(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            handler.complete(jsonObject.getString("msg") + " [asyn call]");
        }

        @JavascriptInterface
        String showAlert(JSONObject jsonObject) throws JSONException {
            ToastUtils.showCenterShort(YuYuanApp.getIns().getApplicationContext(), jsonObject.getString("msg"));
            return jsonObject.getString("msg");
        }

        @JavascriptInterface
        String showLongAlert(JSONObject jsonObject) throws JSONException {
            ToastUtils.showCenterLong(YuYuanApp.getIns().getApplicationContext(), jsonObject.getString("msg"));
            return jsonObject.getString("msg");
        }

        @JavascriptInterface
        void startChatActivity(JSONObject jsonObject) throws JSONException {
            String id = jsonObject.getString("id");
            String name = jsonObject.getString("name");
            Intent intent = new Intent();
            intent.setClass(WebViewActivity.this, ChatActivity.class);
            intent.putExtra("chatName", id);
            intent.putExtra("name", name);
            startActivity(intent);
            MobclickAgent.onEvent(WebViewActivity.this, Constant.UMENG_CLICK_CHAT);
        }

        @JavascriptInterface
        void startNativeActivity(JSONObject jsonObject) throws JSONException {
            LogUtil.i("startNativeActivity start!");
            String url = jsonObject.getString("url");
            String title = jsonObject.getString("title");
            String shareTitle = "";
            String shareImgUrl = "";
            String shareUrl = "";
            String shareInfo = "";
            try {
                shareTitle = jsonObject.getString("shareTitle");
            } catch (Exception e) {
                LogUtil.e(e.toString());
            }
            try {
                shareImgUrl = jsonObject.getString("shareImg");
            } catch (Exception e) {
                LogUtil.e(e.toString());
            }
            try {
                shareUrl = jsonObject.getString("shareUrl");
            } catch (Exception e) {
                LogUtil.e(e.toString());
            }
            try {
                shareInfo = jsonObject.getString("shareInfo");
            } catch (Exception e) {
                LogUtil.e(e.toString());
            }
            LogUtil.i(url);
            Intent intent = new Intent(WebViewActivity.this, WebViewActivity.class);
            intent.putExtra(Constant.WEB_URL, url);
            intent.putExtra(Constant.SHARE_TITLE, shareTitle);
            intent.putExtra(Constant.SHARE_URL, shareUrl);
            intent.putExtra(Constant.SHARE_IMG, shareImgUrl);
            intent.putExtra(Constant.SHARE_INFO, shareInfo);
            if ("订单确认".equals(title)) {
                MobclickAgent.onEvent(WebViewActivity.this, Constant.UMENG_CLICK_YUFU);
            } else if (("购车百科".equals(mTitle) && "购车百科".equals(title))
                    || ("提车作业".equals(mTitle) && "提车作业".equals(title))) {
                intent.putExtra(Constant.IS_SHARE, true);
            }
            if ("一键留言".equals(mTitle)
                    || getIntent().getBooleanExtra(Constant.IS_FROMONEKEY, false)) {
                intent.putExtra(Constant.IS_FROMONEKEY, true);
            }
            if (("一键留言".equals(mTitle)
                    || getIntent().getBooleanExtra(Constant.IS_FROMONEKEY, false))
                    && !Util.isEmpty(getValueByName(url, "answer"))
                    && !"undefined".equals(getValueByName(url, "answer"))) {
                intent.putExtra(Constant.IS_SHARE, true);
            }
            if (!Util.isEmpty(getValueByName(url, "activityId"))) {
                intent.putExtra(Constant.CAR_DETAIL_ID, getValueByName(url, "activityId"));
                intent.putExtra(Constant.IS_ACTIVITIES, true);
            }
            if (!Util.isEmpty(getValueByName(url, "releaseNumber"))) {
                intent.putExtra(Constant.CAR_DETAIL_ID, getValueByName(url, "releaseNumber"));
            }

            if ("-1".equals(title)) {
                intent.putExtra(Constant.IS_DETAIL_ACTIVITY, true);
            }
            intent.putExtra(Constant.WEB_TITLE, title);
            if (url.contains(Constant.URL.SEARCH)) {
                startActivityForResult(intent, Constant.RequestCode.CODE2);
                return;
            }
            if (!Util.isEmpty(title) && "3".equals(title.substring(0, 1))) {
                startActivityForResult(intent, Constant.RequestCode.CODE6);
                return;
            } else if (!Util.isEmpty(title) && "2".equals(title.substring(0, 1))) {
                startActivityForResult(intent, Constant.RequestCode.CODE7);
                return;
            } else if (!Util.isEmpty(title) && "1".equals(title.substring(0, 1))) {
                String chatName = Util.getValueByName(url, "dealerCode");
                intent.putExtra("chatName", chatName);
            }
            startActivity(intent);
        }

        @JavascriptInterface
        long isLogined(JSONObject jsonObject) throws JSONException {

            return Constant.getInstance().getUser() == null ? -1 :
                    Long.valueOf(Constant.getInstance().getUser().getUserId());
        }

        @JavascriptInterface
        void finishActivity(JSONObject jsonObject) throws JSONException {
            hideSoftInput();
            finish();
        }

        @JavascriptInterface
        void searchToBuy(JSONObject jsonObject) throws JSONException {
            String keyword = "";
            if (jsonObject.getString("keyword").contains("keyword")) {
                JSONObject jsonObject1 = new JSONObject(jsonObject.getString("keyword"));
                keyword = jsonObject1.getString("keyword");
            } else {
                keyword = jsonObject.getString("keyword");
            }
            Intent intent = getIntent();
            intent.putExtra("keyword", keyword);
            setResult(0, intent);
            finish();
        }

        @JavascriptInterface
        void searchToBuyFromH5(JSONObject jsonObject) throws JSONException {
            String keyword = "";
            if (jsonObject.getString("keyword").contains("keyword")) {
                JSONObject jsonObject1 = new JSONObject(jsonObject.getString("keyword"));
                keyword = jsonObject1.getString("keyword");
            } else {
                keyword = jsonObject.getString("keyword");
            }
            Intent intent = getIntent();
            intent.putExtra("keyword", keyword);
            setResult(0, intent);
            finish();
        }

        @JavascriptInterface
        String getUserId(JSONObject jsonObject) throws JSONException {
            LogUtil.i("id is " + String.valueOf(Constant.getInstance().getUser().getUserId()));
            return String.valueOf(Constant.getInstance().getUser().getUserId());
        }

        @JavascriptInterface
        String getUserName(JSONObject jsonObject) throws JSONException {
            return String.valueOf(Constant.getInstance().getUser().getPhone());
        }

        @JavascriptInterface
        void doLogin(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            if (handler == null) {
                LogUtil.i("h5Handler is null!");
                return;
            }
            startActivityForResult(new Intent(WebViewActivity.this, LoginActivity.class), Constant.RequestCode.CODE3);
            h5Handler = handler;
        }

        @JavascriptInterface
        void goToPay(final JSONObject jsonObject, CompletionHandler handler) throws JSONException {
            if (handler == null) {
                LogUtil.i("h5Handler is null!");
                return;
            }
            String type = jsonObject.getString("type");
            h5Handler = handler;
            switch (type) {
                case "1":
                    String orderInfo = jsonObject.getString("orderInfo");
                    JSONObject jsonObject1 = null;
                    jsonObject1 = new JSONObject(orderInfo);
                    String url = jsonObject1.optString("alipayUrl");
                    beginAlipayPayWithOrderInfo(url);
//                    Intent intent = new Intent(WebViewActivity.this, WebViewActivity.class);
//                    intent.putExtra(Constant.WEB_TITLE, "在线支付");
////                    intent.putExtra(Constant.WEB_URL, "https://audipay.beautyyan.cn/pay/gateway.do?info=Jw14lpxH2mBpinbmEFAE8%2BV6ts219XtMVJUy4C0znIiaFVxVbSMJOh3DTsGtHXpgVdLC6%2Bd6RnTD%0D%0AqB4IFGOaGFJMlbZwAKbgvXW2sXv2FnQE0bn8KNSWWVcU91zXPp%2FHnMsw03tV%2BPBfk1w1Jrvp5V9M%0D%0A5fnWxqojhOPlxvzOZWI1YH3jTyH0%2Bm2VeuxI3vyRILhRdyTsKghjsy672OA%2Fhhmm6dKLWFsjH2jt%0D%0A8hS4McHW9wv1Nmf4zVtOlpcGlIpxYEP7Im4DDUqysZD%2B4aG1R49tlS4eSFkQ%2B%2B1Ifl0g5KEU0J4k%0D%0Azrj8V7DpA5s%2F1%2BqDgVsKm6kDDZeuWyUX8gDn1lLiyVhnGYt4LNNEZhAX1tdeeO2U1fLbvzz90a6v%0D%0AjDeOKRLkS%2B7BC6CDx6YyV17GyQZqFlk1Z3QtouwruMRqWFVYoSB3Wyxvm3xOwBNEddMR0jRjJQax%0D%0AR2x%2BsmW7fSB6Z8o8WQUB%2Fpu%2FkDktKr6N3ZGyS5ZWZvZgJRzXR4GaSD0PyBtsss4JeEE71j8GxyUI%0D%0A9YU259rHy3HjTz4Lt12hmgH8yODZD8lmUGYaQv66JpGgCWbewhg3QFtCow4GpoT%2FBfVWS%2B3mFDPQ%0D%0AAdZWvH6gdR418c57KplGbMTvNiml7aC1SM9BJa2MVldUBpSnvpSjus56kogfLO4fSKnVgxgK8eO8%0D%0AleMSyt14EnUeXhvGjfQ5kDnUx7QywLm%2BNHmmEwlUNo2HsrSIvX%2B3e5mFYKFFM2Hn11QxPbrBas8r%0D%0AsyJ%2B3BFgT7pVVXTrOEIEzh57TrutRkWAQw82CLMD9SSh9Z5Fy8tHc6m6oGZxvLiCq8nJbnnkzI5C%0D%0Azo%2B3BBEB64xvpjE6YFyWnHfSWl6W9ebxH2UqpcjJj8gM%2BULlnLQcNN9m2NloWeUhmzjK%2Fa%2BnBWah%0D%0AS0vArzDx0IWUBYI9JR05quJkTxv%2FTb4yA%2FcmuKTGOYMzTvjLpsTtTcwtx6usVkmQUNnwKqEP0FD%2F%0D%0AEmokBBZ9EIMTdl6fL%2Bw30XZWk3fQVrjXk6Fb");
//                    intent.putExtra(Constant.WEB_URL, url);
//                    startActivityForResult(intent, Constant.RequestCode.CODE9);
                    break;
                case "2":
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            startWXPay(jsonObject);
                        }
                    });
                    break;
                default:
                    break;
            }
        }

        @JavascriptInterface
        void goToBuy(JSONObject jsonObject) throws JSONException {
            for (int i = 0; i < Constant.getInstance().getListWeb().size(); i++) {
                Constant.getInstance().getListWeb().get(i).finish();
            }
            MainActivity.getInstance().handler.sendEmptyMessage(Constant.MESSAGE.GO_TO_BUY);
        }

        @JavascriptInterface
        void startOrderInfoActivity(JSONObject jsonObject) throws JSONException {
            String url = jsonObject.getString("url");
//            Intent intent = new Intent(WebViewActivity.this, WebViewActivity.class);
//            intent.putExtra(Constant.WEB_TITLE, title);
//            intent.putExtra(Constant.WEB_URL, url);
//            startActivity(intent);
            for (int i = 0; i < Constant.getInstance().getListWeb().size(); i++) {
                Constant.getInstance().getListWeb().get(i).finish();
            }
            Message msg = new Message();
            msg.what = Constant.MESSAGE.GO_TO_ORDER_LIST;
            msg.obj = url;
            MainActivity.getInstance().handler.sendMessage(msg);
        }

        @JavascriptInterface
        void logout(JSONObject jsonObject) throws JSONException {
            for (int i = 0; i < Constant.getInstance().getListWeb().size(); i++) {
                Constant.getInstance().getListWeb().get(i).finish();
            }
            Constant.getInstance().toNullUser();
            MainActivity.getInstance().handler.sendEmptyMessage(Constant.MESSAGE.CLEAR_LOGININFO);
        }

        @JavascriptInterface
        void startIntroductionActivity(JSONObject jsonObject) throws JSONException {
            int type = jsonObject.getInt("type");
            Intent intent = new Intent();
            switch (type) {
                case 1:
                    intent.setClass(WebViewActivity.this, OneOneZeroActivity.class);
                    break;
                case 2:
                    intent.setClass(WebViewActivity.this, YanChangActivity.class);
                    break;
                case 3:
                    intent.setClass(WebViewActivity.this, TwoYearsActivity.class);
                    break;
                default:
                    intent.setClass(WebViewActivity.this, TwoYearsActivity.class);
                    break;
            }
            startActivity(intent);

        }

        @JavascriptInterface
        String getCache(JSONObject jsonObject) throws JSONException {
            String size = "";
            try {
                size = DataClearUtil.getTotalCacheSize(WebViewActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return size;
        }

        @JavascriptInterface
        String clearCache(JSONObject jsonObject) throws JSONException {
            return DataClearUtil.clearAllCache(WebViewActivity.this) ? "0" : "1";
        }

        /**
         *双十一活动数据统计--点击兑换按钮
         * @param jsonObject
         * @throws JSONException
         */
        @JavascriptInterface
        void clickExchangeAudiB(JSONObject jsonObject) throws JSONException {
            MobclickAgent.onEvent(WebViewActivity.this, Constant.UMENG_CLICK_DUIHUAN);

        }

        /**
         *双十一活动数据统计--点击兑换记录
         * @param jsonObject
         * @throws JSONException
         */
        @JavascriptInterface
        void clickExchangeRecord(JSONObject jsonObject) throws JSONException {
            MobclickAgent.onEvent(WebViewActivity.this, Constant.UMENG_CLICK_DUIHUAN_RECORD);
        }

        /**
         *双十一活动数据统计--优惠兑换机页面的登录
         * @param jsonObject
         * @throws JSONException
         */
        @JavascriptInterface
        void clickLogin(JSONObject jsonObject) throws JSONException {
            MobclickAgent.onEvent(WebViewActivity.this, Constant.UMENG_CLICK_LOGIN);
        }

        /**
         *双十一活动数据统计--点击优惠兑换机页面的背景
         * @param jsonObject
         * @throws JSONException
         */
        @JavascriptInterface
        void clickBackground(JSONObject jsonObject) throws JSONException {
            MobclickAgent.onEvent(WebViewActivity.this, Constant.UMENG_CLICK_BACKGROUND);
        }

        /**
         * 拨打400电话
         * @param jsonObject
         * @throws JSONException
         */
        @JavascriptInterface
        void callTel(JSONObject jsonObject) throws JSONException {
            String tel = jsonObject.getString("tel");
            if (!TextUtils.isEmpty(tel)){
                call(tel);
            }
        }
    }

    /**
     * 调用拨号界面
     * @param phone 电话号码
     */
    private void call(String phone) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+phone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                clickBack();
            }
        });
        loadview.setFailClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadview.changeLoadingStatus(0);
                loadUrl(mUrl);
            }
        });
        detailBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDetailBackBtn();
            }
        });
        detailBackBtnZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDetailBackBtn();
            }
        });
    }

    private void hideSoftInput() {
        if (WebViewActivity.this.getCurrentFocus() != null) {
            HideSoftInputHelperTool.hideSoftInput(WebViewActivity.this, WebViewActivity.this.getCurrentFocus().getWindowToken());
        }
    }

    private class IsCollectedListener implements HttpResponseListener {

        @Override
        public void onSuccess(String data, String message) throws JSONException {
            if (isFinishing()) {
                return;
            }
            boolean isCollect = ToBeanHelp.isCollected(data);
            detailCollectImg.setSelected(isCollect);
            detailCollectImg.setEnabled(true);
        }

        @Override
        public void onFail(String data, int errorCode, String message) throws JSONException {
            if (isFinishing()) {
                return;
            }
            detailCollectImg.setEnabled(true);
        }
    }


    private class CollectListener implements HttpResponseListener {

        @Override
        public void onSuccess(String data, String message) throws JSONException {
            if (isFinishing()) {
                return;
            }
            detailCollectImg.setEnabled(true);
            detailCollectImg.setSelected(!detailCollectImg.isSelected());
        }

        @Override
        public void onFail(String data, int errorCode, String message) throws JSONException {
            if (isFinishing()) {
                return;
            }
            detailCollectImg.setEnabled(true);
            ToastUtils.showCenterShort(WebViewActivity.this, detailCollectImg.isSelected() ? "取消收藏失败" : "收藏失败");
        }
    }

    private void clickBack() {
//        if (webviewWeb.canGoBack()) {
//            webviewWeb.goBack();
//        } else {
        if ("支付成功".equals(mTitle) || "订单已超时".equals(mTitle)) {
            for (int i = 0; i < Constant.getInstance().getListWeb().size(); i++) {
                Constant.getInstance().getListWeb().get(i).finish();
            }
            return;
        }
        final String[] result = {null};
        webviewWeb.callHandler("isH5BigPicStatus", new Object[]{}, new CompletionHandler() {
            @Override
            public void complete(String retValue) {
                result[0] = retValue;
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (result.length > 0 && "0".equals(result[0])) {
                    return;
                }
                finish();
            }
        }, 100);
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            clickBack();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (startTime != 0) {
            int duration = (int)(System.currentTimeMillis() - startTime);
            Map<String, String> map = new HashMap<>();
            MobclickAgent.onEventValue(WebViewActivity.this, Constant.UMENG_DUIHUAN_STAY_TIME, map, duration);
            if (duration / 1000 < TIAOCHU_TIME) {
                MobclickAgent.onEvent(WebViewActivity.this, Constant.UMENG_DUIHUAN_TIAOCHU);
            }
        }
        webviewFlay.removeView(webviewWeb);
        webviewWeb.removeAllViews();
        webviewWeb.destroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        webviewWeb.saveState(outState);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView() {
        webviewWeb.setJavascriptInterface(new JsApi());
               /*
         * webSettings 保存着WebView中的状态信息。
		 * 当WebView第一次被创建时，webSetting中存储的都为默认值。
		 * WebSetting和WebView一一绑定的。
		 * 如果webView被销毁了，那么我们再次调用webSetting中的方法时，会抛出异常。
		 */
        WebSettings webSettings = webviewWeb.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        webviewWeb.setWebViewClient(new HarlanWebViewClient());

//        拦截h5弹窗
        HarlanWebChromeClient chromeClient = new HarlanWebChromeClient();
        webviewWeb.setWebChromeClient(chromeClient);

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        webviewWeb.setHorizontalScrollBarEnabled(false);
        webviewWeb.setVerticalScrollBarEnabled(false);
        webviewWeb.setDownloadListener(new MyWebViewDownLoadListener());


// 设置可以访问文件
        webSettings.setAllowFileAccess(true);
// 设置支持缩放
        webSettings.setBuiltInZoomControls(true);

// 使用localStorage则必须打开
        webSettings.setDomStorageEnabled(true);

        webSettings.setGeolocationEnabled(true);


        webSettings.setSupportZoom(false);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true); //适应屏幕


//        webSettings.setDatabaseEnabled(true);   //开启数据库形式存储
//
//        String appCacheDir = getApplication().getDir("cache", MODE_PRIVATE).getPath();   //缓存数据的存储地址
//
//        webSettings.setAppCachePath(appCacheDir);
//
//        webSettings.setAppCacheEnabled(true);  //开启缓存功能
//
//        //缓存模式
//        if (Util.isNetConnected(this)) {
//            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//        }
//        else {
//            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        }


//            loadUrl(url);

//		/*
//		 * 为js提供一个方法，注意该方法一般不写在UI线程中
//		 * addJavascriptInterface(Object obj, String interfaceName)
//		 * obj代表一个java对象，这里我们一般会实现一个自己的类，类里面提供我们要提供给javascript访问的方法
//		 * interfaceName则是访问我们在obj中声明的方法时候所用到的js对象，调用方法为window.interfaceName.方法名(
//		 * )
//		 */
//        webviewWeb.addJavascriptInterface(new SubMaintenanceJavaScriptInterface(), "control");
//    }
//    final class SubMaintenanceJavaScriptInterface {
//
//        @JavascriptInterface
//        public void showToast(String toast) {
//
//            ToastUtils.showShort(WebViewActivity.this, toast);
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
////                    ToastUtils.showShort(WebViewActivity.this, "gocar");
//                    LogUtil.i("-----------------");
//                }
//            });
//
//        }
    }

    private void share() {
        if (!Util.isWeiboInstalled(this)
                && !Util.isWeixinInstalled(this)
                && !Util.isQQClientInstalled(this)) {
            ToastUtils.showCenterShort(WebViewActivity.this, "请至少安装QQ、微博、微信一种");
            return;
        }
        ShareDialog shareDialog = new ShareDialog(this, new ShareListener() {
            @Override
            public void qq() {
                if (!Util.isQQClientInstalled(WebViewActivity.this)) {
                    ToastUtils.showCenterShort(WebViewActivity.this, "未安装QQ");
                    return;
                }
                goToShare(SHARE_MEDIA.QQ);
            }

            @Override
            public void weixin() {
                if (!Util.isWeixinInstalled(WebViewActivity.this)) {
                    ToastUtils.showCenterShort(WebViewActivity.this, "未安装微信");
                    return;
                }
                goToShare(SHARE_MEDIA.WEIXIN);
            }

            @Override
            public void weibo() {
                if (!Util.isWeiboInstalled(WebViewActivity.this)) {
                    ToastUtils.showCenterShort(WebViewActivity.this, "未安装微博");
                    return;
                }
                goToShare(SHARE_MEDIA.SINA);
            }

            @Override
            public void qqZone() {
                if (!Util.isQQClientInstalled(WebViewActivity.this)) {
                    ToastUtils.showCenterShort(WebViewActivity.this, "未安装QQ");
                    return;
                }
                goToShare(SHARE_MEDIA.QZONE);
            }

            @Override
            public void weixinCircle() {
                if (!Util.isWeixinInstalled(WebViewActivity.this)) {
                    ToastUtils.showCenterShort(WebViewActivity.this, "未安装微信");
                    return;
                }
                goToShare(SHARE_MEDIA.WEIXIN_CIRCLE);

            }
        });
        shareDialog.show();
        WindowManager.LayoutParams lpShare = shareDialog.getWindow().getAttributes();
        lpShare.width = Constant.getInstance().getDeviceWidth();
        shareDialog.getWindow().setAttributes(lpShare);
    }

    private void goToShare(SHARE_MEDIA plat) {
        UMImage image = null;
        if (Util.isEmpty(shareImgUrl) || "undefined".equals(shareImgUrl)) {
            image = new UMImage(WebViewActivity.this, R.mipmap.app_icon);
        } else {
            image = new UMImage(WebViewActivity.this, shareImgUrl);
        }
        UMWeb web = new UMWeb(Util.isEmpty(shareUrl) ? Constant.URL.WANT_BUY_CAR : shareUrl);
        web.setTitle(Util.isEmpty(shareTitle) ? "奥迪二手车" : shareTitle);//标题
        web.setThumb(image);  //缩略图
        web.setDescription(Util.isEmpty(shareInfo) ? "奥迪二手车" : shareInfo);//描述
        if (plat.equals(SHARE_MEDIA.SINA)) {
            new ShareAction(WebViewActivity.this)
                    .setPlatform(plat)//传入平台
                    .withText(Util.isEmpty(shareInfo) ? "奥迪二手车" : shareInfo + shareUrl)//分享内容
                    .withMedia(web)
                    .setCallback(shareListener)//回调监听器
                    .share();
        }
        else {
            new ShareAction(WebViewActivity.this)
                    .setPlatform(plat)//传入平台
                    .withMedia(web)
                    .setCallback(shareListener)//回调监听器
                    .share();
        }
    }

    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
//            Toast.makeText(WebViewActivity.this, "", Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(WebViewActivity.this, "分享成功", Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(WebViewActivity.this, "失败" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(WebViewActivity.this,"取消了",Toast.LENGTH_LONG).show();
        }
    };

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }

    /***
     * webChromeClient主要是将javascript中相应的方法翻译成android本地方法
     * 例如：我们重写了onJsAlert方法，那么当页面中需要弹出alert窗口时，便
     * 会执行我们的代码，按照我们的Toast的形式提示用户。
     */
    class HarlanWebChromeClient
            extends
            WebChromeClient {

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
        }

        @Override
        public void onHideCustomView() {
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            // TODO Auto-generated method stub
            Log.d("ZR", consoleMessage.message() + " at " + consoleMessage.sourceId() + ":" + consoleMessage.lineNumber());
            return super.onConsoleMessage(consoleMessage);
        }

        /*
         * 此处覆盖的是javascript中的alert方法。
         * 当网页需要弹出alert窗口时，会执行onJsAlert中的方法
         * 网页自身的alert方法不会被调用。
         */
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            /*
             * 此处代码非常重要，若没有，android就不能与js继续进行交互了，
			 * 且第一次交互后，webview不再展示出来。
			 * result：A JsResult to confirm that the user hit enter.
			 * 我的理解是，confirm代表着此次交互执行完毕。只有执行完毕了，才可以进行下一次交互。
			 */
            result.confirm();
            return true;
        }

        /*
         * 此处覆盖的是javascript中的confirm方法。
         * 当网页需要弹出confirm窗口时，会执行onJsConfirm中的方法
         * 网页自身的confirm方法不会被调用。
         */
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return true;
        }

        /*
         * 此处覆盖的是javascript中的confirm方法。
         * 当网页需要弹出confirm窗口时，会执行onJsConfirm中的方法
         * 网页自身的confirm方法不会被调用。
         */
        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            result.confirm();
            return true;
        }

        /*
         * 如果页面被强制关闭,弹窗提示：是否确定离开？
         * 点击确定 保存数据离开，点击取消，停留在当前页面
         */
        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            return true;
        }

        //扩展浏览器上传文件
        //3.0++版本
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooserImpl(uploadMsg);
        }

        //3.0--版本
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooserImpl(uploadMsg);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileChooserImpl(uploadMsg);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            onenFileChooseImpleForAndroid(filePathCallback);
            return true;
        }
    }

    public ValueCallback<Uri> mUploadMessage;

    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

    public ValueCallback<Uri[]> mUploadMessageForAndroid5;

    private void onenFileChooseImpleForAndroid(ValueCallback<Uri[]> filePathCallback) {
        mUploadMessageForAndroid5 = filePathCallback;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }


    class HarlanWebViewClient
            extends
            WebViewClient {
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            WebResourceResponse response = null;
            response = super.shouldInterceptRequest(view, url);
            return response;
        }

        /*
         * 点击页面的某条链接进行跳转的话，会启动系统的默认浏览器进行加载，调出了我们本身的应用
         * 因此，要在shouldOverrideUrlLoading方法中
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                String decodedURL = URLDecoder.decode(url, "UTF-8");
                UMHybrid.getInstance(WebViewActivity.this).execute(decodedURL, view);
            } catch (Exception e) {
                e.printStackTrace();
            }
            LogUtil.i("访问的url地址：" + url);
            final PayTask task = new PayTask(WebViewActivity.this);
            //处理订单信息
            final String ex = task.fetchOrderInfoFromH5PayUrl(url);
            if (!TextUtils.isEmpty(ex)) {
                //调用支付接口进行支付
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final H5PayResultModel result = task.h5Pay(ex, true);
                        //处理返回结果
                        /**
                         返回码，标识支付状态，含义如下：
                         9000——订单支付成功
                         8000——正在处理中
                         4000——订单支付失败
                         5000——重复请求
                         6001——用户中途取消
                         6002——网络连接出错
                         */
                        Log.i("info", ">>>>>>>>" + result.getReturnUrl() + "<<<<<<<<" + result.getResultCode());
                        Intent intent = getIntent();
                        intent.putExtra("alipay_result", result.getResultCode());
                        setResult(0, intent);
                        finish();

                    }

                }).start();
            } else if (url.startsWith("http:") || url.startsWith("https:")) {
                view.loadUrl(url);
                return true;
            }
            if (url.startsWith("godealer:")) {
                url = Uri.decode(url);
                int end = url.indexOf("?");
                int start = url.indexOf("//");
                String title = '1' + url.substring(start + 2, end);
                url = Constant.URL.STORE_DETAIL + '?' + "&cityName="
                        + Constant.getInstance().getChoosedCity().getCityName() + '&' + url.substring(end + 1, url.length());
                Intent intent = new Intent(WebViewActivity.this, WebViewActivity.class);
                intent.putExtra(Constant.WEB_TITLE, title);
                intent.putExtra(Constant.WEB_URL, url);
                String chatName = Util.getValueByName(url, "dealerCode");
                intent.putExtra("chatName", chatName);
                startActivity(intent);
                return true;
            } else if (url.startsWith("navigation:")) {
                String lat = Util.getValueByName(url, "lat");
                String lon = Util.getValueByName(url, "lon");
                String dealerName = Util.getValueByName(url, "dealerName");
                mapChoose(WebViewActivity.this, lon, lat, dealerName);
                return true;
            }
            return true;

        }

        /*
         * 网页加载完毕(仅指主页，不包括图片)
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        /*
         * 网页加载完毕(仅指主页，不包括图片)
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            LogUtil.i("load web finish!");
            if (isError) {
                return;
            }
            if (isFinishing()) {
                return;
            }
            webviewWeb.setVisibility(View.VISIBLE);
            loadview.changeLoadingStatus(2);
        }

        /*
         * 加载页面资源
         */
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        /*
         * 错误提示
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (isFinishing() || failingUrl.startsWith("godealer:")) {
                return;
            }
            isError = true;
            webviewWeb.setVisibility(View.GONE);
            loadview.changeLoadingStatus(-1);
            LogUtil.i("errorCode" + description);
//            webviewWeb.setVisibility(View.GONE);
//            ToastUtils.showShort(WebViewActivity.this, "加载失败");
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) {
                return;
            }
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (null == mUploadMessageForAndroid5) {
                return;
            }
            Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
        if (resultCode != 0 || data == null) {
            return;
        }
        switch (requestCode) {
            case Constant.RequestCode.CODE11:
                RequestHelp.isCollect(WebViewActivity.this, new IsCollectedListener(), Constant.getInstance().getUser().getUserId(), carId);
                break;
            case Constant.RequestCode.CODE10:
                if (h5Handler == null) {
                    return;
                }
                String cityId = data.getExtras().getString("cityId");
                String cityName = data.getExtras().getString("cityName");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("cityId", cityId);
                jsonObject.addProperty("cityName", cityName);
                h5Handler.complete(jsonObject.toString());
                break;
            case Constant.RequestCode.CODE3:
                if (h5Handler == null) {
                    return;
                }
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("userId", Constant.getInstance().getUser().getUserId());
//            jsonObject.addProperty("userName",Constant.getInstance().getUser().getPhone());

                String userId = data.getExtras().getString("userId");
                String userName = data.getExtras().getString("userName");
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("userId", userId);
                jsonObject2.addProperty("userName", userName);
                h5Handler.complete(jsonObject2.toString());
                break;
            case Constant.RequestCode.CODE4:
                String cityId2 = data.getExtras().getString("cityId");
                String cityName2 = data.getExtras().getString("cityName");
                chooseCityTxt.setText(cityName2);
                webviewWeb.clearCache(true);
                loadview.changeLoadingStatus(0);
                loadUrl(Constant.URL.STORE_LIST + "?cityId=" + cityId2 + "&cityName=" + cityName2);
                break;
            case Constant.RequestCode.CODE6:
                String couponInfo = data.getExtras().getString("coupon_info");
                webviewWeb.callHandler("notifyCouponSelected", new Object[]{couponInfo}, new CompletionHandler() {
                    @Override
                    public void complete(String retValue) {
                    }
                });
                break;
            case Constant.RequestCode.CODE7:
                break;
            case Constant.RequestCode.CODE9:
                String payResult = data.getStringExtra("alipay_result");
                Message msg = mHandler.obtainMessage();
                msg.what = SDK_ALIPAY_FLAG;
                msg.obj = payResult;
                mHandler.sendMessage(msg);
                break;
            default:
                break;
        }
    }

    private void mapChoose(final Context context, final String lon2, final String lat2, final String name) {
        MapChooseDialog dialog = new MapChooseDialog(context, new MapChooseListener() {
            @Override
            public void clickBaiduMap() {
                Intent intent;
                if (Util.isAvilible(context, "com.baidu.BaiduMap")) {
                    try {
                        intent = Intent.getIntent("baidumap://map/direction?origin=我的位置" + "&destination=name:" + name + "|latlng:" + lat2 + ',' + lon2 + "&mode=driving");
                        context.startActivity(intent);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, "您尚未安装百度地图", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void clickGaodeMap() {
                Intent intent;
                if (Util.isAvilible(context, "com.autonavi.minimap")) {
                    try {
                        intent = Intent.getIntent("amapuri://route/plan/?sid=BGVIS1&sname=我的位置&did=BGVIS2&dlat=" + lat2 + "&dlon=" + lon2 + "&dname=" + name + "&dev=0&t=0");
                        context.startActivity(intent);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, "您尚未安装高德地图", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void clickTercentMap() {
                Intent intent;
                if (Util.isAvilible(context, "com.tencent.map")) {
                    try {
                        String url = "qqmap://map/routeplan?type=drive&from=我的位置&to=" + name + "&fromcoord=" + 39 + ',' + 119 + "&tocoord=" + lat2 + ',' + lon2 + "&policy=2&referer=" + getResources().getString(R.string.app_name);
                        intent = Intent.getIntent(url);
                        LogUtil.i("===========" + url);

                        context.startActivity(intent);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, "您尚未安装腾讯地图", Toast.LENGTH_LONG).show();
                }
            }
        });
        if (!Util.isAvilible(context, "com.baidu.BaiduMap")
                && !Util.isAvilible(context, "com.autonavi.minimap")
                && !Util.isAvilible(context, "com.tencent.map")) {
            ToastUtils.showLong(context, "请至少安装百度地图、高德地图、腾讯地图其中一种");
            return;
        }
        dialog.show();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = Constant.getInstance().getDeviceWidth() * 7 / 8;
        dialog.getWindow().setAttributes(lp);

        if (!Util.isAvilible(context, "com.baidu.BaiduMap")) {
            dialog.setBaiduGone();
        }
        if (!Util.isAvilible(context, "com.autonavi.minimap")) {
            dialog.setGaodeGone();
        }
        if (!Util.isAvilible(context, "com.tencent.map")) {
            dialog.setTercentGone();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!Constant.getInstance().isAppOnForeground()) {
            Constant.getInstance().setActive(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ("优惠兑换机".equals(mTitle)) {
            MobclickAgent.onPageStart("优惠兑换机");
        }
        MobclickAgent.onResume(this);
        if (!Constant.getInstance().isActive()) {
            Constant.getInstance().setActive(true);
            Constant.getInstance().getAudiBi(WebViewActivity.this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ("优惠兑换机".equals(mTitle)) {
            MobclickAgent.onPageEnd("优惠兑换机");
        }
        MobclickAgent.onPause(this);
    }

}

