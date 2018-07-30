package com.beautyyan.beautyyanapp.http.bean;

import java.io.Serializable;

/**
 * Created by xuelu on 2017/5/11.
 */

public class Banner implements Serializable {
    private String bannerId;
    private String bannerPath;
    private String bannerOrder;
    private String bannerClickPath;
    private String bannerDesc;
    private int drawId;

    public int getDrawId() {
        return drawId;
    }

    public void setDrawId(int drawId) {
        this.drawId = drawId;
    }


    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getBannerPath() {
        return bannerPath;
    }

    public void setBannerPath(String bannerPath) {
        this.bannerPath = bannerPath;
    }

    public String getBannerOrder() {
        return bannerOrder;
    }

    public void setBannerOrder(String bannerOrder) {
        this.bannerOrder = bannerOrder;
    }

    public String getBannerClickPath() {
        return bannerClickPath;
    }

    public void setBannerClickPath(String bannerClickPath) {
        this.bannerClickPath = bannerClickPath;
    }

    public String getBannerDesc() {
        return bannerDesc;
    }

    public void setBannerDesc(String bannerDesc) {
        this.bannerDesc = bannerDesc;
    }
}
