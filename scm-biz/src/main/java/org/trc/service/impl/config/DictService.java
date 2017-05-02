package org.trc.service.impl.config;

import org.springframework.stereotype.Service;
import org.trc.domain.dict.Dict;
import org.trc.service.config.IDictService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzwdx on 2017/4/19.
 */
@Service("dictService")
public class DictService extends BaseService<Dict,Long> implements IDictService{
}
