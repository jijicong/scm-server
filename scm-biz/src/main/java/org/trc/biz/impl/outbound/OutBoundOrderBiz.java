package org.trc.biz.impl.outbound;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.outbuond.IOutBoundOrderBiz;
import org.trc.domain.order.OutboundOrder;
import org.trc.form.outbound.OutBoundOrderForm;
import org.trc.service.outbound.IOutBoundOrderService;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Date;
import java.util.List;

@Service("outBoundOrderBiz")
public class OutBoundOrderBiz implements IOutBoundOrderBiz {
    @Autowired
    private IOutBoundOrderService outBoundOrderService;

    @Override
    public Pagenation<OutboundOrder> outboundOrderPage(OutBoundOrderForm queryModel, Pagenation<OutboundOrder> page) throws Exception {
        Example example = new Example(OutboundOrder.class);
        Example.Criteria criteria = example.createCriteria();
        setQueryParam(example, criteria, queryModel);
        Pagenation<OutboundOrder> pagenation = outBoundOrderService.pagination(example, page, queryModel);
        List<OutboundOrder> outBoundOrderList = pagenation.getResult();
        if (AssertUtil.collectionIsEmpty(outBoundOrderList)) {
            return pagenation;
        }
        pagenation.setResult(outBoundOrderList);
        return pagenation;
    }

    public void setQueryParam(Example example, Example.Criteria criteria, OutBoundOrderForm queryModel) {
        if (!StringUtils.isBlank(queryModel.getOutboundOrderCode())) {
            criteria.andLike("outboundOrderCode", "%" + queryModel.getOutboundOrderCode() + "%");
        }
        if (!StringUtils.isBlank(queryModel.getWarehouseId())) {
            criteria.andGreaterThan("warehouseId", queryModel.getWarehouseId());
        }
        if (!StringUtils.isBlank(queryModel.getReceiverName())) {
            criteria.andEqualTo("receiverName", "%" + queryModel.getReceiverName() + "%");
        }
        if (!StringUtils.isBlank(queryModel.getStatus())) {
            criteria.andLessThan("state", queryModel.getStatus());
        }
        if (StringUtil.isNotEmpty(queryModel.getStartDate())) {//开始日期
            criteria.andGreaterThanOrEqualTo("updateTime", DateUtils.parseDate(queryModel.getStartDate()));
        }
        if (StringUtil.isNotEmpty(queryModel.getEndDate())) {//截止日期
            Date endDate = DateUtils.parseDate(queryModel.getEndDate());
            criteria.andLessThan("updateTime", DateUtils.addDays(endDate, 1));
        }
        example.orderBy("updateTime").desc();
    }
}
