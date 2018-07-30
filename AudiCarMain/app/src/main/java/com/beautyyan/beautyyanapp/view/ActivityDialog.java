package com.beautyyan.beautyyanapp.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.YuYuanApp;
import com.beautyyan.beautyyanapp.activity.WebViewActivity;
import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.utils.DateUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xuelu on 2017/10/10.
 */

public class ActivityDialog extends Dialog {
    @Bind(R.id.cancel_img)
    ImageView cancelImg;
    @Bind(R.id.duihuan_txt)
    TextView duihuanTxt;

    public ActivityDialog(Context context) {
        super(context, R.style.regular_intro_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_audibi_activity);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(false);
        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        duihuanTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getContext(), Constant.UMENG_CLICK_ADS_TO_DUIHUAN);
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra(Constant.WEB_TITLE, "5优惠兑换机");
                intent.putExtra(Constant.WEB_URL, Constant.getInstance().getAudiBiDuiHuanUrl());
                getContext().startActivity(intent);
                dismiss();
            }
        });
        YuYuanApp.getIns().getSp().setHasActivityOpen(DateUtil.formatMillis(System.currentTimeMillis(),
                DateUtil.FMT_YMD));
    }


    @Override
    public void dismiss() {
        super.dismiss();
        ButterKnife.unbind(this);
    }
}
