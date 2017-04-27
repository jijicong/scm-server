package org.trc.util;

import org.apache.commons.lang.StringUtils;
import org.trc.domain.BaseDO;
import org.trc.enums.ZeroToNineEnum;

import java.util.Date;

/**
 * Created by george on 2017/4/27.
 */
public class ParamsUtil {

    /**
     * 设置BaseDO公共属性
     * @param baseDO
     */
    public static void setBaseDO(BaseDO baseDO){
        if(StringUtils.isEmpty(baseDO.getIsValid())) {
            baseDO.setIsValid(ZeroToNineEnum.ONE.getCode());
        }
        baseDO.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        baseDO.setCreateOperator("test");//FIXME
        Date currentDate = new Date();
        if(null == baseDO.getCreateTime())
            baseDO.setCreateTime(currentDate);
        if(null == baseDO.getUpdateTime())
            baseDO.setUpdateTime(currentDate);
    }

}
