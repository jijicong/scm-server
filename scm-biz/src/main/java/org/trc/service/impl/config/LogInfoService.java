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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    public void recordLog(Object object, String objectId, String userId, String logOperation, String remark,String operateType) {
        try{
            LogInfo logInfo=new LogInfo();
            logInfo.setEntityId(objectId);
            logInfo.setEntityType(object.getClass().getSimpleName());
            logInfo.setOperation(logOperation);
            if(object instanceof ScmDO){
                ScmDO scmDO= (ScmDO) object;
                if (scmDO.getCreateTime()!=null){
                    logInfo.setOperateTime(scmDO.getCreateTime());
                }else{
                    logInfo.setOperateTime(Calendar.getInstance().getTime());
                }
            }else{
                logInfo.setOperateTime(Calendar.getInstance().getTime());
            }
            if(!StringUtils.isBlank(userId)){
                logInfo.setOperatorUserid(userId);
            }
            if(!StringUtils.isBlank(remark)){
                logInfo.setRemark(remark);
            }
            if(!StringUtils.isBlank(operateType)){
                logInfo.setOperateType(operateType);
            }
            logInfoMapper.insert(logInfo);
        }catch (Exception e){
            log.error("日志记录异常message:{},e:{}",e.getMessage(),e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
    public void recordLogs(Object object, String userId, String logOperation, String remark, String operateType, List<String> objectIds) {
        try{
            List<LogInfo> logInfoList =new ArrayList<>();
            for (String objectId: objectIds) {
                LogInfo logInfo=new LogInfo();
                logInfo.setEntityId(objectId);
                logInfo.setEntityType(object.getClass().getSimpleName());
                logInfo.setOperation(logOperation);
                if(object instanceof ScmDO){
                    ScmDO scmDO= (ScmDO) object;
                    if (scmDO.getCreateTime()!=null){
                        logInfo.setOperateTime(scmDO.getCreateTime());
                    }else{
                        logInfo.setOperateTime(Calendar.getInstance().getTime());
                    }
                }else{
                    logInfo.setOperateTime(Calendar.getInstance().getTime());
                }
                if(!StringUtils.isBlank(userId)){
                    logInfo.setOperatorUserid(userId);
                }
                if(!StringUtils.isBlank(remark)){
                    logInfo.setRemark(remark);
                }
                if(!StringUtils.isBlank(operateType)){
                    logInfo.setOperateType(operateType);
                }
                logInfoList.add(logInfo);
            }
            logInfoMapper.insertList(logInfoList);
        }catch (Exception e){
            log.error("日志记录异常message:{},e:{}",e.getMessage(),e);
        }
    }
}
