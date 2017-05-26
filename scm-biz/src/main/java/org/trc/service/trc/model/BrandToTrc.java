package org.trc.service.trc.model;

import javax.ws.rs.MatrixParam;

/**
 * 品牌
 * Created by hzdzf on 2017/5/23.
 */
public class BrandToTrc {

    @MatrixParam("alise")
    private String alise;//品牌别名

    private String brandCode;//品牌编码

    private String isValid;

    private String logo;//品牌LOGO的图片路径

    private String name;//品牌名称

    private String webUrl;//品牌网址

    public String getAlise() {
        return alise;
    }

    public void setAlise(String alise) {
        this.alise = alise;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
}
