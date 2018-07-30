package com.beautyyan.beautyyanapp;
//import com.umeng.socialize.media.WBShareCallBackActivity;

import com.beautyyan.beautyyanapp.utils.Constant;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.media.WBShareCallBackActivity;

/**
 * Created by wangfei on 15/12/3.
 */
public class WBShareActivity extends WBShareCallBackActivity {

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
        MobclickAgent.onResume(this);
        if (!Constant.getInstance().isActive()) {
            Constant.getInstance().setActive(true);
            Constant.getInstance().getAudiBi(this);
        }
    }
}
