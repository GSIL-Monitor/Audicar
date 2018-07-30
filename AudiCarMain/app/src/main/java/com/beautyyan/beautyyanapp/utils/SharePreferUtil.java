package com.beautyyan.beautyyanapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xuelu on 2017/3/29.
 */

public class SharePreferUtil {
    private Context context;
    private SharedPreferences sp;
    private final String SP_NAME = "sp_name";

    //是否是首次打开app
    private final String APP_IS_FIRST = "app_is_first";

    //城市选择
    private final String CHOOSED_CITY_NAME = "choosed_city_name";
    private final String CHOOSED_CITY_ID = "choosed_city_id";

    private final String IS_ACTIVITY_FIRST_OPEN_EVERYDAY = "is_activity_first_open_everyday";

    //是否成功上报腾讯广点通
    private final String IS_POSTED_TERCENT = "is_posted_tercent";


    public SharePreferUtil(Context context) {
        this.context = context;
       sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }
     public void setFirstOpenFalse() {
         sp.edit().putBoolean(APP_IS_FIRST, false).commit();
     }

    public boolean isFirstOpen() {
        return sp.getBoolean(APP_IS_FIRST, true);
    }

    public void setChoosedCityName(String cityStr) {
        sp.edit().putString(CHOOSED_CITY_NAME, cityStr).commit();
    }
    public String getChoosedCityName() {
        return sp.getString(CHOOSED_CITY_NAME, "");
    }

    public void setChoosedCityId(String cityStr) {
        sp.edit().putString(CHOOSED_CITY_ID, cityStr).commit();
    }

    public void setUserName(String name) {
        sp.edit().putString("user_name", name).commit();
    }

    public String getUserName() {
        return sp.getString("user_name", "-1");
    }

    public void setHasActivityOpen(String tag) {
        sp.edit().putBoolean(IS_ACTIVITY_FIRST_OPEN_EVERYDAY
                + tag, true).commit();
    }

    public boolean hasActivityOpen(String tag) {
        return sp.getBoolean(IS_ACTIVITY_FIRST_OPEN_EVERYDAY
                + tag, false);
    }

    public void setUnreadMsgCount(int count) {
        sp.edit().putInt("unread_msg_count" + Constant.getInstance().getUser().getUserId(), count).commit();
    }
    public int getUnreadMsgCount() {
        return sp.getInt("unread_msg_count" + Constant.getInstance().getUser().getUserId(), 0);
    }


    public void setUserId(String id) {
        sp.edit().putString("user_id", id).commit();
    }
    public String getUserId() {
        return sp.getString("user_id", "-1");
    }

    public String getChoosedCityId() {
        return sp.getString(CHOOSED_CITY_ID, "");
    }

    public void setRecommand(String data) {
        sp.edit().putString("rec" + Constant.getInstance().getChoosedCity().getCityName(), data).commit();
    }
    public String getRecommand() {
        return sp.getString("rec"+ Constant.getInstance().getChoosedCity().getCityName(), "");
    }

    public void setNewest(String data) {
        sp.edit().putString("new"+ Constant.getInstance().getChoosedCity().getCityName(), data).commit();
    }
    public String getNewest() {
        return sp.getString("new" + Constant.getInstance().getChoosedCity().getCityName(), "");
    }

    public void setHot(String data) {
        sp.edit().putString("hot" + Constant.getInstance().getChoosedCity().getCityName(), data).commit();
    }
    public String getHot() {
        return sp.getString("hot" + Constant.getInstance().getChoosedCity().getCityName(), "");
    }

    public void setDealer(String data) {
        sp.edit().putString("dealer" + Constant.getInstance().getChoosedCity().getCityName(), data).commit();
    }
    public String getDealer() {
        return sp.getString("dealer" + Constant.getInstance().getChoosedCity().getCityName(), "");
    }

    public void setBanner(String data) {
        sp.edit().putString("banner", data).commit();

    }

    public String getBanner() {
        return sp.getString("banner", "");
    }

    public void setVersionIgnore(int data) {
        sp.edit().putInt("versionIgnore", data).commit();
    }
    public int getVersionIgnore() {
        return sp.getInt("versionIgnore", 1);
    }

}
