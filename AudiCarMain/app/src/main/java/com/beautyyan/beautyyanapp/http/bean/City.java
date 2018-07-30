package com.beautyyan.beautyyanapp.http.bean;

import java.io.Serializable;

/**
 * Created by xuelu on 2017/4/27.
 */

public class City implements Serializable {
    private String cityName = "";
    private String cityId = "";

    public City() {

    }

    public City(String cityName, String cityId) {

        this.cityName = cityName;
        this.cityId = cityId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

}
