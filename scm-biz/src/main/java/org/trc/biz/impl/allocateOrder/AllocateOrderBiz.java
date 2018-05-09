package org.trc.biz.impl.allocateOrder;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
import org.trc.domain.category.Category;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.Skus;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.enums.AllocateOrderEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.NoticsWarehouseStateEnum;
import org.trc.enums.ValidStateEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.AllocateOrderException;
import org.trc.form.AllocateOrder.AllocateItemForm;
import org.trc.form.AllocateOrder.AllocateOrderForm;
import org.trc.service.allocateOrder.IAllocateOrderExtService;
import org.trc.service.allocateOrder.IAllocateOrderService;
import org.trc.service.allocateOrder.IAllocateOutOrderService;
import org.trc.service.allocateOrder.IAllocateSkuDetailService;
import org.trc.service.category.IBrandService;
import org.trc.service.category.ICategoryService;
import org.trc.service.goods.IItemsService;
import org.trc.service.goods.ISkusService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.service.warehouseInfo.IWarehouseItemInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.CommonUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;
import org.trc.util.QueryModel;

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
    private ICategoryService categoryService;
	@Autowired
	private IAllocateOrderExtService allocateOrderExtService;
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
                criteria.andIn("orderStatus", statusList);
                
        	} else if (AllocateOrderEnum.AllocateOrderAuditStatusEnum.WAIT_AUDIT.getCode().equals(auditStatus)) {
        		// 待审核
                criteria.andEqualTo("orderStatus", AllocateOrderEnum.AllocateOrderStatusEnum.AUDIT.getCode());
                
        	} else if (AllocateOrderEnum.AllocateOrderAuditStatusEnum.FINISH_AUDIT.getCode().equals(auditStatus)) {
        		// 已审核
                List<String> statusList = new ArrayList<>();
                statusList.add(AllocateOrderEnum.AllocateOrderStatusEnum.PASS.getCode());
                statusList.add(AllocateOrderEnum.AllocateOrderStatusEnum.REJECT.getCode());
                criteria.andIn("orderStatus", statusList);
        	} else {
        		throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION, 
						"审核状态错误");
        	}
        	
        	example.orderBy("submitTime").desc();
        	
            //提交审核日期开始
            if (!StringUtils.isBlank(form.getSubmitTimeStart())) {
            	criteria.andGreaterThanOrEqualTo("submitTime", form.getSubmitTimeStart());
            }
            
            //提交审核日期结束
            if (!StringUtils.isBlank(form.getSubmitTimeEnd())) {
            	criteria.andLessThanOrEqualTo("submitTime", form.getSubmitTimeEnd());
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
            example.orderBy("updateTime").desc();
            example.setOrderByClause("field(order_status,0,3,1,2,4,5)");
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
        	criteria.andEqualTo("inWarehouseName", form.getInWarehouseCode());
        }
        
        //出入库状态
        if (!StringUtils.isBlank(form.getInOutStatus())) {
            criteria.andEqualTo("inOutStatus", form.getInOutStatus());
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


	@Override
	@Transactional
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
            
            // 设置详细地址
            setDetailAddress(allocateOrder);
            
			// 设置调拨单初始状态-暂存或者提交审核
			allocateOrder.setOrderStatus(orderStatus);
			// 设置调拨单出入库初始状态-初始
			allocateOrder.setInOutStatus(AllocateOrderEnum.AllocateOrderInOutStatusEnum.INIT.getCode());
			allocateOrder.setCreateOperator(aclUserAccreditInfo.getUserId());
			allocateOrder.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
			int insertCount = allocateOrderService.insertSelective(allocateOrder);
			
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
		if (countDetail < 1) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_DELETE_EXCEPTION, 
					"删除调拨单详情失败");
		}
	}
	
	@Override
	@Transactional
	public void dropAllocateOrder(String orderId) {
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
		if (!AllocateOrderEnum.AllocateOrderStatusEnum.PASS.getCode().equals(status)
				&& !(AllocateOrderEnum.AllocateOrderStatusEnum.WAREHOUSE_NOTICE.equals(status) 
						&& (AllocateOrderEnum.AllocateOutOrderStatusEnum.WAIT_NOTICE.getCode().equals(status)
								&& (AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_RECEIVE_FAIL.getCode().equals(status))))) {
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
		
	}
	

	@Override
	@Transactional
	public void noticeWarehouse(String orderId, AclUserAccreditInfo userInfo) {
		AllocateOrder queryOrder = allocateOrderService.selectByPrimaryKey(orderId);
		if (queryOrder == null) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_DROP_EXCEPTION, 
					"未查的相关调拨单信息");
		}
		String status = queryOrder.getOrderStatus();
		/**
		 * 审核通过 的状态才能通知仓库
		 */
		if (!AllocateOrderEnum.AllocateOrderStatusEnum.PASS.getCode().equals(status)) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_DROP_EXCEPTION, 
					"当前调拨单状态不满足作废条件");
		}
		AllocateOrder allocateOrder = new AllocateOrder();
		allocateOrder.setAllocateOrderCode(orderId);
		allocateOrder.setOrderStatus(AllocateOrderEnum.AllocateOrderStatusEnum.WAREHOUSE_NOTICE.getCode());
		int count = allocateOrderService.updateByPrimaryKeySelective(allocateOrder);	
		if (count < 1) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_NOTICE_WAREHOUSE_EXCEPTION, 
					"调拨单通知仓库失败");
		}
		/**
		 * 生成出入库通知单
		 */
		AllocateOutOrder outOrder = new AllocateOutOrder();
		BeanUtils.copyProperties(queryOrder, outOrder);
		allocateOrderExtService.createAllocateOutOrder(outOrder, userInfo.getUserId());
		
		AllocateInOrder inOrder = new AllocateInOrder();
		BeanUtils.copyProperties(queryOrder, inOrder);
		allocateOrderExtService.createAllocateInOrder(inOrder, userInfo.getUserId());
        //allocateOutOrderService.insertSelective(outOrder);
	}
	
	
	@Override
	public AllocateOrder allocateOrderEditGet(String orderId) {
		AllocateOrder retOrder = allocateOrderService.selectByPrimaryKey(orderId);
		if (retOrder == null) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_NOTICE_EDIT_EXCEPTION, 
					"未查到相关调拨单信息");
		}
		AllocateSkuDetail queryDetail = new AllocateSkuDetail();
		queryDetail.setAllocateOrderCode(orderId);
		queryDetail.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
		List<AllocateSkuDetail> detailList = allocateSkuDetailService.select(queryDetail);
		if (!CollectionUtils.isEmpty(detailList)) {
			retOrder.setSkuDetailList(detailList);
		}
		return retOrder;
	}
	
	@Override
	public void allocateOrderAudit(String orderId, String auditOpinion, String auditResult,
			AclUserAccreditInfo property) {
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
		
		if (AllocateOrderEnum.AllocateOrderAuditResultEnum.REJECT.getCode().equals(auditResult)) {
			// 审核驳回
			if (StringUtils.isBlank(auditOpinion)) {
				// 驳回时，审核意见不能为空
				throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_AUDIT_EXCEPTION, 
						"驳回时，审核意见不能为空");
			} else {
				allocateOrder.setAuditOpinion(auditOpinion);
				allocateOrder.setOrderStatus(AllocateOrderEnum.AllocateOrderStatusEnum.REJECT.getCode());
			}
		} else {
			// 审核通过
			allocateOrder.setOrderStatus(AllocateOrderEnum.AllocateOrderStatusEnum.PASS.getCode());
		}
		allocateOrderService.updateByPrimaryKeySelective(allocateOrder);
		
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
        pagenation.setStart(page.getStart());
        pagenation.setPageSize(page.getPageSize());
        pagenation.setTotalCount(totalCount);
        List<AllocateSkuDetail>  skuList = querySkuListPage(form, skus, pagenation);

        page.setResult(skuList);

        return page;
	}



	private List<AllocateSkuDetail> querySkuListPage(AllocateItemForm form, 
			String filterSkuCode, Pagenation<WarehouseItemInfo> pagenation) {
		String skuCode = form.getSkuCode();
		String skuName = form.getSkuName();
		String barCode = form.getBarCode();
		String itemNo = form.getItemNo();
		String brandName = form.getBrandName();
		
        //是否条件查询的标记
        boolean flag = false;
        if(StringUtils.isNotBlank(skuCode) || StringUtils.isNotBlank(skuName) || StringUtils.isNotBlank(barCode) ||
                StringUtils.isNotBlank(itemNo) || StringUtils.isNotBlank(brandName) || StringUtils.isNotBlank(filterSkuCode)){
            flag = true;
        }
        // 查出所有3级分类
        Set<Long> categoryIds = new HashSet<>();
        Category queryCategory = new Category();
        queryCategory.setLevel(3);
		List<Category> categoryList = categoryService.select(queryCategory);
        for (Category cg: categoryList) {
            categoryIds.add(cg.getId());
        }
        //查询分类名称
        Map<Long, String> categoryMap = new HashedMap();
        for(Long categoryId: categoryIds){
            try {
                String categoryName = categoryBiz.getCategoryName(categoryId);
                categoryMap.put(categoryId, categoryName);
            } catch (Exception e) {
                logger.error(String.format("查询分类%s名称异常", categoryId), e);
            }
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
        itemCriteria.andIn("categoryId", categoryIds);
        itemCriteria.andIn("brandId", _brandIds);
        itemCriteria.andEqualTo("isValid", ValidStateEnum.ENABLE.getCode());
        List<Items> itemsList = itemsService.selectByExample(itemExample);
        /*AssertUtil.notEmpty(itemsList, String.format("根据分类ID[%s]、品牌ID[%s]、起停用状态[%s]批量查询商品信息为空",
                CommonUtil.converCollectionToString(new ArrayList<>(categoryIds)), CommonUtil.converCollectionToString(new ArrayList<>(brandIds)), ValidStateEnum.ENABLE.getName()));*/
        if (CollectionUtils.isEmpty(itemsList)) {
        	logger.error(String.format("根据分类ID[%s]、品牌ID[%s]、起停用状态[%s]批量查询商品信息为空",
                    CommonUtil.converCollectionToString(new ArrayList<>(categoryIds)), 
                    CommonUtil.converCollectionToString(new ArrayList<>(_brandIds)), ValidStateEnum.ENABLE.getName()));
            if (!flag) {
                throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_ADD_SKU_EXCEPTION, "无数据，请确认【商品管理】中存在商品类型为”自采“的商品！");
            }
        }

        //查询供应商相关SKU
        List<Long> itemIds = new ArrayList<>();
        for(Items items: itemsList){
            itemIds.add(items.getId());
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
        for(Skus skus: skusList){
            skuCodes.add(skus.getSkuCode());
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
        //查询仓库商品信息
        List<WarehouseItemInfo> warehouseItemInfoList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(skuCodes)){
            Example warehouseItemExample = new Example(WarehouseItemInfo.class);
            Example.Criteria warehouseItemCriteria = warehouseItemExample.createCriteria();
            
            // 获取出入库仓库id
            List<String> whInfoList = new ArrayList<>();
            whInfoList.add(form.getWarehouseInfoInId());
            whInfoList.add(form.getWarehouseInfoOutId());
            
            //warehouseItemCriteria.andIn("skuCode", skuCodes);
            warehouseItemCriteria.andIn("warehouseInfoId", whInfoList);
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
            }
            
            Example tmpExample = new Example(WarehouseItemInfo.class);
            Example.Criteria tmpCriteria = tmpExample.createCriteria();
            
            tmpCriteria.andIn("id", itemInfoIdList);
            
            if (null != pagenation) {
                pagenation = warehouseItemInfoService.pagination(tmpExample, pagenation, new QueryModel());
                warehouseItemInfoList = pagenation.getResult();
            } else {
                warehouseItemInfoList = warehouseItemInfoService.selectByExample(tmpExample);
            }
        }
        if (CollectionUtils.isEmpty(warehouseItemInfoList)) {
            if (!flag) {
                throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_ADD_SKU_EXCEPTION,
                        "无数据，请确认调拨商品在【仓库信息管理】的调入仓库和调出仓库中的“通知仓库状态”为“通知成功”！");
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
            detail.setSkuNo(warehouseItemInfo.getItemNo());
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
	 */
	private void setAllocateSkuDetail(AclUserAccreditInfo aclUserAccreditInfo,
			AllocateSkuDetail detail, String allocateOrderCode, JSONObject jsonObj) {
		if (jsonObj.getLong("planAllocateNum") == null ||
				StringUtils.isBlank(jsonObj.getString("skuCode")) ||
				StringUtils.isBlank(jsonObj.getString("inventoryType"))) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_REVIEW_SAVE_EXCEPTION, 
					"商品明细商品参数不完整");
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
		String outWhCode = allocateOrder.getOutWarehouseCode();
		String inWhCode = allocateOrder.getInWarehouseCode();
		if (outWhCode.equals(inWhCode)) {
			throw new AllocateOrderException(ExceptionEnum.ALLOCATE_ORDER_REVIEW_SAVE_EXCEPTION, 
					"调拨单的出入库仓库不能相同");
		}
        WarehouseInfo queryRecord = new WarehouseInfo();
        queryRecord.setCode(inWhCode);
        queryRecord.setIsDeleted(ZeroToNineEnum.ZERO.getCode());//未删除
        queryRecord.setIsValid(ZeroToNineEnum.ONE.getCode());//有效
		WarehouseInfo whInfo = warehouseInfoService.selectOne(queryRecord);
		AssertUtil.notNull(whInfo, "调入仓库不存在或已停用");
		// 设置调入详细地址
		allocateOrder.setReceiverAddress(whInfo.getAddress());
		
		queryRecord.setCode(outWhCode);
		whInfo = warehouseInfoService.selectOne(queryRecord);
		AssertUtil.notNull(whInfo, "调出仓库不存在或已停用");
		// 设置调出详细地址
		allocateOrder.setSenderAddress(whInfo.getAddress());
	}

}
