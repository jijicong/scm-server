package org.trc.service.impl.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.domain.config.LogInfo;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.util.ScmDO;
import org.trc.enums.LogOperationEnum;
import org.trc.mapper.config.ILogInfoMapper;
import org.trc.service.config.ILogInfoService;
import org.trc.service.impl.BaseService;

import java.util.Calendar;

/**
 * Created by hzqph on 2017/6/20.
 */
@Service
public class LogInfoService extends BaseService<LogInfo,Long> implements ILogInfoService{
    private Logger log = LoggerFactory.getLogger(LogInfoService.class);
    @Autowired
    private ILogInfoMapper logInfoMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public void recordLog(Object object, String objectId, AclUserAccreditInfo aclUserAccreditInfo, LogOperationEnum logOperationEnum, String remark) {
        try{
            LogInfo logInfo=new LogInfo();
            logInfo.setEntityId(objectId);
            logInfo.setEntityType(object.getClass().getSimpleName());
            logInfo.setOperation(logOperationEnum.getMessage());
            if(object instanceof ScmDO){
                ScmDO scmDO= (ScmDO) object;
                logInfo.setOperateTime(scmDO.getCreateTime());
            }else{
                logInfo.setOperateTime(Calendar.getInstance().getTime());
            }
            if(aclUserAccreditInfo!=null){
                logInfo.setOperator(aclUserAccreditInfo.getName());
                logInfo.setOperatorUserid(aclUserAccreditInfo.getUserId());
            }
            if(StringUtils.isBlank(remark)){
                logInfo.setRemark(remark);
            }
            logInfoMapper.insert(logInfo);
        }catch (Exception e){
            log.error("日志记录异常："+e.getMessage());
        }
    }
}
