package org.trc.form.warehouse;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class ScmEntryOrderCreateRequest  extends ScmWarehouseRequestBase{

    /**
	 * 
	 */
	private static final long serialVersionUID = -3957519656766115380L;

	/**
     * 入库单号
     */
    private String entryOrderCode;

    /**
     * 采购单号
     */
    private String purchaseOrderCode;

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 货主编码
     */
    private String ownerCode;

    /**
     * 业务类型
     */
    private String orderType;

    /**
     * 运单号
     */
    private String expressCode;

    /**
     * 订单创建时间
     */
    private Date orderCreateTime;

    /**
     * 预期到货时间
     */
    private String expectStartTime;

    /**
     * 最迟预期到货时间
     */
    private String expectEndTime;

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 发件人姓名
     */
    private String senderName;

    /**
     * 发件人移动电话
     */
    private String senderMobile;

    /**
     * 发件人省份
     */
    private String senderProvince;

    /**
     * 发件人城市
     */
    private String senderCity;

    /**
     * 发件人详细地址
     */
    private String senderDetailAddress;

    /**
     * 收件人姓名
     */
    private String reciverName;

    /**
     * 收件人移动电话
     */
    private String reciverMobile;

    /**
     * 收件人省份
     */
    private String reciverProvince;

    /**
     * 收件人城市
     */
    private String reciverCity;

    /**
     * 收件人详细地址
     */
    private String reciverDetailAddress;

    /**
     * 备注
     */
    private String remark;

    /**
     * 单据类型
     */
    private String billType;

    /**
     * 采购单类型
     */
    private String poType;

    /**
     * 提货单号
     */
    private String billOfLading;

    /**
     * 入库单商品
     */
    private List<ScmEntryOrderItem> entryOrderItemList;
    
    /**
     * 入库单联系人号码
     */
    private String createOperatorNumber;

    /**
     * 入库单联系人
     */
    private String createOperatorName;
}
