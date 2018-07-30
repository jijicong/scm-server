package org.trc.service.impl.warehouseNotice;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.trc.domain.allocateOrder.AllocateOrder;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseNotice.PurchaseOutboundNotice;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.warehouse.PurchaseOutboundNoticeForm;
import org.trc.service.impl.BaseService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.warehouseNotice.IPurchaseOutboundNoticeService;
import org.trc.util.Pagenation;

import tk.mybatis.mapper.entity.Example;

/**
 * Description〈〉
 *
 * @author hzliuwei
 * @create 2018/7/25
 */
@Service("purchaseOutboundNoticeService")
public class PurchaseOutboundNoticeService extends BaseService<PurchaseOutboundNotice, Long> implements 
	IPurchaseOutboundNoticeService {
	
	@Autowired
	private IAclUserAccreditInfoService userInfoService;

	@Override
	public Pagenation<PurchaseOutboundNotice> pageList (PurchaseOutboundNoticeForm form,
			Pagenation<PurchaseOutboundNotice> page, String channelCode) {
		
		Example example = new Example(PurchaseOutboundNotice.class);
        Example.Criteria criteria = example.createCriteria();
        
        //criteria.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
        //业务线编号
        criteria.andEqualTo("channelCode", channelCode);
        
        //退货出库单号
        if (StringUtils.isNotBlank(form.getOutboundNoticeCode())) {
            criteria.andEqualTo("outboundNoticeCode", form.getOutboundNoticeCode());
        }
        
        //采购退货单编号
        if (StringUtils.isNotBlank(form.getPurchaseOutboundOrderCode())) {
            criteria.andEqualTo("purchaseOutboundOrderCode", form.getPurchaseOutboundOrderCode());
        }
        
        //退货仓库
        if (StringUtils.isNotBlank(form.getWarehouseCode())) {
        	criteria.andEqualTo("warehouseCode", form.getWarehouseCode());
        }
        
        //供应商编号
        if (StringUtils.isNotBlank(form.getSupplierId())) {
        	criteria.andEqualTo("supplierId", form.getSupplierId());
        }
        
        //出库单状态
        if (StringUtils.isNotBlank(form.getStatus())) {
        	criteria.andEqualTo("status", form.getStatus());
        }
        
        //创建日期开始
        if (StringUtils.isNotBlank(form.getStartDate())) {
        	criteria.andGreaterThanOrEqualTo("createTime", form.getStartDate() + " 00:00:00");
        }
        
        //创建日期结束
        if (StringUtils.isNotBlank(form.getEndDate())) {
        	criteria.andLessThanOrEqualTo("createTime", form.getEndDate() + " 23:59:59");
        }
        
        //出库单创建人
        if (StringUtils.isNotBlank(form.getCreateUser())) {
            Example userExample = new Example(AclUserAccreditInfo.class);
            Example.Criteria userCriteria = userExample.createCriteria();
            
            userCriteria.andLike("name", "%" + form.getCreateUser() + "%");
            
            List<AclUserAccreditInfo> userList = userInfoService.selectByExample(userExample);
            if (CollectionUtils.isEmpty(userList)) {
            	// 未查询到结果
            	return new Pagenation<>();
            } else {
            	List<String> userIdList = new ArrayList<>();
            	for (AclUserAccreditInfo user : userList) {
            		userIdList.add(user.getUserId());
            	}
            	criteria.andIn("createOperator", userIdList);
            }
        }
        
        Pagenation<PurchaseOutboundNotice> pageResult = this.pagination(example, page, form);
       // List<PurchaseOutboundNotice> result2 = result.getResult();
        return pageResult;


	}
	
}
