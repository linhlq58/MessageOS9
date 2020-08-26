package com.linhleeproject.mymessage.messengeros10.models;

/**
 * Created by Linh Lee on 12/6/2016.
 */
public class ThemeObject {
    private int id;
    private int bgPreview;
    private int bgRes;
    private String solidColor;
    private String strokeColor;
    private String statusBarColor;
    private String keyshadowColor;
    private String keyshadowColorTrans;

    public ThemeObject() {
    }

    public ThemeObject(int id, int bgPreview, int bgRes, String solidColor, String strokeColor, String statusBarColor, String keyshadowColor, String keyshadowColorTrans) {
        this.id = id;
        this.bgPreview = bgPreview;
        this.bgRes = bgRes;
        this.solidColor = solidColor;
        this.strokeColor = strokeColor;
        this.statusBarColor = statusBarColor;
        this.keyshadowColor = keyshadowColor;
        this.keyshadowColorTrans = keyshadowColorTrans;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBgPreview() {
        return bgPreview;
    }

    public void setBgPreview(int bgPreview) {
        this.bgPreview = bgPreview;
    }

    public int getBgRes() {
        return bgRes;
    }

    public void setBgRes(int bgRes) {
        this.bgRes = bgRes;
    }

    public String getSolidColor() {
        return solidColor;
    }

    public void setSolidColor(String solidColor) {
        this.solidColor = solidColor;
    }

    public String getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(String strokeColor) {
        this.strokeColor = strokeColor;
    }

    public String getStatusBarColor() {
        return statusBarColor;
    }

    public void setStatusBarColor(String statusBarColor) {
        this.statusBarColor = statusBarColor;
    }

    public String getKeyshadowColor() {
        return keyshadowColor;
    }

    public void setKeyshadowColor(String keyshadowColor) {
        this.keyshadowColor = keyshadowColor;
    }

    public String getKeyshadowColorTrans() {
        return keyshadowColorTrans;
    }

    public void setKeyshadowColorTrans(String keyshadowColorTrans) {
        this.keyshadowColorTrans = keyshadowColorTrans;
    }
}
