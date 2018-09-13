package org.trc.form.warehouse;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScmAfterSaleOrderCancelResponse {

    /**
     * 是否取消成功: 1-取消成功, 2-取消失败
     */
    private String flag;

    /**
     * 说明信息
     */
    private String message;

}
