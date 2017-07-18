package org.trc.service.config;

import org.trc.domain.config.LogInfo;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.LogOperationEnum;
import org.trc.service.IBaseService;

/**
 * Created by hzqph on 2017/6/20.
 */
public interface ILogInfoService extends IBaseService<LogInfo,Long> {
        /**
         *
         * @param object 操作对象
         * @param objectId 操作对象Id
         * @param userId 登录用户
         * @param logOperation 日志操作类型
         * @param remark 备注
         */
        void recordLog(Object object, String objectId, String userId, String logOperation, String remark);
}
