package org.trc.util;

import org.apache.commons.lang3.StringUtils;
import org.trc.domain.BaseDO;
import org.trc.domain.util.ScmDO;
import org.trc.enums.ZeroToNineEnum;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by george on 2017/4/27.
 */
public class ParamsUtil {

    public static void setBaseDO(ScmDO scmDO){
        Date currentDate = Calendar.getInstance().getTime();
        if(null == scmDO.getCreateTime())
            scmDO.setCreateTime(currentDate);
        if(null == scmDO.getUpdateTime())
            scmDO.setUpdateTime(currentDate);
        scmDO.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        if(scmDO instanceof BaseDO){
            BaseDO baseDO = (BaseDO)scmDO;
            if(StringUtils.isEmpty(baseDO.getIsValid())) {
                baseDO.setIsValid(ZeroToNineEnum.ONE.getCode());
            }
        }
    }

}
