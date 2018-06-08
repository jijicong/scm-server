package org.trc.form.wms;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 采购入库通知单
 * Created by hzgjl on 2018/5/28.
 */
@Data
public class WmsInNoticeDetailRequest implements Serializable {
    //正品入库数量
    private Long normalStorageQuantity;
    //残次品入库数量
    private Long defectiveStorageQuantity;
    //实际入库数量
    private Long actualStorageQuantity;
    //实际入库时间
    private Date actualInstockTime;
    //'sku编码',
    private String skuCode;
}
