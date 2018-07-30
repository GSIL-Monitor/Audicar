package com.beautyyan.beautyyanapp.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

import com.beautyyan.beautyyanapp.YuYuanApp;
import com.beautyyan.beautyyanapp.activity.WebViewActivity;
import com.beautyyan.beautyyanapp.http.Api;
import com.beautyyan.beautyyanapp.http.HttpResponseListener;
import com.beautyyan.beautyyanapp.http.RequestHelp;
import com.beautyyan.beautyyanapp.http.ToBeanHelp;
import com.beautyyan.beautyyanapp.http.bean.City;
import com.beautyyan.beautyyanapp.http.bean.User;
import com.beautyyan.beautyyanapp.view.ActivityDialog;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xmpp.constant.Constants;
import xmpp.xmpp.XmppConnection;

import static com.beautyyan.beautyyanapp.utils.Constant.URL.ADVANCED_SEARCH;
import static com.beautyyan.beautyyanapp.utils.Constant.URL.AUDIBI_DUIHUAN;
import static com.beautyyan.beautyyanapp.utils.Constant.URL.CALL;
import static com.beautyyan.beautyyanapp.utils.Constant.URL.GUESS_PRICE;
import static com.beautyyan.beautyyanapp.utils.Constant.URL.MINE;
import static com.beautyyan.beautyyanapp.utils.Constant.URL.SALE_CAR;
import static com.beautyyan.beautyyanapp.utils.Constant.URL.SEARCH;
import static com.beautyyan.beautyyanapp.utils.Constant.URL.STORE_DETAIL;
import static com.beautyyan.beautyyanapp.utils.Constant.URL.STORE_LIST;
import static com.beautyyan.beautyyanapp.utils.Constant.URL.WANT_BUY_CAR;
import static com.beautyyan.beautyyanapp.utils.Util.getValueByName;

/**
 * Created by xuelu on 2017/3/22.
 */

public class Constant {
    
    //自动更新模块的相关tag
    public static final String APP_SIZE = "app_size";
    public static final String APP_RELEASE_TIME = "app_release_time";
    public static final String APP_CATEGORY = "app_category";
    public static final String APP_INTRODUCTION = "app_introduction";
    public static final String APP_FORCE_UPDATE = "app_force_update";
    public static final String APP_VERSION = "app_version";
    public static final String APP_DOWNLOAD_URL = "app_download_url";

    //umeng统计点击预付意向金次数
    public static final String UMENG_CLICK_YUFU = "click_yufu";
    public static final String UMENG_CLICK_CHAT = "click_chat";

    //umeng统计双十一活 动优惠兑换机活动
    public static final String UMENG_CLICK_DUIHUAN = "click_duihuan";
    public static final String UMENG_CLICK_DUIHUAN_RECORD = "click_duihuan_record";
    public static final String UMENG_CLICK_LOGIN = "click_login";
    public static final String UMENG_CLICK_BACKGROUND = "click_background";
    public static final String UMENG_CLICK_MYAUDIB = "click_myaudib";
    public static final String UMENG_CLICK_ACTIVITY_INTRO = "click_activity_intro";
    public static final String UMENG_DUIHUAN_PV = "duihuan_pv";
    public static final String UMENG_DUIHUAN_STAY_TIME = "duihuan_stay_time";
    public static final String UMENG_DUIHUAN_TIAOCHU = "duihuan_tiaochu";
    public static final String UMENG_CLICK_BANNER_TO_DUIHUAN = "click_banner_to_duihuan";
    public static final String UMENG_CLICK_ADS_TO_DUIHUAN = "click_ads_to_duihuan";

    public static final String IS_OPEN_FROM_SHARE = "is_open_from_share";
    public static final String INTENT = "intent";

    //是否处于后台
    private boolean isActive = true;

    //定位城市
    private City locatedCity = new City();

    private User user;

    //选择城市
    private City choosedCity = new City();

    public static final String WEB_TITLE = "web_title";
    public static final String WEB_URL = "web_url";
    //奥迪购车小助手账号
    public static final String AUDI_CHAT_SERVICE = "customer";

    //是否是车源详情web页
    public static final String IS_DETAIL_ACTIVITY = "is_detail_activity";
    public static final String SHARE_TITLE = "share_title";
    public static final String SHARE_URL = "share_url";
    public static final String SHARE_IMG = "share_img";
    public static final String SHARE_INFO = "share_info";

    public static final String IS_STORELIST_ACTIVITY = "is_storelist_activity";
    public static final String CAR_DETAIL_ID = "car_detail_id";
    public static final String IS_ACTIVITIES = "is_activities";
    //是否是需要分享的页面
    public static final String IS_SHARE = "is_share";
    //是否是从一键留言进入
    public static final String IS_FROMONEKEY = "is_fromonekey";

    //微信开放平台id和key
    public static final String WEIXIN_ID = "wxfb77cc0b1ad8ebe1";
    public static final String WEIXIN_KEY = "59dd9035feb246412e29a8ef21433f82";

    //QQ开放平台id和key
    public static final String QQ_ID = "1106167726";
    public static final String QQ_KEY = "MaELv7ltd3k4u52k";

    //新浪开放平台id和key
    public static final String SINA_ID = "1390747438";
    public static final String SINA_KEY = "1c4d627dd43da972b61337a082cd92a3";



    private String wxPayId = "";

    private boolean xmppHasLogined = false;

    private static Constant constant;
    private int itemWidth;
    private List<WebViewActivity> listWeb = new ArrayList<>();

    public int getItemWidth() {
        return itemWidth;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

   //设备高度
    private int deviceHeight;
    //设备宽度
    private int deviceWidth;

    public static Constant getInstance() {
        if (constant == null) {
            constant = new Constant();
        }
        return constant;
    }

    public void addToWebList(WebViewActivity web) {

        listWeb.add(web);

    }

    public boolean isXmppHasLogined() {
        return xmppHasLogined;
    }

    public void setXmppHasLogined(boolean xmppHasLogined) {
        this.xmppHasLogined = xmppHasLogined;
    }


    public String getWxPayId() {
        return wxPayId;
    }

    public void setWxPayId(String wxPayId) {
        this.wxPayId = wxPayId;
    }

    public List<WebViewActivity> getListWeb() {
        return listWeb;
    }

    public Constant() {
        String name = YuYuanApp.getIns().getSp().getChoosedCityName();
        String id = YuYuanApp.getIns().getSp().getChoosedCityId();
        String userName = YuYuanApp.getIns().getSp().getUserName();
        String userId = YuYuanApp.getIns().getSp().getUserId();
        choosedCity = new City(name, id);
        user = new User();
        user.setUserId(userId);
        user.setPhone(userName);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        YuYuanApp.getIns().getSp().setUserId(user.getUserId());
        YuYuanApp.getIns().getSp().setUserName(user.getPhone());
        this.user = user;
        Constants.USER_NAME = user.getUserId();
    }
    public void toNullUser() {
        user.setPhone("-1");
        user.setUserId("-1");
        setUser(user);
        XmppConnection.getInstance().closeConnection();
        Constant.getInstance().setXmppHasLogined(false);
        Constants.USER_NAME = "";
    }


    /**
     * 消息
     */
    public interface MESSAGE {
        //首页红点显示
        int SHOW_POINT = 0xff001;

        //城市定位更改
        int CHANGE_LOCATED_CITY = 0xff002;

        int GO_TO_BUY = 0xff004;

        int GO_TO_ORDER_LIST = 0xff005;

        int CLEAR_LOGININFO = 0xff006;

        int REFRESH_MINE = 0xff007;


    }

    /**
     * uri
     */
    public interface URL {
        String WANT_BUY_CAR = Api.H5_URL + "buycar";//买车
        String SALE_CAR = Api.H5_URL + "sellcar";//卖车
        String CALL = Api.H5_URL + "interact";//互动
        String MINE = Api.H5_URL + "our";//我的

        String GUESS_PRICE = Api.H5_URL + "assessmentonline";//估价
        String ONE_KEY_MESSAGE = Api.H5_URL + "keymessage";//一键留言
        String CAR_DETAIL = Api.H5_URL + "cardetail";//车辆详情
        String SEARCH = Api.H5_URL + "searchscreen";//搜索页
        String STORE_DETAIL = Api.H5_URL + "dealerhome";//经销商详情页
        String STORE_LIST = Api.H5_URL + "dealer";//经销商列表
        String BUY_CAR_TIPS = Api.H5_URL + "carEncyclopedia";
        String SUBMIT_CAR_WORK = Api.H5_URL + "cyclopediawork";
        String ADVANCED_SEARCH = Api.H5_URL + "advancedfilter";
        String AUDIBI_DUIHUAN = Api.H5_URL + "audiBactivity";//优惠兑换机
    }

    public interface RequestCode {
        int CODE1 = 11;
        int CODE2 = 12;
        int CODE3 = 13;
        int CODE4 = 14;
        int CODE5 = 15;
        int CODE6 = 16;
        int CODE7 = 17;
        int CODE8 = 18;
        int CODE9 = 19;
        int CODE10 = 20;
        int CODE11 = 21;

    }

    public interface BroadCastCode {

        String code1 = "code1";
    }

    public String getBuyCarUrl() {
        return WANT_BUY_CAR + "?cityId=" +  Constant.getInstance().getChoosedCity().getCityId() + "&cityName="
                + Constant.getInstance().getChoosedCity().getCityName();
    }

    public String getSaleCarUrl() {
        return SALE_CAR + "?cityId=" +  Constant.getInstance().getChoosedCity().getCityId() + "&cityName="
                + Constant.getInstance().getChoosedCity().getCityName();
    }

    public String getCallUrl() {
        return CALL + "?cityId=" +  Constant.getInstance().getChoosedCity().getCityId()  + "&cityName="
                + Constant.getInstance().getChoosedCity().getCityName();
    }

    public String getMineUrl() {
        return MINE  + "?cityId=" +  Constant.getInstance().getChoosedCity().getCityId() +
                "&userId=" +  Constant.getInstance().getUser().getUserId() +
                "&userName=" + Constant.getInstance().getUser().getPhone() +
                (YuYuanApp.getIns().getSp().getUnreadMsgCount() == 0
                        || "-1".equals(Constant.getInstance().getUser().getUserId())?
                "&isShowRedPoint=0" : "&isShowRedPoint=1");
    }

    public String getSearch() {
        return SEARCH  + "?cityId=" +  Constant.getInstance().getChoosedCity().getCityId();
    }

    public String getAudiBiDuiHuanUrl() {
        return AUDIBI_DUIHUAN  + "?userId=" +  Constant.getInstance().getUser().getUserId() +
                "&userName=" + Constant.getInstance().getUser().getPhone() +
                "&cityId=" + Constant.getInstance().getLocatedCity().getCityId();
    }

    public String getDealerList() {
        return STORE_LIST + "?cityId=" +  Constant.getInstance().getChoosedCity().getCityId() + "&cityName="
                + Constant.getInstance().getChoosedCity().getCityName();
    }

    public String getStoreDetail() {
        return STORE_DETAIL + "?cityId=" +  Constant.getInstance().getChoosedCity().getCityId() + "&cityName="
                + Constant.getInstance().getChoosedCity().getCityName();
    }

    public String getGuessPriceUrl() {
        return GUESS_PRICE + "?cityId=" +  Constant.getInstance().getChoosedCity().getCityId() + "&cityName="
                + Constant.getInstance().getChoosedCity().getCityName()
                + "&userName=" + Constant.getInstance().getUser().getPhone()
                + "&userId=" + Constant.getInstance().getUser().getUserId();
    }

    public String getAdvancedSearch() {
        return ADVANCED_SEARCH  + "?cityId=" +  Constant.getInstance().getChoosedCity().getCityId() + "&cityName="
                + Constant.getInstance().getChoosedCity().getCityName();
    }



    /**
     * 城市定位
     * @return
     */
    public City getLocatedCity() {
        return locatedCity;
    }

    public void setLocatedCity(City locatedCity) {
        this.locatedCity = locatedCity;
    }
//    public String getLocatedCity() {
//        return locatedCity;
//    }
//
//    public void setLocatedCity(String locatedCity) {
//        this.locatedCity = locatedCity;
//    }


    /**
     * 选择城市
     * @return
     */
    public City getChoosedCity() {
        choosedCity.setCityId(YuYuanApp.getIns().getSp().getChoosedCityId());
        choosedCity.setCityName(YuYuanApp.getIns().getSp().getChoosedCityName());
        LogUtil.i("setId  " +  choosedCity.getCityId());
        return choosedCity;
    }

    public void setChoosedCity(City choosedCity) {
        if (choosedCity == null) {
            return;
        }
        YuYuanApp.getIns().getSp().setChoosedCityName(choosedCity.getCityName());
        YuYuanApp.getIns().getSp().setChoosedCityId(choosedCity.getCityId());
        LogUtil.i("setId  " +  choosedCity.getCityId());
    }

    /**
     * 设备宽高
     * @return
     */
    public int getDeviceHeight() {
        return deviceHeight;
    }

    public void setDeviceHeight(int height) {
        this.deviceHeight = height;
    }

    public int getDeviceWidth() {
        return deviceWidth;
    }

    public void setDeviceWidth(int width) {
        this.deviceWidth = width;
    }

    public int getRandom() {
        return new Random().nextInt(89999999) + 10000000;
    }

    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,6,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     *
     * @param activity
     * @param enable
     */
    public void showStatusBar(Activity activity, boolean enable) {
        if (enable) { //显示状态栏
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(lp);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else { //隐藏状态栏
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(lp);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public void getAudiBi(final Activity activity) {
        Constant.getInstance().isActivityShow(activity);
        if ("-1".equals(Constant.getInstance().getUser().getUserId())) {
            return;
        }
        RequestHelp.getAudiBiNum(activity, Constant.getInstance().getUser().getUserId(),
                new HttpResponseListener() {
                    @Override
                    public void onSuccess(String data, String message) throws JSONException {
                        String num = ToBeanHelp.getAudiBi(data);
                        if ("100".equals(num)) {
                            ToastUtils.showAddAudiBi();
                        }
                    }

                    @Override
                    public void onFail(String data, int errorCode, String message) throws JSONException {
                    }
                });
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {

        ActivityManager activityManager = (ActivityManager) YuYuanApp.getIns()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = YuYuanApp.getIns().getPackageName();
        /**
         * 获取Android设备中所有正在运行的App
         */
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    /**
     * 活动弹窗是否需要展示
     * @return
     */
    public boolean isActivityShow(Context context) {
        long start = DateUtil.getTimeInMillis("2017-10-20", DateUtil.FMT_YMD);
        long end = DateUtil.getTimeInMillis("2017-11-18", DateUtil.FMT_YMD);
        if (System.currentTimeMillis() >= start && System.currentTimeMillis() < end
                && !YuYuanApp.getIns().getSp().hasActivityOpen(
                DateUtil.formatMillis(System.currentTimeMillis(), DateUtil.FMT_YMD))) {
            ActivityDialog dialog = new ActivityDialog(context);
            dialog.show();
            return true;
        }
        return false;
    }

    public static void fromShareSetIntent(Context context, Intent intent) {
        Intent intent2 = new Intent(context, WebViewActivity.class);
        intent2.putExtra(Constant.IS_SHARE, true);
        intent2.putExtra(Constant.WEB_URL, intent.getStringExtra(Constant.WEB_URL));
        intent2.putExtra(Constant.WEB_TITLE, intent.getStringExtra(Constant.WEB_TITLE));
        intent2.putExtra(Constant.SHARE_TITLE, intent.getStringExtra(Constant.SHARE_TITLE));
        intent2.putExtra(Constant.SHARE_URL, intent.getStringExtra(Constant.SHARE_URL));
        intent2.putExtra(Constant.SHARE_IMG, intent.getStringExtra(Constant.SHARE_IMG));
        intent2.putExtra(Constant.SHARE_INFO, intent.getStringExtra(Constant.SHARE_INFO));
        String uri = intent.getStringExtra(Constant.WEB_URL);
        String title = intent.getStringExtra(Constant.WEB_TITLE);
        if (!Util.isEmpty(Util.getValueByName(uri, "releaseNumber"))) {
            intent2.putExtra(Constant.CAR_DETAIL_ID, Util.getValueByName(uri, "releaseNumber"));
        }
        if (!Util.isEmpty(getValueByName(uri, "activityId"))) {
            intent2.putExtra(Constant.CAR_DETAIL_ID, Util.getValueByName(uri, "activityId"));
            intent2.putExtra(Constant.IS_ACTIVITIES, true);
        }
        if ("-1".equals(title)) {
            intent2.putExtra(Constant.IS_DETAIL_ACTIVITY, true);
        }
        context.startActivity(intent2);
    }


}
