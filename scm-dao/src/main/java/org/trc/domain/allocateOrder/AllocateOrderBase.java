package org.trc.domain.allocateOrder;

import org.hibernate.validator.constraints.Length;
import org.trc.domain.util.CommonDO;
import javax.persistence.Column;
import javax.persistence.Transient;

public class AllocateOrderBase extends CommonDO{

    /**
     * 调入仓库编码
     */
    @Length(max = 32, message = "调入仓库编码不得超过32个字符")
    @Column(name = "in_warehouse_code")
    private String inWarehouseCode;

    /**
     * 调入仓库名称
     */
    @Transient
    private String inWarehouseName;

    /**
     * 调出仓库编码
     */
    @Length(max = 32, message = "调出仓库编码不得超过32个字符")
    @Column(name = "out_warehouse_code")
    private String outWarehouseCode;

    /**
     * 调出仓库名称
     */
    @Transient
    private String outWarehouseName;

    /**
     * 出库单创建人
     */
    @Transient
    private String createOperatorName;

    public String getInWarehouseCode() {
        return inWarehouseCode;
    }

    public void setInWarehouseCode(String inWarehouseCode) {
        this.inWarehouseCode = inWarehouseCode;
    }

    public String getInWarehouseName() {
        return inWarehouseName;
    }

    public void setInWarehouseName(String inWarehouseName) {
        this.inWarehouseName = inWarehouseName;
    }

    public String getOutWarehouseCode() {
        return outWarehouseCode;
    }

    public void setOutWarehouseCode(String outWarehouseCode) {
        this.outWarehouseCode = outWarehouseCode;
    }

    public String getOutWarehouseName() {
        return outWarehouseName;
    }

    public void setOutWarehouseName(String outWarehouseName) {
        this.outWarehouseName = outWarehouseName;
    }

    public String getCreateOperatorName() {
        return createOperatorName;
    }

    public void setCreateOperatorName(String createOperatorName) {
        this.createOperatorName = createOperatorName;
    }

}
