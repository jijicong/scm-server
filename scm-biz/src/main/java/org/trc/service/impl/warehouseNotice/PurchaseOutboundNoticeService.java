package org.trc.service.impl.warehouseNotice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseNotice.PurchaseOutboundNotice;
import org.trc.form.warehouse.PurchaseOutboundNoticeForm;
import org.trc.mapper.impower.AclUserAccreditInfoMapper;
import org.trc.mapper.supplier.ISupplierMapper;
import org.trc.mapper.warehouseInfo.IWarehouseInfoMapper;
import org.trc.mapper.warehouseNotice.IPurchaseOutboundNoticeMapper;
import org.trc.service.impl.BaseService;
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
	private AclUserAccreditInfoMapper userInfoMapper;
	@Autowired
	private IWarehouseInfoMapper whiMapper;
	@Autowired
	private IPurchaseOutboundNoticeMapper noticeMapper;
	@Autowired
	private ISupplierMapper supplierMapper;

	@Override
	public Pagenation<PurchaseOutboundNotice> pageList (PurchaseOutboundNoticeForm form,
			Pagenation<PurchaseOutboundNotice> page, String channelCode) {
		
		Example example = new Example(PurchaseOutboundNotice.class);
        Example.Criteria criteria = example.createCriteria();
        
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
        if (StringUtils.isNotBlank(form.getSupplierCode())) {
        	criteria.andEqualTo("supplierCode", form.getSupplierCode());
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
            
            List<AclUserAccreditInfo> userList = userInfoMapper.selectByExample(userExample);
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

	@Override
	public List<PurchaseOutboundNotice> selectNoticeBycode(String code) {
		PurchaseOutboundNotice queryRecord = new PurchaseOutboundNotice();
		queryRecord.setOutboundNoticeCode(code);
		return noticeMapper.select(queryRecord);
	}

	@Override
	public void updateById(String status, Long id, String errMsg, String wmsEntryRtCode) {
		PurchaseOutboundNotice updateRecord = new PurchaseOutboundNotice();
		updateRecord.setId(id);
		updateRecord.setStatus(status);
		updateRecord.setFailureCause(errMsg);
		updateRecord.setEntryOrderId(wmsEntryRtCode);
		noticeMapper.updateByPrimaryKeySelective(updateRecord);		
	}

	@Override
	public void generateNames(Pagenation<PurchaseOutboundNotice> resultPage) {
		List<PurchaseOutboundNotice> resultList = resultPage.getResult();
        if (CollectionUtils.isEmpty(resultList)) {
            return;
        }
        
        Set<String> warehouseCodes = new HashSet<>(); // 仓库
        Set<String> operatorIds = new HashSet<>();  // 创建人
        Set<String> supplyCodes = new HashSet<>();  // 供应商
        
        List<WarehouseInfo> warehouseInfoList = null;
        List<AclUserAccreditInfo> aclUserAccreditInfoList = null;
        List<Supplier> supplierList = null;
        
        for (PurchaseOutboundNotice notice : resultList) {
            warehouseCodes.add(notice.getWarehouseCode());
            operatorIds.add(notice.getCreateOperator());
            supplyCodes.add(notice.getSupplierCode());
        }

        if (!warehouseCodes.isEmpty()) {
            Example example = new Example(WarehouseInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("code", warehouseCodes);
            warehouseInfoList = whiMapper.selectByExample(example);
        }
        if (!operatorIds.isEmpty()) {
            Example example = new Example(AclUserAccreditInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("userId", operatorIds);
            aclUserAccreditInfoList = userInfoMapper.selectByExample(example);
        }
        if (!supplyCodes.isEmpty()) {
        	Example example = new Example(Supplier.class);
        	Example.Criteria criteria = example.createCriteria();
        	criteria.andIn("supplierCode", supplyCodes);
        	supplierList = supplierMapper.selectByExample(example);
        }
        
        for (PurchaseOutboundNotice notice : resultList) {
            if (!CollectionUtils.isEmpty(warehouseInfoList)) {
                for (WarehouseInfo warehouseInfo: warehouseInfoList) {
                    if(StringUtils.equals(notice.getWarehouseCode(), warehouseInfo.getCode())){
                    	notice.setWarehouseName(warehouseInfo.getWarehouseName());
                        break;
                    }
                }
            }
            if (!CollectionUtils.isEmpty(aclUserAccreditInfoList)) {
                for (AclUserAccreditInfo userAccreditInfo: aclUserAccreditInfoList) {
                    if (StringUtils.equals(notice.getCreateOperator(), userAccreditInfo.getUserId())) {
                    	notice.setCreatorName(userAccreditInfo.getName());
                        break;
                    }
                }
            }
            if (!CollectionUtils.isEmpty(supplierList)) {
            	for (Supplier supplier : supplierList) {
            		if (StringUtils.equals(notice.getSupplierCode(), supplier.getSupplierCode())) {
            			notice.setSupplierName(supplier.getSupplierName());
            			break;
            		}
            	}
            }
        }		
	}
	
}
