package com.beautyyan.beautyyanapp.view;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.beautyyan.beautyyanapp.utils.Constant;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class BitmapRecycleBin {

    private final static int MAX_ITEM_COUNT = 10;

    private static final boolean KITKAT = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT);
    private static final boolean HONEYCOMB = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB);
    private static final boolean RECYCLE = false; // 暂时从4.4开始支持

    private static List<RecycleItem> bitmapCache;

    private static int maxItemCount;

    /**
     * 缓存的最大的位图尺寸，不缓存大位图
     */
    private static int maxBmpSize;
    /**
     * 缓存的最小的位图尺寸，只对低于KITKAT的系统做限制，
     * 这些系统上inBitmap限制较多，很多地方用不上，频繁
     * 存取缓存也是浪费
     */
    private static int minBmpSize;

    static {
        if (RECYCLE) {
            bitmapCache = Collections.synchronizedList(new LinkedList<RecycleItem>());
            BitmapRecycleBin.maxItemCount = MAX_ITEM_COUNT;
            int maxWidth = (Constant.getInstance().getDeviceWidth() > Constant.getInstance().getDeviceHeight() ?
                    Constant.getInstance().getDeviceHeight() : Constant.getInstance().getDeviceWidth()) / 2;
            maxBmpSize = maxWidth * maxWidth * 4;
            minBmpSize = KITKAT ? 0 : maxBmpSize / 8;
        }
    }

    public static void recycle(Bitmap bitmap) {
        if (RECYCLE && accept(bitmap)) {
            put(bitmap);
        } else {
            bitmap.recycle();
        }
    }

    public static void addInBitmapOption(String pathName, BitmapFactory.Options options) {
        // reject request if version is older than HONEYCOMB
        if (RECYCLE) {
            // decode bounds
            if (options.outWidth == 0 || options.outHeight == 0) {
                BitmapFactory.Options tmp = new BitmapFactory.Options();
                tmp.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(pathName, tmp);
                options.outWidth = tmp.outWidth;
                options.outHeight = tmp.outHeight;
            }

            addBitmapOptionHoneyComb(options);
        }
    }

    public static void trim() {
        if (RECYCLE) {
            synchronized (bitmapCache) {
                for (RecycleItem item : bitmapCache) {
                    item.destroy();
                }
                bitmapCache.clear();
            }
        }
    }

    private static void put(Bitmap bitmap) {
        int index = 0;

        if (KITKAT) {
            // sorted by allocation byte count
            putKitkat(bitmap);
        } else {
            bitmapCache.add(new RecycleItem(bitmap));
        }
        checkSize();

        log("bitmap size: [" + bitmap.getWidth() + ":" + bitmap.getHeight() + "] cached, count[" + bitmapCache.size() + "]");
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void putKitkat(Bitmap bitmap) {
        synchronized (bitmapCache) {
            int index = 0;
            final Iterator<RecycleItem> iterator = bitmapCache.iterator();
            while (iterator.hasNext()) {
                Bitmap item = iterator.next().get();
                if (null != item) {
                    if (item.getAllocationByteCount() >= bitmap.getAllocationByteCount()) {
                        break;
                    } else {
                        ++index;
                    }
                } else {
                    // Remove from the set if the reference has been cleared.
                    iterator.remove();
                }
            }
            bitmapCache.add(index, new RecycleItem(bitmap));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void addBitmapOptionHoneyComb(BitmapFactory.Options options) {
        // inBitmap only works with mutable bitmaps, so force the decoder to
        // return mutable bitmaps.
        options.inMutable = true;
        // Try to find a bitmap to use for inBitmap.
        Bitmap inBitmap = get(options);

        if (inBitmap != null) {
            // If a suitable bitmap has been found, set it as the value of
            // inBitmap.
            options.inBitmap = inBitmap;
        }
    }

    private static Bitmap get(BitmapFactory.Options options) {
        ensureInSampleSize(options);
        if (!accept(options)) {
            return null;
        }

        statistics(options);

        Bitmap bitmap = null;
        synchronized (bitmapCache) {
            final Iterator<RecycleItem> iterator = bitmapCache.iterator();
            Bitmap item;

            while (iterator.hasNext()) {
                item = iterator.next().get();

                if (null != item && item.isMutable()) {
                    // Check to see it the item can be used for inBitmap.
                    if (canUseForInBitmap(item, options)) {
                        bitmap = item;

                        // Remove from reusable set so it can't be used again.
                        iterator.remove();
                        break;
                    }
                } else {
                    // Remove from the set if the reference has been cleared.
                    iterator.remove();
                }
            }
        }
        if (bitmap != null) {
            log("bitmap[" + bitmap.getWidth() + ":" + bitmap.getHeight() + "] found for size[" + options.outWidth + ":" + options.outHeight + ":" + options.inSampleSize + "]" + " config[" + bitmap.getConfig() + "]");
        } else {
            log("missed request size[" + options.outWidth + ":" + options.outHeight + ":" + options.inSampleSize + "]");
        }
        log("available cache count [" + bitmapCache.size() + "]");
        return bitmap;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean canUseForInBitmap(Bitmap candidate, BitmapFactory.Options targetOptions) {
        if (KITKAT) {
            // From Android 4.4 (KitKat) onward we can re-use if the byte size of
            // the new bitmap is smaller than the reusable bitmap candidate
            // allocation byte count.
            int width = targetOptions.outWidth / targetOptions.inSampleSize;
            int height = targetOptions.outHeight / targetOptions.inSampleSize;
            int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
            return byteCount <= candidate.getAllocationByteCount();
        }

        // On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
        return candidate.getWidth() == targetOptions.outWidth
                && candidate.getHeight() == targetOptions.outHeight
                && targetOptions.inSampleSize == 1;
    }

    /**
     * A helper function to return the byte usage per pixel of a bitmap based on its configuration.
     */
    private static int getBytesPerPixel(Config config) {
        if (config == null) {
            return 1;
        }

        switch (config) {
        case ARGB_8888:
            return 4;
        case RGB_565:
        case ARGB_4444:
            return 2;
        case ALPHA_8:
        default:
            return 1;
        }
    }

    private static void ensureInSampleSize(BitmapFactory.Options options) {
        if (options.inSampleSize <= 1) {
            options.inSampleSize = 1;
        }
    }

    private static void statistics(BitmapFactory.Options options) {

    }

    private static boolean accept(Bitmap bitmap) {
        int byteCount = bitmap.getWidth() * bitmap.getHeight() * getBytesPerPixel(bitmap.getConfig());
        return byteCount > minBmpSize && byteCount < maxBmpSize;
    }

    private static boolean accept(BitmapFactory.Options options) {
        if (!KITKAT && options.inSampleSize != 1) {
            return false;
        }
        int width = options.outWidth / options.inSampleSize;
        int height = options.outHeight / options.inSampleSize;
        int byteCount = width * height * getBytesPerPixel(options.inPreferredConfig);
        return byteCount > minBmpSize && byteCount < maxBmpSize;
    }

    private static void checkSize() {
        if (bitmapCache.size() > maxItemCount) {
            synchronized (bitmapCache) {
                int oldest = 0;
                for (int i = 1; i < bitmapCache.size(); ++i) {
                    if (bitmapCache.get(i).time < bitmapCache.get(oldest).time) {
                        oldest = i;
                    }
                }
                log("cache size[" + bitmapCache.get(oldest).get().getWidth() + ":" + bitmapCache.get(oldest).get().getHeight() + "] remove");
                bitmapCache.remove(oldest).destroy();
            }
        }
    }

    private static void log(String msg) {
        Log.d("BitmapRecycleBin", msg);
    }

    private static final class RecycleItem {
        Bitmap data;
        long time;

        RecycleItem(Bitmap data) {
            this.data = data;
            this.time = SystemClock.elapsedRealtime();
        }

        Bitmap get() {
            return data;
        }

        void destroy() {
            if (data != null) {
                data.recycle();
            }
        }
    }
}
