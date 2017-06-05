package org.trc.service.impl.jingdong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.config.Common;
import org.trc.mapper.config.ICommonMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.jingdong.ICommonService;

/**
 * Created by hzwyz on 2017/6/5 0005.
 */
@Service("commonService")
public class CommonService extends BaseService<Common, Long> implements ICommonService {

    @Autowired
    ICommonMapper commonMapper;
    @Override
    public Common selectByCode(String code) {
        return commonMapper.selectByCode(code);
    }
}
