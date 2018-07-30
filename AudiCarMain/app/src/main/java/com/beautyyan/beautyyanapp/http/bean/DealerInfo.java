package com.beautyyan.beautyyanapp.http.bean;

import java.io.Serializable;

/**
 * Created by xuelu on 2017/5/12.
 */

public class DealerInfo implements Serializable {
    private String dsId;
    private String shopHomeNick;
    private String shopHomeAddress;
    private String dealerCode;
    private String rank;

    public String getDsId() {
        return dsId;
    }

    public void setDsId(String dsId) {
        this.dsId = dsId;
    }

    public String getShopHomeNick() {
        return shopHomeNick;
    }

    public void setShopHomeNick(String shopHomeNick) {
        this.shopHomeNick = shopHomeNick;
    }

    public String getShopHomeAddress() {
        return shopHomeAddress;
    }

    public void setShopHomeAddress(String shopHomeAddress) {
        this.shopHomeAddress = shopHomeAddress;
    }

    public String getDealerCode() {
        return dealerCode;
    }

    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getOnSellCarCount() {
        return onSellCarCount;
    }

    public void setOnSellCarCount(String onSellCarCount) {
        this.onSellCarCount = onSellCarCount;
    }

    public String getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(String activityCount) {
        this.activityCount = activityCount;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getHeadImgPath() {
        return headImgPath;
    }

    public void setHeadImgPath(String headImgPath) {
        this.headImgPath = headImgPath;
    }

    private String score;
    private String onSellCarCount;
    private String activityCount;
    private String longitude;
    private String latitude;
    private String headImgPath;
}
