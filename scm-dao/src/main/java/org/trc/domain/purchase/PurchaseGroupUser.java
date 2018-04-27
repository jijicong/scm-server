package org.trc.domain.purchase;

import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

/**
 * Created by hzcyn on 2018/4/26.
 */
public class PurchaseGroupUser extends BaseDO {
    @Id
    @PathParam("id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("name")
    private String name;
    @FormParam("phoneNumber")
    private String phoneNumber;
    @Transient
    @FormParam("gridValue")
    private String gridValue;
    @Transient
    private Integer status;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGridValue() {
        return gridValue;
    }

    public void setGridValue(String gridValue) {
        this.gridValue = gridValue;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
