package com.beautyyan.beautyyanapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.beautyyan.beautyyanapp.http.bean.User;
import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.utils.LogUtil;

import xmpp.constant.Constants;
import xmpp.xmpp.XmppConnection;

/**
 * Created by x_luthy on 2017/6/30.
 */

public class NetworkBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtil.i("network changed !");
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            if (Constant.getInstance().isXmppHasLogined() ||
                    "-1".equals(Constant.getInstance().getUser().getUserId())) {
                return;
            }
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                loginXmpp(Constant.getInstance().getUser());
            }
        }
    }

    private void loginXmpp(final User user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isSuccess = XmppConnection.getInstance().login(String.valueOf(user.getUserId()), Constants.PWD);
                if (isSuccess) {
                    Constant.getInstance().setXmppHasLogined(true);
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final boolean isSuccess = XmppConnection.getInstance().login(String.valueOf(user.getPhone()), Constants.PWD);
                                    if (isSuccess) {
                                        Constant.getInstance().setXmppHasLogined(true);
                                    }
                        }
                    }).start();

                }
            }
        }).start();
    }
}
