package com.beautyyan.beautyyanapp.user;

import android.content.Context;

public final class WorkUIKit {
    // context
    private static Context context;

    // 自己的用户帐号
    private static String account;

    private static String userId;


    /**
     * 初始化UIKit，须传入context以及用户信息提供者
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        WorkUIKit.context = context.getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    public static String getAccount() {
        return account;
    }

    /**
     * 设置当前登录用户的帐号
     *
     * @param account 帐号
     */
    public static void setAccount(String account) {
        WorkUIKit.account = account;
    }


    public static String getUserId() {
        return userId;
    }
}
