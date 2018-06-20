package org.trc.service.allocateOrder;

import org.trc.domain.allocateOrder.AllocateOrderBase;
import org.trc.domain.allocateOrder.AllocateOutInOrderBase;
import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.domain.allocateOrder.AllocateOutOrder;
import org.trc.form.AllocateOrder.AllocateInOrderParamForm;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

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
     * 设置调拨单其他字段名称
     * @param allocateOrderBaseList
     */
    void setAllocateOrderOtherNames(List<AllocateOrderBase> allocateOrderBaseList);

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
    /**
     * 创建调拨入库单
     * @param allocateInOrder 调拨入库单对象
     * @param createOperator 当前操作人
     * @return 返回入库通知单号
     */
    String createAllocateInOrder(AllocateInOrder allocateInOrder, String createOperator);

    /**
     * 调拨入库单作废
     * @param allocateOrderCode
     */
    void discardedAllocateInOrder(String allocateOrderCode);

    /**
     * 创建调拨出库单
     * @param allocateoutOrder 调拨出库单对象
     * @param createOperator 当前操作人
     * @return 返回出库通知单号
     */
    String createAllocateOutOrder(AllocateOutOrder allocateoutOrder, String createOperator, String status);

    /**
     * 调拨出库单作废
     * @param allocateOrderCode
     */
    void discardedAllocateOutOrder(String allocateOrderCode);

    /**
     * 根据取消来更新调拨入库单状态
     * @param allocateOrderCode 调拨单号
     * @param type 取消类型：0-关闭, 1-取消发货, 2-作废
     * @param flag 操作标识: 0-关闭/取消发货,1-取消关闭/重新发货
     * @param cancelReson 关闭原因
     * @param cancelResult  取消结果
     */
    AllocateInOrderParamForm updateAllocateInOrderByCancel(String allocateOrderCode, String type, String flag, String cancelReson, String cancelResult);

    /**
     * 设置地方名称
     * @param allocateOrderBase
     */
    void setArea(AllocateOrderBase allocateOrderBase);

	/**
	 * 设置出入库仓库名
	 * @param allocateOrderBase
	 */
	void setAllocateOrderWarehouseName(AllocateOrderBase allocateOrderBase);

    /**
     * 设置区域名称
     * @param baseOrder
     */
    void setDistrictName(AllocateOrderBase baseOrder);

}
