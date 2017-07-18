package org.trc.biz.impl.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.config.ILogInfoBiz;
import org.trc.domain.System.Channel;
import org.trc.domain.config.LogInfo;
import org.trc.form.config.LogInfoForm;
import org.trc.service.impl.config.LogInfoService;
import org.trc.service.impl.util.UserNameUtilService;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by hzqph on 2017/7/14.
 */
@Service("logInfoBiz")
public class LogInfoBiz implements ILogInfoBiz {

    @Autowired
    private LogInfoService logInfoService;
    @Autowired
    private UserNameUtilService userNameUtilService;

    @Override
    public Pagenation<LogInfo> logInfoPage(LogInfoForm queryModel, Pagenation<LogInfo> page) {
        Example example = new Example(LogInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("entityType", queryModel.getEntityType());
        criteria.andEqualTo("entityId", queryModel.getEntityId());
        example.orderBy("operateTime").asc();
        Pagenation<LogInfo> pagenation = logInfoService.pagination(example, page, queryModel);

        List<LogInfo> logInfoList = pagenation.getResult();
        userNameUtilService.handleUserName(logInfoList);
        return pagenation;
    }
}
