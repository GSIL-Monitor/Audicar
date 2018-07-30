package com.beautyyan.beautyyanapp.http.bean;

import java.io.Serializable;

/**
 * Created by x_luthy on 2017/7/2.
 */

public class CaptchDetil implements Serializable {
    private String imageFlow;
    private String sercertCode;

    public String getImageFlow() {
        return imageFlow;
    }

    public void setImageFlow(String imageFlow) {
        this.imageFlow = imageFlow;
    }

    public String getSercertCode() {
        return sercertCode;
    }

    public void setSercertCode(String sercertCode) {
        this.sercertCode = sercertCode;
    }
}
