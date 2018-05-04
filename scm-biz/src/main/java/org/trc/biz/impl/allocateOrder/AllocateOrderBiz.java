package org.trc.biz.impl.allocateOrder;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.trc.biz.allocateOrder.IAllocateOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.allocateOrder.AllocateOrder;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.AllocateOrderEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.AllocateOrderException;
import org.trc.exception.WarehouseInfoException;
import org.trc.form.AllocateOrder.AllocateOrderForm;
import org.trc.service.allocateOrder.IAllocateOrderService;
import org.trc.service.allocateOrder.IAllocateSkuDetailService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import tk.mybatis.mapper.entity.Example;

@Service("allocateOrderBiz")
public class AllocateOrderBiz implements IAllocateOrderBiz {

    private Logger logger = LoggerFactory.getLogger(AllocateOrderBiz.class);
    @Autowired
    private IAllocateOrderService allocateOrderService;
    @Autowired
    private ISerialUtilService serialUtilService;
    @Autowired
    private IWarehouseInfoService warehouseInfoService;
    @Autowired
    private IAllocateSkuDetailService allocateSkuDetailService;
    /**
     * 调拨单分页查询
     */
    @Override
    @Cacheable(value = SupplyConstants.Cache.ALLOCATE_ORDER)
    public Pagenation<AllocateOrder> allocateOrderPage(AllocateOrderForm form, 
    		Pagenation<AllocateOrder> page) {

        Example example = new Example(AllocateOrder.class);
        Example.Criteria criteria = example.createCriteria();
        
        //调拨单编号
        if (!StringUtils.isBlank(form.getAllocateOrderCode())) {
            criteria.andEqualTo("allocateOrderCode", form.getAllocateOrderCode());
        }
        
        //调出仓库
        if (!StringUtils.isBlank(form.getOutWarehouseCode())) {
            criteria.andEqualTo("outWarehouseCode", form.getOutWarehouseCode());
        }
        
        //调入仓库
        if (!StringUtils.isBlank(form.getInWarehouseCode())) {
        	criteria.andEqualTo("inWarehouseName", form.getInWarehouseCode());
        }
        
        //出入库状态
        if (!StringUtils.isBlank(form.getInOutStatus())) {
            criteria.andEqualTo("inOutStatus", form.getInOutStatus());
        }
        
        //单据状态
        if (!StringUtils.isBlank(form.getOrderStatus())) {
        	criteria.andEqualTo("orderStatus", form.getOrderStatus());
        	
        }
        
        //创建日期开始
        if (!StringUtils.isBlank(form.getCreateTimeStart())) {
            criteria.andGreaterThanOrEqualTo("createTime", form.getCreateTimeStart());
        }
        
        //创建日期结束
        if (!StringUtils.isBlank(form.getCreateTimeEnd())) {
            criteria.andLessThanOrEqualTo("createTime", form.getCreateTimeEnd());
        }
        
        //更新日期开始
        if (!StringUtils.isBlank(form.getUpdateTimeStart())) {
        	criteria.andGreaterThanOrEqualTo("updateTime", form.getUpdateTimeStart());
        }
        
        //更新日期结束
        if (!StringUtils.isBlank(form.getUpdateTimeEnd())) {
        	criteria.andLessThanOrEqualTo("updateTime", form.getUpdateTimeEnd());
        }
      //  example.orderBy("orderStatus").asc();
        example.orderBy("updateTime").desc();
        example.setOrderByClause("field(orderStatus,0,3,1,2,4,5)");
        Pagenation<AllocateOrder> pagenation = allocateOrderService.pagination(example, page, form);

        return page;
    }


	@Override
	public void saveAllocateOrder(AllocateOrder allocateOrder, String delsIds, String isReview,
			String skuDetail, AclUserAccreditInfo aclUserAccreditInfo) {
		
		// 设置调拨单初始状态-暂存
		String orderStatus = AllocateOrderEnum.AllocateOrderStatusEnum.INIT.getCode();
		
		/**
		 * 提交审核的情况 (isReview = 1)
		 * 商品明细要求至少选择一项商品
		 */
		if (ZeroToNineEnum.ONE.getCode().equals(isReview)) {
			
			orderStatus = AllocateOrderEnum.AllocateOrderStatusEnum.AUDIT.getCode();
			
			if (StringUtils.isBlank(skuDetail)) {
				throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_REVIEW_SAVE_EXCEPTION, 
						"请至少选择一项商品");
			} else {
				JSONArray skuDetailArray = JSONArray.parseArray(skuDetail);
				if (skuDetailArray.size() == 0) {
					throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_REVIEW_SAVE_EXCEPTION, 
							"请至少选择一项商品");
				}
				
			}
		}
		if (StringUtils.isBlank(allocateOrder.getAllocateOrderCode())) { // 新增
			/**
			 * 插入调拨单 
			 */
            String code = serialUtilService.generateCode(SupplyConstants.Serial.ALLOCATE_ORDER_LENGTH, 
            		SupplyConstants.Serial.ALLOCATE_ORDER_CODE,
            			DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
            allocateOrder.setAllocateOrderCode(code);
            
            // 设置详细地址
            setDetailAddress(allocateOrder);
            
			// 设置调拨单初始状态-暂存或者提交审核
			allocateOrder.setOrderStatus(orderStatus);
			// 设置调拨单出入库初始状态-初始
			allocateOrder.setInOutStatus(AllocateOrderEnum.AllocateOrderInOutStatusEnum.INIT.getCode());
			allocateOrder.setCreateOperator(aclUserAccreditInfo.getUserId());
			allocateOrder.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
			int insertCount = allocateOrderService.insert(allocateOrder);
			
			/**
			 * 插入调拨单商品明细
			 */
			if (insertCount > 0 && StringUtils.isNotBlank(skuDetail)) {
				List<AllocateSkuDetail> insertList = new ArrayList<>();
				JSONArray skuDetailArray = JSONArray.parseArray(skuDetail);
				for (Object obj : skuDetailArray) {
					JSONObject jsonObj = (JSONObject) obj;
					AllocateSkuDetail insertDetail = new AllocateSkuDetail();
					// 设置调拨单详情数据
					setAllocateSkuDetail(aclUserAccreditInfo, insertDetail, code, jsonObj);
					insertList.add(insertDetail);
				}
				if (!insertList.isEmpty()) {
					allocateSkuDetailService.insertList(insertList);
				}
				
			}
		} else { // 修改
			allocateOrder.setInOutStatus(null);
			allocateOrder.setOrderStatus(orderStatus);
			setDetailAddress(allocateOrder);
			int updateCount = allocateOrderService.updateByPrimaryKeySelective(allocateOrder);
			if (updateCount > 0) {
				
				/**
				 * 存在删除的数据
				 */
				if (StringUtils.isNotBlank(delsIds)) {
					String[] idArray = delsIds.split(",");
					allocateSkuDetailService.deleteByIds(idArray);
				}
				
				if (StringUtils.isNotBlank(skuDetail)) {
					List<AllocateSkuDetail> updateList = new ArrayList<>();
					List<AllocateSkuDetail> addList = new ArrayList<>();
					JSONArray skuDetailArray = JSONArray.parseArray(skuDetail);
					for (Object obj : skuDetailArray) {
						JSONObject jsonObj = (JSONObject) obj;
						AllocateSkuDetail detail = new AllocateSkuDetail();
						// 修改的数据
						if (StringUtils.isNotEmpty(jsonObj.getString("id"))) {
							detail.setInventoryType(jsonObj.getString("inventoryType"));
							detail.setPlanAllocateNum(jsonObj.getLong("planAllocateNum"));
							updateList.add(detail);
						} else {
							// 新增的数据
							setAllocateSkuDetail(aclUserAccreditInfo, detail, 
									allocateOrder.getAllocateOrderCode(), jsonObj);

							addList.add(detail);
						}
					}
					if (!updateList.isEmpty()) {
						allocateSkuDetailService.updateSkuDetailList(updateList);
					}
					if (!addList.isEmpty()) {
						allocateSkuDetailService.insertList(addList);
					}
				}
			}
		}
	}
	
	@Override
	public void deleteAllocateOrder(String orderId) {
		AllocateOrder allocateOrder = new AllocateOrder();
		allocateOrder.setAllocateOrderCode(orderId);
		allocateOrder.setIsDeleted(ZeroToNineEnum.ONE.getCode());
		int count = allocateOrderService.updateByPrimaryKeySelective(allocateOrder);
		if (count < 1) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_DELETE_EXCEPTION, 
					"删除调拨单失败");
		}
		AllocateSkuDetail allocateSkuDetail = new AllocateSkuDetail();
		allocateSkuDetail.setAllocateOrderCode(orderId);
		allocateSkuDetail.setIsDeleted(ZeroToNineEnum.ONE.getCode());
		//allocateSkuDetailService.updateByExampleSelective(t, example);
	}

	@Override
	public void dropAllocateOrder(String orderId) {

	}


	/**
	 * 新增和修改时设置调拨单详情
	 */
	private void setAllocateSkuDetail(AclUserAccreditInfo aclUserAccreditInfo,
			AllocateSkuDetail detail, String allocateOrderCode, JSONObject jsonObj) {
		detail.setAllocateOrderCode(allocateOrderCode);
		detail.setSkuName(jsonObj.getString("skuName"));
		detail.setSkuCode(jsonObj.getString("skuCode"));
		detail.setSpecNatureInfo(jsonObj.getString("specNatureInfo"));
		detail.setBarCode(jsonObj.getString("barCode"));
		detail.setBrandName(jsonObj.getString("brandName"));
		detail.setBrandCode(jsonObj.getString("brandCode"));
		detail.setInventoryType(jsonObj.getString("inventoryType"));
		detail.setPlanAllocateNum(jsonObj.getLong("planAllocateNum"));
		detail.setInStatus(AllocateOrderEnum.AllocateOrderSkuInStatusEnum.INIT.getCode());
		detail.setOutStatus(AllocateOrderEnum.AllocateOrderSkuOutStatusEnum.INIT.getCode());
		detail.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
		detail.setCreateOperator(aclUserAccreditInfo.getUserId());	
		
	}


	/**
	 * @param allocateOrder
	 * 新增和修改时获取出入仓库的详细地址
	 */
	private void setDetailAddress(AllocateOrder allocateOrder) {
		
        WarehouseInfo queryRecord = new WarehouseInfo();
        queryRecord.setCode(allocateOrder.getAllocateInOrderCode());
		WarehouseInfo whInfo = warehouseInfoService.selectOne(queryRecord);
		AssertUtil.objNotBlank(whInfo, "调入仓库不存在");
		// 设置调入详细地址
		allocateOrder.setReceiveAddress(whInfo.getAddress());
		
		queryRecord.setCode(allocateOrder.getOutWarehouseCode());
		whInfo = warehouseInfoService.selectOne(queryRecord);
		AssertUtil.objNotBlank(whInfo, "调出仓库不存在");
		// 设置调出详细地址
		allocateOrder.setSenderAddress(whInfo.getAddress());
	}


}
