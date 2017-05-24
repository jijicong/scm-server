package org.trc.service.trc;

import org.trc.domain.category.Brand;

/**
 * 泰然城渠道品牌回调
 * Created by hzdzf on 2017/5/22.
 */
public interface BrandService {

    /**
     * @param action    行为
     * @param timeStamp 时间戳
     * @param brand     品牌信息
     * @return 渠道调回信息
     */
    String sendBrandNotice(String action, long timeStamp, Brand brand, String status) throws Exception;
}
