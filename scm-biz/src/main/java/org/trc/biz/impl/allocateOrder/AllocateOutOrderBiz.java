package org.trc.biz.impl.allocateOrder;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.allocateOrder.IAllocateOrderBiz;
import org.trc.biz.allocateOrder.IAllocateOutOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.allocateOrder.AllocateOrder;
import org.trc.domain.allocateOrder.AllocateOutOrder;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.AllocateOrderEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.AllocateOrderException;
import org.trc.form.AllocateOrder.AllocateOrderForm;
import org.trc.form.AllocateOrder.AllocateOutOrderForm;
import org.trc.service.allocateOrder.IAllocateOrderService;
import org.trc.service.allocateOrder.IAllocateOutOrderService;
import org.trc.service.allocateOrder.IAllocateSkuDetailService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;
import org.trc.util.cache.AllocateOrderCacheEvict;
import org.trc.util.cache.OutboundOrderCacheEvict;
import tk.mybatis.mapper.entity.Example;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service("allocateOutOrderBiz")
public class AllocateOutOrderBiz implements IAllocateOutOrderBiz {

    private Logger logger = LoggerFactory.getLogger(AllocateOutOrderBiz.class);
    @Autowired
    private IAllocateOutOrderService allocateOutOrderService;
    @Autowired
    private IWarehouseInfoService warehouseInfoService;
    @Autowired
    private IAllocateSkuDetailService allocateSkuDetailService;
    /**
     * 调拨单分页查询
     */
    @Override
    @Cacheable(value = SupplyConstants.Cache.ALLOCATE_OUT_ORDER)
    public Pagenation<AllocateOutOrder> allocateOutOrderPage(AllocateOutOrderForm form,
															 Pagenation<AllocateOutOrder> page) {

        AssertUtil.notNull(page.getPageNo(),"分页查询参数pageNo不能为空");
        AssertUtil.notNull(page.getPageSize(),"分页查询参数pageSize不能为空");
        AssertUtil.notNull(page.getStart(),"分页查询参数start不能为空");

        Example example = new Example(AllocateOutOrder.class);
        Example.Criteria criteria = example.createCriteria();

        //调拨单编号
        if (!StringUtils.isBlank(form.getAllocateOrderCode())) {
            criteria.andLike("allocateOrderCode","%"+form.getAllocateOrderCode()+"%");
        }

        //调拨出库单编号
        if (!StringUtils.isBlank(form.getAllocateOutOrderCode())) {
            criteria.andLike("allocateOutOrderCode","%"+form.getAllocateOutOrderCode()+"%");
        }

        //单据创建人
        if (!StringUtils.isBlank(form.getCreateOperatorName())) {
            criteria.andLike("createOperatorName","%"+form.getCreateOperatorName()+"%");
        }

        //调出仓库
        if (!StringUtils.isBlank(form.getOutWarehouseCode())) {
            criteria.andEqualTo("outWarehouseCode", form.getOutWarehouseCode());
        }

        //出入库状态
        if (!StringUtils.isBlank(form.getStatus())) {
            criteria.andEqualTo("status", form.getStatus());
        }

        //创建日期开始
        if (!StringUtils.isBlank(form.getStartDate())) {
            criteria.andGreaterThanOrEqualTo("createTime", form.getStartDate());
        }

        //创建日期结束
        if (!StringUtils.isBlank(form.getEndDate())) {
            criteria.andLessThanOrEqualTo("createTime", form.getEndDate());
        }

        example.setOrderByClause("field(status,0,2,1,3,4,5)");
        example.orderBy("updateTime").desc();

        Pagenation<AllocateOutOrder> pagenation = allocateOutOrderService.pagination(example, page, form);

        return pagenation;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @AllocateOrderCacheEvict
    public Response close(Long id, String remark, AclUserAccreditInfo aclUserAccreditInfo) {
        return null;
    }

}
