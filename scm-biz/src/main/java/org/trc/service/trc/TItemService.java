package org.trc.service.trc;

import org.trc.domain.goods.Items;

/**
 * Created by hzdzf on 2017/5/26.
 */
public interface TItemService {

    String sendItemNotice(Items items,long operateTime,String action) throws Exception;
}
