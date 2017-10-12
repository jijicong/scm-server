package org.trc.biz.jingdong;

import org.trc.form.JDModel.*;
import org.trc.form.external.*;
import org.trc.util.Pagenation;

import javax.ws.rs.core.Response;

/**
 * Created by hzwyz on 2017/5/19 0019.
 */
public interface IJingDongBiz {


    //Pagenation<JdBalanceDetail> checkBalanceDetail(BalanceDetailDO queryModel, Pagenation<JdBalanceDetail> page) throws Exception;

    /**
     * 获取所有京东交易类型
     * @return
     */
    ReturnTypeDO getAllTreadType() throws Exception;

    /**
     * 京东账户余额信息查询接口
     */
    Response queryBalanceInfo()throws Exception;

    /**
     * 订单对比明细分页查询接口
     */
    Pagenation<OrderDetailDTO> orderDetailByPage(OrderDetailForm queryModel, Pagenation<OrderDetail> page)throws Exception;

    /**
     * 余额明细分页查询接口
     */
    Pagenation<BalanceDetailDTO> balanceDetailByPage(BalanceDetailDO queryModel, Pagenation<JdBalanceDetail> page)throws Exception;

    /**
     * 余额明细导出接口
     */
    Response exportBalanceDetail(BalanceDetailDO queryModel)throws Exception;

    /**
     * 订单明细导出接口
     */
    Response exportOrderDetail(OrderDetailForm queryModel)throws Exception;

    /**
     * 订单明操作接口
     */
    Response operateRecord(OperateForm orderDetail)throws Exception;

}
