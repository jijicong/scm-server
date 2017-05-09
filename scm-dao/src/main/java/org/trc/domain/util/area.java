package org.trc.domain.util;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 地区信息
 * Created by sone on 2017/5/6.
 */
public class area implements Serializable {
    @Id
    private Long id;
    private String code;
    //三个拼成一个areaName
    private String province;
    private String city;
    private String district;
    private Long parent;

    public area(){}
    public area(Long parent){
        this.parent=parent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "area{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", parent=" + parent +
                '}';
    }
}
