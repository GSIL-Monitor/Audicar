package com.beautyyan.beautyyanapp.utils.updater;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;


import com.beautyyan.beautyyanapp.activity.ChooseCityActivity;
import com.beautyyan.beautyyanapp.activity.MainActivity;
import com.beautyyan.beautyyanapp.activity.UpdateActivity;
import com.beautyyan.beautyyanapp.utils.SystemUtil;

import java.io.File;

/**
 * Created by simple on 16/12/20.
 * <p>
 * 下载监听
 */

public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        long downId = bundle.getLong(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        //下载完成或点击通知栏
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE) ||
                intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
            queryFileUri(context, downId);
        }
    }

    private void queryFileUri(Context context, long downloadApkId) {
        DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadApkId);
        Cursor c = dManager.query(query);
        if (c != null && c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PENDING:
                    LogUtils.debug("STATUS_PENDING");
                    break;
                case DownloadManager.STATUS_PAUSED:
                    LogUtils.debug("STATUS_PAUSED");
                    break;
                case DownloadManager.STATUS_RUNNING:
                    LogUtils.debug("STATUS_RUNNING");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    LogUtils.debug("STATUS_SUCCESSFUL");
                    String downloadFileUrl = c
                            .getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    installApk(context, Uri.parse(downloadFileUrl));
//                    context.unregisterReceiver();
                    break;
                case DownloadManager.STATUS_FAILED:
                    LogUtils.debug("STATUS_FAILED");
                    Updater.showToast(context,"下载失败，开始重新下载...");
                    context.sendBroadcast(new Intent(Updater.DownloadFailedReceiver.tag));
                    break;
            }
            c.close();
        }
    }

    /**
     * 安装app
     * 注意：安装前，退出app
     * @param context
     * @param uri
     */
    private void installApk(Context context, Uri uri) {

        if (UpdateActivity.getInstance() != null) {
            UpdateActivity.getInstance().finish();
        }
        if (ChooseCityActivity.getIns() != null) {
            ChooseCityActivity.getIns().finish();
        }
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().finish();
        }

        File file = new File(uri.getPath());
        if (!file.exists()) {
            LogUtils.debug("apk file not exists");
            return;
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//增加oppo的手机厂商判断，oppo采用旧的系统进行安装
            if (("OPPO".equals(SystemUtil.getDeviceBrand()) || "oppo".equals(SystemUtil.getDeviceBrand()))){
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            }else {
                String packageName = context.getPackageName();
                Uri providerUri = FileProvider
                    .getUriForFile(context, packageName+".fileprovider", file);
//            Uri providerUri = FileProvider
//                    .getUriForFile(context, "com.simplepeng.updaterlibrary.fileprovider", file);
                LogUtils.debug("packageName==" + packageName);
                LogUtils.debug("providerUri==" + providerUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(providerUri, "application/vnd.android.package-archive");
	    }
        } else {
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
