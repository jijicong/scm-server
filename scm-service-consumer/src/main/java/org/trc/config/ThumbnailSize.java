package org.trc.config;

/**
 * 缩略图尺寸
 * Created by hzwdx on 2017/5/4.
 */
public class ThumbnailSize {

    /**
     * 宽度
     */
    private Integer width;
    /**
     * 高度
     */
    private Integer height;

    /**
     * 是否启用:0-否，1-是
     */
    private String isValid;

    public ThumbnailSize(){

    }

    public ThumbnailSize(Integer width, Integer height, String isValid){
        this.width = width;
        this.height = height;
        this.isValid = isValid;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }
}
