package org.trc.biz.impl.allocateOrder;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.allocateOrder.IAllocateOrderBiz;
import org.trc.biz.category.ICategoryBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.domain.allocateOrder.AllocateOrder;
import org.trc.domain.allocateOrder.AllocateOutOrder;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.domain.category.Brand;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.Skus;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.enums.*;
import org.trc.enums.AllocateOrderEnum.AllocateOrderInventoryStatusEnum;
import org.trc.enums.AllocateOrderEnum.AllocateOutOrderStatusEnum;
import org.trc.enums.allocateOrder.AllocateInOrderStatusEnum;
import org.trc.exception.AllocateOrderException;
import org.trc.exception.AllocateOutOrderException;
import org.trc.exception.WarehouseInfoException;
import org.trc.form.AllocateOrder.AllocateItemForm;
import org.trc.form.AllocateOrder.AllocateOrderForm;
import org.trc.form.AllocateOrder.QuerySkuInventory;
import org.trc.form.warehouse.ScmInventoryQueryItem;
import org.trc.form.warehouse.ScmInventoryQueryRequest;
import org.trc.form.warehouse.ScmInventoryQueryResponse;
import org.trc.service.allocateOrder.IAllocateOrderExtService;
import org.trc.service.allocateOrder.IAllocateOrderService;
import org.trc.service.allocateOrder.IAllocateOutOrderService;
import org.trc.service.allocateOrder.IAllocateSkuDetailService;
import org.trc.service.category.IBrandService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.goods.IItemsService;
import org.trc.service.goods.ISkusService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.jingdong.ICommonService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.service.warehouseInfo.IWarehouseItemInfoService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private IAllocateOutOrderService allocateOutOrderService;
    @Autowired
    private IAclUserAccreditInfoService aclUserAccreditInfoService;
    @Autowired
    private IWarehouseItemInfoService warehouseItemInfoService;
    @Autowired
    private ICategoryBiz categoryBiz;
    @Autowired
    private IBrandService brandService;
    @Autowired
    private IItemsService itemsService;
    @Autowired
    private ISkusService skusService;
	@Autowired
	private IAllocateOrderExtService allocateOrderExtService;
	@Autowired
	private ILogInfoService logInfoService;
	@Autowired
	private ICommonService commonService;
	@Autowired
	private IWarehouseApiService warehouseApiService;
	
    /**
     * 调拨单分页查询
     */
    @Override
   // @Cacheable(value = SupplyConstants.Cache.ALLOCATE_ORDER)
    public Pagenation<AllocateOrder> allocateOrderPage(AllocateOrderForm form, 
    		Pagenation<AllocateOrder> page) {

        Example example = new Example(AllocateOrder.class);
        Example.Criteria criteria = example.createCriteria();
        
        criteria.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
        
        //审核状态非空则表示 审核页面
        String auditStatus = form.getAuditStatus();
        if (StringUtils.isNotBlank(auditStatus)) {
        	
        	if (AllocateOrderEnum.AllocateOrderAuditStatusEnum.ALL.getCode().equals(auditStatus)) {
        		// 全部
                List<String> statusList = new ArrayList<>();
                statusList.add(AllocateOrderEnum.AllocateOrderStatusEnum.AUDIT.getCode());
                statusList.add(AllocateOrderEnum.AllocateOrderStatusEnum.PASS.getCode());
                statusList.add(AllocateOrderEnum.AllocateOrderStatusEnum.REJECT.getCode());
                criteria.andIn("auditStatus", statusList);
                
        	} else if (AllocateOrderEnum.AllocateOrderAuditStatusEnum.WAIT_AUDIT.getCode().equals(auditStatus)) {
        		// 待审核
                criteria.andEqualTo("auditStatus", AllocateOrderEnum.AllocateOrderStatusEnum.AUDIT.getCode());
                
        	} else if (AllocateOrderEnum.AllocateOrderAuditStatusEnum.FINISH_AUDIT.getCode().equals(auditStatus)) {
        		// 已审核
                List<String> statusList = new ArrayList<>();
                statusList.add(AllocateOrderEnum.AllocateOrderStatusEnum.PASS.getCode());
                statusList.add(AllocateOrderEnum.AllocateOrderStatusEnum.REJECT.getCode());
                criteria.andIn("auditStatus", statusList);
        	} else {
        		throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION, 
						"审核状态错误");
        	}
        	
        	example.orderBy("submitTime").desc();
        	
            //提交审核日期开始
            if (!StringUtils.isBlank(form.getSubmitTimeStart())) {
            	criteria.andGreaterThanOrEqualTo("submitTime", form.getSubmitTimeStart() + " 00:00:00");
            }
            
            //提交审核日期结束
            if (!StringUtils.isBlank(form.getSubmitTimeEnd())) {
            	criteria.andLessThanOrEqualTo("submitTime", form.getSubmitTimeEnd() + " 23:59:59");
            }
            
            //提交人
            if (!StringUtils.isBlank(form.getSubmitOperatorName())) {
                Example userExample = new Example(AclUserAccreditInfo.class);
                Example.Criteria userCriteria = userExample.createCriteria();
                
                userCriteria.andLike("name", "%" + form.getSubmitOperatorName() + "%");
                
                List<AclUserAccreditInfo> userList = aclUserAccreditInfoService.selectByExample(userExample);
                if (CollectionUtils.isEmpty(userList)) {
                	return new Pagenation<AllocateOrder>();
                } else {
                	List<String> userIdList = new ArrayList<>();
                	for (AclUserAccreditInfo user : userList) {
                		userIdList.add(user.getUserId());
                	}
                	criteria.andIn("submitOperator", userIdList);
                }

                
            }
            
        } else {
        	// 调拨单管理页面
            //单据状态
            if (!StringUtils.isBlank(form.getOrderStatus())) {
            	criteria.andEqualTo("orderStatus", form.getOrderStatus());
            }
            //  example.orderBy("orderStatus").asc();
           // example.setOrderByClause("field(order_status,0)");
           // example.setOrderByClause("field(order_status,3)");
		//	example.orderBy("field(orderStatus,3,0)").asc();
//			example.orderBy("field(orderStatus,3)").desc();
			example.setOrderByClause("field(order_status,3,0) desc");
            example.orderBy("updateTime").desc();
        }
        
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
        	criteria.andEqualTo("inWarehouseCode", form.getInWarehouseCode());
        }
        
        //出入库状态
        if (!StringUtils.isBlank(form.getInOutStatus())) {
            criteria.andEqualTo("inOutStatus", form.getInOutStatus());
        }
        
        //创建日期开始
        if (!StringUtils.isBlank(form.getCreateTimeStart())) {
            criteria.andGreaterThanOrEqualTo("createTime", form.getCreateTimeStart() + " 00:00:00");
        }
        
        //创建日期结束
        if (!StringUtils.isBlank(form.getCreateTimeEnd())) {
            criteria.andLessThanOrEqualTo("createTime", form.getCreateTimeEnd() + " 23:59:59");
        }
        
        //更新日期开始
        if (!StringUtils.isBlank(form.getUpdateTimeStart())) {
        	criteria.andGreaterThanOrEqualTo("updateTime", form.getUpdateTimeStart() + " 00:00:00");
        }
        
        //更新日期结束
        if (!StringUtils.isBlank(form.getUpdateTimeEnd())) {
        	criteria.andLessThanOrEqualTo("updateTime", form.getUpdateTimeEnd() + " 23:59:59");
        }
        
        allocateOrderService.pagination(example, page, form);
        
        List<AllocateOrder> result = page.getResult();
        
        if (!result.isEmpty()) {
        	for (AllocateOrder order: result) {
        		AllocateOutOrder outOrder = new AllocateOutOrder();
        		outOrder.setAllocateOrderCode(order.getAllocateOrderCode());
        		AllocateOutOrder queryOutOrder = allocateOutOrderService.selectOne(outOrder);
        		if (queryOutOrder != null) {
        			order.setOutOrderStatus(queryOutOrder.getStatus());
        		}
        		
        		if (StringUtils.isNotBlank(order.getSubmitOperator())) {
        			AclUserAccreditInfo user = new AclUserAccreditInfo();
        			user.setUserId(order.getSubmitOperator());
        			AclUserAccreditInfo tmpUser = aclUserAccreditInfoService.selectOne(user);
        			order.setSubmitOperatorName(tmpUser == null? "" : tmpUser.getName());
        		}
        	}
        }
		allocateOrderExtService.setAllocateOrderOtherNames(page);
        return page;
    }
    
    
	private void checkJosIsGood(String skuDetail, String outWhCode, String inWhCode) {

		List<String> whiCodeList = Arrays.asList(outWhCode, inWhCode);
		
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria ca = example.createCriteria();
        ca.andIn("code", whiCodeList);
        ca.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());//未删除
        ca.andEqualTo("isValid", ZeroToNineEnum.ONE.getCode());//有效
		List<WarehouseInfo> warehouseList = warehouseInfoService.selectByExample(example);
		
		if (!CollectionUtils.isEmpty(warehouseList) && warehouseList.size() == 2) {
			
			WarehouseInfo whInfo = warehouseList.stream().filter(warehouse -> 
				OperationalNatureEnum.THIRD_PARTY.getCode().equals(warehouse.getOperationalNature())).findAny().orElse(null);
			
			if (whInfo != null) {
				//  存在京东仓的情况
				List<AllocateSkuDetail> skuDetailList = JSON.parseArray(skuDetail, AllocateSkuDetail.class);
				AllocateSkuDetail allocateSku = skuDetailList.stream().filter(sku -> 
					AllocateOrderInventoryStatusEnum.Quality.getCode().equals(sku.getInventoryType())).findAny().orElse(null);
				if (allocateSku != null) {
				//  存在残品调拨的情况
					throw new AllocateOutOrderException(ExceptionEnum.ALLOCATE_ORDER_REVIEW_SAVE_EXCEPTION, "京东仓暂时不允许残品调拨");
				}
			}
			
		} else {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_REVIEW_SAVE_EXCEPTION, 
					"出入仓库查询异常，是否已停用或者删除");
		}
		
		

	}

	@Override
	@Transactional
	public void saveAllocateOrder(AllocateOrder allocateOrder, String delsIds, String isReview,
			String skuDetail, AclUserAccreditInfo aclUserAccreditInfo) {
		// 设置调拨单初始状态-暂存
		String orderStatus = AllocateOrderEnum.AllocateOrderStatusEnum.INIT.getCode();
		
		String outWhCode = allocateOrder.getOutWarehouseCode();
		String inWhCode = allocateOrder.getInWarehouseCode();
		if (outWhCode.equalsIgnoreCase(inWhCode)) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_REVIEW_SAVE_EXCEPTION, 
					"调拨单的出入库仓库不能相同");
		}
		
		/**
		 * 提交审核的情况 (isReview = 1)
		 * 商品明细要求至少选择一项商品
		 */
		boolean isReviewFlg = false; // 是否是提交审核
		if (ZeroToNineEnum.ONE.getCode().equals(isReview)) {
			isReviewFlg = true;
			AssertUtil.notBlank(allocateOrder.getReceiver(), "收货人不能为空");
			AssertUtil.notBlank(allocateOrder.getReceiverMobile(), "收货人手机不能为空");
			AssertUtil.notBlank(allocateOrder.getSender(), "发货人不能为空");
			AssertUtil.notBlank(allocateOrder.getSenderMobile(), "发货人手机不能为空");
			
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
				
				//提交审核的时候，如果为京东仓，需要校验所有调拨商品为正品
				checkJosIsGood(skuDetail, outWhCode, inWhCode);
				
			}
			
				
			// 校验商品是否停用
			checkSkuIsVaild(skuDetail);
			allocateOrder.setSubmitOperator(aclUserAccreditInfo.getUserId());
			allocateOrder.setSubmitTime(new Date());
		}
		if (StringUtils.isBlank(allocateOrder.getAllocateOrderCode())) { // 新增
			/**
			 * 插入调拨单 
			 */
            String code = serialUtilService.generateCode(SupplyConstants.Serial.ALLOCATE_ORDER_LENGTH, 
            		SupplyConstants.Serial.ALLOCATE_ORDER_CODE,
            			DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
            allocateOrder.setAllocateOrderCode(code);
            
            // 设置仓库信息
            setDetailAddress(allocateOrder);
            
			// 设置调拨单初始状态-暂存或者提交审核
			allocateOrder.setOrderStatus(orderStatus);
			// 设置调拨单审核状态的初始状态-提交审核
			if (isReviewFlg) {
				allocateOrder.setAuditStatus(orderStatus);
			}
			// 设置调拨单出入库初始状态-初始
			allocateOrder.setInOutStatus(AllocateOrderEnum.AllocateOrderInOutStatusEnum.INIT.getCode());
			allocateOrder.setCreateOperator(aclUserAccreditInfo.getUserId());
			allocateOrder.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
			int insertCount = allocateOrderService.insertSelective(allocateOrder);
			
			/**
			 * 插入调拨单商品明细
			 */
//			if (insertCount > 0) {
				if (StringUtils.isNotBlank(skuDetail)) {
					List<AllocateSkuDetail> insertList = new ArrayList<>();
					JSONArray skuDetailArray = JSONArray.parseArray(skuDetail);
					for (Object obj : skuDetailArray) {
						JSONObject jsonObj = (JSONObject) obj;
						
						AllocateSkuDetail insertDetail = new AllocateSkuDetail();
						// 设置调拨单详情数据
						setAllocateSkuDetail(aclUserAccreditInfo, insertDetail, code, jsonObj, isReviewFlg);
						insertList.add(insertDetail);
					}
					if (!insertList.isEmpty()) {
						allocateSkuDetailService.insertList(insertList);
					}
				}
				
				logInfoService.recordLog(new AllocateOrder(), code, 
						aclUserAccreditInfo.getUserId(), LogOperationEnum.CREATE.getMessage(), null, ZeroToNineEnum.ZERO.getCode());
				
				if (isReviewFlg) {
					logInfoService.recordLog(new AllocateOrder(), code, 
							aclUserAccreditInfo.getUserId(), LogOperationEnum.SUBMIT.getMessage(), null, ZeroToNineEnum.ZERO.getCode());
				}
				
//			}
			
			
		} else { // 暂存编辑，审核驳回编辑
			AllocateOrder updateOrder = allocateOrderService.selectByPrimaryKey(allocateOrder.getAllocateOrderCode());
			AssertUtil.notNull(updateOrder, "修改的调拨单不存在");
			// 不是初始状态的不能提交审核
			if (!AllocateOrderEnum.AllocateOrderStatusEnum.INIT.getCode().equals(updateOrder.getOrderStatus())
					&& !AllocateOrderEnum.AllocateOrderStatusEnum.REJECT.getCode().equals(updateOrder.getOrderStatus())
					&& isReviewFlg) {
				throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_REVIEW_SAVE_EXCEPTION, 
						"当前调拨单状态不支持提交审核");
			}
			AllocateOrder record = new AllocateOrder();
			record.setAllocateOrderCode(allocateOrder.getAllocateOrderCode());
			record.setReceiver(allocateOrder.getReceiver());
			record.setSender(allocateOrder.getSender());
			record.setMemo(allocateOrder.getMemo());
			record.setReceiverMobile(allocateOrder.getReceiverMobile());
			record.setSenderMobile(allocateOrder.getSenderMobile());
			record.setInWarehouseCode(allocateOrder.getInWarehouseCode());
			record.setOutWarehouseCode(allocateOrder.getOutWarehouseCode());
			record.setSubmitOperator(allocateOrder.getSubmitOperator());
			record.setSubmitTime(allocateOrder.getSubmitTime());
			//record.setUpdateTime(Calendar.getInstance().getTime());
			if (isReviewFlg) {
				record.setOrderStatus(orderStatus);
				record.setAuditStatus(orderStatus);
			}
			// 设置仓库信息
			setDetailAddress(record);
			int updateCount = allocateOrderService.updateByPrimaryKeySelective(record);
//			if (updateCount > 0) {
				
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
							detail.setId(Long.valueOf(jsonObj.getString("id")));
							
							if (isReviewFlg) { // 提交审核的情况下需要校验
								if (jsonObj.getLong("planAllocateNum") == null ||
										StringUtils.isBlank(jsonObj.getString("skuCode")) ||
										StringUtils.isBlank(jsonObj.getString("inventoryType"))) {
									throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_REVIEW_SAVE_EXCEPTION, 
											"商品明细商品参数不完整");
								}

								//提交审核的情况下,查询实时库存
                                List<QuerySkuInventory> querySkuList = JSONArray.parseArray(skuDetail, QuerySkuInventory.class);

                                Map<String, Long> inventryMap = inventoryQuery(allocateOrder.getOutWarehouseCode(),  JSON.toJSONString(querySkuList));
                                if (inventryMap.size()==0){
									throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION,"所选调拨商品的调出仓商品均不存在");
								}else {
									for (String key : inventryMap.keySet()) {
										if(key.equals(jsonObj.getString("skuCode"))){
											Long inventoryNum = inventryMap.get(key);
											if(inventoryNum==null){
												throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION,"调出仓库不存在该件商品");
											}
											if(inventoryNum==0){
												throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION,"调出仓实时库存不能为0");
											}
											if ( jsonObj.getLong("planAllocateNum")>inventoryNum){
												throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION,"调拨数量不能大于调出仓库的实时库存");
											}
										}

									}
								}


							}
							detail.setInventoryType(jsonObj.getString("inventoryType"));
							detail.setPlanAllocateNum(jsonObj.getLong("planAllocateNum"));
							updateList.add(detail);
						} else {
							// 新增的数据
							setAllocateSkuDetail(aclUserAccreditInfo, detail, 
									allocateOrder.getAllocateOrderCode(), jsonObj, isReviewFlg);

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
				
				logInfoService.recordLog(new AllocateOrder(), allocateOrder.getAllocateOrderCode(), 
						aclUserAccreditInfo.getUserId(), LogOperationEnum.UPDATE.getMessage(), null, ZeroToNineEnum.ZERO.getCode());
				
				if (isReviewFlg) {
					logInfoService.recordLog(new AllocateOrder(), allocateOrder.getAllocateOrderCode(), 
							aclUserAccreditInfo.getUserId(), LogOperationEnum.SUBMIT.getMessage(), null, ZeroToNineEnum.ZERO.getCode());
				}
//			}
		}
	}
	
	/**
	 * 校验商品是否停用
	 * @param skuDetail
	 */
	private void checkSkuIsVaild(String skuDetail) {
		JSONArray skuDetailArray = JSONArray.parseArray(skuDetail);
		for (Object obj : skuDetailArray) {
			JSONObject jsonObj = (JSONObject) obj;
			String skuCode = jsonObj.getString("skuCode");
			if (StringUtils.isBlank(skuCode)) {
				throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_REVIEW_SAVE_EXCEPTION, 
						"商品明细商品参数不完整");
			}
			Skus record = new Skus();
			record.setSkuCode(skuCode);
			record.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
			Skus sku = skusService.selectOne(record);
			if (sku == null) {
				throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_REVIEW_SAVE_EXCEPTION, 
						"商品" + skuCode + "不存在");
			}
			if (!ZeroToNineEnum.ONE.getCode().equals(sku.getIsValid())) {
				throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_REVIEW_SAVE_EXCEPTION, 
						"商品" + skuCode + "已被停用");
			}
		}
	}


	@Override
	@Transactional
	public void deleteAllocateOrder(String orderId) {
		AllocateOrder queryOrder = allocateOrderService.selectByPrimaryKey(orderId);
		if (queryOrder == null) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_DELETE_EXCEPTION, 
					"未查到相关调拨单信息");
		}
		String status = queryOrder.getOrderStatus();
		/**
		 * 暂存,审核驳回 的状态才能删除
		 */
		if (!AllocateOrderEnum.AllocateOrderStatusEnum.INIT.getCode().equals(status) 
				&& !AllocateOrderEnum.AllocateOrderStatusEnum.REJECT.getCode().equals(status)) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_DELETE_EXCEPTION, 
					"当前调拨单状态不满足删除条件");
		}
		AllocateOrder allocateOrder = new AllocateOrder();
		allocateOrder.setAllocateOrderCode(orderId);
		allocateOrder.setIsDeleted(ZeroToNineEnum.ONE.getCode());
		int count = allocateOrderService.updateByPrimaryKeySelective(allocateOrder);
		if (count < 1) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_DELETE_EXCEPTION, 
					"删除调拨单失败");
		}
        Example example = new Example(AllocateSkuDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("allocateOrderCode", orderId);
		AllocateSkuDetail allocateSkuDetail = new AllocateSkuDetail();
		allocateSkuDetail.setAllocateOrderCode(orderId);
		allocateSkuDetail.setIsDeleted(ZeroToNineEnum.ONE.getCode());
		int countDetail = allocateSkuDetailService.updateByExampleSelective(allocateSkuDetail, example);
//		if (countDetail < 1) {
//			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_DELETE_EXCEPTION, 
//					"删除调拨单详情失败");
//		}
	}

	@Override
	@Transactional
	public void setDropAllocateOrder(String orderId, AclUserAccreditInfo userInfo) {
		AllocateOrder queryOrder = allocateOrderService.selectByPrimaryKey(orderId);
		if (queryOrder == null) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_DROP_EXCEPTION,
					"未查的相关调拨单信息");
		}
		String status = queryOrder.getOrderStatus();

		/**
		 * 以下两种情况满足其一可以作废：
		 * 1.审核通过
		 * 2.通知仓库 && (对应的调拨出库通知单的状态=“待通知出库”或“出库仓接收失败”)
		 */

		// 是否可以作废标识  true表示可以作废
		boolean canDropFlg = false;
		if (AllocateOrderEnum.AllocateOrderStatusEnum.PASS.getCode().equals(status)) {
			canDropFlg = true;
		}

		if (!canDropFlg) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_DROP_EXCEPTION,
					"当前调拨单状态不满足作废条件");
		}

		AllocateOrder allocateOrder = new AllocateOrder();
		allocateOrder.setAllocateOrderCode(orderId);
		allocateOrder.setOrderStatus(AllocateOrderEnum.AllocateOrderStatusEnum.DROP.getCode());
		int count = allocateOrderService.updateByPrimaryKeySelective(allocateOrder);
		if (count < 1) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_DROP_EXCEPTION,
					"作废调拨单失败");
		}

		logInfoService.recordLog(new AllocateOrder(), orderId,
				userInfo.getUserId(), LogOperationEnum.CANCEL.getMessage(), null, ZeroToNineEnum.ZERO.getCode());

	}
	
	@Override
	@Transactional
	public void dropAllocateOrder(String orderId, AclUserAccreditInfo userInfo) {
		AllocateOrder queryOrder = allocateOrderService.selectByPrimaryKey(orderId);
		if (queryOrder == null) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_DROP_EXCEPTION, 
					"未查的相关调拨单信息");
		}
		String status = queryOrder.getOrderStatus();
		
	    /**
	     * 以下两种情况满足其一可以作废：
	     * 1.审核通过
	     * 2.通知仓库 && (对应的调拨出库通知单的状态=“待通知出库”或“出库仓接收失败”)
	     */
		
		// 是否可以作废标识  true表示可以作废
		boolean canDropFlg = false;
		if (AllocateOrderEnum.AllocateOrderStatusEnum.PASS.getCode().equals(status)) {
			canDropFlg = true;
		} else if (AllocateOrderEnum.AllocateOrderStatusEnum.WAREHOUSE_NOTICE.getCode().equals(status)) {
			AllocateOutOrder queryOutOrder = new AllocateOutOrder();
			queryOutOrder.setAllocateOrderCode(orderId);
		    AllocateOutOrder outOrder = allocateOutOrderService.selectOne(queryOutOrder);
		    if ((outOrder != null)
					&& (AllocateOrderEnum.AllocateOutOrderStatusEnum.WAIT_NOTICE.getCode().equals(outOrder.getStatus())
							|| AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_RECEIVE_FAIL.getCode().equals(outOrder.getStatus()))) {
		    	canDropFlg = true;
		    }
		}
		
		if (!canDropFlg) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_DROP_EXCEPTION, 
					"当前调拨单状态不满足作废条件");
		}
		
		AllocateOrder allocateOrder = new AllocateOrder();
		allocateOrder.setAllocateOrderCode(orderId);
		allocateOrder.setOrderStatus(AllocateOrderEnum.AllocateOrderStatusEnum.DROP.getCode());
		int count = allocateOrderService.updateByPrimaryKeySelective(allocateOrder);
		if (count < 1) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_DROP_EXCEPTION, 
					"作废调拨单失败");
		}
		/**
		 * 调拨单作废，相应得出入库通知单都要变成取消状态
		 */
		
		allocateOrderExtService.discardedAllocateInOrder(orderId);
		allocateOrderExtService.discardedAllocateOutOrder(orderId);
		
		logInfoService.recordLog(new AllocateOrder(), orderId, 
				userInfo.getUserId(), LogOperationEnum.CANCEL.getMessage(), null, ZeroToNineEnum.ZERO.getCode());
		

	}
	
	private boolean isJosAllocateOrder (AllocateOrder queryOrder) {
		// 是否京东仓间调拨，true表示是
		boolean flg = true;
		List<String> whiCodeList = new ArrayList<>();
		whiCodeList.add(queryOrder.getInWarehouseCode());
		whiCodeList.add(queryOrder.getOutWarehouseCode());
		
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria ca = example.createCriteria();
        ca.andIn("code", whiCodeList);
		List<WarehouseInfo> warehouseList = warehouseInfoService.selectByExample(example);
		if (!CollectionUtils.isEmpty(warehouseList) && warehouseList.size() == 2) {
			for (WarehouseInfo warehouse : warehouseList) {
				if (OperationalNatureEnum.SELF_SUPPORT.getCode().equals(warehouse.getOperationalNature())) {
					flg = false;
				}
			}
		} else {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_NOTICE_WAREHOUSE_EXCEPTION, 
					"出入仓库查询异常");
		}
		return flg;
	}
	

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void noticeWarehouse(String orderId, AclUserAccreditInfo userInfo) {
		AllocateOrder queryOrder = allocateOrderService.selectByPrimaryKey(orderId);
		if (queryOrder == null) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_NOTICE_WAREHOUSE_EXCEPTION, 
					"未查的相关调拨单信息");
		}
		String status = queryOrder.getOrderStatus();
		/**
		 * 审核通过 的状态才能通知仓库
		 */
		if (!AllocateOrderEnum.AllocateOrderStatusEnum.PASS.getCode().equals(status)) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_NOTICE_WAREHOUSE_EXCEPTION, 
					"当前调拨单状态不满足通知仓库条件");
		}
		
		noticeWarehouseProcess(queryOrder, userInfo, null, AllocateOutOrderStatusEnum.WAIT_NOTICE);
		
		
		/**
		 * 京东仓库间的调拨暂时先按照发货出库单，采购单入库单的逻辑走，
		 * 故京东仓间的调拨接口暂时先不接
		 **/
/*		if (isJosAllocateOrder(queryOrder)) {
			*//**
			 * 出入仓都是京东仓的情况下，调用京东仓间调拨
			 *//*
			ScmJosAllocateOrderRequest req = new ScmJosAllocateOrderRequest();
			
			List<ScmAllocateOrderItem> itemList = new ArrayList<>();
			List<AllocateSkuDetail> skuList = allocateSkuDetailService.getDetailListByOrderCode(queryOrder.getAllocateOrderCode());
			
			List<String> skuCodeList = skuList.stream().map(
					detail -> detail.getSkuCode()).collect(Collectors.toList());
	        Example example = new Example(WarehouseItemInfo.class);
	        Example.Criteria ca = example.createCriteria();
	        ca.andIn("skuCode", skuCodeList);
	        List<WarehouseItemInfo> warehouseItemInfoList = warehouseItemInfoService.selectByExample(example);
			ScmAllocateOrderItem item = null;
			
			for (AllocateSkuDetail sku : skuList) {
				
	        	if (AllocateOrderInventoryStatusEnum.Quality.getCode().equals(sku.getInventoryType())) {
	        		throw new AllocateOutOrderException(ExceptionEnum.ALLOCATE_OUT_ORDER_NOTICE_EXCEPTION, "京东仓暂时不允许残品调拨");
	        	}
	        	
	        	for (WarehouseItemInfo info : warehouseItemInfoList) {
	        		if (StringUtils.equals(info.getSkuCode(), sku.getSkuCode())) {
	        			item = new ScmAllocateOrderItem();
	        			item.setPlanAllocateNum(sku.getPlanAllocateNum());// 计划配出数量 
	        			item.setSkuCode(sku.getSkuCode()); // 仓库商品编号 
	        			itemList.add(item);
	        			break;
	        		}
	        	}
	        	
			}
			req.setInWarehouseCode(queryOrder.getInWarehouseCode());
			req.setOutWarehouseCode(queryOrder.getOutWarehouseCode());
			req.setAllocateOrderCode(queryOrder.getAllocateOrderCode());
			req.setAllocateOrderItemList(itemList);
			
			AppResult<ScmJosAllocateOrderResponse> response = warehouseApiService.josAllocateOrderCreate(req);

			if (StringUtils.equals(response.getAppcode(), ResponseAck.SUCCESS_CODE)) {
				ScmJosAllocateOrderResponse rep = (ScmJosAllocateOrderResponse) response.getResult();
				// 生成出入通知单时 初始化状态为 出库仓接收成功
				noticeWarehouseProcess(queryOrder, userInfo, rep.getWmsAllocateOrderCode(), 
						AllocateOutOrderStatusEnum.OUT_RECEIVE_SUCC);
			} else {
				throw new AllocateOutOrderException(ExceptionEnum.ALLOCATE_OUT_ORDER_NOTICE_EXCEPTION, response.getDatabuffer());
			}
			
		} else {
			noticeWarehouseProcess(queryOrder, userInfo, null, AllocateOutOrderStatusEnum.WAIT_NOTICE);
			
		}*/
		
	}
	
	private void noticeWarehouseProcess (AllocateOrder queryOrder, AclUserAccreditInfo userInfo, String wmsAllocateOrderCode,
			AllocateOutOrderStatusEnum outstatus) {
		/**
		 * 生成出入库通知单
		 */
		AllocateOutOrder outOrder = new AllocateOutOrder();
		BeanUtils.copyProperties(queryOrder, outOrder);
		String outCode = allocateOrderExtService.createAllocateOutOrder(outOrder, userInfo.getUserId(),outstatus.getCode());
		
		AllocateInOrder inOrder = new AllocateInOrder();
		BeanUtils.copyProperties(queryOrder, inOrder);
		String inCode = allocateOrderExtService.createAllocateInOrder(inOrder, userInfo.getUserId());
		//allocateOutOrderService.insertSelective(outOrder);
		
		/**
		 * 更新调拨单
		 */
		String allocateOrderCode = queryOrder.getAllocateOrderCode();
		AllocateOrder allocateOrder = new AllocateOrder();
		allocateOrder.setAllocateOrderCode(allocateOrderCode);
		allocateOrder.setInOutStatus(AllocateOrderEnum.AllocateOrderInOutStatusEnum.WAIT.getCode());
		allocateOrder.setOrderStatus(AllocateOrderEnum.AllocateOrderStatusEnum.WAREHOUSE_NOTICE.getCode());
		allocateOrder.setAllocateInOrderCode(inCode);
		allocateOrder.setAllocateOutOrderCode(outCode);
		allocateOrder.setWmsAllocateOrderCode(wmsAllocateOrderCode);
		int count = allocateOrderService.updateByPrimaryKeySelective(allocateOrder);	
		if (count < 1) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_NOTICE_WAREHOUSE_EXCEPTION, 
					"调拨单通知仓库失败");
		}
		/**
		 * 更新商品明细状态 等待出库 等待入库
		 */
		AllocateSkuDetail skuDetail = new AllocateSkuDetail();
		skuDetail.setAllocateInStatus(AllocateOrderEnum.AllocateOrderSkuInStatusEnum.WAIT_IN.getCode());
		skuDetail.setAllocateOutStatus(AllocateOrderEnum.AllocateOrderSkuOutStatusEnum.WAIT_OUT.getCode());
		Example example = new Example(AllocateSkuDetail.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("allocateOrderCode", allocateOrderCode);
		criteria.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
		allocateSkuDetailService.updateByExampleSelective(skuDetail, example);
		
		logInfoService.recordLog(new AllocateOrder(), allocateOrderCode, 
				userInfo.getUserId(), LogOperationEnum.NOTICE_WMS.getMessage(), null, ZeroToNineEnum.ZERO.getCode());
	}
	

	//flag  0查询 1编辑
	@Override
	public AllocateOrder allocateOrderEditGet(String orderId,String flag) {
		AllocateOrder retOrder = allocateOrderService.selectByPrimaryKey(orderId);
		if (retOrder == null) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_NOTICE_EDIT_EXCEPTION, 
					"未查到相关调拨单信息");
		}
		//编辑页面对调入仓库和调出仓库校验,
		String outWarehouseCode = retOrder.getOutWarehouseCode();
		String inWarehouseCode = retOrder.getInWarehouseCode();
		Example example1 = new Example(WarehouseInfo.class);
		Example.Criteria criteria1 = example1.createCriteria();
		criteria1.andEqualTo("code",outWarehouseCode);
		List<WarehouseInfo> warehouseInfos1 = warehouseInfoService.selectByExample(example1);
		if(warehouseInfos1.get(0).getIsValid().equals("0")){
			if(flag.equals("1")){
				//启用状态为0，停用
				throw new WarehouseInfoException(ExceptionEnum.SYSTEM_WAREHOUSE_QUERY_EXCEPTION,"该调出仓库"+warehouseInfos1.get(0).getWarehouseName()+"已停用,请修改");
			}

		}

		Example example2 = new Example(WarehouseInfo.class);
		Example.Criteria criteria2 = example2.createCriteria();
		criteria2.andEqualTo("code",inWarehouseCode);
		List<WarehouseInfo> warehouseInfos2 = warehouseInfoService.selectByExample(example2);
		if(warehouseInfos2.get(0).getIsValid().equals("0")){
			if(flag.equals("1")){
				throw new WarehouseInfoException(ExceptionEnum.SYSTEM_WAREHOUSE_QUERY_EXCEPTION,"该调入仓库"+warehouseInfos2.get(0).getWarehouseName()+"已停用,请修改");
			}

		}


		AllocateSkuDetail queryDetail = new AllocateSkuDetail();
		queryDetail.setAllocateOrderCode(orderId);
		queryDetail.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
		List<AllocateSkuDetail> detailList = allocateSkuDetailService.select(queryDetail);
		if (!CollectionUtils.isEmpty(detailList)) {
			QuerySkuInventory querySku = null;
			List<QuerySkuInventory> querySkuList = new ArrayList<>();
			for (AllocateSkuDetail detail : detailList) {
				querySku = new QuerySkuInventory();
				querySku.setSkuCode(detail.getSkuCode());
				querySku.setInventoryType(detail.getInventoryType());
				querySkuList.add(querySku);
			}
			if(flag.equals("1")){//编辑时才查询实时库存
				if (!CollectionUtils.isEmpty(querySkuList)) {
					Map<String, Long> inventryMap = inventoryQuery(retOrder.getOutWarehouseCode(), JSON.toJSONString(querySkuList));
					detailList.forEach(item -> item.setInventoryNum(inventryMap.get(item.getSkuCode())));
				}
			}

			retOrder.setSkuDetailList(detailList);
		}
		allocateOrderExtService.setAllocateOrderWarehouseName(retOrder);
		allocateOrderExtService.setArea(retOrder);
		return retOrder;
	}
	
	@Override
	public void allocateOrderAudit(String orderId, String auditOpinion, String auditResult,
			AclUserAccreditInfo userInfo) {
		AllocateOrder retOrder = allocateOrderService.selectByPrimaryKey(orderId);
		if (retOrder == null) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION, 
					"未查到相关调拨单信息");
		}
		if (!AllocateOrderEnum.AllocateOrderStatusEnum.REJECT.getCode().equals(retOrder.getOrderStatus())
				&& !AllocateOrderEnum.AllocateOrderStatusEnum.AUDIT.getCode().equals(retOrder.getOrderStatus())) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION, 
					"当前状态不能审核");
		}
		if (null == AllocateOrderEnum.AllocateOrderAuditResultEnum
				.getEnumByCode(auditResult)) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION, 
					"审核结果错误");
		}
		
		AllocateOrder allocateOrder = new AllocateOrder();
		allocateOrder.setAllocateOrderCode(orderId);
		
		LogOperationEnum operation = null;
		if (AllocateOrderEnum.AllocateOrderAuditResultEnum.REJECT.getCode().equals(auditResult)) {
			// 审核驳回
			if (StringUtils.isBlank(auditOpinion)) {
				// 驳回时，审核意见不能为空
				throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION, 
						"驳回时，审核意见不能为空");
			} else {
				allocateOrder.setAuditOpinion(auditOpinion);
				allocateOrder.setOrderStatus(AllocateOrderEnum.AllocateOrderStatusEnum.REJECT.getCode());
				allocateOrder.setAuditStatus(AllocateOrderEnum.AllocateOrderStatusEnum.REJECT.getCode());
				operation = LogOperationEnum.AUDIT_REJECT;
			}
		} else {
			// 审核通过，需要校验仓库的实时库存
			AllocateSkuDetail queryDetail = new AllocateSkuDetail();
			queryDetail.setAllocateOrderCode(orderId);
			queryDetail.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
			List<AllocateSkuDetail> detailList = allocateSkuDetailService.select(queryDetail);
			if (!CollectionUtils.isEmpty(detailList)) {
				QuerySkuInventory querySku = null;
				List<QuerySkuInventory> querySkuList = new ArrayList<>();
				for (AllocateSkuDetail detail : detailList) {
					querySku = new QuerySkuInventory();
					querySku.setSkuCode(detail.getSkuCode());
					querySku.setInventoryType(detail.getInventoryType());
					querySkuList.add(querySku);
				}
				if (!CollectionUtils.isEmpty(querySkuList)) {
					Map<String, Long> inventryMap = inventoryQuery(retOrder.getOutWarehouseCode(), JSON.toJSONString(querySkuList));
					for (AllocateSkuDetail detail : detailList) {
						long planNum = detail.getPlanAllocateNum() == null ? 0l : detail.getPlanAllocateNum().longValue();
						if (null == inventryMap.get(detail.getSkuCode())) {
							throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION, 
									"调拨单商品" + detail.getSkuCode() + "暂无库存信息");
						}
						if (planNum > inventryMap.get(detail.getSkuCode()).longValue()) {
							throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION, 
									"调拨单商品" + detail.getSkuCode() + "调拨数量大于库存数量");
						}
					}
					
				} else {
					throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION, 
						"调拨单商品暂无库存信息");
				}
			} else {
				throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION, 
						"未查到相关调拨单的商品信息");
			}
			
			allocateOrder.setAuditOpinion(auditOpinion);
			allocateOrder.setOrderStatus(AllocateOrderEnum.AllocateOrderStatusEnum.PASS.getCode());
			allocateOrder.setAuditStatus(AllocateOrderEnum.AllocateOrderStatusEnum.PASS.getCode());
			operation = LogOperationEnum.AUDIT_PASS;
		}
		allocateOrderService.updateByPrimaryKeySelective(allocateOrder);
		
		logInfoService.recordLog(new AllocateOrder(), orderId, 
				userInfo.getUserId(), operation.getMessage(), auditOpinion, null);
		
		
	}
	
	@Override
	public Pagenation<AllocateSkuDetail> querySkuList(AllocateItemForm form, 
			Pagenation<AllocateSkuDetail> page, String skus) {

        List<AllocateSkuDetail>  totalSkuList = querySkuListPage(form, skus, null);
        int totalCount = totalSkuList.size();
        if (totalCount < 1) {
            return new Pagenation<AllocateSkuDetail>();
        }
        page.setTotalCount(totalCount);
        Pagenation<WarehouseItemInfo> pagenation = new Pagenation();
        pagenation.setPageNo(page.getPageNo());
        pagenation.setStart(page.getStart());
        pagenation.setPageSize(page.getPageSize());
        pagenation.setTotalCount(totalCount);
        List<AllocateSkuDetail>  skuList = querySkuListPage(form, skus, pagenation);

        page.setResult(skuList);

        return page;
	}
	
	
	@Override
	public Response queryWarehouse() {

      WarehouseInfo warehouse = new WarehouseInfo();

      //校验仓库是否已通知
      warehouse.setOwnerWarehouseState(ZeroToNineEnum.ONE.getCode());
      List<WarehouseInfo> warehouseInfoList = warehouseInfoService.select(warehouse);
      if(warehouseInfoList == null || warehouseInfoList.size() < 1){
          String msg = "无数据，请确认【仓储管理-仓库信息管理】中存在“通知成功”的仓库！";
          return ResultUtil.createSuccessResult(msg, "");
      }
      return ResultUtil.createSuccessResult("查询仓库成功", warehouseInfoList);
	}



	private List<AllocateSkuDetail> querySkuListPage(AllocateItemForm form, 
			String filterSkuCode, Pagenation<WarehouseItemInfo> pagenation) {
		String skuCode = form.getSkuCode();
		String skuName = form.getSkuName();
		String barCode = form.getBarCode();
		String itemNo = form.getItemNo();
		String brandName = form.getBrandName();
		
		// 校验仓库信息
		String outWhCode = form.getWarehouseInfoInId();
		String inWhCode = form.getWarehouseInfoOutId();
		if (StringUtils.equals(outWhCode, inWhCode)) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_REVIEW_SAVE_EXCEPTION, 
					"调拨单的出入库仓库不能相同");
		}
		
        WarehouseInfo queryRecord = new WarehouseInfo();
        queryRecord.setCode(inWhCode);
        queryRecord.setIsDeleted(ZeroToNineEnum.ZERO.getCode());//未删除
        queryRecord.setIsValid(ZeroToNineEnum.ONE.getCode());//有效
		WarehouseInfo whInfo = warehouseInfoService.selectOne(queryRecord);
		AssertUtil.notNull(whInfo, "调入仓库不存在或已停用");
		
		queryRecord.setCode(outWhCode);
		whInfo = warehouseInfoService.selectOne(queryRecord);
		AssertUtil.notNull(whInfo, "调出仓库不存在或已停用");
		
        //是否条件查询的标记
        boolean flag = false;
        if(StringUtils.isNotBlank(skuCode) || StringUtils.isNotBlank(skuName) || StringUtils.isNotBlank(barCode) ||
                StringUtils.isNotBlank(itemNo) || StringUtils.isNotBlank(brandName) || StringUtils.isNotBlank(filterSkuCode)){
            flag = true;
        }

        //查询品牌信息
        Example brandExample = new Example(Brand.class);
        Example.Criteria brandCriteria = brandExample.createCriteria();
       // brandCriteria.andIn("id", brandIds);
        if(StringUtils.isNotBlank(brandName)){
            brandCriteria.andLike("name", "%" + brandName + "%");
        }
        List<Brand> brandList = brandService.selectByExample(brandExample);
        AssertUtil.notEmpty(brandList, "品牌信息为空");
        List<String> _brandIds = new ArrayList<>();
        for(Brand brand: brandList){
            _brandIds.add(brand.getId().toString());
        }
        //查询供应商相关商品
        Example itemExample = new Example(Items.class);
        Example.Criteria itemCriteria = itemExample.createCriteria();
        //itemCriteria.andIn("categoryId", categoryIds);
        itemCriteria.andIn("brandId", _brandIds);
        itemCriteria.andEqualTo("isValid", ValidStateEnum.ENABLE.getCode());
        List<Items> itemsList = itemsService.selectByExample(itemExample);
        /*AssertUtil.notEmpty(itemsList, String.format("根据分类ID[%s]、品牌ID[%s]、起停用状态[%s]批量查询商品信息为空",
                CommonUtil.converCollectionToString(new ArrayList<>(categoryIds)), CommonUtil.converCollectionToString(new ArrayList<>(brandIds)), ValidStateEnum.ENABLE.getName()));*/
        if (CollectionUtils.isEmpty(itemsList)) {
        	logger.error(String.format("品牌ID[%s]、起停用状态[%s]批量查询商品信息为空",
                    CommonUtil.converCollectionToString(new ArrayList<>(_brandIds)), ValidStateEnum.ENABLE.getName()));
            if (!flag) {
                throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_ADD_SKU_EXCEPTION, "无数据，请确认【商品管理】中存在商品类型为”自采“的商品！");
            }
        }
        
        // 查出所有3级分类
//        Category queryCategory = new Category();
//        queryCategory.setLevel(3);
//		List<Category> categoryList = categoryService.select(queryCategory);
//        for (Category cg: categoryList) {
//            categoryIds.add(cg.getId());
//        }


        //查询供应商相关SKU
        Map<String, Long> spuCategoryMap = new HashedMap();
        List<Long> itemIds = new ArrayList<>();
        for (Items items: itemsList) {
            itemIds.add(items.getId());
            spuCategoryMap.put(items.getSpuCode(), items.getCategoryId());
        }
        if (CollectionUtils.isEmpty(itemIds)) {
        	return new ArrayList<AllocateSkuDetail>();
        }
        Example skusExample = new Example(Skus.class);
        Example.Criteria skusCriteria = skusExample.createCriteria();
        skusCriteria.andIn("itemId", itemIds);
        skusCriteria.andEqualTo("isValid", ValidStateEnum.ENABLE.getCode());
        if(StringUtils.isNotBlank(skuCode)){
            skusCriteria.andLike("skuCode", "%" + skuCode + "%");
        }
        if(StringUtils.isNotBlank(skuName)){
            skusCriteria.andLike("skuName", "%" + skuName + "%");
        }
        if(StringUtils.isNotBlank(filterSkuCode)){
            String[] _skuCodes = filterSkuCode.split(SupplyConstants.Symbol.COMMA);
            skusCriteria.andNotIn("skuCode", Arrays.asList(_skuCodes));
        }
        List<Skus> skusList = skusService.selectByExample(skusExample);
        //AssertUtil.notEmpty(skusList, String.format("根据商品ID[%s]、起停用状态[%s]批量查询商品SKU信息为空", CommonUtil.converCollectionToString(new ArrayList<>(itemIds)), ValidStateEnum.ENABLE.getName()));

        if (CollectionUtils.isEmpty(skusList)) {
            if (!flag) {
                throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_ADD_SKU_EXCEPTION,"无数据，请确认【商品管理】中存在商品类型为”自采“的商品！");
            }
        }
        List<String> skuCodes = new ArrayList<>();
        for (Skus skus: skusList) {
            skuCodes.add(skus.getSkuCode());
        }
        //查询仓库商品信息
        List<WarehouseItemInfo> warehouseItemInfoList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(skuCodes)){
            Example warehouseItemExample = new Example(WarehouseItemInfo.class);
            Example.Criteria warehouseItemCriteria = warehouseItemExample.createCriteria();
            
            // 获取出入库仓库id
            List<String> whInfoList = new ArrayList<>();
            whInfoList.add(form.getWarehouseInfoInId());
            whInfoList.add(form.getWarehouseInfoOutId());
            
            warehouseItemCriteria.andEqualTo("isDelete", ZeroToNineEnum.ZERO.getCode());
            warehouseItemCriteria.andIn("skuCode", skuCodes);
            warehouseItemCriteria.andIn("warehouseCode", whInfoList);
            warehouseItemCriteria.andEqualTo("noticeStatus", NoticsWarehouseStateEnum.SUCCESS.getCode());
            if (StringUtils.isNotBlank(barCode)) {
                warehouseItemCriteria.andLike("barCode", "%" + barCode + "%");
            }
            if (StringUtils.isNotBlank(itemNo)) {
                warehouseItemCriteria.andLike("itemNo", "%" + itemNo + "%");
            }
            List<WarehouseItemInfo> tmpList = warehouseItemInfoService.selectByExample(warehouseItemExample);
            
            if (CollectionUtils.isEmpty(tmpList)) {
                if (!flag) {
                    throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_ADD_SKU_EXCEPTION,
                            "无数据，请确认调拨商品在【仓库信息管理】的调入仓库和调出仓库中的“通知仓库状态”为“通知成功”！");
                }
            }
            
    		Map<String, List<WarehouseItemInfo>> groupBySkuCodeMap =
    				tmpList.stream().collect(Collectors.groupingBy(WarehouseItemInfo::getSkuCode));
    		
    		List<Long> itemInfoIdList = new ArrayList<>();
    		
    		for (String key : groupBySkuCodeMap.keySet()) {
    			if (groupBySkuCodeMap.get(key).size() == whInfoList.size()) {
    				itemInfoIdList.add(groupBySkuCodeMap.get(key).get(0).getId());
    			}
    		}
            
            if (CollectionUtils.isEmpty(itemInfoIdList)) {
                if (!flag) {
                    throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_ADD_SKU_EXCEPTION,
                            "无数据，请确认调拨商品在【仓库信息管理】的调入仓库和调出仓库中的“通知仓库状态”为“通知成功”！");
                }
            } else {
            	Example tmpExample = new Example(WarehouseItemInfo.class);
            	Example.Criteria tmpCriteria = tmpExample.createCriteria();
            	
            	tmpCriteria.andIn("id", itemInfoIdList);
            	tmpCriteria.andEqualTo("isDelete", ZeroToNineEnum.ZERO.getCode());
            	
            	if (null != pagenation) {
            		pagenation = warehouseItemInfoService.pagination(tmpExample, pagenation, new QueryModel());
            		warehouseItemInfoList = pagenation.getResult();
            	} else {
            		warehouseItemInfoList = warehouseItemInfoService.selectByExample(tmpExample);
            	}
            }
            
        }
        if (CollectionUtils.isEmpty(warehouseItemInfoList)) {
            if (!flag) {
                throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_ADD_SKU_EXCEPTION,
                        "无数据，请确认调拨商品在【仓库信息管理】的调入仓库和调出仓库中的“通知仓库状态”为“通知成功”！");
            }
        }
        
        //查询分类名称
        Set<Long> categoryIds = new HashSet<>();
        Map<Long, String> categoryMap = new HashedMap();
        for (WarehouseItemInfo itemInfo : warehouseItemInfoList) {
        	for (String key : spuCategoryMap.keySet()) {
        		if (StringUtils.equals(key, itemInfo.getSpuCode())) {
        			categoryIds.add(spuCategoryMap.get(key));
        		}
        	}
        }
        
        for (Long categoryId: categoryIds) {
            try {
                String categoryName = categoryBiz.getCategoryName(categoryId);
                categoryMap.put(categoryId, categoryName);
            } catch (Exception e) {
                logger.error(String.format("查询分类%s名称异常", categoryId), e);
            }
        }
        
        for (Skus skus: skusList) {
            for(Items items: itemsList){
                if(skus.getItemId().longValue() == items.getId().longValue()){
                    skus.setCategoryId(items.getCategoryId());
                    skus.setBrandId(items.getBrandId());
                    //设置分类名称
                    for(Map.Entry<Long, String> entry: categoryMap.entrySet()){
                        if(items.getCategoryId().longValue() == entry.getKey().longValue()){
                            skus.setCategoryName(entry.getValue());
                            break;
                        }
                    }
                    //设置品牌名称
                    for(Brand b: brandList){
                        if(items.getBrandId().longValue() == b.getId().longValue()){
                            skus.setBrandName(b.getName());
                            break;
                        }
                    }
                    break;
                }
            }
        }
        
        return getAllocateSkuDetails(warehouseItemInfoList, skusList);
	}
	
    private List<AllocateSkuDetail> getAllocateSkuDetails(List<WarehouseItemInfo> warehouseItemInfoList, List<Skus> skusList){
        List<AllocateSkuDetail> allocateSkuList = new ArrayList<>();
        for (WarehouseItemInfo warehouseItemInfo: warehouseItemInfoList) {
        	AllocateSkuDetail detail = new AllocateSkuDetail();
           // detail.setSpuCode(warehouseItemInfo.getSpuCode());
            detail.setSkuCode(warehouseItemInfo.getSkuCode());
            detail.setBarCode(warehouseItemInfo.getBarCode());
            detail.setSkuNo(warehouseItemInfo.getItemNo()); // 货号
            detail.setSpecNatureInfo(warehouseItemInfo.getSpecNatureInfo());
            //detail.setWarehouseItemInfoId(warehouseItemInfo.getWarehouseInfoId());
            //detail.setWarehouseItemId(warehouseItemInfo.getWarehouseItemId());
            for(Skus skus: skusList){
                if(StringUtils.equals(warehouseItemInfo.getSkuCode(), skus.getSkuCode())){
                    detail.setSkuName(skus.getSkuName());
                    detail.setBrandCode(skus.getBrandId().toString());
                    detail.setBrandName(skus.getBrandName());
                    //detail.setCategoryId(skus.getCategoryId());
                    detail.setAllCategoryName(skus.getCategoryName());
                    break;
                }
            }
            allocateSkuList.add(detail);
        }
        return allocateSkuList;
    }


	/**
	 * 新增和修改时设置调拨单详情
	 * @param isReviewFlg 
	 */
	private void setAllocateSkuDetail(AclUserAccreditInfo aclUserAccreditInfo,
			AllocateSkuDetail detail, String allocateOrderCode, JSONObject jsonObj, boolean isReviewFlg) {
		if (isReviewFlg) { // 提交审核的情况下需要校验
			if (jsonObj.getLong("planAllocateNum") == null ||
					StringUtils.isBlank(jsonObj.getString("skuCode")) ||
					StringUtils.isBlank(jsonObj.getString("inventoryType"))) {
				throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_REVIEW_SAVE_EXCEPTION, 
						"商品明细商品参数不完整");
			}

			//新增调拨单时审核
			//提交审核的情况下,查询实时库存
			Example example = new Example(AllocateOrder.class);
			Example.Criteria criteria = example.createCriteria();
			criteria.andEqualTo("allocateOrderCode",allocateOrderCode);
			List<AllocateOrder> allocateOrders = allocateOrderService.selectByExample(example);

			List<QuerySkuInventory> querySkuList = new ArrayList<>();
			QuerySkuInventory querySkuInventory = new QuerySkuInventory();
			querySkuInventory.setInventoryType(jsonObj.getString("inventoryType"));
			querySkuInventory.setSkuCode(jsonObj.getString("skuCode"));
			querySkuList.add(querySkuInventory);

			Map<String, Long> inventryMap = inventoryQuery(allocateOrders.get(0).getOutWarehouseCode(), JSON.toJSONString(querySkuList));
			if(inventryMap.size()==0){
				throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION,"该调拨商品的调出仓商品不存在");
			}else {
				for (String key : inventryMap.keySet()) {
					if(key.equals(jsonObj.getString("skuCode"))){
						Long inventoryNum = inventryMap.get(key);
						if(inventoryNum==null){
							throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION,"调出仓库不存在该件商品");
						}
						if(inventoryNum==0){
							throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION,"调出仓实时库存不能为0");
						}
						if ( jsonObj.getLong("planAllocateNum")>inventoryNum){
							throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION,"调拨数量不能大于调出仓库的实时库存");
						}
					}

				}
			}

		}
		detail.setAllocateOrderCode(allocateOrderCode);
		detail.setSkuName(jsonObj.getString("skuName"));
		detail.setSkuCode(jsonObj.getString("skuCode"));
		detail.setSpecNatureInfo(jsonObj.getString("specNatureInfo"));
		detail.setBarCode(jsonObj.getString("barCode"));
		detail.setBrandName(jsonObj.getString("brandName"));
		detail.setBrandCode(jsonObj.getString("brandCode"));
		detail.setInventoryType(jsonObj.getString("inventoryType"));
		detail.setPlanAllocateNum(jsonObj.getLong("planAllocateNum"));
		detail.setSkuNo(jsonObj.getString("skuNo"));
		detail.setAllocateInStatus(AllocateOrderEnum.AllocateOrderSkuInStatusEnum.INIT.getCode());
		detail.setAllocateOutStatus(AllocateOrderEnum.AllocateOrderSkuOutStatusEnum.INIT.getCode());
		detail.setInStatus(AllocateInOrderStatusEnum.WAIT_OUT_FINISH.getCode().toString());
		detail.setOutStatus(AllocateOrderEnum.AllocateOutOrderStatusEnum.WAIT_NOTICE.getCode());
		detail.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
		detail.setCreateOperator(aclUserAccreditInfo.getUserId());	
		
	}


	/**
	 * @param allocateOrder
	 * 新增和修改时获取出入仓库的信息
	 */
	private void setDetailAddress(AllocateOrder allocateOrder) {
		String outWhCode = allocateOrder.getOutWarehouseCode();
		String inWhCode = allocateOrder.getInWarehouseCode();
		
        WarehouseInfo queryRecord = new WarehouseInfo();
        queryRecord.setCode(inWhCode);
        queryRecord.setIsDeleted(ZeroToNineEnum.ZERO.getCode());//未删除
        queryRecord.setIsValid(ZeroToNineEnum.ONE.getCode());//有效
		WarehouseInfo whInfo = warehouseInfoService.selectOne(queryRecord);
		AssertUtil.notNull(whInfo, "调入仓库不存在或已停用");
		// 设置调入仓库信息
		allocateOrder.setReceiverAddress(whInfo.getAddress());
		allocateOrder.setReceiverCity(whInfo.getCity());
		allocateOrder.setReceiverProvince(whInfo.getProvince());
		
		queryRecord.setCode(outWhCode);
		whInfo = warehouseInfoService.selectOne(queryRecord);
		AssertUtil.notNull(whInfo, "调出仓库不存在或已停用");
		// 设置调出仓库信息
		allocateOrder.setSenderAddress(whInfo.getAddress());
		allocateOrder.setSenderCity(whInfo.getCity());
		allocateOrder.setSenderProvince(whInfo.getProvince());
	}


	@Override
	public Map<String, Long> inventoryQuery(String warehouseCode, String skus) {
       // List<String> skuList = Arrays.asList(skus.split(SupplyConstants.Symbol.COMMA))
		List<QuerySkuInventory> queryList = JSONArray.parseArray(skus, QuerySkuInventory.class);
		List<String> skuList = queryList.stream().map(item -> item.getSkuCode()).collect(Collectors.toList());
		Map<String, String> queryMap = queryList.stream()
				.collect(Collectors.toMap(QuerySkuInventory::getSkuCode, QuerySkuInventory::getInventoryType));
		
        List<WarehouseItemInfo> whItemList = new ArrayList<>();
        
        Example example = new Example(WarehouseItemInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("warehouseCode", warehouseCode);
        criteria.andIn("skuCode", skuList);
        criteria.andEqualTo("itemType", ItemTypeEnum.NOEMAL.getCode());//正常的商品
        criteria.andEqualTo("noticeStatus", ItemNoticeStateEnum.NOTICE_SUCCESS.getCode());//通知成功
        criteria.andEqualTo("isDelete", ZeroToNineEnum.ZERO.getCode());
        
        whItemList = warehouseItemInfoService.selectByExample(example);
        
		ScmInventoryQueryRequest request = new ScmInventoryQueryRequest();
        commonService.getWarehoueType(warehouseCode, request);
        
        List<ScmInventoryQueryItem> scmInventoryQueryItemList = new ArrayList<>();
        
        ScmInventoryQueryItem item = null;
        for (WarehouseItemInfo itemInfo: whItemList) {
        	item = new ScmInventoryQueryItem();
            item.setWarehouseCode(itemInfo.getWmsWarehouseCode());
            item.setInventoryStatus(queryMap.get(itemInfo.getSkuCode()));//库存状态，枚举值：1.良品；2.残品；3.样品。
            item.setInventoryType(JingdongInventoryTypeEnum.SALE.getCode());// 可销售
            item.setOwnerCode(itemInfo.getWarehouseOwnerId());// 京东仓库需要
            item.setItemCode(itemInfo.getSkuCode());
            item.setItemId(itemInfo.getWarehouseItemId());
            scmInventoryQueryItemList.add(item);
        }
        request.setScmInventoryQueryItemList(scmInventoryQueryItemList);
        AppResult<List<ScmInventoryQueryResponse>> appResult = warehouseApiService.inventoryQuery(request);
        List<ScmInventoryQueryResponse> resList = new ArrayList<>();
        if (StringUtils.equals(ResponseAck.SUCCESS_CODE, appResult.getAppcode())) {
        	resList = (List<ScmInventoryQueryResponse>) appResult.getResult();
//        	resList.stream()
//				.filter((res) -> (InventoryQueryResponseEnum.MARKETABLE.getCode().equals(res.getInventoryStatus())
//						&& EntryOrderDetailItemStateEnum.QUALITY_PRODUCTS.getCode().equals(res.getInventoryStatus()))
//				.forEach((res) -> item.setInventoryType("1"));
        	try {
        		Map<String, Long> retTempMap = resList.stream()
        				.collect(Collectors.toMap(ScmInventoryQueryResponse::getItemId, ScmInventoryQueryResponse::getQuantity));
                
        		Map<String, Long> retMap = new HashMap<>();
        		for (WarehouseItemInfo itemInfo: whItemList) {
        			if (retTempMap.get(itemInfo.getWarehouseItemId()) != null) {
        				retMap.put(itemInfo.getSkuCode(), retTempMap.get(itemInfo.getWarehouseItemId()));
        			}
                }
        		return retMap;
        	} catch (Exception e) {
        		e.printStackTrace();
        		logger.error("库存查询返回的格式有误:", e);
        		throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_QUERY_INVENTORY_EXCEPTION, "库存查询返回的格式有误");
        	}
        } else {
        	throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_QUERY_INVENTORY_EXCEPTION, appResult.getDatabuffer());
        }
	}


}
