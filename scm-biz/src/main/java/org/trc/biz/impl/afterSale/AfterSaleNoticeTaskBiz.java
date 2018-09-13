package org.trc.biz.impl.afterSale;


import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.afterSale.IAfterSaleNoticeTaskBiz;
import org.trc.biz.impl.config.LogInfoBiz;
import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.domain.afterSale.AfterSaleOrderDetail;
import org.trc.enums.*;
import org.trc.exception.AfterSaleException;
import org.trc.form.CancelSendNoticeTrcForm;
import org.trc.form.TrcConfig;
import org.trc.form.TrcParam;
import org.trc.form.afterSale.AfterSaleNoticeWmsForm;
import org.trc.form.afterSale.AfterSaleNoticeWmsResultVO;
import org.trc.service.ITrcService;
import org.trc.service.afterSale.IAfterSaleOrderDetailService;
import org.trc.service.afterSale.IAfterSaleOrderService;
import org.trc.service.config.ILogInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.ParamsUtil;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service("afterSaleNoticeTaskBiz")
public class AfterSaleNoticeTaskBiz implements IAfterSaleNoticeTaskBiz {
    private Logger logger = LoggerFactory.getLogger(AfterSaleNoticeTaskBiz.class);

    @Autowired
    IAfterSaleOrderService afterSaleOrderService;

    @Autowired
    IAfterSaleOrderDetailService afterSaleOrderDetailService;

    @Autowired
    ILogInfoService logInfoService;

    @Autowired
    private ITrcService trcService;

    @Autowired
    private TrcConfig trcConfig;

    @Override
    public void cancelSendOutGoods() {
        //1.售后单状态为取消中,售后类型为取消发货的售后单
        Example example = new Example(AfterSaleOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("afterSaleType", AfterSaleTypeEnum.CANCEL_DELIVER.getCode());
        criteria.andEqualTo("status", AfterSaleOrderEnum.AfterSaleOrderStatusEnum.STATUS_IS_CANCELING.getCode());
        List<AfterSaleOrder> afterSaleOrderList = afterSaleOrderService.selectByExample(example);
        //2.获取售后单详情
        if (!AssertUtil.collectionIsEmpty(afterSaleOrderList)) {
            Set<String> afterSaleCodeSet = new HashSet<>();
            Map<String, AfterSaleOrder> afterSaleCodeMap = new HashMap<>();
            for (AfterSaleOrder afterSaleOrder : afterSaleOrderList) {
                afterSaleCodeSet.add(afterSaleOrder.getAfterSaleCode());
                AfterSaleOrder saleOrder = JSON.parseObject(JSON.toJSONString(afterSaleOrder), AfterSaleOrder.class);
                afterSaleCodeMap.put(afterSaleOrder.getAfterSaleCode(), saleOrder);
            }
            //
            Example detailExample = new Example(AfterSaleOrderDetail.class);
            Example.Criteria detailCriteria = detailExample.createCriteria();
            detailCriteria.andIn("afterSaleCode", afterSaleCodeSet);
            List<AfterSaleOrderDetail> afterSaleOrderDetailList = afterSaleOrderDetailService.selectByExample(detailExample);
            if (!AssertUtil.collectionIsEmpty(afterSaleOrderDetailList)) {
                List<AfterSaleNoticeWmsForm> wmsFromList = new ArrayList<>();
                for (AfterSaleOrderDetail orderDetail : afterSaleOrderDetailList) {
                    AfterSaleNoticeWmsForm saleNoticeWmsFrom = new AfterSaleNoticeWmsForm();
                    saleNoticeWmsFrom.setAfterSaleCode(orderDetail.getAfterSaleCode());
                    saleNoticeWmsFrom.setScmShopOrderCode(orderDetail.getScmShopOrderCode());
                    saleNoticeWmsFrom.setSkuCode(orderDetail.getSkuCode());
                    wmsFromList.add(saleNoticeWmsFrom);
                }
                //3.请求仓库
                if (AssertUtil.collectionIsEmpty(wmsFromList)) {
                    List<AfterSaleNoticeWmsResultVO> saleNoticeWmsResultVOList = afterSaleOrderService.deliveryCancelResult(wmsFromList);
                    //更新对应的售后单状态并通知渠道
                    for (AfterSaleOrder afterSaleOrder : afterSaleOrderList) {
                        for (AfterSaleNoticeWmsResultVO wmsResultVO : saleNoticeWmsResultVOList) {
                            if (StringUtils.equals(wmsResultVO.getAfterSaleCode(), afterSaleOrder.getAfterSaleCode())) {
                                switch (wmsResultVO.getFlg()) {
                                    case "1":
                                        afterSaleOrder.setStatus(AfterSaleOrderEnum.AfterSaleOrderStatusEnum.STATUS_2.getCode());
                                        break;
                                    case "2":
                                        afterSaleOrder.setStatus(AfterSaleOrderEnum.AfterSaleOrderStatusEnum.STATUS_IS_CANCELING.getCode());
                                        break;
                                    case "3":
                                        afterSaleOrder.setStatus(AfterSaleOrderEnum.AfterSaleOrderStatusEnum.STATUS_IS_FAIL.getCode());
                                        break;
                                }
                                break;
                            }
                        }
                    }
                    //更新数据库
                    try {
                        int count = afterSaleOrderService.updateAfterSaleOrderList(afterSaleOrderList);
                        if (count == 0) {
                            String msg = "修改售后单状态" + JSON.toJSONString(afterSaleOrderList) + "操作失败";
                            logger.error(msg);
                            throw new AfterSaleException(ExceptionEnum.AFTER_SALE_ORDER_UPDATE_EXCEPTION, msg);
                        } else {
                            //记录日志
                            recordLog(afterSaleCodeMap, afterSaleOrderList, saleNoticeWmsResultVOList);
                            //推送取消发货通知
                            pushAfterSaleState(saleNoticeWmsResultVOList);
                        }
                    } catch (Exception e) {
                        logger.error("更新数据库异常");
                    }
                }
            }
        }
    }

    private void pushAfterSaleState(List<AfterSaleNoticeWmsResultVO> saleNoticeWmsResultVOList) {
        for (AfterSaleNoticeWmsResultVO saleNoticeWmsResultVO : saleNoticeWmsResultVOList) {
            if (!StringUtils.equals(saleNoticeWmsResultVO.getFlg(), "2")) {
                TrcParam trcParam = ParamsUtil.generateTrcSign(trcConfig.getKey(), TrcActionTypeEnum.SUBMIT_ORDER_NOTICE);
                CancelSendNoticeTrcForm sendNoticeTrcForm = (CancelSendNoticeTrcForm) trcParam;
                sendNoticeTrcForm.setAfterSaleCode(saleNoticeWmsResultVO.getAfterSaleCode());
                if (StringUtils.equals(saleNoticeWmsResultVO.getFlg(), "1")) {
                    sendNoticeTrcForm.setAfterSaleOrderState("1");
                } else if (StringUtils.equals(saleNoticeWmsResultVO.getFlg(), "3")) {
                    sendNoticeTrcForm.setAfterSaleOrderState("2");
                }
                trcService.cancelSendNotice(sendNoticeTrcForm);
            }
        }
    }

    private void recordLog(Map<String, AfterSaleOrder> afterSaleCodeMap, List<AfterSaleOrder> afterSaleOrderList, List<AfterSaleNoticeWmsResultVO> saleNoticeWmsResultVOList) {
        Map<String, AfterSaleNoticeWmsResultVO> wmsResultVOMap = new HashMap<>();
        for (AfterSaleNoticeWmsResultVO saleNoticeWmsResultVO : saleNoticeWmsResultVOList) {
            wmsResultVOMap.put(saleNoticeWmsResultVO.getAfterSaleCode(), saleNoticeWmsResultVO);
        }
        for (AfterSaleOrder afterSaleOrder : afterSaleOrderList) {
            //数据对比记录日志,失败的记录备注
            AfterSaleOrder oldAfterSaleOrder = afterSaleCodeMap.get(afterSaleOrder.getAfterSaleCode());
            //对比状态
            if (null != oldAfterSaleOrder && (oldAfterSaleOrder.getStatus() != afterSaleOrder.getStatus())) {
                if (afterSaleOrder.getStatus() == AfterSaleOrderEnum.AfterSaleOrderStatusEnum.STATUS_IS_FAIL.getCode()) {
                    AfterSaleNoticeWmsResultVO saleNoticeWmsResultVO = wmsResultVOMap.get(afterSaleOrder.getAfterSaleCode());
                    String remake = "";
                    if (null != saleNoticeWmsResultVO) {
                        remake = saleNoticeWmsResultVO.getMsg();
                    }
                    logInfoService.recordLog(afterSaleOrder, afterSaleOrder.getId(), LogInfoBiz.ADMIN_SIGN, "", remake, LogOperationEnum.UPDATE.getMessage());
                } else {
                    logInfoService.recordLog(afterSaleOrder, afterSaleOrder.getId(), LogInfoBiz.ADMIN_SIGN, "", "", LogOperationEnum.UPDATE.getMessage());
                }
            }
        }

    }

}
