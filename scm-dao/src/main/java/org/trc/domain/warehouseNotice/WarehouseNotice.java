package org.trc.domain.warehouseNotice;

import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.*;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.io.Serializable;
import java.util.Date;

/**
 * 入库通知单信息
 * Created by sone on 2017/7/10.
 */
@Table(name = "warehouse_notice")
@Data
public class WarehouseNotice implements Serializable{

    @Id
    @PathParam("id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //'入库通知单编号',
    @FormParam("warehouseNoticeCode")
    @NotEmpty(message ="入库通知单编号不能为空")
    @Length(max = 64, message = "入库通知的编码字母和数字不能超过64个,汉字不能超过32个")
    private String warehouseNoticeCode;

    //'采购单编号',
    @FormParam("purchaseOrderCode")
    @NotEmpty(message ="采购单编号不能为空")
    @Length(max = 32, message = "采购订单的编码字母和数字不能超过32个,汉字不能超过16个")
    private String purchaseOrderCode;

    //采购订单id
    @Transient
    private Long purhcaseOrderId;

    //'采购合同编号',
    /*@NotEmpty*/
    @FormParam("contractCode")
    @Length(max = 32, message = "采购合同的编码字母和数字不能超过32个,汉字不能超过16个")
    private String contractCode;

    @Transient //采购组名称
    private String purchaseGroupName;
    //'归属采购组编号',
    /*@NotEmpty*/
    @FormParam("purchaseGroupCode")
    @Length(max = 32, message = "采购组的编码字母和数字不能超过32个,汉字不能超过16个")
    private String purchaseGroupCode;

    //'所在仓库id',
    @FormParam("warehouseId")
    private Long warehouseId;
    //'仓库编号',
    @FormParam("warehouseCode")
    @NotEmpty(message ="仓库编号不能为空")
    @Length(max = 32, message = "仓库的编码字母和数字不能超过32个,汉字不能超过16个")
    private String warehouseCode;
    //'状态:1-待通知收货,2-待仓库反馈,3-收货异常,4-全部收货,5-作废',
    @Transient
    private String warehouseName;
    @FormParam("status")
    @NotEmpty(message ="状态不能为空")
    @Length(max = 2, message = "状态字母和数字不能超过2个")
    private String status;
    //'供应商id',
    @FormParam("supplierId")
    private Long supplierId;
    //'供应商编号',
    @FormParam("supplierCode")
    @Length(max = 32, message = "供应商编码字母和数字不能超过32个,汉字不能超过16个")
    private String supplierCode;

    //供应商名称
    @Transient
    private String supplierName;
    //'采购类型编号',
    @FormParam("purchaseType")
    @NotEmpty(message ="采购类型编号不能为空")
    @Length(max = 32, message = "采购类型字母和数字不能超过32个,汉字不能超过16个")
    private String purchaseType;
    //货主ID
    @FormParam("channelCode")
    private String channelCode;

    //仓库信息主键
    @FormParam("warehouseInfoId")
    private Long warehouseInfoId;

    @Transient
    private String purchaseTypeName;
    //'归属采购人编号',

    private String purchasePersonId;

    @Transient //归属采购人名称
    private String purchasePersonName;
    //'提运单号',
    @FormParam("takeGoodsNo")
    private String takeGoodsNo;
    // '要求到货日期,格式:yyyy-mm-dd',

    private String requriedReceiveDate;
    //'截止到货日期,格式:yyyy-mm-dd',

    private String endReceiveDate;
    //'备注',

    private String remark;
    //'创建人',

    private String createOperator;
    //'创建时间,格式yyyy-mm-dd hh:mi:ss',
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updateTime;
    /**
     * scm2.0新增字段 
     **/
    //仓储系统入库单编码,入库单申请时返回
    @Column(name ="entry_order_id")
    private String entryOrderId;
    //货主编码
    @Column(name ="owner_code")
    private String ownerCode;
    //奇门仓库编码
    @Column(name ="qimen_warehouse_code")
    private String qimenWarehouseCode;
    //发件人
    @Column(name ="sender")
    private String sender;
    //收货人手机
    @Column(name ="receiver_number")
    private String receiverNumber;
    //收货人
    @Column(name ="receiver")
    private String receiver;
    //发件人所在省
    @Column(name ="sender_province")
    private String senderProvince;
    //发件人所在城市
    @Column(name ="sender_city")
    private String senderCity;
    //发件人手机
    @Column(name ="sender_number")
    private String senderNumber;
    //发件方详细地址
    @Column(name ="sender_address")
    private String senderAddress;
    //收件方省份
    @Column(name ="receiver_province")
    private String receiverProvince;
    //收件方地址
    @Column(name ="receiver_address")
    private String receiverAddress;
    //收件方城市
    @Column(name ="receiver_city")
    private String receiverCity;
    // 仓库接收失败原因warehouseNotice
    @Column(name ="failure_cause")
    private String failureCause;
    //异常原因
    @Column(name ="exception_cause")
    private String exceptionCause;
    //完成状态：0-未完成，1-已完成 (仓库反馈收货完成的、作废的、已取消的都是已完成)
    @Column(name ="finish_status")
    private String finishStatus;

}
