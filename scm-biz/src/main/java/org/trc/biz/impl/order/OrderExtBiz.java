package org.trc.biz.impl.order;

import org.springframework.stereotype.Service;
import org.trc.biz.order.IOrderExtBiz;
import org.trc.util.cache.SupplierOrderCacheEvict;

@Service("orderExtBiz")
public class OrderExtBiz implements IOrderExtBiz {

    @Override
    @SupplierOrderCacheEvict
    public void cleanOrderCache() {

    }

}
