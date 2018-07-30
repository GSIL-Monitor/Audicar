package com.beautyyan.beautyyanapp.http.bean;

import java.io.Serializable;

/**
 * Created by xuelu on 2017/5/3.
 */

public class CarType implements Serializable {

    private String name;
    private int drawId;
    private String carKind;

    public String getCarKind() {
        return carKind;
    }

    public void setCarKind(String carKind) {
        this.carKind = carKind;
    }

    public int getDrawId() {
        return drawId;
    }

    public void setDrawId(int drawId) {
        this.drawId = drawId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
