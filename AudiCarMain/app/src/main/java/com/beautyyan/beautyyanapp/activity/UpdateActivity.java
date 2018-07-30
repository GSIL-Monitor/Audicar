package com.beautyyan.beautyyanapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.utils.LogUtil;
import com.beautyyan.beautyyanapp.utils.SharePreferUtil;
import com.beautyyan.beautyyanapp.utils.ToastUtils;
import com.beautyyan.beautyyanapp.utils.Util;
import com.beautyyan.beautyyanapp.utils.updater.ProgressListener;
import com.beautyyan.beautyyanapp.utils.updater.Updater;
import com.umeng.analytics.MobclickAgent;

import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by apple on 2017/7/8.
 */

public class UpdateActivity extends Activity implements View.OnClickListener , EasyPermissions.PermissionCallbacks{

    private static UpdateActivity instance;

    //是否强制更新，1是强制更新，0不是强制更新
    private int appForceUpdate = 1;

    private String appSize = null;

    private String appIntroduction = null;

    private String appVersion = null;

    private String appDownloadUrl = null;


    //显示更新版本的弹框界面
    private RelativeLayout viewUpdate;
    //显示正在下载的界面
    private RelativeLayout viewDownloading;

    //
    private TextView tvContent;
    //
    private TextView tvDownloadingProgress;

    //左边放弃更新的按钮
    private Button btnCancel;
    //右边立即更新的按钮
    private Button btnSure;
    //整一行的立即更新按钮
    private Button btnSureFull;

    private String fileName;
    private Updater updater;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acti_update);

        instance = this;

        //获取上一界面传递过来的"是否强制更新"数据
        appForceUpdate = getIntent().getIntExtra(Constant.APP_FORCE_UPDATE,1);
        appSize = getIntent().getStringExtra(Constant.APP_SIZE);
        appIntroduction = getIntent().getStringExtra(Constant.APP_INTRODUCTION);
        appVersion = getIntent().getStringExtra(Constant.APP_VERSION);
        appDownloadUrl = getIntent().getStringExtra(Constant.APP_DOWNLOAD_URL);

        LogUtil.i("+++++" + appForceUpdate + "+++++" + appSize + "++++" + appIntroduction + "+++++++" + appDownloadUrl + "+++++++");

        initView();
        initListener();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {

        viewUpdate = (RelativeLayout)this.findViewById(R.id.rl_update);
        viewDownloading = (RelativeLayout)this.findViewById(R.id.rl_downloading);

        tvContent = (TextView)this.findViewById(R.id.tv_update_content);
        tvDownloadingProgress = (TextView)this.findViewById(R.id.tv_downloading_progress);

        btnCancel = (Button)this.findViewById(R.id.btn_update_cancel);
        btnSure = (Button)this.findViewById(R.id.btn_update_sure);
        btnSureFull = (Button)this.findViewById(R.id.btn_update_sure_full);

        if (!Util.isEmpty(appIntroduction)) {
            tvContent.setText(appIntroduction);
        }

        //根据是否强制更新，进行界面调整，1是强制更新，0不是强制更新
        if (appForceUpdate == 0) {
            btnCancel.setVisibility(View.VISIBLE);
            btnSure.setVisibility(View.VISIBLE);
            btnSureFull.setVisibility(View.GONE);
        }else{
            btnCancel.setVisibility(View.GONE);
            btnSure.setVisibility(View.GONE);
            btnSureFull.setVisibility(View.VISIBLE);
        }

    }

    private void initListener() {

        btnCancel.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        btnSureFull.setOnClickListener(this);
    }

    public static UpdateActivity getInstance() {
        return instance;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update_cancel:
                //忽略此版本
                int versionIgnore = Integer.parseInt(appVersion);
                if (versionIgnore == 0) {
                    versionIgnore = 1;
                }
                SharePreferUtil su = new SharePreferUtil(com.beautyyan.beautyyanapp.activity.UpdateActivity.this);
                su.setVersionIgnore(versionIgnore);

                finish();
                break;
            case R.id.btn_update_sure:
            case R.id.btn_update_sure_full:
                if (!Util.isEmpty(appDownloadUrl)) {
                    //
                    fileName = "AudiApp_" + appVersion + "_" + (new Date()).getTime() + ".apk";

                    try {
                        //http://gdown.baidu.com/data/wisegame/41a04ccb443cd61a/QQ_692.apk
                        updater = new Updater.Builder(UpdateActivity.this)
                                .setDownloadUrl(appDownloadUrl)
//                                .setDownloadUrl("http://gdown.baidu.com/data/wisegame/593138c8a18ec9d6/QQkongjian_102.apk")//仅供测试
                                .setApkFileName(fileName)
                                .setNotificationTitle(getString(R.string.str_downloading_noti_title))
                                .start();
                        updater.addProgressListener(new ProgressListener() {
                            @Override
                            public void onProgressChange(long totalBytes, long curBytes, int progress) {

                            LogUtil.i(".....curBytes:"+curBytes+".......totalBytes:"+totalBytes+"......progress:"+progress+"........");

                                //要监听下载进度完成后，进行操作，请前往
                                if (progress < 0) {
                                    tvDownloadingProgress.setText("");
                                }else{//获取下载进度失败
                                    //正在下载
                                    tvDownloadingProgress.setText(progress + "%");
                                }

                            }
                        });
                        //隐藏选择弹框，显示进度框
                        viewUpdate.setVisibility(View.GONE);
                        viewDownloading.setVisibility(View.VISIBLE);
                    }catch (Exception e) {
                        ToastUtils.showCenterShort(UpdateActivity.this,getString(R.string.str_download_fail));
                    }
                }else{
                    ToastUtils.showCenterLong(UpdateActivity.this,getString(R.string.str_update_fail));
                }
                break;
            default:
                break;
        }
    }

//    private void checkDownloadPermission(){
//
//        //所要申请的权限
//        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//
//        if (EasyPermissions.hasPermissions(this, perms)) {//检查是否获取该权限
//
//            startDownload();
//
//        } else {
//            //第二个参数是被拒绝后再次申请该权限的解释
//            //第三个参数是请求码
//            //第四个参数是要申请的权限
//            EasyPermissions.requestPermissions(this, "更新需要的必要权限", 0, perms);
//        }
//
//    }

    /**
     * 请求权限回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (updater != null) {
            updater.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }
    }

    /**
     * 请求权限通过
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        LogUtil.i("onPermissionsGranted");
        if (updater != null) {
            updater.onPermissionsGranted(requestCode,perms);
        }
    }

    /**
     * 请求权限拒绝
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        LogUtil.i("onPermissionsDenied");
        if (updater != null) {
            updater.onPermissionsDenied(requestCode,perms);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!Constant.getInstance().isAppOnForeground()) {
            Constant.getInstance().setActive(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (!Constant.getInstance().isActive()) {
            Constant.getInstance().setActive(true);
            Constant.getInstance().getAudiBi(UpdateActivity.this);
        }
    }
}
