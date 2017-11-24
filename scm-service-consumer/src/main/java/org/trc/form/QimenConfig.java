package org.trc.form;

/**
 * Created by hzcyn on 2017/11/22.
 */
public class QimenConfig {
	
    //商品批量同步URL
    private String qimenItemsSyncUrl;
    
    //入库单创建接口URL
    private String qimenEntryorderCreateUrl;

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

}
