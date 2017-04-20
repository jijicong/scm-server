package org.trc.common;

import java.io.Serializable;

/**
 * Created by george on 2016/12/30.
 */
public class LogisticAck implements Serializable {

    /**
     * 成功与否
     */
    private boolean success;

    /**
     * 失败原因
     */
    private String reason;

    /**
     * 物流状态：2-在途中,3-签收,4-问题件
     */
    private String state;

    private String traces;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTraces() {
        return traces;
    }

    public void setTraces(String traces) {
        this.traces = traces;
    }

    public static LogisticAck renderFailure(String reason) {
        LogisticAck logisticAck = new LogisticAck();
        logisticAck.setReason(reason);
        logisticAck.setSuccess(false);
        return logisticAck;
    }

    @Override
    public String toString() {
        return "LogisticAck{" +
                "success=" + success +
                ", reason='" + reason + '\'' +
                ", state=" + state +
                ", traces='" + traces + '\'' +
                '}';
    }
}
