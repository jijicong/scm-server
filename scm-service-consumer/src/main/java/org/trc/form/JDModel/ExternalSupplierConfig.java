package org.trc.form.JDModel;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by hzwdx on 2017/6/21.
 */
@Setter
@Getter
public class ExternalSupplierConfig {
    //scm-external项目服务地址
    private String scmExternalUrl;
    //京东sku分页查询url地址
    private String skuPageUrl;
    //京东sku查询url地址
    private String skuQueryUrl;
    //添加京东sku通知url地址
    private String skuAddNotice;
    //京东仓库名称
    private String jdWarehouse;
    //粮油仓库名称
    private String lyWarehouse;
    //京东图片查看url
    private String jdPictureUrl;
    //京东sku价格查询接口url地址
    private String jdSkuPriceUrl;
    //京东地址查询接口url地址
    private String jdAddressUrl;
    //京东下单接口地址
    private String jdSubmitOrderUrl;
    //京东订单反查接口地址
    private String orderQueryUrl;
    //配送信息查看url
    private String orderLogisticsUrl;
    //京东下单发票抬头
    private String companyName;
    //京东对账明细
    private String checkOrderDetailUrl;
    //京东业务类型接口
    private String treadTypeUrl;
    //京东账户信息接口url地址
    private String jdBalanceInfoUrl;
    //京东订单对比明细分页查询接口url地址
    private String jdOrderDetailPageUrl;
    //京东余额变动明细分页查询接口url地址
    private String jdBalanceDetailPageUrl;
    //京东订单对比明细导出接口url地址
    private String jdExportOrderUrl;
    //京东余额变动明细导出接口url地址
    private String jdExportBalanceUrl;
    //京东订单对比明细操作接口url地址
    private String jdOrderOperateUrl;
    //京东余额明细统计接口url地址
    private String balancestatisticsUrl;
    //京东订单对比明细操作查询接口url地址
    private String jdOperateStateUrl;
    //粮油下单接口地址
    private String lySubmitOrderUrl;
    //京东报表补偿接口地址
    private String reportCompensateUrl;
    //京东订单状态反查接口地址
    private String jdOrderStatusUrl;
    //调拨出库单取消接口URL
    private String allocateOrderOutCancelUrl;
    //调拨入库单取消接口URL
    private String allocateOrderInCancelUrl;

    /**
     * 商品同步接口URL
     */
    private String itemsSyncUrl;

    /**
     * 商品库存查询接口URL
     */
    private String inventoryQueryUrl;

    /**
     * 入库单创建接口URL
     */
    private String entryOrderCreateUrl;

    /**
     * 发货通知单创建接口URL
     */
    private String deliveryOrderCreateUrl;

    /**
     * 入库单详情接口URL
     */
    private String entryOrderDetailQueryUrl;

    /**
     * 出库单详情接口URL
     */
    private String deliveryOrderDetailQueryUrl;

    /**
     * 单据取消接口URL
     */
    private String orderCancelUrl;

    /**
     * 物流包裹详情接口URL
     */
    private String orderPackUrl;
    
    /**
     * 调拨出库单通知接口URL
     */
    private String allocateOrderOutUrl;
    
    /**
     * 调拨入库单通知接口URL
     */
    private String allocateOrderInUrl;
    
    /**
     * 京东仓间调拨单创建接口URL
     */
    private String josAllocateOrderCreateUrl;
    
    /**
     * 采购退货出库单创建URL
     */
    private String entryReturnOrderCreateUrl;




}
