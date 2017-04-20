package org.trc.service.impl;

import org.springframework.stereotype.Service;
import org.trc.domain.score.DictType;
import org.trc.service.IDictTypeService;

/**
 * Created by hzwdx on 2017/4/19.
 */
@Service("dictTypeService")
public class DictTypeService extends BaseService<DictType,Long> implements IDictTypeService{
}
