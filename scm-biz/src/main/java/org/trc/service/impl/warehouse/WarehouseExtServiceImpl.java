package org.trc.service.impl.warehouse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.trc.constants.SupplyConstants;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.enums.*;
import org.trc.form.order.WarehouseOwernSkuDO;
import org.trc.form.warehouse.ScmInventoryQueryItem;
import org.trc.form.warehouse.ScmInventoryQueryRequest;
import org.trc.form.warehouse.ScmInventoryQueryResponse;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.service.warehouse.IWarehouseExtService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.service.warehouseInfo.IWarehouseItemInfoService;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;
import org.trc.util.ResponseAck;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service("warehouseExtService")
public class WarehouseExtServiceImpl implements IWarehouseExtService {

    private Logger logger = LoggerFactory.getLogger(WarehouseExtServiceImpl.class);

    @Autowired
    private IWarehouseInfoService warehouseInfoService;
    @Autowired
    private IWarehouseItemInfoService warehouseItemInfoService;
    @Autowired
    private IWarehouseApiService warehouseApiService;


    @Override
    public List<ScmInventoryQueryResponse> getWarehouseInventory(List<String> skuCodes,String inventoryType) {
        //获取可用仓库信息
        List<WarehouseInfo> warehouseInfoList = getWarehouseInfo();
        List<String> warehouseInfoIds = new ArrayList<>();
        for(WarehouseInfo warehouseInfo2: warehouseInfoList){
            warehouseInfoIds.add(warehouseInfo2.getId().toString());
        }
        //获取仓库绑定商品信息
        List<WarehouseItemInfo> warehouseItemInfoList = getWarehouseItemInfo(skuCodes, warehouseInfoIds);
        /**
         * 获取仓库库存
         */
        List<WarehouseOwernSkuDO> warehouseOwernSkuDOListQimen = new ArrayList<>();
        List<WarehouseOwernSkuDO> warehouseOwernSkuDOListJingdong = new ArrayList<>();
        List<WarehouseOwernSkuDO> warehouseOwernSkuDOListZy = new ArrayList<>();
        for(WarehouseInfo warehouseInfo: warehouseInfoList){
            List<WarehouseItemInfo> tmpWarehouseItemInfoList = new ArrayList<>();
            WarehouseOwernSkuDO warehouseOwernSkuDO = new WarehouseOwernSkuDO();
            for(WarehouseItemInfo warehouseItemInfo: warehouseItemInfoList){
                if(warehouseItemInfo.getWarehouseInfoId().longValue() == warehouseInfo.getId().longValue()){
                    tmpWarehouseItemInfoList.add(warehouseItemInfo);
                }
            }
            if(tmpWarehouseItemInfoList.size() > 0){
                warehouseOwernSkuDO.setWarehouseInfo(warehouseInfo);
                warehouseOwernSkuDO.setWarehouseItemInfoList(tmpWarehouseItemInfoList);
                if(StringUtils.equals(WarehouseOperateNatureEnum.OUTER_WAREHOUSE.getCode(), warehouseInfo.getOperationalNature())){//第三方仓库
                    if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(),warehouseInfo.getIsThroughWms().toString())){//奇门仓储
                        warehouseOwernSkuDO.setWarehouseType(WarehouseTypeEnum.Qimen.getCode());
                        warehouseOwernSkuDOListQimen.add(warehouseOwernSkuDO);
                    }else{//京东仓储
                        warehouseOwernSkuDO.setWarehouseType(WarehouseTypeEnum.Jingdong.getCode());
                        warehouseOwernSkuDOListJingdong.add(warehouseOwernSkuDO);
                    }
                }else if(StringUtils.equals(WarehouseOperateNatureEnum.SELF_WAREHOUSE.getCode(), warehouseInfo.getOperationalNature())){//自营仓库
                    warehouseOwernSkuDO.setWarehouseType(WarehouseTypeEnum.Zy.getCode());
                    warehouseOwernSkuDOListZy.add(warehouseOwernSkuDO);
                }
            }
        }

        List<ScmInventoryQueryResponse> scmInventoryQueryResponseList = new ArrayList<>();
        if(warehouseOwernSkuDOListQimen.size() > 0){
            scmInventoryQueryResponseList.addAll(getWarehouseSkuStock(WarehouseTypeEnum.Qimen.getCode(), warehouseOwernSkuDOListQimen,inventoryType));
        }
        if(warehouseOwernSkuDOListJingdong.size() > 0){
            scmInventoryQueryResponseList.addAll(getWarehouseSkuStock(WarehouseTypeEnum.Jingdong.getCode(), warehouseOwernSkuDOListJingdong,inventoryType));
        }
        if(warehouseOwernSkuDOListZy.size() > 0){
            scmInventoryQueryResponseList.addAll(getWarehouseSkuStock(WarehouseTypeEnum.Zy.getCode(), warehouseOwernSkuDOListZy,inventoryType));
        }
        if(!CollectionUtils.isEmpty(scmInventoryQueryResponseList)){
            for(ScmInventoryQueryResponse response : scmInventoryQueryResponseList){
                for(WarehouseItemInfo itemInfo: warehouseItemInfoList){
                    if(StringUtils.equals(response.getOwnerCode(), itemInfo.getWarehouseOwnerId()) &&
                            StringUtils.equals(response.getWarehouseCode(), itemInfo.getWmsWarehouseCode()) &&
                            StringUtils.equals(response.getItemId(), itemInfo.getWarehouseItemId())){
                        response.setItemCode(itemInfo.getSkuCode());
                        break;
                    }
                }
            }
        }
        return scmInventoryQueryResponseList;
    }

    private List<ScmInventoryQueryResponse> getWarehouseSkuStock(String warehouseType, List<WarehouseOwernSkuDO> warehouseOwernSkuDOList,String inventoryType){
        ScmInventoryQueryRequest request = new ScmInventoryQueryRequest();
        request.setWarehouseType(warehouseType);
        List<ScmInventoryQueryItem> scmInventoryQueryItemList = new ArrayList<>();
        for(WarehouseOwernSkuDO warehouseOwernSkuDO: warehouseOwernSkuDOList){
            for(WarehouseItemInfo warehouseItemInfo: warehouseOwernSkuDO.getWarehouseItemInfoList()){
                ScmInventoryQueryItem item = new ScmInventoryQueryItem();
                if (WarehouseTypeEnum.Zy.getCode().equals(warehouseType)) {// 自营仓逻辑处理
                	item.setWarehouseCode(warehouseOwernSkuDO.getWarehouseInfo().getCode());
                	if (StringUtils.isNotBlank(inventoryType)) {
                		item.setInventoryType(inventoryType);
                		if (JingdongInventoryTypeEnum.SALE.getCode().equals(inventoryType)) {// 可销售暂时先默认设置为良品
                			item.setInventoryStatus(JingdongInventoryStateEnum.GOOD.getCode());//良品
                		}
                	}
                } else {
                	item.setWarehouseCode(warehouseOwernSkuDO.getWarehouseInfo().getWmsWarehouseCode());
                }
                item.setOwnerCode(warehouseOwernSkuDO.getWarehouseInfo().getWarehouseOwnerId());
                item.setItemCode(warehouseItemInfo.getSkuCode());
                item.setItemId(warehouseItemInfo.getWarehouseItemId());
                scmInventoryQueryItemList.add(item);
            }
        }
        request.setScmInventoryQueryItemList(scmInventoryQueryItemList);
        AppResult<List<ScmInventoryQueryResponse>> appResult = warehouseApiService.inventoryQuery(request);
        List<ScmInventoryQueryResponse> scmInventoryQueryResponseList = new ArrayList<>();
        if(StringUtils.equals(ResponseAck.SUCCESS_CODE, appResult.getAppcode())){
            scmInventoryQueryResponseList = (List<ScmInventoryQueryResponse>) appResult.getResult();
        }
        return scmInventoryQueryResponseList;
    }


    /**
     * 获取可用仓库信息
     * @return
     */
    @Override
    public List<WarehouseInfo> getWarehouseInfo(){
        WarehouseInfo warehouseInfo = new WarehouseInfo();
        warehouseInfo.setOwnerWarehouseState(OwnerWarehouseStateEnum.NOTICE_SUCCESS.getCode());//通知成功
        warehouseInfo.setIsValid(ZeroToNineEnum.ONE.getCode());//启用
        List<WarehouseInfo> warehouseInfoList = warehouseInfoService.select(warehouseInfo);
        if(CollectionUtils.isEmpty(warehouseInfoList)){
            logger.error("没有查询到可用仓库");
        }
        return warehouseInfoList;
    }

    /**
     * 获取仓库绑定商品信息
     * @param skuCodes
     * @param warehouseInfoIds
     * @return
     */
    private List<WarehouseItemInfo> getWarehouseItemInfo(List<String> skuCodes, List<String> warehouseInfoIds){
        if(CollectionUtils.isEmpty(skuCodes) || CollectionUtils.isEmpty(warehouseInfoIds)){
            return new ArrayList<>();
        }
        //查询跟仓库绑定过的商品,其中没有绑定过的在后面的拆单时会归到异常订单里面
        Example example = new Example(WarehouseItemInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("warehouseInfoId", warehouseInfoIds);
        criteria.andIn("skuCode", skuCodes);
        criteria.andEqualTo("itemType", ItemTypeEnum.NOEMAL.getCode());//正常的商品
        criteria.andEqualTo("noticeStatus", ItemNoticeStateEnum.NOTICE_SUCCESS.getCode());//通知成功
        List<WarehouseItemInfo> warehouseItemInfoList = warehouseItemInfoService.selectByExample(example);
        //AssertUtil.notEmpty(warehouseItemInfoList, "还没有跟仓库绑定商品");
        if(CollectionUtils.isEmpty(warehouseItemInfoList)){
            logger.error(String.format("自采sku[%s]尚未与仓库完成绑定", StringUtils.join(skuCodes, SupplyConstants.Symbol.COMMA)));
        }
        return warehouseItemInfoList;
    }

    @Override
    public WarehouseTypeEnum getWarehouseType(String warehouseCode) {
        WarehouseInfo warehouseInfo = new WarehouseInfo();
        warehouseInfo.setCode(warehouseCode);
        warehouseInfo = warehouseInfoService.selectOne(warehouseInfo);
        AssertUtil.notNull(warehouseInfo, String.format("根据仓库编码%s查询仓库信息为空", warehouseCode));
        if(StringUtils.equals(WarehouseOperateNatureEnum.OUTER_WAREHOUSE.getCode(), warehouseInfo.getOperationalNature())){//第三方仓库
            if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(),warehouseInfo.getIsThroughWms().toString())){//奇门仓储
                return WarehouseTypeEnum.Qimen;
            }else{//京东仓储
                return WarehouseTypeEnum.Jingdong;
            }
        }else if(StringUtils.equals(WarehouseOperateNatureEnum.SELF_WAREHOUSE.getCode(), warehouseInfo.getOperationalNature())){//自营仓库
            return WarehouseTypeEnum.Zy;
        }
        return null;
    }

    @Override
    public String getWmsWarehouseCode(String warehouseCode) {
        WarehouseInfo warehouseInfo = new WarehouseInfo();
        warehouseInfo.setCode(warehouseCode);
        warehouseInfo = warehouseInfoService.selectOne(warehouseInfo);
        AssertUtil.notNull(warehouseInfo, String.format("根据仓库编码%s查询仓库信息为空", warehouseCode));
        return warehouseInfo.getWmsWarehouseCode();
    }

    @Override
    public List<WarehouseItemInfo> getWarehouseItemInfos(List<String> skuCodes) {
        //获取可用仓库信息
        List<WarehouseInfo> warehouseInfoList = getWarehouseInfo();
        List<String> warehouseInfoIds = new ArrayList<>();
        for(WarehouseInfo warehouseInfo2: warehouseInfoList){
            warehouseInfoIds.add(warehouseInfo2.getId().toString());
        }
        //获取仓库绑定商品信息
        return getWarehouseItemInfo(skuCodes, warehouseInfoIds);
    }
}
