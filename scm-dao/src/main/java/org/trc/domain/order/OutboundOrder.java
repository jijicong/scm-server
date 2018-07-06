package org.trc.domain.order;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class OutboundOrder extends OrderBase {

    // 主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //系统订单号
    @NotEmpty
    @Length(max = 32)
    private String scmShopOrderCode;

    //出库通知单编码
    private String outboundOrderCode;

    //店铺订单编号
    private String warehouseOrderCode;

    private String wmsOrderCode;

    private Long shopId;

    private String shopName;

    private String shopOrderCode;

    private Long supplierId;

    private String supplierCode;

    private Long warehouseId;

    private String warehouseCode;

    private String orderType;

    private Integer itemNum;

    private String receiverProvince;

    private String receiverCity;

    private String receiverDistrict;

    private String receiverAddress;

    private String receiverZip;

    //收货人
    private String receiverName;

    private String receiverPhone;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

    private String channelCode;

    private String sellCode;

    private String buyerMessage;

    private String sellerMessage;

    private String status;

    private String platformOrderCode;

    private String remark;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date payTime;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;

    private String isClose;

    private String isCancel;

    private String message;

    private Integer newCode;

    @Transient
    private String isTimeOut;

    @Transient
    private List<OutboundDetail> outboundDetailList;

    @Transient
    private String warehouseName;

    /**
     * 是否门店订单:1-非门店,2-门店'
     */
    private Integer isStoreOrder;
}
