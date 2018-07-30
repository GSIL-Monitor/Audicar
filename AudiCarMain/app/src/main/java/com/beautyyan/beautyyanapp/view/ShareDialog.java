package com.beautyyan.beautyyanapp.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.listener.ShareListener;
import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.utils.Util;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xuelu on 2017/9/19.
 */

public class ShareDialog extends Dialog implements View.OnClickListener {
    @Bind(R.id.ll_weixin)
    LinearLayout llWeixin;
    @Bind(R.id.ll_wxcircle)
    LinearLayout llWxcircle;
    @Bind(R.id.ll_qq)
    LinearLayout llQq;
    @Bind(R.id.ll_qqzone)
    LinearLayout llQqzone;
    @Bind(R.id.ll_weibo)
    LinearLayout llWeibo;
    @Bind(R.id.btn_cancel)
    Button btnCancel;
    private ShareListener listener;

    public ShareDialog(Context context, ShareListener listener) {
        super(context, R.style.price_range_dialog);
        getWindow().setGravity(Gravity.BOTTOM);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_board);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        ButterKnife.unbind(this);
    }

    private void initView() {
        LinearLayout.LayoutParams lpWeibo = (LinearLayout.LayoutParams) llWeibo.getLayoutParams();
        LinearLayout.LayoutParams lpWeixin = (LinearLayout.LayoutParams) llWeixin.getLayoutParams();
        LinearLayout.LayoutParams lpWxcircle = (LinearLayout.LayoutParams) llWxcircle.getLayoutParams();
        LinearLayout.LayoutParams lpQq = (LinearLayout.LayoutParams) llQq.getLayoutParams();
        LinearLayout.LayoutParams lpQqzone = (LinearLayout.LayoutParams) llQqzone.getLayoutParams();
        lpWeibo.width
                =
        lpWeixin.width
                =
        lpWxcircle.width
                =
        lpQq.width
                =
        lpQqzone.width
                = Constant.getInstance().getDeviceWidth() / 5;

        if (!Util.isQQClientInstalled(getContext())) {
            llQq.setVisibility(View.GONE);
            llQqzone.setVisibility(View.GONE);
        }
        if (!Util.isWeixinInstalled(getContext())) {
            llWeixin.setVisibility(View.GONE);
            llWxcircle.setVisibility(View.GONE);
        }
        if (!Util.isWeiboInstalled(getContext())) {
            llWeibo.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        llWeibo.setOnClickListener(this);
        llWeixin.setOnClickListener(this);
        llWxcircle.setOnClickListener(this);
        llQq.setOnClickListener(this);
        llQqzone.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.ll_weixin:
                listener.weixin();
                break;
            case R.id.ll_wxcircle:
                listener.weixinCircle();
                break;
            case R.id.ll_qq:
                listener.qq();
                break;
            case R.id.ll_qqzone:
                listener.qqZone();
                break;
            case R.id.ll_weibo:
                listener.weibo();
                break;
            default:
                break;
        }

    }
}
