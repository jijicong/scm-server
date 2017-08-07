package org.trc.biz.impl.purchase;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.trc.biz.purchase.IPurchaseDetailBiz;
import org.trc.domain.category.Brand;
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.PurchaseOrderDetailException;
import org.trc.service.category.IBrandService;
import org.trc.service.purchase.IPurchaseDetailService;
import org.trc.service.purchase.IPurchaseOrderService;
import org.trc.util.AssertUtil;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sone on 2017/5/25.
 */
@Service("purchaseDetailBiz")
public class PurchaseDetailBiz implements IPurchaseDetailBiz{

    @Resource
    private IPurchaseDetailService iPurchaseDetailService;

    @Resource
    private IPurchaseOrderService purchaseOrderService;

    @Resource
    private IBrandService iBrandService;

    @Override
    public List<PurchaseDetail> purchaseDetailListByPurchaseCode(String purchaseOrderCode) throws Exception{

        AssertUtil.notBlank(purchaseOrderCode,"采购单的编码为空!");
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setPurchaseOrderCode(purchaseOrderCode);
        purchaseOrder = purchaseOrderService.selectOne(purchaseOrder);
        AssertUtil.notNull(purchaseOrder.getId(),"查询采购单信息为空!");
        List<PurchaseDetail>  purchaseDetailList = purchaseDetailList(purchaseOrder.getId());
        if(CollectionUtils.isEmpty(purchaseDetailList)){
            throw new PurchaseOrderDetailException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_DETAIL_QUERY_EXCEPTION,"采购单商品信息查询失败!");
        }
        return purchaseDetailList;
    }

    @Override
    public List<PurchaseDetail> purchaseDetailList(Long purchaseId) throws Exception {

        AssertUtil.notNull(purchaseId,"采购单id为空,采购明细查询失败");

        PurchaseDetail purchaseDetailTT = new PurchaseDetail();

        purchaseDetailTT.setPurchaseId(purchaseId);

        List<PurchaseDetail> purchaseDetailList = iPurchaseDetailService.select(purchaseDetailTT);

        if(CollectionUtils.isEmpty(purchaseDetailList)){
            return new ArrayList<PurchaseDetail>();
        }
        //品牌的名称
        List<Long> brandIds = new ArrayList<>();
        //分类的全路径
        List<Long> categoryIds = new ArrayList<>();
        //获得所有分类的id 拼接，并且显示name的拼接--brand
        for (PurchaseDetail purchaseDetail: purchaseDetailList){
            categoryIds.add(purchaseDetail.getCategoryId());
            brandIds.add(purchaseDetail.getBrandId());
            purchaseDetail.setPurchasePriceD(new BigDecimal(purchaseDetail.getPurchasePrice()).divide(new BigDecimal(100)));
            purchaseDetail.setTotalPurchaseAmountD(new BigDecimal(purchaseDetail.getTotalPurchaseAmount()).divide(new BigDecimal(100)));
        }

        List<Brand> brandList = iBrandService.selectBrandList(brandIds);
        for (Brand brand :brandList) {
            for (PurchaseDetail purchaseDetail:purchaseDetailList) {
                if(brand.getId().equals(purchaseDetail.getBrandId())){
                    //purchaseDetail.setAllCategory(purchaseDetailTmp.getAllCategory());
                    //purchaseDetail.setAllCategoryName(purchaseDetailTmp.getAllCategoryName());
                    purchaseDetail.setBrandName(brand.getName());
                }
            }
        }

        List<PurchaseDetail> temp = purchaseOrderService.selectAllCategory(categoryIds);
        //categoryId    allCategoryName    allCategory >>>>>>分类全路径赋值
        for (PurchaseDetail purchaseDetailTmp: temp) {
            for (PurchaseDetail purchaseDetail:purchaseDetailList) {
                if(purchaseDetailTmp.getCategoryId().equals(purchaseDetail.getCategoryId())){
                    //purchaseDetail.setAllCategory(purchaseDetailTmp.getAllCategory());
                    purchaseDetail.setAllCategoryName(purchaseDetailTmp.getAllCategoryName());
                }
            }
        }

        return purchaseDetailList;

    }

}
