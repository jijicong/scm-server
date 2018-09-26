package org.trc.form;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 退货入库单入库结果通知
 */
@Getter
@Setter
public class ReturnInResultNoticeForm extends TrcParam{
    /**
     * 售后单编号
     */
    private String afterSaleCode;
    /**
     * 店铺订单编码
     */
    private String shopOrderCode;
    /**
     * 理货结果备注
     */
    private String memo;
    /**
     * 理货图片附件图片七牛存储路径,多个用逗号分隔
     */
    private String recordPic;
    /**
     * sku明细
     */
    private List<ReturnInSkuInfo> skus;

}
