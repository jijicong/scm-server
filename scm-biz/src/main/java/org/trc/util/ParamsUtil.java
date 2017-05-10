package org.trc.util;

import org.apache.commons.lang.StringUtils;
import org.trc.domain.BaseDO;
import org.trc.domain.util.CommonDO;
import org.trc.domain.util.ScmDO;
import org.trc.enums.ZeroToNineEnum;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by george on 2017/4/27.
 */
public class ParamsUtil {

/*    public static void setBaseDO(BaseDO baseDO){
        if(StringUtils.isEmpty(baseDO.getIsValid())) {
            baseDO.setIsValid(ZeroToNineEnum.ONE.getCode());
        }
        baseDO.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        baseDO.setCreateOperator("test");//FIXME
        Date currentDate = Calendar.getInstance().getTime();
        if(null == baseDO.getCreateTime())
            baseDO.setCreateTime(currentDate);
        if(null == baseDO.getUpdateTime())
            baseDO.setUpdateTime(currentDate);
    }*/

    public static void setBaseDO(ScmDO scmDO){
        scmDO.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        Date currentDate = Calendar.getInstance().getTime();
        if(null == scmDO.getCreateTime())
            scmDO.setCreateTime(currentDate);
        if(null == scmDO.getUpdateTime())
            scmDO.setUpdateTime(currentDate);
        if(scmDO instanceof CommonDO){
            CommonDO commonDO = (CommonDO)scmDO;
            commonDO.setCreateOperator("test");//FIXME
        }else if(scmDO instanceof BaseDO){
            BaseDO baseDO = (BaseDO)scmDO;
            if(StringUtils.isEmpty(baseDO.getIsValid())) {
                baseDO.setIsValid(ZeroToNineEnum.ONE.getCode());
            }
        }
    }


}
