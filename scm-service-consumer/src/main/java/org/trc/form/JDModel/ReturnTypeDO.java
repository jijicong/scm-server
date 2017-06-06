package org.trc.form.JDModel;

/**
 * Created by hzwyz on 2017/6/5 0005.
 */
public class ReturnTypeDO<T> {
    //返回结果 true--成功  FALSE--失败
    private Boolean success;
    //返回信息
    private String resultMessage;
    //编号
    private String resultCode;
    //数据
    private T result;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
