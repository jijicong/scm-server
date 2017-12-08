package org.trc.service.config;

import org.trc.domain.config.QureyCondition;
import org.trc.domain.config.RequestFlow;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by hzdzf on 2017/6/7.
 */
public interface IRequestFlowService extends IBaseService<RequestFlow,Long> {
    int changeState(RequestFlow requestFlow) throws Exception;

    int updateRequestFlowByRequestNum(RequestFlow requestFlow);

    List<RequestFlow> queryBatch(QureyCondition condition);
    
    /**
     * 插入请求流水记录
     * @param requester  请求者
     * @param responder  发送者
     * @param type  请求类型
     * @param status 状态
     * @param reqParam 请求参数
     * @return 请求流水
     */
    String insertRequestFlow(String requester, String responder, String type, String status, String reqParam);
}
