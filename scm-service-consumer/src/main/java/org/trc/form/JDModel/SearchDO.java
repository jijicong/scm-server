package org.trc.form.JDModel;

/**
 * Created by hzwyz on 2017/5/23 0023.
 */
public class SearchDO {

    //授权时获取的access token
    private String token;

    //搜索关键字
    private String keyword;

    //分类ID
    private String catId;

    //当前第几页
    private String pageIndex;

    //每页大小
    private String pageSize;

    //价格区间，低价
    private String min;

    //价格区间，高价
    private String max;

    //品牌
    private String brands;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(String pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getBrands() {
        return brands;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }
}
