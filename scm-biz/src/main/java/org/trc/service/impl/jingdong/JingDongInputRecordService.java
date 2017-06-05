package org.trc.service.impl.jingdong;

import org.springframework.stereotype.Service;
import org.trc.domain.config.InputRecordDO;
import org.trc.service.impl.BaseService;
import org.trc.service.jingdong.IJingDongInputRecordService;

/**
 * Created by hzwyz on 2017/6/5 0005.
 */
@Service("jingDongInputRecordService")
public class JingDongInputRecordService extends BaseService<InputRecordDO, Long> implements IJingDongInputRecordService{
}
