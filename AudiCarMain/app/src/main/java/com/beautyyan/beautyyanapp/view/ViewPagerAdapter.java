package com.beautyyan.beautyyanapp.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.http.bean.Banner;
import com.beautyyan.beautyyanapp.listener.BannerOnClickListener;
import com.beautyyan.beautyyanapp.utils.Util;

import java.util.HashMap;
import java.util.List;


public class ViewPagerAdapter extends PagerAdapter {

	private Context context;
	private List<Banner> bannerList;
	private HashMap<Integer, View> views;
	private BannerOnClickListener listener;
	private int size ;

	public ViewPagerAdapter(Context context, /*List<BannerInfo> bannerList,*/ HashMap<Integer, View> views, BannerOnClickListener bannerOnClickListener) {
		this.context = context;
		this.bannerList = bannerList;
		this.views = views;
		this.listener = bannerOnClickListener;
//		this.size = bannerList.size();
	}
	
	public void setData(List<Banner> bannerList){
		this.bannerList = bannerList;
		this.size = bannerList.size();
		notifyDataSetChanged();
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (size == 0) {
			return null;
		}
		final int pos = position % size;
		View view = views.get(pos);
		if (view == null) {
			view = View.inflate(context, R.layout.banner_item, null);
			ImageView recyclerImageView = (ImageView) view.findViewById(R.id.banner_item_image);
			final Banner banner = bannerList.get(pos);
			
//			Util.loadImg(activity, String.valueOf(bannerInfo.getBannerId()), appendBannerUrl(bannerInfo.getImgUrl()), img);
//			int resId = 0;
//			switch (pos)
//			{
//				case 0:
//					resId = R.mipmap.test1;
//					break;
//				case 1:
//					resId = R.mipmap.test2;
//					break;
//				case 2:
//					resId = R.mipmap.test3;
//					break;
//				case 3:
//					resId = R.mipmap.test4;
//					break;
//
//			}
//			recyclerImageView.setImageResource(banner.getDrawId());
			Util.picasso(recyclerImageView, banner.getBannerPath(), R.mipmap.banner_default_bg);

			//banner跳转
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.click(banner);
				}
			});
//			view.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					context.startActivity(new Intent(context, (pos == 0 || pos == 2) ? OneOneZeroActivity.class : TwoYearsActivity.class));
//				}
//			});
			views.put(pos, view);
		}
		if (view.getParent() != null) {
			container.removeView(view);
		}
		container.addView(view);
		return view;
	}


	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(views.get(position));
	}

}

