package org.trc.domain.jingDong;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.ws.rs.PathParam;

/**
 * Created by sone on 2017/6/19.
 */
@Table(name="mapping_table")
public class JingDongArea {

    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //'行政编码'
    private String areaCode;
    //'省名称'
    private String province;
    //'市名称'
    private String city;
    //'区县名称'
    private String district;
    //镇
    private String town;
    //'京东编码'
    private String jdCode;
    //父id
    private Long parent;

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getJdCode() {
        return jdCode;
    }

    public void setJdCode(String jdCode) {
        this.jdCode = jdCode;
    }
}
