package org.trc.domain.order;

import javax.persistence.Transient;
import java.io.Serializable;

public class OrderBaseDO implements Serializable {

    /**
     * 销售渠道编码
     */
    private String sellCode;
    /**
     * 销售渠道名称
     */
    @Transient
    private String sellName;

    public String getSellCode() {
        return sellCode;
    }

    public void setSellCode(String sellCode) {
        this.sellCode = sellCode;
    }

    public String getSellName() {
        return sellName;
    }

    public void setSellName(String sellName) {
        this.sellName = sellName;
    }
}
