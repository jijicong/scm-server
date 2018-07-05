package org.trc.domain.allocateOrder;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.trc.domain.util.CommonDO;
import javax.persistence.Column;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;

@Getter
@Setter
public class AllocateOrderBase extends CommonDO{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1718564900810599995L;

	/**
     * 调入仓库编码
     */
    @Length(max = 32, message = "调入仓库编码不得超过32个字符")
    @Column(name = "in_warehouse_code")
    @NotBlank(message = "调入仓库编码不能为空")
    @FormParam("inWarehouseCode")
    private String inWarehouseCode;

    /**
     * 调入仓库名称
     */
    @Transient
    private String inWarehouseName;

    /**
     * 调出仓库编码
     */
    @Length(max = 32, message = "调出仓库编码不得超过32个字符")
    @Column(name = "out_warehouse_code")
    @NotBlank(message = "调出仓库编码不能为空")
    @FormParam("outWarehouseCode")
    private String outWarehouseCode;

    /**
     * 调出仓库名称
     */
    @Transient
    private String outWarehouseName;

    /**
     * 出库单创建人
     */
    @Transient
    private String createOperatorName;

    /**
     * 收货人所在省
     */
    @Column(name = "receiver_province")
    private String receiverProvince;

    @Transient
    private String receiverProvinceName;

    /**
     * 收货人所在城市
     */
    @Column(name = "receiver_city")
    private String receiverCity;

    @Transient
    private String receiverCityName;

    /**
     * 发件人所在省
     */
    @Column(name = "sender_province")
    private String senderProvince;

    @Transient
    private String senderProvinceName;

    /**
     * 发件人所在城市
     */
    @Column(name = "sender_city")
    private String senderCity;

    @Transient
    private String senderCityName;

    @Transient
    private String senderDistrictName;

    @Transient
    private String receiverDistrictName;

}
