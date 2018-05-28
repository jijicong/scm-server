package org.trc.form.warehouse.allocateOrder;

import java.util.List;

import org.trc.form.warehouse.ScmDeliveryOrderDO;
import org.trc.form.warehouse.ScmWarehouseRequestBase;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class ScmAllocateOrderOutRequest  extends ScmWarehouseRequestBase{

    /**
	 * 
	 */
	private static final long serialVersionUID = 164535272175184562L;

	/**
     * 京东调拨单请求数据列表
     */
    private List<ScmDeliveryOrderDO> scmDeleveryOrderDOList;
    
    /**
     * 调拨出库单编号
     */
    private String allocateOutOrderCode;

    /**
     * 调拨单编号
     */
    private String allocateOrderCode;

    /**
     * 调入仓库编码
     */
    private String inWarehouseCode;
    
    /**
     * 出库备注
     */
    private String outMemo;

    /**
     * 调出仓库编码
     */
    private String outWarehouseCode;
    
    /**
     * 创建人
     */
    private String createOperator;
    
    /**
     * 出库单联系人号码
     */
    private String createOperatorNumber;

    /**
     * 出库单联系人
     */
    private String createOperatorName;

    /**
     * 收货人
     */
    private String receiver;

    /**
     * 收货人手机
     */
    private String receiverMobile;

    /**
     * 收货地址
     */
    private String receiverAddress;

    /**
     * 收货人所在省
     */
    private String receiverProvince;

    /**
     * 收货人所在城市
     */
    private String receiverCity;

    /**
     * 收货人所在区
     */
    private String receiverDistrict;
    
    /**
     * 商品列表
     */
    List<ScmAllocateOrderItem> allocateOrderItemList;

    private String receiverProvinceName;

    private String receiverCityName;

    private String senderProvinceName;

    private String senderCityName;


}
