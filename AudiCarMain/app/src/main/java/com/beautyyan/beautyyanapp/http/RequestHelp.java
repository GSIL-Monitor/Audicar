package com.beautyyan.beautyyanapp.http;

import android.app.Activity;

import com.google.gson.JsonObject;

/**
 * Created by xuelu on 2017/5/8.
 */

public class RequestHelp {

    private static final String APPLICATION_JSON = "application/json";

    /**
     * 获取版本更新信息
     */
    public static void getUpdateVersionInfo(Activity context, HttpResponseListener listener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("appCategory", "01");//安卓：01    ios:02
        HttpsPostWithJSON.sendJson(context, "app.query.getAppVersion", Api.BASE_URL, jsonObject, listener);
    }

    /**
     * 获取开通城市列表
     * @param listener
     */
    public static void getOpenCities(Activity context, HttpResponseListener listener) {

        HttpsPostWithJSON.sendJson(context, "app.sys.openCities", Api.BASE_URL, new JsonObject(), listener);
    }

    /**
     * 获取banner
     */
    public static void getBanner(Activity context, HttpResponseListener listener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("queryType", "2");
        HttpsPostWithJSON.sendJson(context, "app.query.banner", Api.BASE_URL, jsonObject, listener);
    }

    /**
     * 获取车源列表
     * @param listener
     * @param cityId
     * @param page
     * @param pageSize
     * @param dealerCode
     * @param keyword
     * @param orderBy
     * @param queryType
     * @param labels
     * @param modelLine
     * @param price
     */
    public static void getCarList(Activity context, HttpResponseListener listener, String cityId, int page, int pageSize,
                                 String dealerCode, String keyword, String orderBy, String queryType,
                                  String labels, String modelLine, String price ) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cityId", cityId);
        jsonObject.addProperty("page", page);
        jsonObject.addProperty("pageSize", pageSize);
        jsonObject.addProperty("dealerCode", dealerCode);
        jsonObject.addProperty("keyword", keyword);
        jsonObject.addProperty("orderBy", orderBy);
        jsonObject.addProperty("queryType", queryType);
        jsonObject.addProperty("labels", labels);
        jsonObject.addProperty("modelLine", modelLine);
        jsonObject.addProperty("price", price);
        HttpsPostWithJSON.sendJson(context, "app.query.carList", Api.BASE_URL, jsonObject, listener);
    }

    /**
     * 获取经销商列表
     */
    public static void getDealerList(Activity context, HttpResponseListener listener, String cityId, int page, int pageSize, String dealerCode, String keyword, String queryType) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("page", page);
        jsonObject.addProperty("pageSize", pageSize);
        jsonObject.addProperty("cityId", cityId);
        jsonObject.addProperty("dealerCode", dealerCode);
        jsonObject.addProperty("keyword", keyword);
        jsonObject.addProperty("queryType", queryType);
        HttpsPostWithJSON.sendJson(context, "app.query.dealerList", Api.BASE_URL, jsonObject, listener);
    }

    /**
     * 获取验证码
     * @param listener
     * @param phone
     * @param codeType
     */
    public static void sendCode(Activity context, HttpResponseListener listener, String phone, String codeType) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phone", phone);
        jsonObject.addProperty("codeType", codeType);
        HttpsPostWithJSON.sendJson(context, "app.verify.sendCode", Api.BASE_URL, jsonObject, listener);
    }

    /**
     * 校验验证码
     * @param listener
     * @param phone
     * @param code
     * @param codeType
     */
    public static void validCode(Activity context, HttpResponseListener listener, String phone, String code, String codeType) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phone", phone);
        jsonObject.addProperty("code", code);
        jsonObject.addProperty("codeType", codeType);
        HttpsPostWithJSON.sendJson(context, "app.verify.validCode", Api.BASE_URL, jsonObject, listener);
    }

    /**
     * 登录
     * @param listener
     * @param phone
     */
    public static void login(Activity context, HttpResponseListener listener, String phone) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phone", phone);
        HttpsPostWithJSON.sendJson(context, "app.verify.login", Api.BASE_URL, jsonObject, listener);
    }


    /**
     * 收藏车源
     * @param listener
     * @param userId
     * @param goodId
     * @param goodType
     * @param collectionId
     * @param oprType
     */
    public static void collect(Activity context, HttpResponseListener listener, String userId, String goodId, String goodType, String collectionId, String oprType) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("goodId", goodId);
        jsonObject.addProperty("goodType", goodType);
        jsonObject.addProperty("collectionId", collectionId);
        jsonObject.addProperty("oprType", oprType);

        HttpsPostWithJSON.sendJson(context, "app.subs.collect", Api.BASE_URL, jsonObject, listener);
    }


    /**
     * 车辆详情查询是否被收藏
     * @param listener
     * @param userId
     * @param goodId
     */
    public static void isCollect(Activity context, HttpResponseListener listener, String userId, String goodId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("goodId", goodId);

        HttpsPostWithJSON.sendJson(context, "app.query.collectInfo", Api.BASE_URL, jsonObject, listener);
    }

    public static void submitScore(Activity context, HttpResponseListener listener, String companyName, String userId, String dealerCode, int score) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("dealerCode", dealerCode);
        jsonObject.addProperty("score", score);
        jsonObject.addProperty("companyName", companyName);

        HttpsPostWithJSON.sendJson(context, "app.subs.rate", Api.BASE_URL, jsonObject, listener);
    }

    /**
     * 第三方账号绑定
     * @param context
     * @param listener
     * @param platType
     * @param platAccId
     */

    public static void bindAccount(Activity context, HttpResponseListener listener, String platType, String platAccId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("platType", platType);
        jsonObject.addProperty("platAccId", platAccId);

        HttpsPostWithJSON.sendJson(context, "app.subs.bindAccount", Api.BASE_URL, jsonObject, listener);
    }

    /**
     * 绑定手机号
     * @param context
     * @param listener
     * @param platformId
     * @param phone
     */
    public static void bindPhone(Activity context, HttpResponseListener listener, String platformType, String platformId, String phone) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("platformId", platformId);
        jsonObject.addProperty("phone", phone);
        jsonObject.addProperty("platformType", platformType);

        HttpsPostWithJSON.sendJson(context, "app.subs.bindPhone", Api.BASE_URL, jsonObject,  listener);
    }

    /**
     * 图片验证码
     * @param context
     * @param listener
     */
    public static void getCaptcha(Activity context, HttpResponseListener listener) {
        JsonObject jsonObject = new JsonObject();
        HttpsPostWithJSON.sendJson(context, "app.query.getCaptcha", Api.BASE_URL, jsonObject, listener);
    }

    public static void addLinkman(Activity context, HttpResponseListener listener, String customerId, String servivceId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("customerId", customerId);
        jsonObject.addProperty("servivceId", servivceId);
        HttpsPostWithJSON.sendJson(context, "app.subs.addLinkman", Api.BASE_URL, jsonObject, listener);
    }

    public static void getAudiBiNum(Activity context, String userId, HttpResponseListener listener) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        HttpsPostWithJSON.sendJson(context, "app.subs.getCurrenty", Api.BASE_URL, jsonObject, listener);
    }
}
