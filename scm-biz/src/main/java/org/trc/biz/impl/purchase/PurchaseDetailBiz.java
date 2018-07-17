package org.trc.biz.impl.purchase;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.trc.biz.category.ICategoryBiz;
import org.trc.biz.purchase.IPurchaseDetailBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Brand;
import org.trc.domain.goods.Items;
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.PurchaseOrderStatusEnum;
import org.trc.exception.PurchaseOrderDetailException;
import org.trc.service.category.IBrandService;
import org.trc.service.impl.goods.ItemsService;
import org.trc.service.impl.purchase.WarehouseNoticeService;
import org.trc.service.impl.warehouseNotice.WarehouseNoticeDetailsService;
import org.trc.service.purchase.IPurchaseDetailService;
import org.trc.service.purchase.IPurchaseOrderService;
import org.trc.util.AssertUtil;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sone on 2017/5/25.
 */
@Service("purchaseDetailBiz")
public class PurchaseDetailBiz implements IPurchaseDetailBiz{

    @Autowired
    private IPurchaseDetailService iPurchaseDetailService;

    @Autowired
    private IPurchaseOrderService purchaseOrderService;

    @Autowired
    private IBrandService iBrandService;

    @Autowired
    private ICategoryBiz categoryBiz;

    @Autowired
    private ItemsService itemsService;

    @Autowired
    private WarehouseNoticeDetailsService warehouseNoticeDetailsService;

    @Autowired
    private WarehouseNoticeService warehouseNoticeService;

    @Override
    @Cacheable(value = SupplyConstants.Cache.PURCHASE_ORDER)
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
    @Cacheable(value = SupplyConstants.Cache.PURCHASE_ORDER)
    public List<PurchaseDetail> purchaseDetailList(Long purchaseId) throws Exception {

        AssertUtil.notNull(purchaseId,"采购单id为空,采购明细查询失败");

        PurchaseDetail purchaseDetailTT = new PurchaseDetail();

        purchaseDetailTT.setPurchaseId(purchaseId);

        List<PurchaseDetail> purchaseDetailList = iPurchaseDetailService.select(purchaseDetailTT);

        if(CollectionUtils.isEmpty(purchaseDetailList)){
            return new ArrayList<PurchaseDetail>();
        }

        PurchaseOrder purchaseOrder = purchaseOrderService.selectByPrimaryKey(purchaseId);

        //品牌的名称
        List<Long> brandIds = new ArrayList<>();
        //获得所有分类的id 拼接，并且显示name的拼接--brand
        for (PurchaseDetail purchaseDetail: purchaseDetailList){
            brandIds.add(purchaseDetail.getBrandId());
            if(purchaseDetail.getPurchasePrice() != null){
                purchaseDetail.setPurchasePriceD(purchaseDetail.getPurchasePrice());
            }else {
                purchaseDetail.setPurchasePriceD(null);
            }
            if(purchaseDetail.getTotalPurchaseAmount() != null){
                purchaseDetail.setTotalPurchaseAmountD(purchaseDetail.getTotalPurchaseAmount());
            }else {
                purchaseDetail.setTotalPurchaseAmountD(null);
            }

            //当采购单状态为入库通知状态才有 商品入库状态和仓库反馈入库信息
            if(StringUtils.equals(PurchaseOrderStatusEnum.WAREHOUSE_NOTICE.getCode(), purchaseOrder.getStatus())){
                Example warehouseNoticeExample = new Example(WarehouseNotice.class);
                warehouseNoticeExample.createCriteria().andEqualTo("purchaseOrderCode", purchaseOrder.getPurchaseOrderCode());
                List<WarehouseNotice> warehouseNotices = warehouseNoticeService.selectByExample(warehouseNoticeExample);
                if(warehouseNotices != null && !warehouseNotices.isEmpty()){
                    Example warehouseNoticeDetailsExample = new Example(WarehouseNoticeDetails.class);
                    warehouseNoticeDetailsExample.createCriteria().andEqualTo("warehouseNoticeCode", warehouseNotices.get(0).getWarehouseNoticeCode());
                    List<WarehouseNoticeDetails> details = warehouseNoticeDetailsService.selectByExample(warehouseNoticeDetailsExample);
                    for (WarehouseNoticeDetails detail : details){
                        if(StringUtils.equals(detail.getSkuCode(), purchaseDetail.getSkuCode())){
                            String storageTime = null;
                            if(detail.getStorageTime() != null){
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                storageTime = format.format(detail.getStorageTime());
                            }
                            purchaseDetail.setStorageTime(storageTime);
                            purchaseDetail.setActualStorageQuantity(detail.getActualStorageQuantity());
                            purchaseDetail.setNormalStorageQuantity(detail.getNormalStorageQuantity());
                            purchaseDetail.setDefectiveStorageQuantity(detail.getDefectiveStorageQuantity());
                            break;
                        }
                    }
                }
            }
        }

        List<Brand> brandList = iBrandService.selectBrandList(brandIds);
        for (Brand brand :brandList) {
            for (PurchaseDetail purchaseDetail:purchaseDetailList) {
                if(brand.getId().equals(purchaseDetail.getBrandId())){
                    purchaseDetail.setBrandName(brand.getName());
                }
            }
        }
        handCategoryName(purchaseDetailList);
        setSkuQuality(purchaseDetailList);
        return purchaseDetailList;

    }

    private void handCategoryName(List<PurchaseDetail> purchaseDetailList) throws Exception{
        for (PurchaseDetail purchaseDetail: purchaseDetailList) {
            purchaseDetail.setAllCategoryName(categoryBiz.getCategoryName(purchaseDetail.getCategoryId()));
        }
    }

    /**
     * 设置sku保质期
     * @param purchaseDetailList
     */
    private void setSkuQuality(List<PurchaseDetail> purchaseDetailList){
        List<String> spuCodes = new ArrayList<>();
        for(PurchaseDetail detail: purchaseDetailList){
            spuCodes.add(detail.getSpuCode());
        }
        Example example = new Example(Items.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("spuCode", spuCodes);
        List<Items> itemsList = itemsService.selectByExample(example);
        if(!CollectionUtils.isEmpty(itemsList)){
            for(PurchaseDetail detail: purchaseDetailList){
                for(Items items: itemsList){
                    if(StringUtils.equals(detail.getSpuCode(), items.getSpuCode())){
                        detail.setIsQuality(items.getIsQuality());
                        detail.setQualityDay(items.getQualityDay());
                        break;
                    }
                }
            }
        }
    }

}
