package org.trc.biz.jingdong;

import org.trc.form.JDModel.*;
import org.trc.util.Pagenation;

/**
 * Created by hzwyz on 2017/5/19 0019.
 */
public interface IJingDongBiz {


    Pagenation<JdBalanceDetail> checkBalanceDetail(BalanceDetailDO queryModel, Pagenation<JdBalanceDetail> page) throws Exception;

    /**
     * 获取所有京东交易类型
     * @return
     */
    ReturnTypeDO getAllTreadType() throws Exception;

}
