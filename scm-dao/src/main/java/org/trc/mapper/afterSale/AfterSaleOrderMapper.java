package org.trc.mapper.afterSale;

import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * <p>
 * 售后主表 Mapper 接口
 * </p>
 *
 * @author wangjie
 * @since 2018-08-27
 */
public interface AfterSaleOrderMapper extends BaseMapper<AfterSaleOrder> {
    int updateAfterSaleOrderList(List<AfterSaleOrder> afterSaleOrderList) throws Exception;
}