package org.trc.biz.impl.impower;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
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
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ValidEnum;
import org.trc.exception.ConfigException;
import org.trc.exception.ParamValidException;
import org.trc.form.impower.UserAccreditInfoForm;
import org.trc.service.System.IChannelService;
import org.trc.service.impower.IRoleService;
import org.trc.service.impower.IUserAccreditInfoRoleRelationService;
import org.trc.service.impower.IUserAccreditInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.CommonUtil;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by sone on 2017/5/11.
 */
@Service("userAccreditInfoBiz")
public class UserAccreditInfoBiz<T> implements IUserAccreditInfoBiz {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserAccreditInfoBiz.class);

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
    public Pagenation<UserAddPageDate> UserAccreditInfoPage(UserAccreditInfoForm form, Pagenation<UserAddPageDate> page) throws Exception {
        PageHelper.startPage(page.getPageNo(), page.getPageSize());
        Map<String, Object> map = new HashMap<>();
        map.put("name", form.getName());
        map.put("phone", form.getPhone());
        map.put("isValid", form.getIsValid());
        List<UserAccreditInfo> pageDateList = userAccreditInfoService.selectAccreditInfoList(map);
        List<UserAddPageDate> pageDateRoleList = page.getResult();
        if (pageDateList != null && !pageDateList.isEmpty() && pageDateList.size() > 0) {//1.按要求查处需要的授权用户
            pageDateRoleList = handleRolesStr(pageDateList);
        }
        int count = userAccreditInfoService.selectCountUser(map);
        page.setTotalCount(count);
        page.setResult(pageDateRoleList);
        return page;
    }

    @Override
    public List<UserAddPageDate> handleRolesStr(List<UserAccreditInfo> list) throws Exception{

        List<UserAddPageDate> pageDateRoleList = new ArrayList<>();
        Long[] userIds = new Long[list.size()];
        int temp = 0;
        for (UserAccreditInfo userAccreditInfo : list) {
            userIds[temp] = userAccreditInfo.getId();
            temp += 1;
        }
        //2.查询出这些用户对应的对应的角色名称集合
        List<UserAddPageDate> userAddPageDateList = userAccreditInfoService.selectUserAddPageList(userIds);
        if (userAddPageDateList != null && !userAddPageDateList.isEmpty() && userAddPageDateList.size() > 0) {
            for (UserAccreditInfo userAccreditInfo : list) {
                UserAddPageDate userAddPageDate = new UserAddPageDate(userAccreditInfo);
//                userAddPageDate.setId(userAccreditInfo.getId());
//                userAddPageDate.setName(userAccreditInfo.getName());
//                userAddPageDate.setPhone(userAccreditInfo.getPhone());
//                userAddPageDate.setUserType(userAccreditInfo.getUserType());
//                userAddPageDate.setIsValid(userAccreditInfo.getIsValid());
//                userAddPageDate.setCreateOperator(userAccreditInfo.getCreateOperator());
//                userAddPageDate.setUpdateTime(userAccreditInfo.getUpdateTime());
                StringBuilder rolesStr = new StringBuilder();
                for (UserAddPageDate seletUserAddPageDate : userAddPageDateList) {
                    if (userAccreditInfo.getId().equals(seletUserAddPageDate.getId())) {
                        if (rolesStr == null || rolesStr.length() == 0) {
                            rolesStr.append(seletUserAddPageDate.getRoleNames());
                        } else {
                            rolesStr.append("," + seletUserAddPageDate.getRoleNames());
                        }
                    }
                }
                userAddPageDate.setRoleNames(rolesStr.toString());
                pageDateRoleList.add(userAddPageDate);
            }
        }
        return pageDateRoleList;
    }

    @Override
    public void updateUserAccreditInfoStatus(UserAccreditInfo userAccreditInfo) throws Exception {

        AssertUtil.notNull(userAccreditInfo,"授权管理模块修改授权信息失败，授权信息为空");
        UserAccreditInfo updateUserAccreditInfo = new UserAccreditInfo();
        updateUserAccreditInfo.setId(userAccreditInfo.getId());
        if (userAccreditInfo.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updateUserAccreditInfo.setIsValid(ValidEnum.NOVALID.getCode());
        } else {
            updateUserAccreditInfo.setIsValid(ValidEnum.VALID.getCode());
        }
        updateUserAccreditInfo.setUpdateTime(Calendar.getInstance().getTime());
        int count = userAccreditInfoService.updateByPrimaryKeySelective(updateUserAccreditInfo);
        if(count == 0){
            String msg = CommonUtil.joinStr("修改授权", JSON.toJSONString(userAccreditInfo),"数据库操作失败").toString();
            LOGGER.error(msg);
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
        }

    }

    /**
     * 用户名是否存在
     * @param name 用户姓名
     * @return
     * @throws Exception
     */
    @Override
    public int checkUserByName(Long id,String name) throws Exception {
        AssertUtil.notNull(id,"根据用户授权的用户名称查询角色的参数id为空");
        AssertUtil.notBlank(name,"根据用户授权的用户名称查询角色的参数name为空");
        Example example = new Example(UserAccreditInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("id", id);
        criteria.andEqualTo("name", name);
        return userAccreditInfoService.selectByExample(example).size();
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

    /**
     * 根据ID查询
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public UserAddPageDate findUserAccreditInfoById(Long id) throws Exception {
        AssertUtil.notNull(id, "根据授权Id的查询用户userAccreditInfo，参数Id为空");
        UserAddPageDate userAddPageDate;
        UserAccreditInfo userAccreditInfo = new UserAccreditInfo();
        userAccreditInfo.setId(id);
        userAccreditInfo = userAccreditInfoService.selectOne(userAccreditInfo);
        if (null == userAccreditInfo) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", id.toString(), "]查询角色为空").toString();
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, msg);
        }
        userAddPageDate = new UserAddPageDate(userAccreditInfo);
        AssertUtil.notNull(userAddPageDate.getId(), "根据授权Id的查询用户userAccreditRoleRelation，参数Id为空");
        Example example = new Example(UserAccreditRoleRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userAccreditId", userAddPageDate.getId());
        List<UserAccreditRoleRelation> userAccreditRoleRelationList = userAccreditInfoRoleRelationService.selectByExample(example);
        AssertUtil.notNull(userAccreditRoleRelationList, "根据userAccreditId未查询到用户角色关系");
        List<Long> userAccreditroleIds = new ArrayList<>();

        for (UserAccreditRoleRelation userAccreditRoleRelation : userAccreditRoleRelationList) {
            //根据RoleId() 查询用到的角色
            userAccreditroleIds.add(userAccreditRoleRelation.getRoleId());
        }
        JSONArray roleIdArray = (JSONArray) JSONArray.toJSON(userAccreditroleIds);
        userAddPageDate.setRoleNames(roleIdArray.toString());
        return userAddPageDate;
    }

    /**
     * 修改授权
     * @param userAddPageDate
     * @throws Exception
     */
    @Override
    public void updateUserAccredit(UserAddPageDate userAddPageDate) throws Exception {
        //写入user_accredit_info表
        AssertUtil.notNull(userAddPageDate,"页面请求参数为空");
        UserAccreditInfo userAccreditInfo = userAddPageDate;
        userAccreditInfo.setIsDeleted("0");
        userAccreditInfo.setCreateOperator("test");
        userAccreditInfoService.updateByPrimaryKey(userAccreditInfo);

        //写入user_accredit_role_relation表
        if (StringUtils.isNotBlank(userAddPageDate.getRoleNames())) {
            userAccreditInfoRoleRelationService.deleteByUserAccreditId(userAccreditInfo.getId());
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
