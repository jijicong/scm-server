package org.trc.service.config;

import org.trc.domain.config.QureyCondition;
import org.trc.domain.config.RequestFlow;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by hzdzf on 2017/6/7.
 */
public interface IRequestFlowService extends IBaseService<RequestFlow,Long> {
    int changeState(String requestNum) throws Exception;

    int updateRequestFlowByRequestNum(RequestFlow requestFlow) throws Exception;

    List<RequestFlow> queryBatch(QureyCondition condition);
}
