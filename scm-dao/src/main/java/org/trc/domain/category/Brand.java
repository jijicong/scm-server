package org.trc.domain.category;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.*;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;


/**
 * Created by hzqph on 2017/4/27.
 */
@Table(name = "brand")
public class Brand extends BaseDO {

    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//主键ID
    @FormParam("name")
    @NotEmpty
    @Length(max = 50, message = "品牌名称不得超过50个字符")
    private String name;//品牌名称
    @FormParam("brandCode")
    private String brandCode;//品牌编码
    @FormParam("source")
    private String source;//来源:scm-系统自行添加，tairan-泰然城导入
    @FormParam("alise")
    @Length(max = 50, message = "品牌别名不得超过50个字符")
    private String alise;//品牌别名
    @FormParam("webUrl")
    @Length(max = 200, message = "品牌网址不得超过50个字符")
    private String webUrl;//品牌网址
    @FormParam("logo")
    private String logo;//品牌LOGO的图片路径
    @FormParam("sort")
    private Integer sort;//序号
    @FormParam("lastEditOperator")
    private String lastEditOperator;//最新更新人
    @Transient
    private int num;//

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAlise() {
        return alise;
    }

    public void setAlise(String alise) {
        this.alise = alise;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getLastEditOperator() {
        return lastEditOperator;
    }

    public void setLastEditOperator(String lastEditOperator) {
        this.lastEditOperator = lastEditOperator;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }


}
