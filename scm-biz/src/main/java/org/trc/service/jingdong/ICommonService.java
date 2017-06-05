package org.trc.service.jingdong;

import org.trc.domain.config.Common;
import org.trc.service.IBaseService;

/**
 * Created by hzwyz on 2017/6/5 0005.
 */
public interface ICommonService extends IBaseService<Common, Long> {
    public Common selectByCode(String code);
}
