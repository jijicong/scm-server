package org.trc.form.warehouse;

import java.util.Date;
import java.util.List;

public class ScmEntryOrderDetailRequest extends ScmWarehouseRequestBase{

    /**
     * 入库单号
     */
    private String entryOrderCode;

    public String getEntryOrderCode() {
        return entryOrderCode;
    }

    public void setEntryOrderCode(String entryOrderCode) {
        this.entryOrderCode = entryOrderCode;
    }
}
