package com.beautyyan.beautyyanapp.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.YuYuanApp;
import com.beautyyan.beautyyanapp.utils.Constant;

import xmpp.util.HideSoftInputHelperTool;


/**
 * Created by xuelu on 2017/3/22.
 */

public abstract class BaseActivity extends Activity {
    private ImageView backBtn;
    private TextView titleText;
    private FrameLayout flContent;
    private LinearLayout llRoot;
    private View rlTitle;
    private View viewAuto;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            savedInstanceState = null;
        }
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(R.style.AppTheme);
        super.setContentView(R.layout.activity_base);
        backBtn = (ImageView) super.findViewById(R.id.back_btn);
        titleText = (TextView) super.findViewById(R.id.title_text);
        flContent = (FrameLayout) super.findViewById(R.id.fl_content);
        llRoot = (LinearLayout) super.findViewById(R.id.ll_root);
        rlTitle = super.findViewById(R.id.rl_title);
        viewAuto = super.findViewById(R.id.view_auto);
        YuYuanApp.getIns().setStatusBar(this, viewAuto);
        initView();
        initData();
        initListener();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                HideSoftInputHelperTool.hideSoftInputBoard(backBtn, BaseActivity.this);
            }
        });
    }

    public void setTitleBgColor(int color) {
        rlTitle.setBackgroundColor(color);
    }

    public void setTitleTxtColor(int color) {
        titleText.setTextColor(color);
    }

    public void setBackImageRes(int res) {
        backBtn.setImageResource(res);
    }

    public void setViewAutoBgColor(int color) {
        viewAuto.setBackgroundColor(color);
    }

    public void setViewAutoAlpha(float alpha) {
        viewAuto.setAlpha(alpha);
    }

    public void setContentView(int layoutResID) {
        flContent.addView(getLayoutInflater().inflate(layoutResID, null), ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        bindButterKnife();
    }

    public void setTitleName(String name) {
        titleText.setText(name);
    }

    public void showToast(String toast)
    {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    public void setBackBtnGone() {
        backBtn.setVisibility(View.GONE);
    }
    public void setTitleBarGone() {
        rlTitle.setVisibility(View.GONE);
    }


    public void startToActivity(Class<?> cls) {
        Intent intent = new Intent(BaseActivity.this, cls);
        startActivity(intent);
    }


    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics() );
        return res;
    }

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
        if (!Constant.getInstance().isActive()) {
            Constant.getInstance().setActive(true);
            Constant.getInstance().getAudiBi(BaseActivity.this);
        }
    }

    protected abstract void bindButterKnife();
    protected abstract void initView();
    protected abstract void initData();
    protected abstract void initListener();
}
