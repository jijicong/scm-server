package org.trc.service.impl.config;

import org.springframework.stereotype.Service;
import org.trc.domain.config.SkuListForm;
import org.trc.service.config.ISkusListService;
import org.trc.service.impl.BaseService;

/**
 * Created by hzwyz on 2017/6/15 0015.
 */
@Service("skusListService")
public class SkusListService extends BaseService<SkuListForm,Long> implements ISkusListService {
}
