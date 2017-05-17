package org.trc.biz.impl.impower;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.impower.IUserAccreditInfoBiz;
import org.trc.domain.System.Channel;
import org.trc.domain.impower.Role;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.domain.impower.UserAccreditRoleRelation;
import org.trc.domain.impower.UserAddPageDate;
import org.trc.enums.CommonExceptionEnum;
import org.trc.exception.ParamValidException;
import org.trc.form.impower.UserAccreditInfoForm;
import org.trc.service.System.IChannelService;
import org.trc.service.impower.IRoleService;
import org.trc.service.impower.IUserAccreditInfoRoleRelationService;
import org.trc.service.impower.IUserAccreditInfoService;
import org.trc.util.CommonUtil;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
@Service("userAccreditInfoBiz")
public class UserAccreditInfoBiz implements IUserAccreditInfoBiz {

    private final static Logger log = LoggerFactory.getLogger(UserAccreditInfoBiz.class);

    @Resource
    private IUserAccreditInfoService userAccreditInfoService;

    @Resource
    private IRoleService roleService;

    @Autowired
    private IChannelService channelService;

    @Autowired
    private IUserAccreditInfoRoleRelationService userAccreditInfoRoleRelationService;

    /**
     * 分页查询
     *
     * @param form 授权信息查询条件
     * @param page 分页信息
     * @return
     * @throws Exception
     */
    @Override
    public Pagenation<UserAccreditInfo> UserAccreditInfoPage(UserAccreditInfoForm form, Pagenation<UserAccreditInfo> page) throws Exception {

        return null;
    }

    /**
     * 用户名是否存在
     *
     * @param name 用户姓名
     * @return
     * @throws Exception
     */
    @Override
    public UserAccreditInfo findUserAccreditInfoByName(String name) throws Exception {

        if (StringUtil.isEmpty(name) || name == "") {
            String msg = CommonUtil.joinStr("根据用户授权的用户名称查询角色的参数name为空").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        UserAccreditInfo userAccreditInfo = new UserAccreditInfo();
        userAccreditInfo.setName(name);
        return userAccreditInfoService.selectOne(userAccreditInfo);
    }

    /**
     * 查询已启用的渠道
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<Channel> findChannel() throws Exception {
        Example example = new Example(Channel.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isValid", "1");
        example.orderBy("updateTime").desc();
        return channelService.selectByExample(example);
    }


    /**
     * 查询 channelJurisdiction渠道角色
     * wholeJurisdiction全局角色
     */
    @Override
    public List<Role> findChannelOrWholeJur(String roleType) throws Exception {
        Example example = new Example(Role.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isBlank(roleType)) {
            criteria.andIsNotNull("roleType");
        } else {

            criteria.andEqualTo("roleType", roleType);
        }
        example.orderBy("updateTime").desc();
        return roleService.selectByExample(example);
    }

    @Override
    public void saveUserAccreditInfo(UserAddPageDate userAddPageDate) throws Exception {
        //写入user_accredit_info表
        UserAccreditInfo userAccreditInfo = new UserAccreditInfo();
        userAccreditInfo.setName(userAddPageDate.getName());
        userAccreditInfo.setChannelCode(userAddPageDate.getChannelCode());
        userAccreditInfo.setPhone(userAddPageDate.getPhone());
        userAccreditInfo.setRemark(userAddPageDate.getRemark());
        userAccreditInfo.setUserType(userAddPageDate.getUserType());
        userAccreditInfo.setUserId(userAddPageDate.getUserId());
        userAccreditInfo.setIsDeleted("0");
        userAccreditInfo.setCreateOperator("test");
        userAccreditInfo.setIsValid(userAddPageDate.getIsValid());
        userAccreditInfo.setCreateTime(Calendar.getInstance().getTime());
        userAccreditInfo.setUpdateTime(Calendar.getInstance().getTime());
        userAccreditInfoService.insert(userAccreditInfo);

        //写入user_accredit_role_relation表
        if (StringUtils.isNotBlank(userAddPageDate.getRoleNames())) {
            Long roleIds[] = org.trc.util.StringUtil.splitByComma(userAddPageDate.getRoleNames());
            List<UserAccreditRoleRelation> uAcRoleRelationList = new ArrayList<>();
            for (int i = 0; i < roleIds.length; i++) {
                UserAccreditRoleRelation userAccreditRoleRelation = new UserAccreditRoleRelation();
                userAccreditRoleRelation.setUserAccreditId(userAccreditInfo.getId());
                userAccreditRoleRelation.setUserId(userAccreditInfo.getUserId());
                userAccreditRoleRelation.setRoleId(roleIds[i]);
                userAccreditRoleRelation.setIsDeleted("0");
                userAccreditRoleRelation.setIsValid(userAccreditInfo.getIsValid());
                userAccreditRoleRelation.setCreateOperator("test");
                userAccreditRoleRelation.setCreateTime(userAccreditInfo.getCreateTime());
                userAccreditRoleRelation.setUpdateTime(userAccreditInfo.getUpdateTime());
                uAcRoleRelationList.add(userAccreditRoleRelation);
            }
            userAccreditInfoRoleRelationService.insertList(uAcRoleRelationList);
        }
    }


}
