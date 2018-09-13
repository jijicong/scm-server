package org.trc.biz.impl.afterSale;

import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.afterSale.IAfterSaleWarehouseNoticeBiz;
import org.trc.domain.System.SellChannel;
import org.trc.domain.afterSale.AfterSaleWarehouseNotice;
import org.trc.domain.afterSale.AfterSaleWarehouseNoticeDetail;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.ValidEnum;
import org.trc.form.afterSale.AfterSaleWarehouseNoticeDO;
import org.trc.form.afterSale.AfterSaleWarehouseNoticeVO;
import org.trc.service.System.ISellChannelService;
import org.trc.service.afterSale.IAfterSaleWarehouseNoticeDetailService;
import org.trc.service.afterSale.IAfterSaleWarehouseNoticeService;
import org.trc.service.impl.system.SellChannelService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;
import org.trc.util.QueryModel;

import tk.mybatis.mapper.entity.Example;

@Service("afterSaleWarehouseInfoBiz")
public class AfterSaleWarehouseNoticeBiz implements IAfterSaleWarehouseNoticeBiz{

	@Autowired
	private IAfterSaleWarehouseNoticeService  afterSaleWarehouseNoticeService;
	@Autowired
	private IAfterSaleWarehouseNoticeDetailService  afterSaleWarehouseNoticeDetailService;
	@Autowired
	private IWarehouseInfoService  warehouseInfoService;
	@Autowired
	private ISellChannelService sellChannelService;
	
	@Override
	public Pagenation<AfterSaleWarehouseNotice> warehouseNoticeList(AfterSaleWarehouseNoticeDO afterSaleWarehouseNoticeDO,Pagenation<AfterSaleWarehouseNotice> page,
			AclUserAccreditInfo aclUserAccreditInfo) {
		
		AssertUtil.notNull(aclUserAccreditInfo, "用户授权信息为空");
		//根据条件分页查询
		Example example = new Example(AfterSaleWarehouseNotice.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(aclUserAccreditInfo.getChannelCode())){
            criteria.andEqualTo("channelCode", aclUserAccreditInfo.getChannelCode());
        }
        if(afterSaleWarehouseNoticeDO!=null) {
        	//创建时间开始
            if (StringUtils.isNotBlank(afterSaleWarehouseNoticeDO.getCreateTimeStart())) {
            	Date startDate=DateUtils.stringToDate(afterSaleWarehouseNoticeDO.getCreateTimeStart()+" 00:00:00", "yyyy-MM-dd HH:mm:ss") ;
            	criteria.andGreaterThan("createTime", startDate);
            }
            //创建时间结束
            if (StringUtils.isNotBlank(afterSaleWarehouseNoticeDO.getCreateTimeEnd())) {
            	Date endDate=DateUtils.stringToDate(afterSaleWarehouseNoticeDO.getCreateTimeEnd()+" 23:59:59", "yyyy-MM-dd HH:mm:ss") ;
            	criteria.andLessThan("createTime", endDate);
             }

            //入库单编号   
            if (StringUtils.isNotBlank(afterSaleWarehouseNoticeDO.getWarehouseNoticeCode())) {
            	criteria.andLike("warehouseNoticeCode", "%" + afterSaleWarehouseNoticeDO.getWarehouseNoticeCode() + "%");
            }
            //售后单编号
            if (StringUtils.isNotBlank(afterSaleWarehouseNoticeDO.getAfterSaleCode())) {
                criteria.andLike("afterSaleCode", "%" + afterSaleWarehouseNoticeDO.getAfterSaleCode() + "%");
            }
            //订单编号 
            if (StringUtils.isNotBlank(afterSaleWarehouseNoticeDO.getShopOrderCode())) {
            	criteria.andLike("shopOrderCode", "%" + afterSaleWarehouseNoticeDO.getShopOrderCode() + "%");
            }
            //入库仓库编码 warehouseCode
            if (StringUtils.isNotBlank(afterSaleWarehouseNoticeDO.getWarehouseCode())) {
            	criteria.andEqualTo("warehouseCode", afterSaleWarehouseNoticeDO.getWarehouseCode());
            }
            
            //操作人
            if (StringUtils.isNotBlank(afterSaleWarehouseNoticeDO.getOperator())) {
            	criteria.andLike("operator", "%" + afterSaleWarehouseNoticeDO.getOperator() + "%");
            }
			//入库仓库编码 warehouseCode
			if (StringUtils.isNotBlank(afterSaleWarehouseNoticeDO.getStatus())) {
				criteria.andEqualTo("status", Integer.parseInt(afterSaleWarehouseNoticeDO.getStatus()));
			}
        }
        example.orderBy("createTime").desc();
        return afterSaleWarehouseNoticeService.pagination(example, page, new QueryModel());
	}

	@Override
	public AfterSaleWarehouseNoticeVO warehouseNoticeInfo(String warehouseNoticeCode) throws Exception{
		AfterSaleWarehouseNoticeVO VO=new AfterSaleWarehouseNoticeVO();
		//查询退货入库单
		AfterSaleWarehouseNotice selectNotice=new AfterSaleWarehouseNotice();
		selectNotice.setWarehouseNoticeCode(warehouseNoticeCode);
		selectNotice.setShopId(null);
		AfterSaleWarehouseNotice afterSaleWarehouseNotice=afterSaleWarehouseNoticeService.selectOne(selectNotice);
		AssertUtil.notNull(afterSaleWarehouseNotice, "根据该退货入库单号"+warehouseNoticeCode+"查询结果为空!");
		
		//查询退货入库单详情
		AfterSaleWarehouseNoticeDetail selectDetail=new AfterSaleWarehouseNoticeDetail();
		selectDetail.setWarehouseNoticeCode(warehouseNoticeCode);
		List<AfterSaleWarehouseNoticeDetail> afterSaleWarehouseNoticeDetailList=afterSaleWarehouseNoticeDetailService.select(selectDetail);
		AssertUtil.notNull(afterSaleWarehouseNoticeDetailList, "根据该退货入库单号"+warehouseNoticeCode+"查询详情结果为空!");
		
		BeanUtils.copyProperties(VO, afterSaleWarehouseNotice);
		VO.setWarehouseNoticeDetailList(afterSaleWarehouseNoticeDetailList);
		String sellCodeName=getSellCodeName(VO.getSellCode());
		VO.setSellCodeName(sellCodeName);
		return VO;
	}

	private String getSellCodeName(String sellCode) {
		SellChannel select=new SellChannel();
		select.setSellCode(sellCode);
		SellChannel sellChannel=sellChannelService.selectOne(select);
		if(sellChannel!=null) {
			return sellChannel.getSellName();
		}
		return null;
	}

	@Override
	public List<WarehouseInfo> selectWarehouse() {
		WarehouseInfo warehouseInfo=new WarehouseInfo();
		warehouseInfo.setIsSupportReturn(Integer.parseInt(ValidEnum.VALID.getCode()));
		return warehouseInfoService.select(warehouseInfo);
	}
	

}
