package com.beautyyan.beautyyanapp.utils;

import android.content.Context;

import java.io.File;

/**
 * Created by xuelu on 2017/4/19.
 *
 */

public class DataManagerUtil {

    //清除缓存
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    /** * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理
     * * * @param directory */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }
}
