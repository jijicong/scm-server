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

    //单据取消接口URL
    private String qimenOrderCancelUrl;

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
}
