package org.trc.biz.impl.purchase;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trc.biz.purchase.IPurchaseOrderBiz;
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.supplier.Supplier;
import org.trc.enums.CommonExceptionEnum;
import org.trc.exception.ParamValidException;
import org.trc.form.purchase.ItemForm;
import org.trc.service.purchase.IPurchaseOrderService;
import org.trc.util.CommonUtil;
import org.trc.util.Pagenation;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/5/25.
 */
@Service("purchaseOrderBiz")
public class PurchaseOrderBiz implements IPurchaseOrderBiz{

    private final static Logger LOGGER = LoggerFactory.getLogger(PurchaseOrderBiz.class);
    @Resource
    private IPurchaseOrderService purchaseOrderService;

    @Override
    public List<Supplier> findSuppliersByUserId(String userId) throws Exception {
        //有没有对应的渠道。渠道有没有对应的供应商
        if (StringUtils.isBlank(userId)) {
            String msg = CommonUtil.joinStr("根据userId查询供应商的参数userId为空").toString();
            LOGGER.error(msg);
            throw  new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        List<Supplier> supplierList = purchaseOrderService.findSuppliersByUserId(userId);
        if(supplierList==null){
            supplierList = new ArrayList<Supplier>();
        }
        return supplierList;
    }

    @Override
    public Pagenation<PurchaseDetail> findPurchaseDetailBySupplierCode(String supplierCode, ItemForm form, Pagenation<PurchaseDetail> page) throws Exception {
        PageHelper.startPage(page.getPageNo(), page.getPageSize());
        Map<String, Object> map = new HashMap<>();
        map.put("supplierCode",supplierCode);
        map.put("name", form.getName());
        map.put("skuCode", form.getSkuCode());
        map.put("BrandName", form.getBrandName());
        List<PurchaseDetail>  purchaseDetailList = purchaseOrderService.selectItemsBySupplierCode(map);
        int count = purchaseOrderService.selectCountItems(map);
        page.setTotalCount(count);
        page.setResult(purchaseDetailList);
        return page;
    }
}
