package org.trc.biz.impl.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.config.ILogInfoBiz;
import org.trc.domain.config.LogInfo;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.config.LogInfoForm;
import org.trc.service.config.ILogInfoService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hzqph on 2017/7/14.
 */
@Service("logInfoBiz")
public class LogInfoBiz implements ILogInfoBiz {

    @Autowired
    private ILogInfoService logInfoService;
    @Autowired
    private IAclUserAccreditInfoService aclUserAccreditInfoService;
    public final static String ADMIN_SIGN = "admin";
    public final static String ADMIN="系统";
    @Override
    public Pagenation<LogInfo> logInfoPage(LogInfoForm queryModel, Pagenation<LogInfo> page) {
        Example example = new Example(LogInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("entityType", queryModel.getEntityType());
        criteria.andEqualTo("entityId", queryModel.getEntityId());
        List<String> operateTypeList=new ArrayList<>();
        operateTypeList.add(null);
        String condition="operate_type is null ";
        if (!StringUtils.isBlank(queryModel.getOperateType())) {
            condition=("(operate_type is null or operate_type="+queryModel.getOperateType()+")");
        }
        criteria.andCondition(condition);
        example.orderBy("operateTime").asc();
        Pagenation<LogInfo> pagenation = logInfoService.pagination(example, page, queryModel);
        List<LogInfo> logInfoList = pagenation.getResult();
        handleUserName(logInfoList);
        return pagenation;
    }

    /**
     * 处理用户名
     * @param logInfoList
     */
    private void handleUserName(List<LogInfo> logInfoList) {
        List<String> userIds = new ArrayList<>();
        for (LogInfo logInfo : logInfoList) {
            userIds.add(logInfo.getOperatorUserid());
        }
        if (!AssertUtil.collectionIsEmpty(userIds)) {
            String[] userIdArr = new String[userIds.size()];
            userIds.toArray(userIdArr);
            Map<String, AclUserAccreditInfo> userMap = aclUserAccreditInfoService.selectByIds(userIdArr);
            for (LogInfo logInfo: logInfoList){
                if (userMap != null) {
                    AclUserAccreditInfo aclUserAccreditInfo=userMap.get(logInfo.getOperatorUserid());
                    if(aclUserAccreditInfo!=null){
                        logInfo.setOperator(aclUserAccreditInfo.getName());
                    }
                }
                if(logInfo.getOperatorUserid().equals(ADMIN_SIGN)){
                    logInfo.setOperator(ADMIN);
                }
            }
        }
    }
}
