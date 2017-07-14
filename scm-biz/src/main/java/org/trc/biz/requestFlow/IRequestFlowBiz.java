package org.trc.biz.requestFlow;

import org.trc.enums.RequestFlowTypeEnum;

/**
 * Created by hzwdx on 2017/7/14.
 */
public interface IRequestFlowBiz {

    /**
     * 保存请求流水
     * @param requestParam 请求参数
     * @param request 请求方标志
     * @param responser 响应方标志
     * @param requestFlowTypeEnum 请求流水枚举
     * @param remoteInvokeResult 远程调用返回结果
     * @param guidUtilPrifix 请求流水前缀
     */
    void saveRequestFlow(String requestParam, String request, String responser,
                                RequestFlowTypeEnum requestFlowTypeEnum, Object remoteInvokeResult, String guidUtilPrifix);

}
