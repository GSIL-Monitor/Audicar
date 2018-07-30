
package com.beautyyan.beautyyanapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.beautyyan.beautyyanapp.utils.LogUtil;
import com.beautyyan.beautyyanapp.utils.SharePreferUtil;
import com.beautyyan.beautyyanapp.utils.SystemBarTintManager;
import com.umeng.socialize.UMShareAPI;

import xmpp.constant.MyApplication;

/**
 * Created by xuelu on 2017/4/6.
 */

public class YuYuanApp extends Application {

    private SharePreferUtil sp;
    private static YuYuanApp instance;
    public Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
//        MobclickAgent. startWithConfigure(new MobclickAgent.UMAnalyticsConfig())
        LogUtil.i("app start!");
        UMShareAPI.get(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        sp = new SharePreferUtil(this);
        instance = this;
        initXmppContext();
    }

    private void initXmppContext() {
        MyApplication.setInstance(this);
    }

    public static YuYuanApp getIns() {
        return instance;
    }

    public SharePreferUtil getSp() {
        return sp;
    }

    public void setStatusBar(Activity context, View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(context, true);
            SystemBarTintManager tintManager = new SystemBarTintManager(context);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarAlpha(0.7f);
            SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = context.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
            if (view != null) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view.getLayoutParams();
                params.height = config.getStatusBarHeight();
            }
        }
    }

    public void setStatusBarForDetail(Activity context, View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setTranslucentStatus(context, true);
            SystemBarTintManager tintManager = new SystemBarTintManager(context);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.title_bg));
            SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
            if (view != null) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view.getLayoutParams();
                params.height = config.getStatusBarHeight();
            }
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(Activity context, boolean on) {

        Window win = context.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
