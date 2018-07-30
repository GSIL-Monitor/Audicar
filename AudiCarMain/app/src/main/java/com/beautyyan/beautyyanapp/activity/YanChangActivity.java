package com.beautyyan.beautyyanapp.activity;

import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.beautyyan.beautyyanapp.R;

/**
 * Created by xuelu on 2017/5/16.
 */

public class YanChangActivity extends BaseActivity {

    private ImageView img;
    private ScrollView scRoot;

    @Override
    protected void bindButterKnife() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_service_pro);
        setTitleName("延长质保");
        setViewAutoAlpha(1);
        img = (ImageView) findViewById(R.id.img);
        scRoot = (ScrollView) findViewById(R.id.sc_root);
        img.setImageResource(R.mipmap.yan_chang_acti);
        scRoot.setBackgroundColor(Color.BLACK);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }
}
