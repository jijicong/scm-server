package org.trc.mapper.config;


import org.trc.domain.config.QureyCondition;
import org.trc.domain.config.RequestFlow;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by hzdzf on 2017/6/7.
 */
public interface IRequestFlowMapper extends BaseMapper<RequestFlow> {

    int changeState(RequestFlow requestFlow) throws Exception;

    int updateRequestFlowByRequestNum(RequestFlow requestFlow);

    List<RequestFlow> queryBatch(QureyCondition condition);
}
