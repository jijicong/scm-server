package org.trc.service.impl.recordTime;

import org.springframework.stereotype.Service;
import org.trc.domain.recordTime.MethodInfo;
import org.trc.service.impl.BaseService;
import org.trc.service.recordTime.IMethodInfoService;

/**
 * Created by sone on 2017/8/21.
 */
@Service("methodInfoService")
public class MethodInfoService extends BaseService<MethodInfo,Long> implements IMethodInfoService {

}
