package com.beautyyan.beautyyanapp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
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
import android.widget.RelativeLayout;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.YuYuanApp;
import com.beautyyan.beautyyanapp.activity.ChooseCityActivity;
import com.beautyyan.beautyyanapp.activity.LoginActivity;
import com.beautyyan.beautyyanapp.activity.MainActivity;
import com.beautyyan.beautyyanapp.activity.WebViewActivity;
import com.beautyyan.beautyyanapp.dsbridge.CompletionHandler;
import com.beautyyan.beautyyanapp.dsbridge.DWebView;
import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.utils.DataClearUtil;
import com.beautyyan.beautyyanapp.utils.LogUtil;
import com.beautyyan.beautyyanapp.utils.ToastUtils;
import com.beautyyan.beautyyanapp.utils.Util;
import com.beautyyan.beautyyanapp.view.LoadingView;
import com.google.gson.JsonObject;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import xmpp.activites.MsgActivity;

import static android.app.Activity.RESULT_OK;
import static com.beautyyan.beautyyanapp.activity.WebViewActivity.FILECHOOSER_RESULTCODE;
import static com.beautyyan.beautyyanapp.activity.WebViewActivity.FILECHOOSER_RESULTCODE_FOR_ANDROID_5;
import static com.beautyyan.beautyyanapp.utils.Util.getValueByName;

/**
 * Created by xuelu on 2017/3/24.
 */

@SuppressLint("ValidFragment")
public class WebViewFragment extends Fragment {

    @Bind(R.id.webview_web)
    DWebView webviewWeb;
    @Bind(R.id.webview_flay)
    RelativeLayout fLayWebView;
    @Bind(R.id.loadview)
    LoadingView loadview;
    @Bind(R.id.rl_title)
    RelativeLayout rlTitle;
    private View rootView;
    private String url;
    public boolean isLoadFinish = false;
    public boolean isLoading = false;
    public boolean isCityChanged = false;
    public boolean hasLoaded = false;

    private CompletionHandler h5Handler;

    private boolean isError = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.acti_webview_lay, null);
            ButterKnife.bind(this, rootView);
            rlTitle.setVisibility(View.GONE);
            if (savedInstanceState != null) {
                webviewWeb.restoreState(savedInstanceState);
            }
            webviewWeb.setJavascriptInterface(new JsApi());
            setWebView();
            loadview.setFailClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadUrl(url);
                }
            });
        }
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    public WebViewFragment(String url) {
        this.url = url;
    }

    public WebViewFragment() {
    }

    public void loadUrl(String url) {
        isLoading = true;
        loadview.changeLoadingStatus(0);
        webviewWeb.loadUrl(url);
        isError = false;
    }

    /**
     * 0消失，1展示
     *
     * @param isShow
     */
    public void showRedPoint(int isShow) {

        LogUtil.i("js --- showRedPoint");
        webviewWeb.callHandler("showRedPoint", new Object[]{isShow}, new CompletionHandler() {
            @Override
            public void complete(String retValue) {

            }
        });
    }


    /**
     * 根据车型价格区间筛选
     *
     * @param cityId
     * @param carType
     * @param priceMin
     * @param priceMax 不限：priceMin=0&priceMax=52 10万以下：0 10 10-15万：10 15 15-20万：15 20 20-30万：20 30 30-50万：30 50 50万以上：50 52
     */
    public void searchByCartype(String cityId, String cityName, String carType, int priceMin, int priceMax) {

        LogUtil.i("js --- searchByCartype");
        webviewWeb.callHandler("searchByCartype", new Object[]{cityId, cityName, carType, priceMin, priceMax}, new CompletionHandler() {
            @Override
            public void complete(String retValue) {

            }
        });
    }

    /**
     * 高级筛选
     *
     * @param json
     */
    public void searchByAdvancedCondition(String json) {

        LogUtil.i("js --- searchByAdvancedCondition");
        webviewWeb.callHandler("searchByAdvancedCondition", new Object[]{json}, new CompletionHandler() {
            @Override
            public void complete(String retValue) {

            }
        });
    }


    /**
     * 根据超值好车、急售车源、两年0利率
     *
     * @param cityId
     * @param saleType 超值好车：1急售车源：2两年0利率：3
     */
    public void searchBySaletype(String cityId, String cityName, int saleType) {
        LogUtil.i("js --- searchBySaletype");
        webviewWeb.callHandler("searchBySaletype", new Object[]{cityId, cityName, saleType}, new CompletionHandler() {
            @Override
            public void complete(String retValue) {

            }
        });

    }

    /**
     * 根据车系
     *
     * @param cityId
     * @param carKind
     */
    public void searchByCarkind(String cityId, String cityName, String carKind) {
        LogUtil.i("js --- searchByCarkind");
        webviewWeb.callHandler("searchByCarkind", new Object[]{cityId, cityName, carKind}, new CompletionHandler() {
            @Override
            public void complete(String retValue) {

            }
        });
    }

    /**
     * 搜索全部
     */
    public void searchAll(String cityId, String cityName) {
        LogUtil.i("js --- searchAll");
        webviewWeb.callHandler("searchAll", new Object[]{cityId, cityName}, new CompletionHandler() {
            @Override
            public void complete(String retValue) {

            }
        });
    }

    /**
     * 清除h5界面弹窗
     */
    public void clearPage() {
        LogUtil.i("js --- clearPage");
        webviewWeb.callHandler("clearPage", new Object[]{}, new CompletionHandler() {
            @Override
            public void complete(String retValue) {

            }
        });
    }

    /**
     * h5调用原生
     */
    private class JsApi {
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
        }

        @JavascriptInterface
        void startNativeActivity(JSONObject jsonObject) throws JSONException {
            LogUtil.i("startNativeActivity start!");

            String url = jsonObject.getString("url");
            String title = jsonObject.getString("title");
            LogUtil.i(url);
            Intent intent = new Intent(getActivity(), WebViewActivity.class);
            intent.putExtra(Constant.WEB_URL, url);
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
            intent.putExtra(Constant.SHARE_TITLE, shareTitle);
            intent.putExtra(Constant.SHARE_URL, shareUrl);
            intent.putExtra(Constant.SHARE_IMG, shareImgUrl);
            intent.putExtra(Constant.SHARE_INFO, shareInfo);
            if (!Util.isEmpty(Util.getValueByName(url, "activityId"))) {
                intent.putExtra(Constant.CAR_DETAIL_ID, Util.getValueByName(url, "activityId"));
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
            if ("2".equals(title.substring(0, 1))) {
                startActivityForResult(intent, Constant.RequestCode.CODE8);
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
        void startChooseCityActivity(JSONObject jsonObject, CompletionHandler handler) throws JSONException {

            h5Handler = handler;
            LogUtil.i("=======" + jsonObject.toString());
            Intent intent = new Intent(getActivity(), ChooseCityActivity.class);
            intent.putExtra("path", jsonObject.optString("path"));
            intent.putExtra("from_h5", true);
            startActivityForResult(intent, Constant.RequestCode.CODE1);
        }

        @JavascriptInterface
        String getUserId(JSONObject jsonObject) throws JSONException {
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
            startActivityForResult(new Intent(getActivity(), LoginActivity.class), Constant.RequestCode.CODE3);
            h5Handler = handler;
        }

        @JavascriptInterface
        void startMsgActivity(JSONObject jsonObject) throws JSONException {

            Intent intent = new Intent(getActivity(), MsgActivity.class);
            startActivity(intent);
        }

        @JavascriptInterface
        String getCache(JSONObject jsonObject) throws JSONException {
            String size = "";
            try {
                size = DataClearUtil.getTotalCacheSize(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return size;
        }

        @JavascriptInterface
        String clearCache(JSONObject jsonObject) throws JSONException {
            return DataClearUtil.clearAllCache(getActivity()) ? "0" : "1";
        }

        /**
         * 双十一活动数据统计--点击“我的”里面的我的奥迪币
         *
         * @param jsonObject
         * @throws JSONException
         */
        @JavascriptInterface
        void clickMyAudiB(JSONObject jsonObject) throws JSONException {
            MobclickAgent.onEvent(getActivity(), Constant.UMENG_CLICK_MYAUDIB);
        }

    }


    public void clearData() {
        ButterKnife.unbind(this);
    }

    public void sendNative() {
        webviewWeb.callHandler("search", new Object[]{1, 2}, new CompletionHandler() {
            @Override
            public void complete(String retValue) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView() {
               /*
         * webSettings 保存着WebView中的状态信息。
		 * 当WebView第一次被创建时，webSetting中存储的都为默认值。
		 * WebSetting和WebView一一绑定的。
		 * 如果webView被销毁了，那么我们再次调用webSetting中的方法时，会抛出异常。
		 */
        WebSettings webSettings = webviewWeb.getSettings();

        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        webviewWeb.setWebViewClient(new HarlanWebViewClient());

        // 拦截h5弹窗
        HarlanWebChromeClient chromeClient = new HarlanWebChromeClient();
        webviewWeb.setWebChromeClient(chromeClient);

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        webviewWeb.setHorizontalScrollBarEnabled(false);
        webviewWeb.setVerticalScrollBarEnabled(false);

        webSettings.setSupportZoom(false);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true); //适应屏幕
        webSettings.setBuiltInZoomControls(true);

//        webSettings.setDomStorageEnabled(true);    //开启DOM形式存储
//
//        webSettings.setDatabaseEnabled(true);   //开启数据库形式存储
//
//        String appCacheDir = getActivity().getApplication().getDir("cache", getActivity().MODE_PRIVATE).getPath();   //缓存数据的存储地址
//
//        webSettings.setAppCachePath(appCacheDir);

//        webSettings.setAppCacheEnabled(true);  //开启缓存功能
//
//        //缓存模式
//        if (Util.isNetConnected(getActivity())) {
//            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//        }
//        else {
//            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        }

        webSettings.setAllowFileAccess(true);


//            webviewWeb.loadUrl(url);

		/*
         * 为js提供一个方法，注意该方法一般不写在UI线程中
		 * addJavascriptInterface(Object obj, String interfaceName)
		 * obj代表一个java对象，这里我们一般会实现一个自己的类，类里面提供我们要提供给javascript访问的方法
		 * interfaceName则是访问我们在obj中声明的方法时候所用到的js对象，调用方法为window.interfaceName.方法名(
		 * )
		 */
//        webviewWeb.addJavascriptInterface(new SubMaintenanceJavaScriptInterface(), "qyreader");
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
            //使用当前的WebView加载页面
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            loadview.changeLoadingStatus(0);
        }

        /*
         * 网页加载完毕(仅指主页，不包括图片)
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            isLoadFinish = true;
            isLoading = false;
            LogUtil.i("---------------------loadwebfinish" + url);
            if (isError) {
                return;
            }
            if (getActivity().isFinishing()) {
                return;
            }
            if (!hasLoaded) {
                hasLoaded = true;
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
            if (getActivity().isFinishing()) {
                return;
            }
            if (webviewWeb == null) {
                return;
            }
            isError = true;
            webviewWeb.setVisibility(View.GONE);
            loadview.changeLoadingStatus(-1);
            LogUtil.i("---------------------loaderror" + description);
//            webviewWeb.setVisibility(View.GONE);
//            loadview.changeLoadingStatus(-1);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
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

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
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

    public ValueCallback<Uri> mUploadMessage;

    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
        if (data == null || resultCode != 0) {
            return;
        }
        if (requestCode == Constant.RequestCode.CODE1) {
            String cityId = data.getExtras().getString("cityId");
            String cityName = data.getExtras().getString("cityName");
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("cityId", cityId);
            jsonObject.addProperty("cityName", cityName);
            h5Handler.complete(jsonObject.toString());
        } else if (requestCode == Constant.RequestCode.CODE2) {
            String keyword = data.getExtras().getString("keyword");
            MainActivity.getInstance().searchByCartype(keyword, 0, 52);
        } else if (requestCode == Constant.RequestCode.CODE3) {
            if (h5Handler == null) {
                return;
            }
//            JsonObject   jsonObject = new JsonObject();
//            jsonObject.addProperty("userId", Constant.getInstance().getUser().getUserId());
//            jsonObject.addProperty("userName",Constant.getInstance().getUser().getPhone());

            String userId = data.getExtras().getString("userId");
            String userName = data.getExtras().getString("userName");
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userId", userId);
            jsonObject.addProperty("userName", userName);
            h5Handler.complete(jsonObject.toString());
        } else if (requestCode == Constant.RequestCode.CODE8) {
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
            searchByAdvancedCondition(json);


        }
    }

    public void setLayerType(int type) {
        if (webviewWeb == null) {
            return;
        }
        webviewWeb.setLayerType(type, null);
    }

    public int getLayerType() {
        if (webviewWeb == null) {
            return -1;
        }
        return webviewWeb.getLayerType();
    }
}
