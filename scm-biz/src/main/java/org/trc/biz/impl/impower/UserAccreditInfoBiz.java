package org.trc.biz.impl.impower;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trc.biz.impower.IUserAccreditInfoBiz;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.form.impower.UserAccreditInfoForm;
import org.trc.service.impower.IRoleService;
import org.trc.service.impower.IUserAccreditInfoService;
import org.trc.util.Pagenation;

import javax.annotation.Resource;

/**
 * Created by sone on 2017/5/11.
 */
@Service
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
}
