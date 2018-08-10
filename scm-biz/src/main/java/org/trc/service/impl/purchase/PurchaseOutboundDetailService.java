package org.trc.service.impl.purchase;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.purchase.PurchaseOutboundDetail;
import org.trc.enums.ZeroToNineEnum;
import org.trc.enums.purchase.PurchaseOutboundStatusEnum;
import org.trc.enums.warehouse.PurchaseOutboundNoticeStatusEnum;
import org.trc.mapper.purchase.IPurchaseOutboundDetailMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.purchase.IPurchaseOutboundDetailService;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * Description〈〉
 *
 * @author hzliuwei
 * @create 2018/7/25
 */
@Service("purchaseOutboundDetailService")
public class PurchaseOutboundDetailService extends BaseService<PurchaseOutboundDetail, Long> implements IPurchaseOutboundDetailService {

    @Autowired
    private IPurchaseOutboundDetailMapper detailMapper;

    private Logger logger = LoggerFactory.getLogger(PurchaseOutboundDetailService.class);

    @Override
    public List<PurchaseOutboundDetail> selectDetailByNoticeCode(String outboundNoticeCode) {
        PurchaseOutboundDetail queryDetail = new PurchaseOutboundDetail();
        queryDetail.setOutboundNoticeCode(outboundNoticeCode);
        // 未删除的记录
        queryDetail.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        return detailMapper.select(queryDetail);

    }

    @Override
    public void updateByOrderCode(PurchaseOutboundNoticeStatusEnum status, Date nowTime, Long actualQty, String outboundNoticeCode) {
        PurchaseOutboundDetail updateRecord = new PurchaseOutboundDetail();
        //采购退货单详情商品出库状态
        setOutboundStatus(status, updateRecord);
        updateRecord.setStatus(status.getCode());
        updateRecord.setActualStorageQuantity(actualQty);
        updateRecord.setStorageTime(nowTime);
        Example example = new Example(PurchaseOutboundDetail.class);
        Example.Criteria ca = example.createCriteria();
        if (StringUtils.isBlank(outboundNoticeCode)) {
            throw new IllegalArgumentException("invoking function updateByOrderCode with outboundNoticeCode null!");
        }
        ca.andEqualTo("outboundNoticeCode", outboundNoticeCode);
        ca.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
        detailMapper.updateByExampleSelective(updateRecord, example);

    }

    private void setOutboundStatus(PurchaseOutboundNoticeStatusEnum status, PurchaseOutboundDetail updateRecord) {
        if (StringUtils.equals(PurchaseOutboundNoticeStatusEnum.PASS.getCode(), status.getCode())) {
            updateRecord.setOutboundStatus(PurchaseOutboundStatusEnum.FINISH.getCode());
        } else if (StringUtils.equals(PurchaseOutboundNoticeStatusEnum.RECEIVE_EXCEPTION.getCode(), status.getCode())) {
            updateRecord.setOutboundStatus(PurchaseOutboundStatusEnum.EXCEPTION.getCode());
        }
    }
}
