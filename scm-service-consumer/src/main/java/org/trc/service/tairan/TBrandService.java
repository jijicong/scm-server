package org.trc.service.tairan;

import org.trc.domain.category.Brand;
import org.trc.service.tairan.model.ResultModel;

/**
 * 泰然城渠道品牌回调
 * Created by hzdzf on 2017/5/22.
 */
public interface TBrandService {

    /**
     * @param action      行为
     * @param oldBrand    旧品牌信息
     * @param brand       品牌信息
     * @param operateTime 时间戳
     * @return 渠道调回信息
     */
    ResultModel sendBrandNotice(String action, Brand oldBrand, Brand brand, long operateTime) throws Exception;
}
