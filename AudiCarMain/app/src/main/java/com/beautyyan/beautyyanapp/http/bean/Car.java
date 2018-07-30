package com.beautyyan.beautyyanapp.http.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xuelu on 2017/5/10.
 */

public class Car implements Serializable {
    private String agcyAddress;
    private String agcyName;
    private String area;
    private List<Bigimages> bigImages;
    private String carAir = "";
    private String carModel = "";
    private int deposit;
    private String emissionStandards;
    private String exteriorColor;
    private String id;
    private String loginId;
    private String mileage;
    private String modelLine;
    private String modelName;
    private String price;
    private String tagIds;
    private String registration;
    private String manufacture;
    private String transmission;
    private String modelCode = "";
    private String shareOfficial;
    private String shareLogo;
    private String deviceType = "";

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getShareOfficial() {
        return shareOfficial;
    }

    public void setShareOfficial(String shareOfficial) {
        this.shareOfficial = shareOfficial;
    }

    public String getShareLogo() {
        return shareLogo;
    }

    public void setShareLogo(String shareLogo) {
        this.shareLogo = shareLogo;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getTagIds() {
        return tagIds;
    }

    public void setTagIds(String tagIds) {
        this.tagIds = tagIds;
    }


    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getAgcyAddress() {
        return agcyAddress;
    }

    public void setAgcyAddress(String agcyAddress) {
        this.agcyAddress = agcyAddress;
    }

    public String getAgcyName() {
        return agcyName;
    }

    public void setAgcyName(String agcyName) {
        this.agcyName = agcyName;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public List<Bigimages> getBigImages() {
        return bigImages;
    }

    public void setBigImages(List<Bigimages> bigImages) {
        this.bigImages = bigImages;
    }

    public String getCarAir() {
        return carAir;
    }

    public void setCarAir(String carAir) {
        this.carAir = carAir;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public int getDeposit() {
        return deposit;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    public String getEmissionStandards() {
        return emissionStandards;
    }

    public void setEmissionStandards(String emissionStandards) {
        this.emissionStandards = emissionStandards;
    }

    public String getExteriorColor() {
        return exteriorColor;
    }

    public void setExteriorColor(String exteriorColor) {
        this.exteriorColor = exteriorColor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getModelLine() {
        return modelLine;
    }

    public void setModelLine(String modelLine) {
        this.modelLine = modelLine;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getReleaseNumber() {
        return releaseNumber;
    }

    public void setReleaseNumber(String releaseNumber) {
        this.releaseNumber = releaseNumber;
    }

    public Smallimage getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(Smallimage smallImage) {
        this.smallImage = smallImage;
    }

    private String releaseNumber;
    private Smallimage smallImage;
}
