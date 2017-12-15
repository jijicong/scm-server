package org.trc.form;

/**
 * Created by hzcyn on 2017/11/22.
 */
public class QimenConfig {
	
    //商品批量同步URL
    private String qimenItemsSyncUrl;
    
    //入库单创建接口URL
    private String qimenEntryorderCreateUrl;

    //发货通知单创建接口URL
    private String qimenDeliveryOrderCreateUrl;

    //商品库存查询接口URL
    private String qimenInventoryQueryUrl;

    //单据取消接口URL
    private String qimenOrderCancelUrl;
    
    //退货入库单创建接口URL
    private String qimenReturnOrderCreateUrl;
    
    //出库单创建接口URL
    private String qimenStockoutCreateUrl;
    
    //单据挂起（恢复）接口URL
    private String qimenOrderPendingUrl;

    public String getQimenItemsSyncUrl() {
        return qimenItemsSyncUrl;
    }

    public void setQimenItemsSyncUrl(String qimenItemsSyncUrl) {
        this.qimenItemsSyncUrl = qimenItemsSyncUrl;
    }

	public String getQimenEntryorderCreateUrl() {
		return qimenEntryorderCreateUrl;
	}

	public void setQimenEntryorderCreateUrl(String qimenEntryorderCreateUrl) {
		this.qimenEntryorderCreateUrl = qimenEntryorderCreateUrl;
	}

    public String getQimenDeliveryOrderCreateUrl() {
        return qimenDeliveryOrderCreateUrl;
    }

    public void setQimenDeliveryOrderCreateUrl(String qimenDeliveryOrderCreateUrl) {
        this.qimenDeliveryOrderCreateUrl = qimenDeliveryOrderCreateUrl;
    }

    public String getQimenOrderCancelUrl() {
        return qimenOrderCancelUrl;
    }

    public void setQimenOrderCancelUrl(String qimenOrderCancelUrl) {
        this.qimenOrderCancelUrl = qimenOrderCancelUrl;
    }

    public String getQimenInventoryQueryUrl() {
        return qimenInventoryQueryUrl;
    }

    public void setQimenInventoryQueryUrl(String qimenInventoryQueryUrl) {
        this.qimenInventoryQueryUrl = qimenInventoryQueryUrl;
    }

	public String getQimenReturnOrderCreateUrl() {
		return qimenReturnOrderCreateUrl;
	}

	public void setQimenReturnOrderCreateUrl(String qimenReturnOrderCreateUrl) {
		this.qimenReturnOrderCreateUrl = qimenReturnOrderCreateUrl;
	}

	public String getQimenStockoutCreateUrl() {
		return qimenStockoutCreateUrl;
	}

	public void setQimenStockoutCreateUrl(String qimenStockoutCreateUrl) {
		this.qimenStockoutCreateUrl = qimenStockoutCreateUrl;
	}

	public String getQimenOrderPendingUrl() {
		return qimenOrderPendingUrl;
	}

	public void setQimenOrderPendingUrl(String qimenOrderPendingUrl) {
		this.qimenOrderPendingUrl = qimenOrderPendingUrl;
	}
    
}
