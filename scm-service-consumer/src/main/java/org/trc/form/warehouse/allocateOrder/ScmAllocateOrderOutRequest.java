package org.trc.form.warehouse.allocateOrder;

import java.util.List;

import org.trc.form.warehouse.ScmDeliveryOrderDO;
import org.trc.form.warehouse.ScmWarehouseRequestBase;
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

    private String inWarehouseName;
    
    /**
     * 出库备注
     */
    private String outMemo;

    /**
     * 调出仓库编码
     */
    private String outWarehouseCode;

    private String outWarehouseName;
    
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

	public List<ScmDeliveryOrderDO> getScmDeleveryOrderDOList() {
		return scmDeleveryOrderDOList;
	}

	public void setScmDeleveryOrderDOList(List<ScmDeliveryOrderDO> scmDeleveryOrderDOList) {
		this.scmDeleveryOrderDOList = scmDeleveryOrderDOList;
	}

	public String getAllocateOutOrderCode() {
		return allocateOutOrderCode;
	}

	public void setAllocateOutOrderCode(String allocateOutOrderCode) {
		this.allocateOutOrderCode = allocateOutOrderCode;
	}

	public String getAllocateOrderCode() {
		return allocateOrderCode;
	}

	public void setAllocateOrderCode(String allocateOrderCode) {
		this.allocateOrderCode = allocateOrderCode;
	}

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

	public String getOutMemo() {
		return outMemo;
	}

	public void setOutMemo(String outMemo) {
		this.outMemo = outMemo;
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

	public String getCreateOperator() {
		return createOperator;
	}

	public void setCreateOperator(String createOperator) {
		this.createOperator = createOperator;
	}

	public String getCreateOperatorNumber() {
		return createOperatorNumber;
	}

	public void setCreateOperatorNumber(String createOperatorNumber) {
		this.createOperatorNumber = createOperatorNumber;
	}

	public String getCreateOperatorName() {
		return createOperatorName;
	}

	public void setCreateOperatorName(String createOperatorName) {
		this.createOperatorName = createOperatorName;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getReceiverMobile() {
		return receiverMobile;
	}

	public void setReceiverMobile(String receiverMobile) {
		this.receiverMobile = receiverMobile;
	}

	public String getReceiverAddress() {
		return receiverAddress;
	}

	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}

	public String getReceiverProvince() {
		return receiverProvince;
	}

	public void setReceiverProvince(String receiverProvince) {
		this.receiverProvince = receiverProvince;
	}

	public String getReceiverCity() {
		return receiverCity;
	}

	public void setReceiverCity(String receiverCity) {
		this.receiverCity = receiverCity;
	}

	public String getReceiverDistrict() {
		return receiverDistrict;
	}

	public void setReceiverDistrict(String receiverDistrict) {
		this.receiverDistrict = receiverDistrict;
	}

	public List<ScmAllocateOrderItem> getAllocateOrderItemList() {
		return allocateOrderItemList;
	}

	public void setAllocateOrderItemList(List<ScmAllocateOrderItem> allocateOrderItemList) {
		this.allocateOrderItemList = allocateOrderItemList;
	}

	public String getReceiverProvinceName() {
		return receiverProvinceName;
	}

	public void setReceiverProvinceName(String receiverProvinceName) {
		this.receiverProvinceName = receiverProvinceName;
	}

	public String getReceiverCityName() {
		return receiverCityName;
	}

	public void setReceiverCityName(String receiverCityName) {
		this.receiverCityName = receiverCityName;
	}

	public String getSenderProvinceName() {
		return senderProvinceName;
	}

	public void setSenderProvinceName(String senderProvinceName) {
		this.senderProvinceName = senderProvinceName;
	}

	public String getSenderCityName() {
		return senderCityName;
	}

	public void setSenderCityName(String senderCityName) {
		this.senderCityName = senderCityName;
	}


}
