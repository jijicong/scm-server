package org.trc.form;

import lombok.Getter;
import lombok.Setter;

/**
 * 组装数据,用于请求发货通知单接口获取当前售后单是否已经完成了取消操作.
 */
@Setter
@Getter
public class CancelSendNoticeTrcForm extends TrcParam{

    /**
     * 售后单号
     */
    private String afterSaleCode;

    /**
     * 售后单状态
     */
    private String afterSaleOrderState;

}
