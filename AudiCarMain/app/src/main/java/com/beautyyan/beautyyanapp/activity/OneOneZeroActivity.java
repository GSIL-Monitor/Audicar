package com.beautyyan.beautyyanapp.activity;

import android.widget.ImageView;

import com.beautyyan.beautyyanapp.R;

/**
 * Created by xuelu on 2017/5/16.
 */

public class OneOneZeroActivity extends BaseActivity {

    private ImageView img;

    @Override
    protected void bindButterKnife() {

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_service_pro);
        setTitleName("110项检测");
        setViewAutoAlpha(1);
        img = (ImageView) findViewById(R.id.img);
        img.setImageResource(R.mipmap.one_one_zero_acti);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }
}
