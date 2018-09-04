package org.trc.biz.impl.afterSale;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.afterSale.IAfterSaleOrderTabBiz;
import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.domain.afterSale.AfterSaleOrderDetail;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.form.afterSale.AfterSaleDetailTabVO;
import org.trc.service.afterSale.IAfterSaleOrderDetailService;
import org.trc.service.afterSale.IAfterSaleOrderService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import org.trc.util.QueryModel;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service("afterSaleOrderTabBiz")
public class AfterSaleOrderTabBiz implements IAfterSaleOrderTabBiz {
    @Autowired
    IAfterSaleOrderService afterSaleOrderService;
    @Autowired
    IAfterSaleOrderDetailService afterSaleOrderDetailService;
    @Autowired
    IWarehouseInfoService warehouseInfoService;

    @Override
    public Pagenation<AfterSaleDetailTabVO> queryAfterSaleOrderTabPage(String scmShopOrderCode,QueryModel form, Pagenation<AfterSaleOrder> page) {
        //1.通过店铺订单号查询仓库级订单,通过店铺订单号查询退货单
        AssertUtil.notBlank(scmShopOrderCode, "查询退货单时系统订单号不能为空!");
        //查询退货单
        Example example = new Example(AfterSaleOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("scmShopOrderCode", scmShopOrderCode);
        Pagenation<AfterSaleOrder> pageResult = afterSaleOrderService.pagination(example, page, form);
        Pagenation<AfterSaleDetailTabVO> pagenation = JSON.parseObject(JSON.toJSONString(pageResult),Pagenation.class);
        List<AfterSaleOrder> afterSaleOrderList = pageResult.getResult();
        if (!AssertUtil.collectionIsEmpty(afterSaleOrderList)) {
            //退货单不为空,则查询sku级别退货单
            List<AfterSaleOrderDetail> afterSaleOrderDetailList = getAfterSaleOrderDetail(afterSaleOrderList);
            //返回的list
            List<AfterSaleDetailTabVO> afterSaleDetailTabVOList = new ArrayList<>();
            //组装返回数据
            for (AfterSaleOrderDetail saleOrderDetail : afterSaleOrderDetailList) {
                AfterSaleDetailTabVO saleDetailTabVO = new AfterSaleDetailTabVO();
                saleDetailTabVO.setSkuName(saleOrderDetail.getSkuName());
                saleDetailTabVO.setSkuCode(saleOrderDetail.getSkuCode());
                saleDetailTabVO.setInNum(saleOrderDetail.getInNum());
                saleDetailTabVO.setDefectiveInNum(saleOrderDetail.getDefectiveInNum());
                for (AfterSaleOrder afterSaleOrder : afterSaleOrderList) {
                    if (StringUtils.equals(saleOrderDetail.getScmShopOrderCode(), afterSaleOrder.getScmShopOrderCode())) {
                        saleDetailTabVO.setAfterSaleCode(afterSaleOrder.getAfterSaleCode());
                        saleDetailTabVO.setLaunchType(afterSaleOrder.getLaunchType());
                        saleDetailTabVO.setAfterSaleType(afterSaleOrder.getAfterSaleType());
                        saleDetailTabVO.setStatus(afterSaleOrder.getStatus());
                        saleDetailTabVO.setReturnWarehouseCode(afterSaleOrder.getReturnWarehouseCode());
                        saleDetailTabVO.setLogisticsCorporation(afterSaleOrder.getLogisticsCorporation());
                        saleDetailTabVO.setWaybillNumber(afterSaleOrder.getWaybillNumber());
                        break;
                    }
                }
                afterSaleDetailTabVOList.add(saleDetailTabVO);
            }
            if (!AssertUtil.collectionIsEmpty(afterSaleDetailTabVOList)) {
                //设置仓库名称
                handWarehouseName(afterSaleDetailTabVOList);
                pagenation.setResult(afterSaleDetailTabVOList);
                return pagenation;
            }
        }
        return pagenation;

    }

    private void handWarehouseName(List<AfterSaleDetailTabVO> afterSaleDetailTabVOList) {
        Set<String> warehouseCodeSet = new HashSet<>();
        for (AfterSaleDetailTabVO detailTab : afterSaleDetailTabVOList) {
            warehouseCodeSet.add(detailTab.getReturnWarehouseCode());
        }
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("code", warehouseCodeSet);
        List<WarehouseInfo> warehouseInfoList = warehouseInfoService.selectByExample(example);
        if (!AssertUtil.collectionIsEmpty(warehouseInfoList)) {
            for (AfterSaleDetailTabVO detailTab : afterSaleDetailTabVOList) {
                for (WarehouseInfo warehouseInfo : warehouseInfoList) {
                    if (StringUtils.equals(detailTab.getReturnWarehouseCode(), warehouseInfo.getCode())) {
                        detailTab.setReturnWarehouseName(warehouseInfo.getWarehouseName());
                        break;
                    }
                }
            }
        }
    }

    private List<AfterSaleOrderDetail> getAfterSaleOrderDetail(List<AfterSaleOrder> afterSaleOrderList) {
        Set<String> scmShopCodeSet = new HashSet<>();
        for (AfterSaleOrder afterSaleOrder : afterSaleOrderList) {
            scmShopCodeSet.add(afterSaleOrder.getScmShopOrderCode());
        }
        Example example = new Example(AfterSaleOrderDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("scmShopOrderCode", scmShopCodeSet);
        List<AfterSaleOrderDetail> afterSaleOrderDetailList = afterSaleOrderDetailService.selectByExample(example);
        if (!AssertUtil.collectionIsEmpty(afterSaleOrderDetailList)) {
            return afterSaleOrderDetailList;
        }
        return new ArrayList<>();
    }
}
