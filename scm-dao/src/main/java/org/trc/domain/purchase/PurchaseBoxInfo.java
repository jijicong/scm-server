package org.trc.domain.purchase;

import org.hibernate.validator.constraints.Length;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

/**
 * Created by hzcyn on 2018/7/24.
 */
public class PurchaseBoxInfo extends BaseDO {

    private static final long serialVersionUID = 1L;

    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //主键
    private Long id;

    @FormParam("purchaseDetailId")
    //采购单详情主键
    private Long purchaseDetailId;

    @FormParam("purchaseOrderCode")
    //采购单单号
    private String purchaseOrderCode;

    @FormParam("amountPerBox")
    //每箱数量
    private Long amountPerBox;

    @FormParam("boxNumber")
    @Length(max = 15, message = "箱号字母和数字不能超过15个")
    //箱号
    private String boxNumber;

    @FormParam("boxAmount")
    //箱数
    private Long boxAmount;

    @FormParam("amount")
    //总数
    private Long amount;

    @FormParam("grossWeight")
    //毛重
    private Long grossWeight;

    @FormParam("cartonSize")
    @Length(max = 50, message = "外箱尺寸字母和数字不能超过50个")
    //外箱尺寸
    private String cartonSize;

    @FormParam("volume")
    //体积
    private Long volume;

    @FormParam("remark")
    @Length(max = 100, message = "备注字母和数字不能超过100个")
    //备注
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPurchaseDetailId() {
        return purchaseDetailId;
    }

    public void setPurchaseDetailId(Long purchaseDetailId) {
        this.purchaseDetailId = purchaseDetailId;
    }

    public String getPurchaseOrderCode() {
        return purchaseOrderCode;
    }

    public void setPurchaseOrderCode(String purchaseOrderCode) {
        this.purchaseOrderCode = purchaseOrderCode;
    }

    public Long getAmountPerBox() {
        return amountPerBox;
    }

    public void setAmountPerBox(Long amountPerBox) {
        this.amountPerBox = amountPerBox;
    }

    public String getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(String boxNumber) {
        this.boxNumber = boxNumber;
    }

    public Long getBoxAmount() {
        return boxAmount;
    }

    public void setBoxAmount(Long boxAmount) {
        this.boxAmount = boxAmount;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(Long grossWeight) {
        this.grossWeight = grossWeight;
    }

    public String getCartonSize() {
        return cartonSize;
    }

    public void setCartonSize(String cartonSize) {
        this.cartonSize = cartonSize;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
