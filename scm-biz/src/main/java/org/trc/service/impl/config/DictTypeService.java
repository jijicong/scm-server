package org.trc.service.impl.config;

import org.springframework.stereotype.Service;
import org.trc.domain.dict.DictType;
import org.trc.service.config.IDictTypeService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzwdx on 2017/4/19.
 */
@Service("dictTypeService")
public class DictTypeService extends BaseService<DictType,Long> implements IDictTypeService{
}
