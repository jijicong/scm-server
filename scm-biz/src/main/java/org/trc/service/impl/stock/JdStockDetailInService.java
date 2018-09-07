package org.trc.service.impl.stock;

import org.springframework.stereotype.Service;
import org.trc.domain.stock.JdStockInDetail;
import org.trc.service.impl.BaseService;
import org.trc.service.stock.IJdStockInDetailService;

/**
 * @author hzliuwei
 * @create 2018/9/7
 */
@Service("jdStockDetailInService")
public class JdStockDetailInService extends BaseService<JdStockInDetail, Long> implements IJdStockInDetailService {
}
