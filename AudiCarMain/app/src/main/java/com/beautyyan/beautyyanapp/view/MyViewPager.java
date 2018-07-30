package com.beautyyan.beautyyanapp.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by xuelu on 2017/3/22.
 */

public class MyViewPager extends ViewPager {

//    private boolean isSliding = true;

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //	@Override
//	public boolean dispatchTouchEvent(MotionEvent arg0) {
//		//告诉父view，我的事件自己处理(解决ViewPager嵌套引起子ViewPager无法触摸问题)
//		getParent().requestDisallowInterceptTouchEvent(true);
//
//		return super.dispatchTouchEvent(arg0);
//	}
//    public void setSliding(boolean isSliding) {
//        this.isSliding = isSliding;
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent arg0) {
//        return super.onInterceptTouchEvent(arg0);
//    }
//    @Override
//    public boolean onTouchEvent(MotionEvent arg0) {
//        return super.onTouchEvent(arg0);
//    }


}

