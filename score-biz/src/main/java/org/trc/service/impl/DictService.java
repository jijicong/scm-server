package org.trc.service.impl;

import org.springframework.stereotype.Service;
import org.trc.domain.score.Dict;
import org.trc.service.IDictService;

/**
 * Created by hzwdx on 2017/4/19.
 */
@Service("dictService")
public class DictService extends BaseService<Dict,Long> implements IDictService{
}
