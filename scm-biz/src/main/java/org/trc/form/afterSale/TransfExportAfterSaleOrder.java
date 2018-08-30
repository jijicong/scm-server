package org.trc.form.afterSale;

import com.google.common.collect.Lists;
import org.trc.util.DateUtils;
import org.trc.util.StringUtil;

import java.util.List;
import java.util.Objects;

/**
 * @Auther: hzluoxingcheng
 * @Date: 2018/8/29 18:47
 * @Description:
 */
public class TransfExportAfterSaleOrder {

    public static  List<ExceptorAfterSaleOrder> getExceptorAfterSaleOrder(List<AfterSaleOrderVO> result ){
        if(Objects.equals(null,result) || result.isEmpty()){
            return  null;
        }
        List<ExceptorAfterSaleOrder> list = Lists.newArrayList();
        for(AfterSaleOrderVO vo :result ){
            List<AfterSaleOrderDetailVO>  deList = vo.getAfterSaleOrderDetailVOList();
            if(Objects.equals(null,deList) || deList.isEmpty()){
                continue;
            }
            for(AfterSaleOrderDetailVO dvo:deList){
                ExceptorAfterSaleOrder ord = new ExceptorAfterSaleOrder();
                ord.setAfterSaleCode(vo.getAfterSaleCode());
                ord.setCreateTime(DateUtils.formatDateTime(vo.getCreateTime()));
                ord.setDeliverWarehouseName(dvo.getDeliverWarehouseName());
                ord.setLogisticsCorporation(vo.getLogisticsCorporation());
                ord.setRefundAmont(dvo.getRefundAmont());
                ord.setReturnNum(dvo.getReturnNum());
                ord.setReturnWarehouseName(vo.getReturnWarehouseName());
                ord.setScmShopOrderCode(vo.getScmShopOrderCode());
                ord.setSellCodeName(vo.getSellCodeName());
                ord.setShopName(vo.getShopName());
                ord.setSkuCode(dvo.getSkuCode());
                ord.setSkuName(dvo.getSkuName());
                ord.setSpecNatureInfo(dvo.getSpecNatureInfo());
                ord.setStatus(vo.getStatus());
                ord.setWaybillNumber(vo.getWaybillNumber());
                list.add(ord);
            }
        }
        return list;

    }



}
