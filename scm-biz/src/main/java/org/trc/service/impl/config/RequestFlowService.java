package org.trc.service.impl.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.constant.RequestFlowConstant;
import org.trc.domain.config.QureyCondition;
import org.trc.domain.config.RequestFlow;
import org.trc.enums.RequestFlowStatusEnum;
import org.trc.enums.RequestFlowTypeEnum;
import org.trc.mapper.config.IRequestFlowMapper;
import org.trc.service.config.IRequestFlowService;
import org.trc.service.impl.BaseService;
import org.trc.util.GuidUtil;

import java.util.Calendar;
import java.util.List;

/**
 * Created by hzdzf on 2017/6/7.
 */
@Service("requestFlowService")
public class RequestFlowService extends BaseService<RequestFlow,Long> implements IRequestFlowService {
	private final static Logger log = LoggerFactory.getLogger(RequestFlowService.class);
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

	@Override
	//@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public String insertRequestFlow(String requester, String responder, String type, String status, String reqParam) {
		try {
			RequestFlow requestFlow = new RequestFlow();
			requestFlow.setRequester(requester);
			requestFlow.setResponder(responder);
			requestFlow.setType(type);
			requestFlow.setRequestTime(Calendar.getInstance().getTime());
			String requestNum = GuidUtil.getNextUid(RequestFlowConstant.TRC);
			requestFlow.setRequestNum(requestNum);
			requestFlow.setStatus(status);
			requestFlow.setRequestParam(reqParam);
			requestFlowMapper.insert(requestFlow);
			return requestNum;
		} catch (Exception e) {
			log.error("请求流水日志记录异常:{}",e.getMessage());
			e.printStackTrace();
		}
		return "";
		
	}
}
