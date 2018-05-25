package org.trc.form.wms;

import java.io.Serializable;

/**
 * Created by hzcyn on 2018/5/23.
 */
public class WmsAllocateDetailRequest implements Serializable {

    private Long realOutNum;

    private Long realInNum;

    private Long nornalInNum;

    private Long defectInNum;

    private String skuCode;

    public Long getRealOutNum() {
        return realOutNum;
    }

    public void setRealOutNum(Long realOutNum) {
        this.realOutNum = realOutNum;
    }

    public Long getRealInNum() {
        return realInNum;
    }

    public void setRealInNum(Long realInNum) {
        this.realInNum = realInNum;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public Long getNornalInNum() {
        return nornalInNum;
    }

    public void setNornalInNum(Long nornalInNum) {
        this.nornalInNum = nornalInNum;
    }

    public Long getDefectInNum() {
        return defectInNum;
    }

    public void setDefectInNum(Long defectInNum) {
        this.defectInNum = defectInNum;
    }
}
