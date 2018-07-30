
package com.beautyyan.beautyyanapp.wxapi;


import com.beautyyan.beautyyanapp.utils.Constant;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

public class WXEntryActivity extends WXCallbackActivity {
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
            Constant.getInstance().getAudiBi(WXEntryActivity.this);
        }
    }


}
