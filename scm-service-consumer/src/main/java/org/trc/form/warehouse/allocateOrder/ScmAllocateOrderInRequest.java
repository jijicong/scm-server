package org.trc.form.warehouse.allocateOrder;

import java.util.Date;
import java.util.List;

import org.trc.form.warehouse.ScmEntryOrderItem;
import org.trc.form.warehouse.ScmWarehouseRequestBase;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class ScmAllocateOrderInRequest extends ScmWarehouseRequestBase{

    /**
	 * 
	 */
	private static final long serialVersionUID = -6243477722939078819L;

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
    private Date expectStartTime;

    /**
     * 最迟预期到货时间
     */
    private Date expectEndTime;

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
    
    /************************
     * ***********************
     *************************/
    
    /**
     * 调拨入库单编号
     */
    private String allocateInOrderCode;

    /**
     * 调拨单编号
     */
    private String allocateOrderCode;

    /**
     * 调入仓库编码
     */
    private String inWarehouseCode;

    /**
     * 调入仓库名称
     */
    private String inWarehouseName;

    /**
     * 调出仓库编码
     */
    private String outWarehouseCode;

    /**
     * 调出仓库名称
     */
    private String outWarehouseName;

    /**
     * 接收人
     */
    private String receiver;

    /**
     * 接收人省
     */
//    private String reciverProvince;

    /**
     * 接收人城市
     */
    //private String reciverCity;

    /**
     * 接收人详细地址
     */
    private String receiverAddress;

    /**
     * 接收人手机
     */
    private String receiverMobile;

    /**
     * 发送人
     */
    private String sender;

    /**
     * 发送人省
     */
    //private String senderProvince;

    /**
     * 发送人城市
     */
    //private String senderCity;

    /**
     * 发送人手机
     */
  //  private String senderMobile;

    /**
     * 发送人详细地址
     */
    private String senderAddress;

    /**
     * 备注
     */
    private String memo;

	/**
	 * 入库单联系人号码
	 */
    private String createOperatorNumber;

    /**
     * 入库单联系人
     */
    private String createOperatorName;


    private String receiverProvinceName;

    private String receiverCityName;

    private String senderProvinceName;

    private String senderCityName;

    private String senderDistrictName;

    private String receiverDistrictName;
}
