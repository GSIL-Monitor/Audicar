package com.beautyyan.beautyyanapp.http;


import android.app.Activity;

import com.beautyyan.beautyyanapp.YuYuanApp;
import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.utils.LogUtil;
import com.beautyyan.beautyyanapp.utils.ThreadPoolUtils;
import com.beautyyan.beautyyanapp.utils.Util;
import com.google.gson.JsonObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by xuelu
 */
public class HttpsPostWithJSON {

    private static final String APPLICATION_JSON = "application/json";

    public static void sendJson(final Activity context, final String serviceName, final String url, final JsonObject jsonObject, final HttpResponseListener listener){
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                JsonObject jsonObject1 = new JsonObject();
                jsonObject1.add("request_data", jsonObject);
                jsonObject1.addProperty("serviceName", serviceName);
                jsonObject1.addProperty("timestamp", Util.getTimeStamp());
                int num = Constant.getInstance().getRandom();
                jsonObject1.addProperty("serialNumber", Util.getFormatTime("yyyyMMddHHmmss", System.currentTimeMillis()) + "123app" + num);
                LogUtil.i("json--" + serviceName + jsonObject1.toString());
                String strResult = null;
                try {
                    strResult = sendMessage(serviceName, url,jsonObject1.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final String str = strResult;
                if (Util.isEmpty(str) || str.startsWith("<")) {
                    YuYuanApp.getIns().handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (context != null && context.isFinishing()) {
                                    return;
                                }
                                listener.onFail("failed", -1, "请求失败");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    return;
                }
                JSONObject jb = null;
                final String[] data = new String[1];
                final String[] message = new String[1];
                final Integer[] code = new Integer[1];
                try {
                    jb = new JSONObject(strResult);
                    code[0] = jb.optInt("retCode");
                    message[0] = jb.optString("retMessage");
                    if (code[0] != 0) {
                        YuYuanApp.getIns().handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (context != null && context.isFinishing()) {
                                    return;
                                }
                                try {
                                    listener.onFail("failed", code[0], message[0]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        return;
                    }

                    data[0] = jb.optString("response_data");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                YuYuanApp.getIns().handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (context != null && context.isFinishing()) {
                                return;
                            }
                            listener.onSuccess(data[0], message[0]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }



    public static String sendMessage(String serviceName, String url, String json) throws Exception {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try{
            //加载证书
//            java.security.KeyStore trustStore = java.security.KeyStore.getInstance("类型");
//
//            InputStream instream = YuYuanApp.getIns().getAssets().open("证书名");
//            trustStore.load(instream, "密码".toCharArray());
//            SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
//            //不校验域名
//            socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//            //这个8446是和被访问端约定的端口，一般为443
//            Scheme sch = new Scheme("https", socketFactory, 28100);
//            httpClient.getConnectionManager().getSchemeRegistry().register(sch);



            httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
            StringEntity entity = new StringEntity(json,"utf-8");
            entity.setContentType(APPLICATION_JSON);
            entity.setContentEncoding("UTF-8");
            httpPost.setEntity(entity);
            HttpResponse ss = httpClient.execute(httpPost);
            String strResult = EntityUtils.toString(ss.getEntity(),"UTF-8");
            LogUtil.i(serviceName + "---response json data is "+ strResult);
            return strResult;
        }catch (Exception  e) {
            LogUtil.e(e.toString());
            throw e;
        }finally {
            // 释放连接
             httpClient.getConnectionManager().shutdown();
        }
    }

    public static String postData(String url, String filepath) throws Exception {
        int statusCode = -1;
        String result = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {
            FileBody bin = new FileBody(new File(filepath));

            MultipartEntity reqEntity = new MultipartEntity();

            reqEntity.addPart("fileupload", bin);//file1为请求后台的File upload;属性
//            reqEntity.addPart("filename", comment);//filename1为请求后台的普通参数;属性
            httpPost.setEntity(reqEntity);
            HttpResponse response = httpClient.execute(httpPost);
            statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {

                System.out.println("服务器正常响应.....");

                HttpEntity resEntity = response.getEntity();

                result = EntityUtils.toString(resEntity);
                return result;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                httpClient.getConnectionManager().shutdown();
                return result;
            } catch (Exception ignore) {
                return result;
            }
        }
    }
}
