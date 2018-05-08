package org.trc.service.allocateOrder;

import org.trc.domain.allocateOrder.AllocateOutInOrderBase;
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
     * 设置是否能操作
     * @param pagenation
     */
    void setIsTimeOut(Pagenation pagenation);

    /**
     * 修改关闭状态
     * @param allocateOutInOrderBase
     * @param remark
     * @param isClose
     * @param status
     */
    void updateOrderCancelInfo(AllocateOutInOrderBase allocateOutInOrderBase, String remark, boolean isClose, String status);

    /**
     * 修改取消关闭状态
     * @param allocateOutInOrderBase
     * @param isClose
     */
    void updateOrderCancelInfoExt(AllocateOutInOrderBase allocateOutInOrderBase, boolean isClose);
}
