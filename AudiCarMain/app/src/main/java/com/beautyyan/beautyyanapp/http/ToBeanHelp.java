package com.beautyyan.beautyyanapp.http;

import com.beautyyan.beautyyanapp.http.bean.Banner;
import com.beautyyan.beautyyanapp.http.bean.CaptchDetil;
import com.beautyyan.beautyyanapp.http.bean.Car;
import com.beautyyan.beautyyanapp.http.bean.City;
import com.beautyyan.beautyyanapp.http.bean.Dealer;
import com.beautyyan.beautyyanapp.http.bean.User;
import com.beautyyan.beautyyanapp.utils.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by xuelu on 2017/5/8.
 */

public class ToBeanHelp {

    private static String getData(String strData, String name) {
        if (Util.isEmpty(strData)) {
            return "";
        }
        JSONObject jb = null;
        try {
            jb = new JSONObject(strData);
            strData = jb.optString(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return strData;
    }

    /**
     * 获取开通城市列表
     *
     * @param jsonString
     * @return
     */
    public static List<City> citiesBean(String jsonString) {
        jsonString = getData(jsonString, "cities");
        Gson gson = new Gson();
        return (List<City>) gson.fromJson(jsonString, new TypeToken<List<City>>() {
        }.getType());
    }

    /**
     * 获取车源列表
     *
     * @param jsonString
     * @return
     */
    public static List<Car> carsBean(String jsonString) {
        jsonString = getData(jsonString, "cars");
        Gson gson = new Gson();
        return (List<Car>) gson.fromJson(jsonString, new TypeToken<List<Car>>() {
        }.getType());
    }

    /**
     * 获取banner
     */
    public static List<Banner> bannersBean(String jsonString) {
        jsonString = getData(jsonString, "banners");
        Gson gson = new Gson();
        return (List<Banner>) gson.fromJson(jsonString, new TypeToken<List<Banner>>() {
        }.getType());
    }

    /**
     * 获取优质经销商列表
     */
    public static List<Dealer> dealerListBean(String jsonString) {
        jsonString = getData(jsonString, "dealers");
        Gson gson = new Gson();
        return (List<Dealer>) gson.fromJson(jsonString, new TypeToken<List<Dealer>>() {
        }.getType());
    }

    /**
     * 用户解析
     *
     * @param jsonString
     * @return
     */
    public static User userBean(String jsonString) {
        User user = new User();
        String id = "-1";
        try {
            String userId = getData(jsonString, "userId");
            String phone = getData(jsonString, "phone");
            if ("".equals(userId)) {
                userId = "-1";
            }
            id = userId;
            user.setPhone(phone);
            user.setUserId(id);
        } catch (Exception e) {

        }
        return user;
    }

    public static boolean isCollected(String jsonString) throws JSONException {
        JSONObject jb = new JSONObject(jsonString);
        return jb.optBoolean("collect");

    }

    public static CaptchDetil captchBean(String jsonString) {
        jsonString = getData(jsonString, "captchDetil");
        Gson gson = new Gson();
        return (CaptchDetil) gson.fromJson(jsonString, new TypeToken<CaptchDetil>() {
        }.getType());
    }

    public static String getDataUrl(String jsonString) {
        jsonString = getData(jsonString, "fileUrl");
        return jsonString;
    }

    public static String getAudiBi(String jsonString) {
        jsonString = getData(jsonString, "getNumber");
        return jsonString;
    }

    public static String getAccessToken(String jsonString) {
        jsonString = getData(jsonString, "token");
        return jsonString;
    }
}
