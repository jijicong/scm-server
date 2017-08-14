package org.trc.service.impl.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.config.QureyCondition;
import org.trc.domain.config.RequestFlow;
import org.trc.mapper.config.IRequestFlowMapper;
import org.trc.service.config.IRequestFlowService;
import org.trc.service.impl.BaseService;

import java.util.List;

/**
 * Created by hzdzf on 2017/6/7.
 */
@Service("requestFlowService")
public class RequestFlowService extends BaseService<RequestFlow,Long> implements IRequestFlowService {
    @Autowired
    IRequestFlowMapper requestFlowMapper;

    public int changeState(RequestFlow requestFlow) throws Exception{
        return requestFlowMapper.changeState(requestFlow);
    }

    public int updateRequestFlowByRequestNum(RequestFlow requestFlow){
        return requestFlowMapper.updateRequestFlowByRequestNum(requestFlow);
    }
    public List<RequestFlow> queryBatch(QureyCondition condition){
        return requestFlowMapper.queryBatch(condition);
    }
}
