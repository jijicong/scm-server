package org.trc.form.wms;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hzcyn on 2018/5/23.
 */
public class WmsAllocateOutInRequest implements Serializable {

    private  String allocateOrderCode;

    private List<WmsAllocateDetailRequest> wmsAllocateDetailRequests;

    public String getAllocateOrderCode() {
        return allocateOrderCode;
    }

    public void setAllocateOrderCode(String allocateOrderCode) {
        this.allocateOrderCode = allocateOrderCode;
    }

    public List<WmsAllocateDetailRequest> getWmsAllocateDetailRequests() {
        return wmsAllocateDetailRequests;
    }

    public void setWmsAllocateDetailRequests(List<WmsAllocateDetailRequest> wmsAllocateDetailRequests) {
        this.wmsAllocateDetailRequests = wmsAllocateDetailRequests;
    }
}
