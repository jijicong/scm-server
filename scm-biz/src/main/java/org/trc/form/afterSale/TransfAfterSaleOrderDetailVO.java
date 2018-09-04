package org.trc.form.afterSale;

import com.google.common.collect.Lists;
import org.trc.domain.afterSale.AfterSaleOrderDetail;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Auther: hzluoxingcheng
 * @Date: 2018/8/29 16:37
 * @Description: 转换成售后单子表展示数据
 */
public class TransfAfterSaleOrderDetailVO {


      public static  AfterSaleOrderDetailVO getAfterSaleOrderDetailVO(AfterSaleOrderDetail afterSaleOrderDetail,String spuCode){
          if(Objects.equals(null,afterSaleOrderDetail)){
              return null;
          }
          AfterSaleOrderDetailVO  afterSaleOrderDetailVO = new AfterSaleOrderDetailVO();
          afterSaleOrderDetailVO.setReturnNum(afterSaleOrderDetail.getReturnNum());
          afterSaleOrderDetailVO.setSkuCode(afterSaleOrderDetail.getSkuCode());
          afterSaleOrderDetailVO.setSkuName(afterSaleOrderDetail.getSkuName());
          afterSaleOrderDetailVO.setSpecNatureInfo(afterSaleOrderDetail.getSpecNatureInfo());
          afterSaleOrderDetailVO.setAfterSaleCode(afterSaleOrderDetail.getAfterSaleCode());
          afterSaleOrderDetailVO.setSpuCode(spuCode);
          afterSaleOrderDetailVO.setRefundAmont(afterSaleOrderDetail.getRefundAmont());
          afterSaleOrderDetailVO.setPicture(afterSaleOrderDetail.getPicture());
          return afterSaleOrderDetailVO;
      }

      public static  List<AfterSaleOrderDetailVO> getAfterSaleOrderDetailVOList(List<AfterSaleOrderDetail> afterSaleOrderDetails,Map<String,String> skuSpuMap){
            if(Objects.equals(null,afterSaleOrderDetails) || afterSaleOrderDetails.isEmpty()){
                return Lists.newArrayList();
            }
            List<AfterSaleOrderDetailVO> volist = Lists.newArrayList();
            for(AfterSaleOrderDetail d:afterSaleOrderDetails){
                String spuCode = skuSpuMap.get(d.getSkuCode());
                volist.add(getAfterSaleOrderDetailVO(d,spuCode));
            }
            return volist;
      }


}
