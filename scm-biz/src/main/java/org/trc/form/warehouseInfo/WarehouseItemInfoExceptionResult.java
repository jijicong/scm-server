package org.trc.form.warehouseInfo;

import java.io.Serializable;

/**
 * Created by hzcyn on 2017/11/20.
 */
public class WarehouseItemInfoExceptionResult implements Serializable {

    private String url;
    private String successCount;
    private String failCount;

    public WarehouseItemInfoExceptionResult(){
    }

    public WarehouseItemInfoExceptionResult(String url, String successCount, String failCount) {
        this.url = url;
        this.successCount = successCount;
        this.failCount = failCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(String successCount) {
        this.successCount = successCount;
    }

    public String getFailCount() {
        return failCount;
    }

    public void setFailCount(String failCount) {
        this.failCount = failCount;
    }

    @Override
    public String toString() {
        return "WarehouseItemInfoExceptionResult{" +
                "url='" + url + '\'' +
                ", successCount='" + successCount + '\'' +
                ", failCount='" + failCount + '\'' +
                '}';
    }
}
