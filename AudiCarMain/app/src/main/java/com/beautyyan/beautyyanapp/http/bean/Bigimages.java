package com.beautyyan.beautyyanapp.http.bean;

import java.io.Serializable;

/**
 * Created by xuelu on 2017/5/10.
 */

public class Bigimages implements Serializable {
    private String attachFlag;
    private String path;
    public void setAttachflag(String attachflag) {
        this.attachFlag = attachflag;
    }
    public String getAttachflag() {
        return attachFlag;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public String getPath() {
        return path;
    }
}
