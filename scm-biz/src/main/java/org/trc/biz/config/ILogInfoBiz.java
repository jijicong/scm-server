package org.trc.biz.config;

import org.trc.domain.System.Channel;
import org.trc.domain.config.LogInfo;
import org.trc.form.config.LogInfoForm;
import org.trc.util.Pagenation;

/**
 * Created by hzqph on 2017/7/14.
 */
public interface ILogInfoBiz {

    /**
     * 操作日志分页记录
     * @param queryModel
     * @param page
     * @return
     */
     Pagenation<LogInfo> logInfoPage(LogInfoForm queryModel,Pagenation<LogInfo> page);

}
