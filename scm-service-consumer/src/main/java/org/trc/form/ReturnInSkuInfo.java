package org.trc.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnInSkuInfo {
    //sku编码
    private String skuCode;
    //sku商品名称
    private String skuName;
    //入库正品数量
    private Integer inNum;
    //入库残品数量
    private Integer defectiveInNum;
    // 渠道商品订单号
    private String orderItemCode;

}
