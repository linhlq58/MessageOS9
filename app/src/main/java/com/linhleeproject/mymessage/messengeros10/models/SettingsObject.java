package com.linhleeproject.mymessage.messengeros10.models;

/**
 * Created by Linh Lee on 12/5/2016.
 */
public class SettingsObject {
    private int imgRes;
    private String name;

    public SettingsObject(int imgRes, String name) {
        this.imgRes = imgRes;
        this.name = name;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
