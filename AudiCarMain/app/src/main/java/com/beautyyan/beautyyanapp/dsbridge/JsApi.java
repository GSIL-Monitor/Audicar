package com.beautyyan.beautyyanapp.dsbridge;

import android.webkit.JavascriptInterface;

import com.beautyyan.beautyyanapp.YuYuanApp;
import com.beautyyan.beautyyanapp.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xuelu on 2017/4/26.
 */

public class JsApi {
    //for synchronous invocation
    @JavascriptInterface
    String testSyn(JSONObject jsonObject) throws JSONException {
        return jsonObject.getString("msg") + "［syn call］";
    }
    //for asynchronous invocation
    @JavascriptInterface
    void testAsyn(JSONObject jsonObject, CompletionHandler handler) throws JSONException {
        handler.complete(jsonObject.getString("msg")+" [asyn call]");
    }

    @JavascriptInterface
    String showAlert(JSONObject jsonObject) throws JSONException {
        ToastUtils.showCenterShort(YuYuanApp.getIns().getApplicationContext(), jsonObject.getString("msg"));
        return jsonObject.getString("msg");
    }

}
