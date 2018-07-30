package com.beautyyan.beautyyanapp.view;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.utils.SampleSizeUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import xmpp.util.ImageUtil;

public class BitmapDecoder {
    public static Bitmap decodeSampledForDisplay(String pathName) {
        return decodeSampledForDisplay(pathName, true);
    }

    public static Bitmap decodeSampledForDisplay(String pathName, boolean withTextureLimit) {
        float ratio = ImageUtil.MAX_IMAGE_RATIO;
        int[][] reqBounds = new int[][]{
                new int[]{Constant.getInstance().getDeviceWidth() * 2, Constant.getInstance().getDeviceHeight()},
                new int[]{Constant.getInstance().getDeviceWidth(), Constant.getInstance().getDeviceHeight() * 2},
                new int[]{(int) (Constant.getInstance().getDeviceWidth() * 1.414), (int) (Constant.getInstance().getDeviceHeight() * 1.414)},
        };

        // decode bound
        int[] bound = decodeBound(pathName);
        // pick request bound
        int[] reqBound = pickReqBoundWithRatio(bound, reqBounds, ratio);

        int width = bound[0];
        int height = bound[1];
        int reqWidth = reqBound[0];
        int reqHeight = reqBound[1];

        // calculate sample size
        int sampleSize = SampleSizeUtil.calculateSampleSize(width, height, reqWidth, reqHeight);

        if (withTextureLimit) {
            // adjust sample size
            sampleSize = SampleSizeUtil.adjustSampleSizeWithTexture(sampleSize, width, height);
        }

        int RETRY_LIMIT = 5;
        Bitmap bitmap = decodeSampled(pathName, sampleSize);
        while (bitmap == null && RETRY_LIMIT > 0) {
            sampleSize++;
            RETRY_LIMIT--;
            bitmap = decodeSampled(pathName, sampleSize);
        }

        return bitmap;
    }

    public static int[] decodeBound(String pathName) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        return new int[]{options.outWidth, options.outHeight};
    }

    public static int[] decodeBound(Resources res, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        return new int[]{options.outWidth, options.outHeight};
    }

    private static int[] pickReqBoundWithRatio(int[] bound, int[][] reqBounds, float ratio) {
        float hRatio = bound[1] == 0 ? 0 : (float) bound[0] / (float) bound[1];
        float vRatio = bound[0] == 0 ? 0 : (float) bound[1] / (float) bound[0];

        if (hRatio >= ratio) {
            return reqBounds[0];
        } else if (vRatio >= ratio) {
            return reqBounds[1];
        } else {
            return reqBounds[2];
        }
    }

    public static Bitmap decodeSampled(String pathName, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        // RGB_565
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // sample size
        options.inSampleSize = sampleSize;

        BitmapRecycleBin.addInBitmapOption(pathName, options);

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(pathName, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }

        return checkInBitmap(bitmap, options, pathName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static Bitmap checkInBitmap(Bitmap bitmap,
                                        BitmapFactory.Options options, String path) {
        boolean honeycomb = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
        if (honeycomb && bitmap != options.inBitmap && options.inBitmap != null) {
            options.inBitmap.recycle();
            options.inBitmap = null;
        }

        if (bitmap == null) {
            try {
                bitmap = BitmapFactory.decodeFile(path, options);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static int[] decodeBound(File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            int[] bound = decodeBound(is);
            return bound;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new int[]{0, 0};
    }

    public static int[] decodeBound(InputStream is) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);

        return new int[]{options.outWidth, options.outHeight};
    }

    public static Bitmap decodeSampled(String pathName, int reqWidth, int reqHeight) {
        return decodeSampled(pathName, getSampleSize(pathName, reqWidth, reqHeight));
    }


    public static int getSampleSize(String pathName, int reqWidth, int reqHeight) {
        // decode bound
        int[] bound = decodeBound(pathName);

        // calculate sample size
        int sampleSize = SampleSizeUtil.calculateSampleSize(bound[0], bound[1], reqWidth, reqHeight);

        return sampleSize;
    }

    /**
     * ******************************* decode resource ******************************************
     */

    public static Bitmap decodeSampled(Resources resources, int resId, int reqWidth, int reqHeight) {
        return decodeSampled(resources, resId, getSampleSize(resources, resId, reqWidth, reqHeight));
    }

    public static int getSampleSize(Resources resources, int resId, int reqWidth, int reqHeight) {
        // decode bound
        int[] bound = decodeBound(resources, resId);

        // calculate sample size
        int sampleSize = SampleSizeUtil.calculateSampleSize(bound[0], bound[1], reqWidth, reqHeight);

        return sampleSize;
    }


    public static Bitmap decodeSampled(Resources res, int resId, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        // RGB_565
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // sample size
        options.inSampleSize = sampleSize;

        try {
            return BitmapFactory.decodeResource(res, resId, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return null;
    }
}
