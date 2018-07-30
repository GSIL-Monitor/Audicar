package com.beautyyan.beautyyanapp.http.bean;

import java.io.Serializable;

/**
 * Created by xuelu on 2017/5/12.
 */

public class Dealer implements Serializable {
    private int page;
    private int totalRecords;
    private String dsId;
    private String shopHomeNick;
    private String shopHomeAddress;
    private String dealerCode;
    private int rank;
    private double score;
    private String onSellCarCount;
    private String activityCount;
    private String longitude;
    private String latitude;
    private String headImgPath;

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

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public DealerInfo getDealerInfo() {
        return dealerInfo;
    }

    public void setDealerInfo(DealerInfo dealerInfo) {
        this.dealerInfo = dealerInfo;
    }

    private DealerInfo dealerInfo;


}
