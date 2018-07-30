package com.beautyyan.beautyyanapp.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.YuYuanApp;

/**
 * Created by xuelu on 2017/3/24.
 */

public class ToastUtils {
    public static void showShort(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
    public static void showLong(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

    public static void showCenterShort(Context context, String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View view = View.inflate(context, R.layout.toast_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.txt);
        tv.setText(str);
        toast.setView(view);
        toast.show();
    }
    public static void showCenterLong(Context context, String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View view = View.inflate(context, R.layout.toast_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.txt);
        tv.setText(str);
        toast.setView(view);
        toast.show();
    }

    //增加奥迪币
    public static void showAddAudiBi() {
        Toast toast = Toast.makeText(YuYuanApp.getIns(), "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View view = View.inflate(YuYuanApp.getIns(), R.layout.add_audibi_layout, null);
        toast.setView(view);
        toast.show();
    }

}
