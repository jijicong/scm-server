package org.trc.service.impl.recordTime;

import org.springframework.stereotype.Service;
import org.trc.domain.recordTime.MethodInfo;
import org.trc.domain.recordTime.MethodLongTime;
import org.trc.service.impl.BaseService;
import org.trc.service.recordTime.IMethodInfoService;
import org.trc.service.recordTime.IMethodLongTimeService;

/**
 * Created by sone on 2017/8/21.
 */
@Service("methodLongTimeService")
public class MethodLongTimeService extends BaseService<MethodLongTime,Long> implements IMethodLongTimeService{

}
