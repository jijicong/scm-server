package org.trc.form.warehouse;

public class ScmOrderCancelResponse {

    /**
     * 是否成功：true/false
     */
    private Boolean success;

    /**
     * 说明信息
     */
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
