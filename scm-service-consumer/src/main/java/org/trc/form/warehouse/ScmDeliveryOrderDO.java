package org.trc.form.warehouse;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 发货单信息
 */
@Setter
@Getter
public class ScmDeliveryOrderDO {

    /**
     * 出库单号
     */
    private String deliveryOrderCode;

    /**
     * 出库单类型
     */
    private String orderType;

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 货主编码
     */
    private String ownerCode;

    /**
     * 店铺编号
     */
    private String shopNo;

    /**
     * 店铺名称
     */
    private String shopNick;


    /**
     * 发件人姓名
     */
    private String senderName;

    /**
     * 发件人移动电话
     */
    private String senderMobile;

    /**
     * 发件人省份
     */
    private String senderProvince;

    /**
     * 发件人城市
     */
    private String senderCity;

    /**
     * 发件人详细地址
     */
    private String senderDetailAddress;

    /**
     * 收件人姓名
     */
    private String reciverName;

    /**
     * 收件人移动电话
     */
    private String reciverMobile;

    /**
     * 收件人省份
     */
    private String reciverProvince;

    /**
     * 收件人城市
     */
    private String reciverCity;

    /**
     * 收件人县
     */
    private String reciverCountry;

    /**
     * 收件人镇
     */
    private String reciverTown;

    /**
     * 收件人详细地址
     */
    private String reciverDetailAddress;

    /**
     * 是否需要发票：0-否,1-是
     */
    private String invoiceFlag;

    /**
     * 发票类型
     */
    private String invoiceType;

    /**
     * 发票抬头
     */
    private String invoiceTitle;

    /**
     * 发票金额
     */
    private BigDecimal invoiceAmount;

    /**
     * 发票标识
     */
    private String invoiceState;

    /**
     * 发票内容
     */
    private String invoiceContent;

    /**
     * 购方税号(税务识别号) 
     */
    private String invoiceTax;

    /**
     * 买家留言
     */
    private String buyerMessage;

    /**
     * 卖家留言
     */
    private String sellerMessage;

    /**
     * 发货单创建时间
     */
    private Date createTime;

    /**
     * 前台订单/店铺订单的创建时间/下单时间
     */
    private Date placeOrderTime;

    /**
     * 操作(审核)时间
     */
    private Date operateTime;

    /**
     * 物流公司编码
     */
    private String logisticsCode;

    /**
     * 订单标记位，首位为1代表货到付款(京东)
     */
    private String orderMark;

    /**
     * ISV来源编号(京东)
     */
    private String isvSource;

    /**
     * 销售平台来源(京东)
     */
    private String salePlatformSource;

    /**
     * 承运商编号(京东)
     */
    private String shipperNo;

    /**
     * 安维标识，0 不需要安装 1 京东安装 2 厂家安装(京东)
     */
    private String installVenderId;


    /**
     * 供应链订单类型：0-门店订单,1-非门店订单
     */
    private String scmOrderType;

    /**
     * 销售渠道订单号
     */
    private String shopOrderCode;
    /**
     * 供应链业务线编码
     */
    private String channelCode;
    /**
     * 供应链业务线名称
     */
    private String channelName;
    /**
     * 供应链销售渠道编码
     */
    private String sellChannelCode;
    /**
     * 供应链销售渠道名称
     */
    private String sellChannelName;
    /**
     * 付款时间
     */
    private Date payTime;

    /**
     * 订单列表
     */
    private List<ScmDeliveryOrderItem> scmDeleveryOrderItemList;


}
