package org.trc.service.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trc.domain.util.QiNiuUrlInfo;
import org.trc.mapper.util.IQiNiuUrlInfoMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.util.IQiNiuUrlInfoService;

import javax.annotation.Resource;

/**
 * Created by hzcyn on 2018/9/4.
 */
@Service("qiNiuUrlInfoService")
public class QiNiuUrlInfoService extends BaseService<QiNiuUrlInfo, Long> implements IQiNiuUrlInfoService {

    private Logger log = LoggerFactory.getLogger(QiNiuUrlInfoService.class);

    @Resource
    private IQiNiuUrlInfoMapper qiNiuUrlInfoMapper;

}
