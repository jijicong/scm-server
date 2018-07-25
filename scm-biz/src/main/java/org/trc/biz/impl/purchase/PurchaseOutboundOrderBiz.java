package org.trc.biz.impl.purchase;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.trc.biz.purchase.IPurchaseOutboundOrderBiz;
import org.trc.domain.purchase.PurchaseOutboundOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.form.purchase.PurchaseOutboundOrderForm;
import org.trc.service.purchase.IPurchaseOutboundOrderService;
import org.trc.service.supplier.ISupplierService;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description〈〉
 *
 * @author hzliuwei
 * @create 2018/7/24
 */
public class PurchaseOutboundOrderBiz implements IPurchaseOutboundOrderBiz {

    @Autowired
    private IPurchaseOutboundOrderService purchaseOutboundOrderService;

    @Autowired
    private ISupplierService supplierService;

    /**
     * 查询采购退货单列表
     *
     * @param form        查询条件
     * @param page        分页数据
     * @param channelCode
     * @return
     */
    @Override
    public Pagenation<PurchaseOutboundOrder> purchaseOutboundOrderPageList(PurchaseOutboundOrderForm form, Pagenation<PurchaseOutboundOrder> page, String channelCode) {

        AssertUtil.notBlank(channelCode, "未获得授权");
        setSelectCondition(form, channelCode);
        return null;
    }

    private Example setSelectCondition(PurchaseOutboundOrderForm form, String channelCode) {

        Example example = new Example(PurchaseOutboundOrder.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("channelCode", channelCode);

        if(StringUtils.isNotBlank(form.getPurchaseOutboundOrderCode())){
            criteria.andLike("purchaseOutboundOrderCode", "%" + form.getPurchaseOutboundOrderCode() + "%");
        }
        if(StringUtils.isNotBlank(form.getSupplierName())){
            List<Supplier> suppliers = supplierService.selectSupplierByName(form.getSupplierName());
            if(CollectionUtils.isEmpty(suppliers)){
                return null;
            }

            List<String> supplierCodes = suppliers.stream().map(Supplier::getSupplierCode).collect(Collectors.toList());

            criteria.andIn("supplierCode", supplierCodes);
        }
        return example;
    }
}
