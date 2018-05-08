package org.trc.service.allocateOrder;

import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;

/**
 * 调拨扩展公共服务接口
 */
public interface IAllocateOrderExtService {

    /**
     * 设置创建人
     * @param createOpertorName
     * @param criteria
     */
    void setCreateOperator(String createOpertorName, Example.Criteria criteria);

    /**
     * 设置调拨单其他字段名称
     * @param pagenation
     */
    void setAllocateOrderOtherNames(Pagenation pagenation);

    /**
     * 创建调拨入库单
     * @param allocateInOrder 调拨入库单对象
     * @param createOperator 当前操作人
     */
    void createAllocateInOrder(AllocateInOrder allocateInOrder, String createOperator);

    /**
     * 调拨入库单作废
     * @param allocateOrderCode
     */
    void discardedAllocateInOrder(String allocateOrderCode);

}
