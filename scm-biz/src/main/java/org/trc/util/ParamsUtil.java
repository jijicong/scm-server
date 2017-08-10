package org.trc.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.trc.domain.BaseDO;
import org.trc.domain.util.ScmDO;
import org.trc.enums.TrcActionTypeEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.TrcParam;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by george on 2017/4/27.
 */
public class ParamsUtil {

    private static final String OR = "|";

    private static final String UNDER_LINE = "_";

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

    /**
     * 生成泰然城请求头信息
     * @param action
     * @return
     */
    public static TrcParam generateTrcSign(String trcKey, TrcActionTypeEnum action){
        String noticeNum = GuidUtil.getNextUid(action.getCode() + UNDER_LINE);
        StringBuilder stringBuilder = new StringBuilder();
        Long currentTime = System.currentTimeMillis();
        stringBuilder.append(trcKey).append(OR).append(action.getCode()).append(OR).append(noticeNum).append(OR).append(currentTime);
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        TrcParam trcParam = new TrcParam();
        trcParam.setAction(action.getCode());
        trcParam.setOperateTime(currentTime);
        trcParam.setNoticeNum(noticeNum);
        trcParam.setSign(sign);
        return trcParam;
    }

    public static void main(String[] args){
        generateTrcSign("gyl-tairan", TrcActionTypeEnum.EDIT_BRAND);
    }

}
