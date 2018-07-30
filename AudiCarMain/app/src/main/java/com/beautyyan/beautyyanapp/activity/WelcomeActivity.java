package com.beautyyan.beautyyanapp.activity;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.YuYuanApp;
import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.view.MyViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xuelu on 2017/3/29.
 */

public class WelcomeActivity extends BaseActivity {

    @Bind(R.id.myVp)
    MyViewPager myVp;
    @Bind(R.id.vpPoints)
    LinearLayout vpPoints;
    @Bind(R.id.start)
    Button start;
    private List<Integer> listPic = new ArrayList<>();
    private Context context;

    @Override
    protected void bindButterKnife() {
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void initView() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_welcome);
        setTitleBarGone();
        ButterKnife.bind(this);
        YuYuanApp.getIns().setStatusBar(this, null);
        context = this;
        start.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        listPic.add(R.mipmap.welcome_bg1);
        listPic.add(R.mipmap.welcome_bg2);
        listPic.add(R.mipmap.welcome_bg3);
        listPic.add(R.mipmap.welcome_bg4);
        myVp.setAdapter(new Adapter());
        for (int i = 0; i < listPic.size(); i++) {
            View view = View.inflate(context, R.layout.banner_grid_item, null);
            vpPoints.addView(view);
        }
        myVp.setCurrentItem(0);
        setPoint();
    }

    @Override
    protected void initListener() {
        myVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setPoint();
                if (position == listPic.size() - 1) {
                    start.setVisibility(View.VISIBLE);
                }
                else {
                    start.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    Constant.getInstance().showStatusBar(WelcomeActivity.this, true);
                }
                startToActivity(MainActivity.class);
                overridePendingTransition(R.anim.bottom_to_top, R.anim.do_nothing);
                finish();
            }
        });
    }

    private void setPoint() {
        for (int i = 0; i < vpPoints.getChildCount(); i++) {
            View view = vpPoints.getChildAt(i);
            ImageView imageView = (ImageView) view.findViewById(R.id.banner_grid_item_image);
            if (i == myVp.getCurrentItem()) {
                imageView.setBackgroundResource(R.drawable.banner_selected);
            } else {
                imageView.setBackgroundResource(R.drawable.banner_unselected);
            }
        }
    }

    private class Adapter extends PagerAdapter {

        public Adapter() {
        }

        @Override
        public int getCount() {
            return listPic.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(context, R.layout.banner_item, null);
            ImageView img = (ImageView) view.findViewById(R.id.banner_item_image);
            img.setImageResource(listPic.get(position));
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
