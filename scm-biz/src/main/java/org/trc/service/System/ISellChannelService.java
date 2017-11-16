package org.trc.service.System;

import org.trc.domain.System.SellChannel;
import org.trc.service.IBaseService;

import java.util.List;

/**
 *
 * @author sone
 * @date 2017/5/2
 */
public interface ISellChannelService extends IBaseService<SellChannel,Long>{
    /**查询销售渠道列表
     * @return
     */
    List<SellChannel> queryAllSellChannel();
}
