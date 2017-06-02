package org.trc.biz.impl.purchase;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.purchase.IPurchaseOrderBiz;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.PurchaseOrderStatusEnum;
import org.trc.enums.ValidEnum;
import org.trc.exception.ConfigException;
import org.trc.exception.ParamValidException;
import org.trc.form.purchase.ItemForm;
import org.trc.service.System.IChannelService;
import org.trc.service.impower.IUserAccreditInfoService;
import org.trc.service.purchase.IPurchaseDetailService;
import org.trc.service.purchase.IPurchaseOrderService;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
    @Resource
    private IPurchaseDetailService purchaseDetailService;
    @Resource
    private IUserAccreditInfoService userAccreditInfoService ;

    private final static String  SERIALNAME = "CGD";

    private final static Integer LENGTH = 5;

    @Resource
    private ISerialUtilService serialUtilService;

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
    //保存采购单--状态为暂存
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void savePurchaseOrder(PurchaseOrder purchaseOrder, String userId) throws Exception {
        AssertUtil.notNull(purchaseOrder,"采购单对象为空");
        ParamsUtil.setBaseDO(purchaseOrder);
        int count = 0;
        //根据用户的id查询渠道
        UserAccreditInfo user = new UserAccreditInfo();
        user.setUserId(userId);
        user = userAccreditInfoService.selectOne(user);//查询用户对应的渠道
        purchaseOrder.setChannelCode(user.getChannelCode());
        purchaseOrder.setPurchaseOrderCode(serialUtilService.generateCode(LENGTH,SERIALNAME, DateUtils.dateToCompactString(purchaseOrder.getCreateTime())));
        purchaseOrder.setIsValid(ValidEnum.VALID.getCode());
        purchaseOrder.setStatus(PurchaseOrderStatusEnum.HOLD.getCode());//设置暂存状态
        purchaseOrder.setEnterWarehouseNotice(PurchaseOrderStatusEnum.TO_BE_NOTIFIED.getCode());//设置入库通知的状态
        BigDecimal pp = purchaseOrder.getPaymentProportion();
        if(pp!=null){
            BigDecimal bd = new BigDecimal("100");
            pp=pp.divide(bd);
            if(pp.doubleValue()>1 || pp.doubleValue()<=0){ //范围校验
                String msg = CommonUtil.joinStr("采购单保存,付款比例超出范围").toString();
                LOGGER.error(msg);
                throw new ConfigException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
            }
            purchaseOrder.setPaymentProportion(pp);
        }
        count = purchaseOrderService.insert(purchaseOrder);
        if (count<1){
            String msg = CommonUtil.joinStr("采购单保存,数据库操作失败").toString();
            LOGGER.error(msg);
            throw new ConfigException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }
        String purchaseOrderStrs = purchaseOrder.getGridValue();//采购商品详情的字符串

        Long orderId = purchaseOrder.getId();

        String code = purchaseOrder.getPurchaseOrderCode();

        System.out.println(orderId);

        savePurchaseDetail(purchaseOrderStrs,orderId,code);//保存采购商品
    }

    /**
     *保存采购商品
     */
    public void savePurchaseDetail(String purchaseOrderStrs,Long orderId,String code) throws Exception{

        if(StringUtils.isBlank(purchaseOrderStrs)){
            String msg = CommonUtil.joinStr("保存采购商品的信息为空").toString();
            LOGGER.error(msg);
            throw  new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }

        List<PurchaseDetail> purchaseDetailList = JSONArray.parseArray(purchaseOrderStrs,PurchaseDetail.class);
        for (PurchaseDetail purchaseDetail : purchaseDetailList) {
            purchaseDetail.setPurchaseId(orderId);
            purchaseDetail.setPurchaseOrderCode(code);
            ParamsUtil.setBaseDO(purchaseDetail);
        }
        int count = 0;
        count = purchaseDetailService.insertList(purchaseDetailList);
        if (count<1){
            String msg = CommonUtil.joinStr("采购商品保存,数据库操作失败").toString();
            LOGGER.error(msg);
            throw new ConfigException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }

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
