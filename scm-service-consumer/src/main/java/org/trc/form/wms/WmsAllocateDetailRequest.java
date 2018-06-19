package org.trc.form.wms;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by hzcyn on 2018/5/23.
 */
@Setter
@Getter
public class WmsAllocateDetailRequest implements Serializable {

    private Long realOutNum;

    private Long realInNum;

    private Long nornalInNum;

    private Long defectInNum;

    private String skuCode;

    private String inventoryType;

}
