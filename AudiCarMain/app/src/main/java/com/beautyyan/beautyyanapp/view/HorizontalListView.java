package com.beautyyan.beautyyanapp.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.activity.WebViewActivity;
import com.beautyyan.beautyyanapp.http.Api;
import com.beautyyan.beautyyanapp.http.bean.Car;
import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.utils.LogUtil;
import com.beautyyan.beautyyanapp.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * Created by xuelu on 2017/4/19.
 */

public class HorizontalListView extends LinearLayout {
    private boolean isFist = true;
    private int targetWidth = Constant.getInstance().getItemWidth();
    private Context context;

    public HorizontalListView(Context context) {
        super(context);
        buildView(context, null);
    }
    public HorizontalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        buildView(context, null);
    }

    public void buildView(final Context context, List<Car> list) {
        this.context = context;
        if (list == null) {
            return;
        }
        for (final Car item : list) {
            final View view = LayoutInflater.from(context).inflate(R.layout.item_carinfo, null);
            try {
                if (isFist) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            targetWidth = view.findViewById(R.id.img).getWidth();
                            LogUtil.i("targetWidth:" + targetWidth);
                        }
                    });
                    isFist = false;
                }
                String url  = returnUrl(item.getSmallImage().getPath());
                final String modelLineStr  = item.getModelLine() == null ? "" : item.getModelLine() + " ";
                String modelCodeStr = item.getModelCode() + " ";
                String modelNameStr  = item.getModelName();
                String carYears = item.getRegistration().substring(0, 4) + "年";
                String manuFacture = item.getManufacture() == null ? "" : item.getManufacture() + " ";
                String tagIdsStr = item.getTagIds();

                TextView tvCarxi = (TextView) view.findViewById(R.id.tv_carxi);
                RecyclerImageView recyclerImageView = (RecyclerImageView) view.findViewById(R.id.img);
                TextView tvYears = (TextView) view.findViewById(R.id.tv_years);
                TextView tvPrice = (TextView) view.findViewById(R.id.tv_price);
                TextView tvTag1 = (TextView) view.findViewById(R.id.tag1_txt);
                TextView tvTag2 = (TextView) view.findViewById(R.id.tag2_txt);
                TextView tvTag3 = (TextView) view.findViewById(R.id.tag3_txt);

                View tagLl = view.findViewById(R.id.tag_ll);
                if (Util.isEmpty(tagIdsStr)) {
                    tagLl.setVisibility(View.INVISIBLE);
                }
                else {
                    tagLl.setVisibility(View.VISIBLE);
                    String[] strArr = tagIdsStr.split("_");
                    switch (strArr.length) {
                        case 1:
                            tvTag1.setVisibility(View.VISIBLE);
                            tvTag1.setText(strArr[0]);
                            break;
                        case 2:
                            tvTag1.setVisibility(View.VISIBLE);
                            tvTag1.setText(strArr[0]);

                            tvTag2.setVisibility(View.VISIBLE);
                            tvTag2.setText(strArr[1]);
                            break;
                        case 3:
                            tvTag1.setVisibility(View.VISIBLE);
                            tvTag1.setText(strArr[0]);

                            tvTag2.setVisibility(View.VISIBLE);
                            tvTag2.setText(strArr[1]);

                            tvTag3.setVisibility(View.VISIBLE);
                            tvTag3.setText(strArr[2]);
                            break;
                        default:
                            tvTag1.setVisibility(View.VISIBLE);
                            tvTag1.setText(strArr[0]);

                            tvTag2.setVisibility(View.VISIBLE);
                            tvTag2.setText(strArr[1]);

                            tvTag3.setVisibility(View.VISIBLE);
                            tvTag3.setText(strArr[2]);
                            break;
                    }
                }

                String transmissionStr = "";
                switch (item.getTransmission()) {
                    case "01":
                        transmissionStr = "自动挡 ";
                        break;
                    case "02":
                        transmissionStr = "手动挡 ";
                        break;
                    case "03":
                        transmissionStr = "手自一体 ";
                        break;
                }

                final String tag = "奥迪 " + (item.getCarModel() == null ? "" :
                        item.getCarModel()) + " " + modelCodeStr + transmissionStr +
                        item.getCarAir() + " " + item.getDeviceType();
                tvCarxi.setText(tag);
                tvYears.setText(carYears + '/' + Util.getOnePointNumber(item.getMileage()) + '/' + item.getArea());
                tvPrice.setText(Util.getTwoPointNumber(item.getPrice()) + '万');
                Picasso.with(recyclerImageView.getContext())
                        .load(url)
                        .memoryPolicy(NO_CACHE, NO_STORE)
                        .placeholder(R.mipmap.car_head_default_bg)
                        .config(Bitmap.Config.RGB_565)
                        .into(recyclerImageView);
                LogUtil.i("picurl is " + url);

                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, WebViewActivity.class);
                        intent.putExtra(Constant.WEB_URL, Constant.URL.CAR_DETAIL + "?releaseNumber=" + item.getReleaseNumber() + "&cityId=" + Constant.getInstance().getChoosedCity().getCityId() + "&modelLine=" + item.getModelLine());
                        LogUtil.i("carDetail ---" + Constant.URL.CAR_DETAIL + "?releaseNumber=" + item.getReleaseNumber() + "&cityId=" + Constant.getInstance().getChoosedCity().getCityId() + "&modelLine=" + item.getModelLine());
                        intent.putExtra(Constant.WEB_TITLE, "");
                        intent.putExtra(Constant.IS_DETAIL_ACTIVITY, true);
                        intent.putExtra(Constant.CAR_DETAIL_ID, item.getReleaseNumber());


                        String shareTitle = tag;
                        String shareUrl = Api.H5_URL + "sharecar?releaseNumber=" + item.getReleaseNumber()
                                + "&modelLine=" + Util.encodeStr(item.getModelLine())
                                + "&cityId=" + Constant.getInstance().getLocatedCity().getCityId();
                        String shareImgUrl = item.getShareLogo();
                        String shareInfo = item.getShareOfficial();

                        intent.putExtra(Constant.SHARE_TITLE, shareTitle);
                        intent.putExtra(Constant.SHARE_URL, shareUrl);
                        intent.putExtra(Constant.SHARE_IMG, shareImgUrl);
                        intent.putExtra(Constant.SHARE_INFO, shareInfo);

                        context.startActivity(intent);
                    }
                });
                addView(view);
            }
            catch (NullPointerException e) {

            }
        }
    }

//    private Transformation getTransformation(final ImageView img, final String url) {
//        Transformation transformation = new Transformation() {
//
//            @Override
//            public Bitmap transform(Bitmap source) {
//
//                LogUtil.i("source.getHeight()="+source.getHeight()+",source.getWidth()="+source.getWidth()+",targetWidth="+targetWidth + url);
//                if (targetWidth == 0) {
//                    targetWidth = Util.dip2px(context, 144);
//                    LogUtil.i("targetWidth is " + targetWidth);
//                }
//                if(source.getWidth()==0){
//                    return source;
//                }
//
//                //如果图片小于设置的宽度，则返回原图
//                if(source.getWidth()<targetWidth){
//                    return source;
//                }else{
//                    //如果图片大小大于等于设置的宽度，则按照设置的宽度比例来缩放
//                    double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
//                    int targetHeight = (int) (targetWidth * aspectRatio);
//                    if (targetHeight != 0 && targetWidth != 0) {
//                        Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
//                        if (result != source) {
//                            // Same bitmap is returned if sizes are the same
//                            source.recycle();
//                        }
//                        return result;
//                    } else {
//                        return source;
//                    }
//                }
//
//            }
//
//            @Override
//            public String key() {
//                return "transformation" + " desiredWidth";
//            }
//        };
//        return transformation;
//    }

    private String returnUrl(String url) {
        String[] strArr = url.split("\\.");
        if (strArr == null) {
            return "";
        }
        url = url.substring(0, url.length() - strArr[strArr.length - 1].length() - 1) + "_304x228." + strArr[strArr.length - 1];
        LogUtil.i("url is " + url);
        return url;
    }

}
