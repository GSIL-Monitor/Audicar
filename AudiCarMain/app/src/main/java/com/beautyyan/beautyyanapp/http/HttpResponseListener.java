package com.beautyyan.beautyyanapp.http;

import org.json.JSONException;

/**
 * Created by xuelu on 2017/5/8.
 */

public interface HttpResponseListener {
    void onSuccess(String data, String message) throws JSONException;
    void onFail(String data, int errorCode, String message) throws JSONException;
}
