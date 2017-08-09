package org.trc.biz.impl.requestFlow;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.requestFlow.IRequestFlowBiz;
import org.trc.domain.config.RequestFlow;
import org.trc.enums.RequestFlowTypeEnum;
import org.trc.enums.SuccessFailureEnum;
import org.trc.form.JDModel.ReturnTypeDO;
import org.trc.model.ToGlyResultDO;
import org.trc.service.config.IRequestFlowService;
import org.trc.util.AppResult;
import org.trc.util.GuidUtil;
import org.trc.util.ResponseAck;

import java.util.Calendar;

/**
 * Created by hzwdx on 2017/7/14.
 */
@Service("requestFlowBiz")
public class RequestFlowBiz implements IRequestFlowBiz {

    private Logger log = LoggerFactory.getLogger(RequestFlowBiz.class);

    @Autowired
    private IRequestFlowService requestFlowService;

    @Override
    public void saveRequestFlow(String requestParam, String request, String responser, RequestFlowTypeEnum requestFlowTypeEnum, Object remoteInvokeResult, String guidUtilPrifix) {
        try{
            RequestFlow requestFlow = new RequestFlow();
            requestFlow.setType(requestFlowTypeEnum.getCode());
            requestFlow.setRequester(request);
            requestFlow.setResponder(responser);
            requestFlow.setRequestParam(requestParam);
            requestFlow.setResponseParam(JSON.toJSONString(remoteInvokeResult));
            requestFlow.setRequestNum(GuidUtil.getNextUid(guidUtilPrifix));
            requestFlow.setRequestTime(Calendar.getInstance().getTime());
            if(remoteInvokeResult instanceof AppResult){
                AppResult appResult = (AppResult)remoteInvokeResult;
                if (StringUtils.equals(appResult.getAppcode(), SuccessFailureEnum.SUCCESS.getCode())) {
                    requestFlow.setStatus(SuccessFailureEnum.SUCCESS.getCode());
                } else {
                    requestFlow.setStatus(SuccessFailureEnum.FAILURE.getCode());
                }
                requestFlow.setRemark(appResult.getDatabuffer());
            }else if(remoteInvokeResult instanceof ReturnTypeDO){
                ReturnTypeDO returnTypeDO = (ReturnTypeDO)remoteInvokeResult;
                if (returnTypeDO.getSuccess()) {
                    requestFlow.setStatus(SuccessFailureEnum.SUCCESS.getCode());
                } else {
                    requestFlow.setStatus(SuccessFailureEnum.FAILURE.getCode());
                }
                requestFlow.setRemark(returnTypeDO.getResultMessage());
            }else if(remoteInvokeResult instanceof ToGlyResultDO){
                ToGlyResultDO toGlyResultDO = (ToGlyResultDO)remoteInvokeResult;
                if (StringUtils.equals(toGlyResultDO.getStatus(), SuccessFailureEnum.SUCCESS.getCode())) {
                    requestFlow.setStatus(SuccessFailureEnum.SUCCESS.getCode());
                } else {
                    requestFlow.setStatus(SuccessFailureEnum.FAILURE.getCode());
                }
                requestFlow.setRemark(toGlyResultDO.getMsg());
            }else if(remoteInvokeResult instanceof ResponseAck){
                ResponseAck responseAck = (ResponseAck)remoteInvokeResult;
                if (StringUtils.equals(responseAck.getCode(), ResponseAck.SUCCESS_CODE)) {
                    requestFlow.setStatus(SuccessFailureEnum.SUCCESS.getCode());
                } else {
                    requestFlow.setStatus(SuccessFailureEnum.FAILURE.getCode());
                }
                requestFlow.setRemark(responseAck.getMessage());
            }
            requestFlowService.insert(requestFlow);
        }catch (Exception e){
            String msg = String.format("保存请求流水异常,异常信息:%s", e.getMessage());
            log.error(msg, e);
        }

    }
}
