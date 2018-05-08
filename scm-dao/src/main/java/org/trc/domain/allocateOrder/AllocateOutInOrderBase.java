package org.trc.domain.allocateOrder;

import javax.persistence.Column;

/**
 * 调拨出库、入库单公共基础类
 */
public class AllocateOutInOrderBase extends AllocateOrderBase{

    /**
     * 是否取消
     */
    @Column(name = "is_cancel")
    private String isCancel;

    /**
     * 是否关闭
     */
    @Column(name = "is_close")
    private String isClose;

    public String getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(String isCancel) {
        this.isCancel = isCancel;
    }

    public String getIsClose() {
        return isClose;
    }

    public void setIsClose(String isClose) {
        this.isClose = isClose;
    }
}
