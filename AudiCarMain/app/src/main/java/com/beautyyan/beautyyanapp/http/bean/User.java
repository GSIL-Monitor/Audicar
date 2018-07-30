package com.beautyyan.beautyyanapp.http.bean;

import java.io.Serializable;

/**
 * Created by xuelu on 2017/5/13.
 */

public class User implements Serializable {
    public String userId = "-1";
    public String phone= "-1";

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

}
