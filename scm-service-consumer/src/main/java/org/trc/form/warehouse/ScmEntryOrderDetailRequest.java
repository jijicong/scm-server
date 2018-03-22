package org.trc.form.warehouse;

import java.util.Date;

public class ScmEntryOrderDetailRequest extends ScmWarehouseRequestBase{

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 货主编码
     */
    private String ownerCode;

    /**
     * 入库单号
     */
    private String entryOrderCode;

    /**
     * 仓储系统入库单ID
     */
    private String entryOrderId;

    /**
     * 入库单类型
     */
    private String ntryOrderType;

    /**
     * 入库单状态
     */
    private String status;

    /**
     * 采购入库单入库状态
     */
    private String storageStatus;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 入库单生成时间
     */
    private Date createTime;

    /**
     * 供应商编号
     */
    private String supplierNo;

    /**
     * 物流开放平台采购单号
     */
    private String poOrderNo;

    /**
     * 采购入库单创建人
     */
    private String createUser;







}
