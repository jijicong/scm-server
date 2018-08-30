package org.trc.form.afterSale;

import com.google.common.collect.Lists;
import org.trc.domain.System.SellChannel;
import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.domain.warehouseInfo.WarehouseInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Auther: hzluoxingcheng
 * @Date: 2018/8/29 16:08
 * @Description:组装售后单列表展示数据
 */
public class TransfAfterSaleOrderVO {

    public static  AfterSaleOrderVO getAfterSaleOrderVO(AfterSaleOrder afterSaleOrder,WarehouseInfo searWarehouseInfo,List<AfterSaleOrderDetailVO> detailVOList ,SellChannel sellChannel){
        if(Objects.equals(null,afterSaleOrder)){
            return null;
        }
        if(Objects.equals(null,detailVOList) || detailVOList.isEmpty()){
            return null;
        }
        Map<String,List<AfterSaleOrderDetailVO>> detailvoMap = new HashMap<>();
        for(AfterSaleOrderDetailVO vo:detailVOList){
            String afterSaleCode = vo.getAfterSaleCode();
            List<AfterSaleOrderDetailVO> list =  detailvoMap.get(afterSaleCode);
            if(Objects.equals(null,list)){
                list = Lists.newArrayList();
            }
            list.add(vo);
            detailvoMap.put(afterSaleCode,list);
        }
        AfterSaleOrderVO afterSaleOrderVO = new AfterSaleOrderVO();
        //创建时间
        afterSaleOrderVO.setCreateTime(afterSaleOrder.getCreateTime());
        //系统订单号
        afterSaleOrderVO.setScmShopOrderCode(afterSaleOrder.getScmShopOrderCode());
        //售后订单号
        afterSaleOrderVO.setAfterSaleCode(afterSaleOrder.getAfterSaleCode());
        //销售渠道编码
        afterSaleOrderVO.setSellCode(afterSaleOrder.getSellCode());
        //渠道名称
        afterSaleOrderVO.setSellCodeName(afterSaleOrder.getSellName());
        //店铺名称
        afterSaleOrderVO.setShopName(afterSaleOrder.getShopName());
        //赋值退货仓库名称
        afterSaleOrderVO.setReturnWarehouseName(searWarehouseInfo.getWarehouseName());
        //快递公司名称
        afterSaleOrderVO.setLogisticsCorporation(afterSaleOrder.getLogisticsCorporation());
        // 运单号
        afterSaleOrderVO.setWaybillNumber(afterSaleOrder.getWaybillNumber());
        //售后单状态
        afterSaleOrderVO.setStatus(afterSaleOrder.getStatus());
        //售后单子表列表
        afterSaleOrderVO.setAfterSaleOrderDetailVOList(detailvoMap.get(afterSaleOrder.getAfterSaleCode()));
        if(!Objects.equals(null,sellChannel)){
            //渠道名称
            afterSaleOrderVO.setSellCodeName(sellChannel.getSellName());
        }
        return afterSaleOrderVO;
    }

}
