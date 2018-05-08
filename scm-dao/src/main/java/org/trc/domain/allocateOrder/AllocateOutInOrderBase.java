package org.trc.domain.allocateOrder;

import javax.persistence.Column;
import javax.persistence.Transient;

/**
 * 调拨出库、入库单公共基础类
 */
public class AllocateOutInOrderBase extends AllocateOrderBase{

    /**
     * 是否取消
     */
    @Column(name = "is_cancel")
    private String isCancel;

    private String memo;

    /**
     * 是否关闭
     */
    @Column(name = "is_close")
    private String isClose;

    private String status;

    private String oldStatus;

    @Transient
    private String isTimeOut;

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

    public String getIsTimeOut() {
        return isTimeOut;
    }

    public void setIsTimeOut(String isTimeOut) {
        this.isTimeOut = isTimeOut;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }
}
