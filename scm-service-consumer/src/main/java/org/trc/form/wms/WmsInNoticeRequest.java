package org.trc.form.wms;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 采购入库通知单
 * Created by hzgjl on 2018/5/28.
 */
@Data
public class WmsInNoticeRequest implements Serializable{
    private String warehouseNoticeCode;
    private List<WmsInNoticeDetailRequest> InNoticeDetailRequests;
}
