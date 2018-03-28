package org.trc.form.warehouse;

public class ScmOrderCancelResponse {

    /**
     * 是否取消成功: 1-取消成功, 2-取消失败, 3-取消中
     */
    private String flag;

    /**
     * 说明信息
     */
    private String message;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
