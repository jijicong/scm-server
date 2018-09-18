package org.trc.service.impl.stock;

import org.springframework.stereotype.Service;
import org.trc.domain.stock.JdStockOutDetail;
import org.trc.service.impl.BaseService;
import org.trc.service.stock.IJdStockOutDetailService;

/**
 * @author hzliuwei
 * @create 2018/9/7
 */
@Service("jdStockOutDetailService")
public class JdStockOutDetailService extends BaseService<JdStockOutDetail, Long> implements IJdStockOutDetailService {
}
