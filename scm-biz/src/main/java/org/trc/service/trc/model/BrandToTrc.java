package org.trc.service.trc.model;

/**
 * Created by hzdzf on 2017/5/23.
 */
public class BrandToTrc {

    private String alise;//品牌别名

    private String brandCode;//品牌编码

    private String createOperator; //创建人

    private String isDeleted; //是否删除:0-否,1-是

    private String isValid;

    private String lastEditOperator;//最新更新人

    private String logo;//品牌LOGO的图片路径

    private String name;//品牌名称

    private String sort;//序号

    private String source;//来源:scm-系统自行添加，trc-泰然城导入

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

    public String getCreateOperator() {
        return createOperator;
    }

    public void setCreateOperator(String createOperator) {
        this.createOperator = createOperator;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getLastEditOperator() {
        return lastEditOperator;
    }

    public void setLastEditOperator(String lastEditOperator) {
        this.lastEditOperator = lastEditOperator;
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

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
}
