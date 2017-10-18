package org.trc.service;


import org.trc.form.JDModel.*;
import org.trc.form.SupplyItemsExt;
import org.trc.form.external.*;
import org.trc.form.liangyou.LiangYouSupplierOrder;
import org.trc.util.Pagenation;
import org.trc.util.ResponseAck;

import java.io.InputStream;
import java.util.List;

/**
 * Created by hzwyz on 2017/5/18 0018.
 */
public interface IJDService {


    /**
     *
     * @param form
     * @param page
     * @return
     * @throws Exception
     */
    ReturnTypeDO skuPage(SupplyItemsExt form, Pagenation<SupplyItemsExt> page) throws Exception;

    /**
     * 通知更新Sku使用状态
     * @param skuDOList
     * @return
     * @throws Exception
     */
    ReturnTypeDO noticeUpdateSkuUsedStatus(List<SkuDO> skuDOList);

    /**
     * 提交京东订单
     * @param jingDongOrder
     * @return
     * @throws Exception
     */
    ResponseAck submitJingDongOrder(JingDongSupplierOrder jingDongOrder);

    /**
     * 提交粮油订单
     * @param liangYouOrder
     * @return
     * @throws Exception
     */
    ResponseAck submitLiangYouOrder(LiangYouSupplierOrder liangYouOrder);


    /**
     * 查询代发供应商物流信息
     * @param warehouseOrderCode
     * @param flag 0-京东,1-粮油
     * @return
     */
    ReturnTypeDO getLogisticsInfo(String warehouseOrderCode, String flag);

    /**
     * 京东sku价格查询,多个sku用逗号分隔
     * @param skus
     * @return
     */
    ReturnTypeDO getSellPrice(String skus);

   /* *//**
     * 获取京东对账详情
     * @param queryModel
     * @param page
     * @return
     *//*
    ReturnTypeDO checkBalanceDetail(BalanceDetailDO queryModel, Pagenation<JdBalanceDetail> page);*/

    /**
     * 获取所有京东交易类型
     * @return
     */
    ReturnTypeDO getAllTreadType();

    /**
     * 获取京东区域
     * @return
     */
    ReturnTypeDO getJingDongArea();

    /**
     * 京东账户余额信息查询接口
     */
    ReturnTypeDO queryBalanceInfo();

    /**
     * 订单对比明细分页查询接口
     */
    ReturnTypeDO orderDetailByPage(OrderDetailForm queryModel, Pagenation<OrderDetail> page);

    /**
     * 余额明细分页查询接口
     */
    ReturnTypeDO balanceDetailByPage(BalanceDetailDO queryModel, Pagenation<JdBalanceDetail> page);

    /**
     * 余额明细导出接口
     */
    List<BalanceDetailDTO> exportBalanceDetail(BalanceDetailDO queryModel);

    /**
     * 订单明细导出接口
     */
    List<OrderDetailDTO> exportOrderDetail(OrderDetailForm queryModel);

    /**
     * 订单明操作接口
     */
    ReturnTypeDO operateRecord(OperateForm orderDetail);

    /**
     * 订单明操作查询接口
     */
    ReturnTypeDO getOperateState(Long id);

}
