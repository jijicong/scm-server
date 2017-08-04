package org.trc.domain.util;

import javax.persistence.Id;
import java.io.Serializable;

/**
 * 地区信息
 * Created by sone on 2017/5/6.
 */
public class Area implements Serializable {
    @Id
    private Long id;
    private String code;
    private String province;
    private String city;
    private String district;
    private Long parent;
    public Area(){}
    public Area(Long parent){
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
        return "Area{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", parent=" + parent +
                '}';
    }
}
