package org.trc.biz.impl.goods;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.qimen.api.request.InventoryQueryRequest;
import com.qimen.api.response.InventoryQueryResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.trc.biz.goods.ISkuRelationBiz;
import org.trc.biz.impl.trc.TrcBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.SkuStock;
import org.trc.domain.goods.Skus;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.supplier.SupplierApply;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.*;
import org.trc.exception.ParamValidException;
import org.trc.exception.QimenException;
import org.trc.form.order.InventoryQueryItemDO;
import org.trc.service.IQimenService;
import org.trc.service.goods.IExternalItemSkuService;
import org.trc.service.goods.ISkuStockService;
import org.trc.service.goods.ISkusService;
import org.trc.service.impl.goods.ItemsService;
import org.trc.service.supplier.ISupplierApplyService;
import org.trc.service.supplier.ISupplierService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;
import org.trc.util.CommonUtil;

import org.trc.util.ResponseAck;
import tk.mybatis.mapper.entity.Example;

/**
 * @author: Ding
 * @mail: hzdzf@tairanchina.com
 * @create: 2017-06-19 16:01
 */
@Service("skuRelationBiz")
public class SkuRelationBiz implements ISkuRelationBiz {

    private Logger logger = LoggerFactory.getLogger(SkuRelationBiz.class);

    @Autowired
    @Qualifier("skusService")
    private ISkusService skusService;

    @Autowired
    @Qualifier("externalItemSkuService")
    private IExternalItemSkuService externalItemSkuService;
    @Autowired
    private ISkuStockService skuStockService;
    @Autowired
    private ISupplierService supplierService;
    @Autowired
    private ItemsService itemsService;
    @Autowired
    private ISupplierApplyService supplierApplyService;
    @Autowired
    private IWarehouseInfoService warehouseInfoService;
    @Autowired
    private IQimenService qimenService;

    @Override
    public List<Skus> getSkuInformation(String skuCode, String channelCode) {
        AssertUtil.notBlank(skuCode,"查询SKU信息sckCode不能为空");
        AssertUtil.isTrue(skuCode.indexOf(TrcBiz.COMMA_ZH) == -1, "分隔多个sku编码必须是英文逗号");
        String[] skuCodes = skuCode.split(SupplyConstants.Symbol.COMMA);
        for(String _skuCode: skuCodes) {
            AssertUtil.isTrue(_skuCode.startsWith(SupplyConstants.Goods.SKU_PREFIX), String.format("skuCode[%s]不是自采商品", _skuCode));
        }
        Example example = new Example(Skus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", Arrays.asList(skuCodes));
        List<Skus> skusList = skusService.selectByExample(example);
//        setSkuStock(skusList);

        //设置库存
        this.setStock(skusList, channelCode);
        // 将spu里面的相关信息设置到sku中，例如：spu商品主图mainPicture字段
        setSpuInfo(skusList);
        return skusList;
    }

    public void setStock(List<Skus> skusList, String channelCode){
        if(skusList == null || skusList.size() < 1){
            return;
        }
        //通过奇门获取库存信息
        List<InventoryQueryResponse.Item> itemList = this.getQimenStockByskuCode(this.getSkuCodes(skusList), channelCode);
        //合并同skuCode库存
        Map<String, Long> map = this.getSkuMap(itemList);
        //赋值stock
        this.setSkuStock(skusList, map);
    }

    //赋值stock
    private void setSkuStock(List<Skus> skusList, Map<String, Long> map){
        for(Skus skus : skusList){
            String skuCode = skus.getSkuCode();
            if(map.containsKey(skuCode)){
                skus.setStock(map.get(skuCode));
            }else{
                skus.setStock(0L);
            }
        }
    }

    private List<String> getSkuCodes(List<Skus> skusList){
        List<String> skuCodeList = new ArrayList<String>();
        for(Skus s : skusList){
            skuCodeList.add(s.getSkuCode());
        }
        return skuCodeList;
    }
    
    private void setSpuInfo (List<Skus> skusList) {
        StringBuilder sb = new StringBuilder();
        for (Skus skus: skusList) {
            sb.append("\"").append(skus.getSpuCode()).append("\"").append(SupplyConstants.Symbol.COMMA);
        }
        if (sb.length() > 0) {
            Example example = new Example(Items.class);
            Example.Criteria criteria = example.createCriteria();
            String ids = sb.substring(0, sb.length()-1);
            String condition = String.format("spu_code in (%s)", ids);
            criteria.andCondition(condition);
            List<Items> itemsList = itemsService.selectByExample(example);
            for (Skus sku: skusList) {
            	sku.setName(sku.getSkuName());// 将skuName赋值给name，提供给泰然城用
                for(Items item: itemsList){
                    if(StringUtils.equals(sku.getSpuCode(), item.getSpuCode())){
                        sku.setMainPicture(item.getMainPicture());
                    }
                }
            }
        }
    }

    private void setSkuStock(List<Skus> skusList){
        StringBuilder sb = new StringBuilder();
        for(Skus skus: skusList){
            sb.append("\"").append(skus.getSkuCode()).append("\"").append(SupplyConstants.Symbol.COMMA);
        }
        if(sb.length() > 0){
            Example example = new Example(SkuStock.class);
            Example.Criteria criteria = example.createCriteria();
            String ids = sb.substring(0, sb.length()-1);
            String condition = String.format("sku_code in (%s)", ids);
            criteria.andCondition(condition);
            List<SkuStock> skuStockList = skuStockService.selectByExample(example);
            for(Skus skus: skusList){
                for(SkuStock skuStock: skuStockList){
                    if(StringUtils.equals(skus.getSkuCode(), skuStock.getSkuCode())){
                        skus.setStock(skuStock.getAvailableInventory());
                    }
                }
            }
        }
    }

    @Override
    public List<ExternalItemSku> getExternalSkuInformation(String skuCode,String channelCode) {
        AssertUtil.notBlank(skuCode,"查询SKU信息sckCode不能为空");
        AssertUtil.isTrue(skuCode.indexOf(TrcBiz.COMMA_ZH) == -1, "分隔多个sku编码必须是英文逗号");
        String[] skuCodes = skuCode.split(SupplyConstants.Symbol.COMMA);
        for(String _skuCode: skuCodes){
            AssertUtil.isTrue(_skuCode.startsWith(SupplyConstants.Goods.EXTERNAL_SKU_PREFIX), String.format("skuCode[%s]不是代发商品", _skuCode));
        }
        Example example = new Example(ExternalItemSku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", Arrays.asList(skuCodes));
        criteria.andEqualTo("isValid", ValidEnum.VALID.getCode());
        if (StringUtils.isNotBlank(channelCode)){
            //查询到当前渠道下审核通过的一件代发供应商
            Example example2 = new Example(SupplierApply.class);
            Example.Criteria criteria2 = example2.createCriteria();
            criteria2.andEqualTo("status", ZeroToNineEnum.TWO.getCode());
                criteria2.andEqualTo("channelCode",channelCode);
            List<SupplierApply> supplierApplyList = supplierApplyService.selectByExample(example2);
            AssertUtil.notEmpty(supplierApplyList,"当前渠道没有一件代发供应商!");
            List<String>  supplierInterfaceIdList = new ArrayList<>();
            for (SupplierApply supplierApply:supplierApplyList) {
                Supplier supplier = new Supplier();
                supplier.setSupplierCode(supplierApply.getSupplierCode());
                supplier.setSupplierKindCode(SupplyConstants.Supply.Supplier.SUPPLIER_ONE_AGENT_SELLING);
                supplier=  supplierService.selectOne(supplier);
                if (null!=supplier){
                    supplierInterfaceIdList.add(supplier.getSupplierInterfaceId());
                }
            }
            criteria.andIn("supplierCode",supplierInterfaceIdList);
        }
        List<ExternalItemSku> externalItemSkuList = externalItemSkuService.selectByExample(example);
        if(!CollectionUtils.isEmpty(externalItemSkuList)){
            setMoneyWeight(externalItemSkuList);
            setSupplierInfo(externalItemSkuList);
        }
        return externalItemSkuList;
    }

    /**
     * 设置代付供应商信息
     * @param externalItemSkuList
     */
    private void setSupplierInfo(List<ExternalItemSku> externalItemSkuList){
        Set<String> supplierCodes = new HashSet<>();
        for(ExternalItemSku externalItemSku: externalItemSkuList){
            supplierCodes.add(externalItemSku.getSupplierCode());
        }
        Example example = new Example(Supplier.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("supplierInterfaceId", supplierCodes);
        criteria.andEqualTo("supplierKindCode", SupplyConstants.Supply.Supplier.SUPPLIER_ONE_AGENT_SELLING);//一件代付供应商
        List<Supplier> supplierList = supplierService.selectByExample(example);
        for(ExternalItemSku externalItemSku: externalItemSkuList){
            for(Supplier supplier: supplierList){
                if(StringUtils.equals(externalItemSku.getSupplierCode(), supplier.getSupplierInterfaceId())){
                    externalItemSku.setSupplierCode2(externalItemSku.getSupplierCode());
                    externalItemSku.setSupplierCode(supplier.getSupplierCode());
                    externalItemSku.setSupplierName(supplier.getSupplierName());
                    break;
                }
            }
        }
    }

    /**
     * 设置金额和重量
     * @param externalItemSkuList
     */
    private void setMoneyWeight(List<ExternalItemSku> externalItemSkuList){
        for(ExternalItemSku externalItemSku: externalItemSkuList){
            if(null != externalItemSku.getSupplierPrice())
                externalItemSku.setSupplierPrice(CommonUtil.getMoneyLong(externalItemSku.getSupplierPrice()));
            if(null != externalItemSku.getSupplyPrice())
                externalItemSku.setSupplyPrice(CommonUtil.getMoneyLong(externalItemSku.getSupplyPrice()));
            if(null != externalItemSku.getMarketReferencePrice())
                externalItemSku.setMarketReferencePrice(CommonUtil.getMoneyLong(externalItemSku.getMarketReferencePrice()));
            if(null != externalItemSku.getWeight())
                externalItemSku.setWeight(CommonUtil.getWeightLong(externalItemSku.getWeight()));
        }
    }

    /**
     * 根据业务线获取所有仓库信息
     * @param channelCode
     * @return
     */
    private List<WarehouseInfo> getWharehouseInfoListByChannelCode(String channelCode){
        WarehouseInfo warehouseInfo = new WarehouseInfo();
        if(StringUtils.isNotEmpty(channelCode)){
            warehouseInfo.setChannelCode(channelCode);
        }
        warehouseInfo.setIsDelete(Integer.parseInt(ZeroToNineEnum.ZERO.getCode()));
        warehouseInfo.setOwnerWarehouseState(ZeroToNineEnum.ONE.getCode());
        return warehouseInfoService.select(warehouseInfo);
    }

    /**
     * 根据skuCode和业务线获取奇门库存
     * @return
     */
    private List<InventoryQueryResponse.Item> getQimenStockByskuCode(List<String> skuCodes, String channelCode){
        //根据业务线获取所有仓库信息
        List<WarehouseInfo> warehouseInfoList = this.getWharehouseInfoListByChannelCode(channelCode);
        AssertUtil.notNull(warehouseInfoList,"当前业务线没有对应的库存仓库信息!");

        //调用奇门库存查询接口校验绑定过商品的库存
        InventoryQueryRequest request = new InventoryQueryRequest();
        InventoryQueryRequest.Criteria criteria = null;
        List<InventoryQueryRequest.Criteria> criteriaList = new ArrayList<>();
        for(WarehouseInfo info : warehouseInfoList){
            String warehouseOwnerId = info.getWarehouseOwnerId();
            for(String skuCode : skuCodes){
                criteria = new InventoryQueryRequest.Criteria();
                criteria.setInventoryType(InventoryTypeEnum.ZP.getCode());//正品
                criteria.setItemCode(skuCode);
                criteria.setOwnerCode(warehouseOwnerId);
                criteriaList.add(criteria);
            }
        }
        request.setCriteriaList(criteriaList);
        AppResult appResult = qimenService.inventoryQuery(request);
        if(!StringUtils.equals(appResult.getAppcode(), ResponseAck.SUCCESS_CODE)){
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("调用奇门库存查询接口失败, %s", appResult.getDatabuffer()));
        }
        AssertUtil.notNull(appResult.getResult(), "调用奇门库存查询接口返回结果数据为空");
        AssertUtil.notBlank(appResult.getResult().toString(), "调用奇门库存查询接口返回结果数据为空");
        InventoryQueryResponse inventoryQueryResponse = null;
        try{
            inventoryQueryResponse = JSON.parseObject(appResult.getResult().toString()).toJavaObject(InventoryQueryResponse.class);
        }catch (ClassCastException e) {
            String msg = String.format("调用奇门库存查询接口返回库存结果信息格式错误,%s", e.getMessage());
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        if(inventoryQueryResponse.isSuccess()){
            return inventoryQueryResponse.getItems();
        }else {
            throw new QimenException(ExceptionEnum.QIMEN_INVENTORY_QUERY_EXCEPTION, inventoryQueryResponse.getMessage());
        }
    }

    /**
     * 整合sku库存信息
     * @param itemList
     * @return
     */
    private Map<String, Long> getSkuMap(List<InventoryQueryResponse.Item> itemList){
        Map<String, Long> skuMap = new HashMap<String, Long>();
        for(InventoryQueryResponse.Item item : itemList){
            String skuCode = item.getItemCode();
            Long stockNum = item.getQuantity();
            if(skuMap.containsKey(skuCode)){
                skuMap.put(skuCode, stockNum + skuMap.get(skuCode));
            }else{
                skuMap.put(skuCode, stockNum);
            }
        }
        return skuMap;
    }
}
