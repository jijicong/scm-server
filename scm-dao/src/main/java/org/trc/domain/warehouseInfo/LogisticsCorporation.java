package org.trc.domain.warehouseInfo;

import org.hibernate.validator.constraints.Length;
import org.trc.domain.BaseDO;
import org.trc.domain.util.CommonDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

/**
 * Created by hzcyn on 2018/5/3.
 */
public class LogisticsCorporation extends CommonDO {
    //主键
    @Id
    @PathParam("id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //物流公司编号
    @FormParam("logisticsCode")
    @Length(max = 5, message = "物流公司编号不能超过5个字节")
    private String logisticsCode;

    //物流公司名称
    @FormParam("logisticsCorporationName")
    @Length(max = 100, message = "物流公司名称不能超过100个字节")
    private String logisticsCorporationName;

//    //物流公司编码
//    @FormParam("logisticsCorporationCode")
//    @Length(max = 10, message = "物流公司编码不能超过10个字节")
//    private String logisticsCorporationCode;

    //物流公司类型
    @FormParam("logisticsCorporationType")
    @Length(max = 2, message = "物流公司类型不能超过2个字节")
    private String logisticsCorporationType;

    //联系人
    @FormParam("contacts")
    @Length(max = 20, message = "联系人不能超过20个字节")
    private String contacts;

    //联系人电话
    @FormParam("contactsNumber")
    @Length(max = 16, message = "联系人电话不能超过16个字节")
    private String contactsNumber;

    //备注
    @FormParam("remark")
    @Length(max = 255, message = "备注不能超过255个字节")
    private String remark;

    @FormParam("isValid")
    private int isValid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    public String getLogisticsCorporationName() {
        return logisticsCorporationName;
    }

    public void setLogisticsCorporationName(String logisticsCorporationName) {
        this.logisticsCorporationName = logisticsCorporationName;
    }

//    public String getLogisticsCorporationCode() {
//        return logisticsCorporationCode;
//    }
//
//    public void setLogisticsCorporationCode(String logisticsCorporationCode) {
//        this.logisticsCorporationCode = logisticsCorporationCode;
//    }

    public String getLogisticsCorporationType() {
        return logisticsCorporationType;
    }

    public void setLogisticsCorporationType(String logisticsCorporationType) {
        this.logisticsCorporationType = logisticsCorporationType;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getContactsNumber() {
        return contactsNumber;
    }

    public void setContactsNumber(String contactsNumber) {
        this.contactsNumber = contactsNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }
}
