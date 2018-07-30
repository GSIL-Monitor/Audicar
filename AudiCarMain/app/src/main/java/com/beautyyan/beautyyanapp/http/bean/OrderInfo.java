package com.beautyyan.beautyyanapp.http.bean;

import java.io.Serializable;

/**
 * Created by xuelu on 2017/5/19.
 */

public class OrderInfo implements Serializable {
    private String userId;
    private String phone;
    private String consignee;//购买人姓名
    private String certId;//购买人身份证
    private double orderAmount;//订单金额
    private String platCouponId;//平台优惠券id
    private String shopCouponId;//店铺优惠券id
    private String goodType;//商品类型
    private String releaseNumber;//车源ID
    private String activityId;//活动id

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getCertId() {
        return certId;
    }

    public void setCertId(String certId) {
        this.certId = certId;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getPlatCouponId() {
        return platCouponId;
    }

    public void setPlatCouponId(String platCouponId) {
        this.platCouponId = platCouponId;
    }

    public String getShopCouponId() {
        return shopCouponId;
    }

    public void setShopCouponId(String shopCouponId) {
        this.shopCouponId = shopCouponId;
    }

    public String getGoodType() {
        return goodType;
    }

    public void setGoodType(String goodType) {
        this.goodType = goodType;
    }

    public String getReleaseNumber() {
        return releaseNumber;
    }

    public void setReleaseNumber(String releaseNumber) {
        this.releaseNumber = releaseNumber;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }
}
