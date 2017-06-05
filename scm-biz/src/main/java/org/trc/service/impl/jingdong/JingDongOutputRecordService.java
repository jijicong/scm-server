package org.trc.service.impl.jingdong;

import org.springframework.stereotype.Service;
import org.trc.domain.config.OutputRecordDO;
import org.trc.service.impl.BaseService;
import org.trc.service.jingdong.IJingDongOutputRecordService;

/**
 * Created by hzwyz on 2017/6/5 0005.
 */
@Service("jingDongOutputRecordService")
public class JingDongOutputRecordService extends BaseService<OutputRecordDO, Long> implements IJingDongOutputRecordService {
}
