package com.beautyyan.beautyyanapp.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.YuYuanApp;


/**
 * 加载动画
 * @author 刘宾  
 * @date 2015年2月6日 上午10:09:56
 */
public class LoadingView
        extends
		LinearLayout
{
//	private RelativeLayout 	  parentLayout;
	private RelativeLayout emptyLayout;
	private RelativeLayout failLayout;
	private ImageView ivAnim;
	private ImageView ivAnim2;
	private RelativeLayout rlAnim;


	
	private Context mContext;
	private AnimationDrawable animationDrawable;
	private AnimationDrawable animationDrawable2;
	private TextView reloadTxt;
	
	public LoadingView(Context context) {
		super(context);
		initView(context);
	}
	
	public LoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	
	@SuppressLint("NewApi")
    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}
	
	private void initView(Context context) {
		mContext = context;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		RelativeLayout containerView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.loading_view, null);
		
//		parentLayout = (RelativeLayout) containerView.findViewById(R.id.loading_view_layout);
		emptyLayout = (RelativeLayout) containerView.findViewById(R.id.loading_view_empty_layout);
		failLayout = (RelativeLayout) containerView.findViewById(R.id.loading_view_fail_layout);
		ivAnim = (ImageView) containerView.findViewById(R.id.loading_view_anim_img);
		ivAnim2 = (ImageView) containerView.findViewById(R.id.loading_view_anim_img2);
		rlAnim = (RelativeLayout) containerView.findViewById(R.id.rl_anim);
		setFailView(R.mipmap.icon_loading_failed, R.string.loading_failed);
		setLoadingAnim(R.drawable.loading_anim);
		ivAnim2.setBackgroundResource(R.drawable.loading_anim2);
		addView(containerView, lp);
	}
	/**
	 * 设置加载动画
	 * @param animId 动画资源id
	 */
	public void setLoadingAnim(int animId){
		if(null==ivAnim) return;
		ivAnim.setBackgroundResource(animId);
	}
	
	/** 自定义数据为空的View */
	public void setEmptyView(int resId){
		if(null==emptyLayout) return;
//		emptyLayout.removeAllViews();
		emptyLayout.addView(LayoutInflater.from(mContext).inflate(resId, null));
	}
	/** 自定义数据为空的View */
	public void setEmptyView(View view){
		if(null==emptyLayout || null==view) return;
//		emptyLayout.removeAllViews();
		emptyLayout.addView(view);
	}

	/** 自定义数据加载失败的View */
	public void setFailView(int resId){
		if(null==failLayout) return;
//		failLayout.removeAllViews();
		failLayout.addView(LayoutInflater.from(mContext).inflate(resId, null));
	}
	/** 自定义数据加载失败的View */
	public void setFailView(View view){
		if(null==failLayout || null==view) return;
//		failLayout.removeAllViews();
		failLayout.addView(view);
	}
	/** 
	 * 使用默认的数据加载失败View 
	 */
	public void setFailView(int drawableId,int stringId){
		if(null==failLayout) return;
//		failLayout.removeAllViews();
		final View currView = LayoutInflater.from(mContext).inflate(R.layout.load_fail_view, null);
//		((ImageView)currView.findViewById(R.id.comment_empty_view_img)).setImageResource(drawableId);
//		((TextView)currView.findViewById(R.id.comment_empty_view_tv)).setText(mContext.getResources().getString(stringId));
		reloadTxt = (TextView) currView.findViewById(R.id.reload_txt);
		failLayout.addView(currView);
	}
	
	
	/** 设置空View点击事件 */
	public void setEmptyClickListener(OnClickListener listener){
		if(null==emptyLayout || null==listener) return;
		emptyLayout.setOnClickListener(listener);
	}
	/** 设置数据加载失败View点击事件 */
	public void setFailClickListener(OnClickListener listener){
		if(null==reloadTxt || null==listener) return;
		reloadTxt.setOnClickListener(listener);
	}
	
	/**
	 * 改变加载动画状态
	 * @param status
	 *            0:加载中，1：加载空白，2 为加载成功  -1：加载失败
	 */
	public void changeLoadingStatus(final int status) {
		YuYuanApp.getIns().handler.post(new Runnable() {
			@Override
			public void run() {
				switch (status) {
					case 0:
						if(null==animationDrawable || animationDrawable2 == null){
							animationDrawable = (AnimationDrawable) ivAnim.getBackground();
							animationDrawable2 = (AnimationDrawable) ivAnim2.getBackground();
						}
						animationDrawable.start();
						animationDrawable2.start();

						rlAnim.setVisibility(View.VISIBLE);
						emptyLayout.setVisibility(View.GONE);
						failLayout.setVisibility(View.GONE);
						break;
					case -1:
						if (animationDrawable != null && animationDrawable.isRunning())
						{
							animationDrawable.stop();
						}
						if (animationDrawable2 != null && animationDrawable2.isRunning())
						{
							animationDrawable2.stop();
						}
						rlAnim.setVisibility(View.GONE);
						emptyLayout.setVisibility(View.GONE);
						failLayout.setVisibility(View.VISIBLE);
						break;
					case 1:
						if (animationDrawable != null && animationDrawable.isRunning())
						{
							animationDrawable.stop();
						}
						if (animationDrawable2 != null && animationDrawable2.isRunning())
						{
							animationDrawable2.stop();
						}
						rlAnim.setVisibility(View.GONE);
						emptyLayout.setVisibility(View.VISIBLE);
						failLayout.setVisibility(View.GONE);
						break;
					case 2 :
						if (animationDrawable != null && animationDrawable.isRunning())
						{
							animationDrawable.stop();
						}
						if (animationDrawable2 != null && animationDrawable2.isRunning())
						{
							animationDrawable2.stop();
						}
						rlAnim.setVisibility(View.GONE);
						emptyLayout.setVisibility(View.GONE);
						failLayout.setVisibility(View.GONE);
				}
			}
		});



//		YuYuanApp.getIns().handler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//
//			}
//		}, 500);

	}

}
