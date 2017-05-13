package org.trc.biz.impl.impower;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trc.biz.impower.IUserAccreditInfoBiz;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.enums.CommonExceptionEnum;
import org.trc.exception.ParamValidException;
import org.trc.form.impower.UserAccreditInfoForm;
import org.trc.service.impower.IRoleService;
import org.trc.service.impower.IUserAccreditInfoService;
import org.trc.util.CommonUtil;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;

/**
 * Created by sone on 2017/5/11.
 */
@Service("userAccreditInfoBiz")
public class UserAccreditInfoBiz implements IUserAccreditInfoBiz{

    private final static Logger log = LoggerFactory.getLogger(UserAccreditInfoBiz.class);

    @Resource
    private IUserAccreditInfoService userAccreditInfoService;

    @Resource
    private IRoleService roleService;

    @Override
    public Pagenation<UserAccreditInfo> UserAccreditInfoPage(UserAccreditInfoForm form, Pagenation<UserAccreditInfo> page) throws Exception {

        return null;
    }

    @Override
    public UserAccreditInfo findUserAccreditInfoByName(String name) throws Exception {

        if(StringUtil.isEmpty(name)  || name==""){
            String msg=CommonUtil.joinStr("根据用户授权的用户名称查询角色的参数name为空").toString();
            log.error(msg);
            throw  new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        UserAccreditInfo userAccreditInfo=new UserAccreditInfo();
        userAccreditInfo.setName(name);

        return null;
    }

}
