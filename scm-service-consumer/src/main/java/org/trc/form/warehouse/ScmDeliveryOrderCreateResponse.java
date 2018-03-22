package org.trc.form.warehouse;

/**
 * 发货通知单创建返回参数
 */
public class ScmDeliveryOrderCreateResponse {

    /**
     * 响应码，成功时返回200
     */
    private String code;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 出库单号
     */
    private String deliveryOrderCode;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeliveryOrderCode() {
        return deliveryOrderCode;
    }

    public void setDeliveryOrderCode(String deliveryOrderCode) {
        this.deliveryOrderCode = deliveryOrderCode;
    }
}
