package org.trc.biz.impl.impower;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.tairanchina.md.account.user.model.UserDO;
import com.tairanchina.md.api.QueryType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.impower.IUserAccreditInfoBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Channel;
import org.trc.domain.impower.Role;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.domain.impower.UserAccreditRoleRelation;
import org.trc.domain.impower.UserAddPageDate;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.domain.purchase.PurchaseGroupUserRelation;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ValidEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.ConfigException;
import org.trc.form.impower.UserAccreditInfoForm;
import org.trc.service.System.IChannelService;
import org.trc.service.impl.UserDoService;
import org.trc.service.impower.IRoleService;
import org.trc.service.impower.IUserAccreditInfoRoleRelationService;
import org.trc.service.impower.IUserAccreditInfoService;
import org.trc.service.purchase.IPurchaseGroupService;
import org.trc.service.purchase.IPurchaseGroupuUserRelationService;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import org.trc.util.StringUtil;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import java.util.*;

import static org.trc.util.StringUtil.splitByComma;

/**
 * Created by sone on 2017/5/11.
 */
@Service("userAccreditInfoBiz")
public class UserAccreditInfoBiz<T> implements IUserAccreditInfoBiz {

    private Logger LOGGER = LoggerFactory.getLogger(UserAccreditInfoBiz.class);
    private final static String ROLE_PURCHASE = "采购组员";

    @Resource
    private IUserAccreditInfoService userAccreditInfoService;

    @Resource
    private IRoleService roleService;

    @Autowired
    private IChannelService channelService;

    @Autowired
    private IUserAccreditInfoRoleRelationService userAccreditInfoRoleRelationService;

    @Resource
    private UserDoService userDoService;

    @Resource
    private IPurchaseGroupuUserRelationService purchaseGroupuUserRelationService;

    @Autowired
    private IPurchaseGroupService purchaseGroupService;


    /**
     * 分页查询
     *
     * @param form 授权信息查询条件
     * @param page 分页信息
     * @return
     * @throws Exception
     */
    @Override
    public Pagenation<UserAddPageDate> userAccreditInfoPage(UserAccreditInfoForm form, Pagenation<UserAddPageDate> page) throws Exception {
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
    public List<UserAccreditInfo> findPurchase() throws Exception {
        return userAccreditInfoService.findPurchase();
    }

    @Override
    public List<UserAddPageDate> handleRolesStr(List<UserAccreditInfo> list) throws Exception {
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
                StringBuilder rolesStr = new StringBuilder();
                for (UserAddPageDate selectUserAddPageDate : userAddPageDateList) {
                    if (userAccreditInfo.getId().equals(selectUserAddPageDate.getId())) {
                        if (rolesStr == null || rolesStr.length() == 0) {
                            rolesStr.append(selectUserAddPageDate.getRoleNames());
                        } else {
                            rolesStr.append(SupplyConstants.Symbol.COMMA + selectUserAddPageDate.getRoleNames());
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

        AssertUtil.notNull(userAccreditInfo, "授权管理模块修改授权信息失败，授权信息为空");
        UserAccreditInfo updateUserAccreditInfo = new UserAccreditInfo();
        updateUserAccreditInfo.setId(userAccreditInfo.getId());
        if (userAccreditInfo.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updateUserAccreditInfo.setIsValid(ValidEnum.NOVALID.getCode());
        } else {
            updateUserAccreditInfo.setIsValid(ValidEnum.VALID.getCode());
        }
        updateUserAccreditInfo.setUpdateTime(Calendar.getInstance().getTime());
        int count = userAccreditInfoService.updateByPrimaryKeySelective(updateUserAccreditInfo);
        if (count == 0) {
            String msg = String.format("修改授权%s数据库操作失败", JSON.toJSONString(userAccreditInfo));
            LOGGER.error(msg);
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
        }

    }

    /**
     * 用户名是否存在
     *
     * @param name 用户姓名
     * @return
     * @throws Exception
     */
    @Override
    public int checkUserByName(Long id, String name) throws Exception {
        AssertUtil.notNull(id, "根据用户授权的用户名称查询角色的参数id为空");
        AssertUtil.notBlank(name, "根据用户授权的用户名称查询角色的参数name为空");
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
        criteria.andEqualTo("isValid", ZeroToNineEnum.ONE.getCode());
        example.orderBy("updateTime").desc();
        List<Channel> channelList = channelService.selectByExample(example);
        AssertUtil.notNull(channelList, "未查询到已经启用的渠道");
        return channelList;
    }


    /**
     * 查询 channelJurisdiction渠道角色
     * wholeJurisdiction全局角色
     * roleType为空时查询所有角色
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
        criteria.andEqualTo("isValid", ValidEnum.VALID.getCode());
        example.orderBy("updateTime").desc();
        return roleService.selectByExample(example);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveUserAccreditInfo(UserAddPageDate userAddPageDate, ContainerRequestContext requestContext) throws Exception {
        checkUserAddPageDate(userAddPageDate);
        //获取页面的UserId
        String userId = (String) requestContext.getProperty("userId");
        AssertUtil.notBlank(userId, "获取当前登录用户失败,请重新登录!");
        UserDO userDO = userDoService.getUserDo(userAddPageDate.getPhone());
        AssertUtil.notNull(userDO, "该手机号未在泰然城注册");
        //写入user_accredit_info表
        UserAccreditInfo userAccreditInfo = new UserAccreditInfo();
        userAccreditInfo.setName(userAddPageDate.getName());
        userAccreditInfo.setChannelCode(userAddPageDate.getChannelCode());
        userAccreditInfo.setPhone(userDO.getPhone());
        userAccreditInfo.setRemark(userAddPageDate.getRemark());
        userAccreditInfo.setUserType(userAddPageDate.getUserType());
        userAccreditInfo.setUserId(userDO.getUserId());
        userAccreditInfo.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        userAccreditInfo.setCreateOperator(userId);
        userAccreditInfo.setIsValid(userAddPageDate.getIsValid());
        userAccreditInfo.setCreateTime(Calendar.getInstance().getTime());
        userAccreditInfo.setUpdateTime(Calendar.getInstance().getTime());
        userAccreditInfoService.insert(userAccreditInfo);

        //写入user_accredit_role_relation表
        if (StringUtils.isNotBlank(userAddPageDate.getRoleNames())) {
            Long roleIds[] = splitByComma(userAddPageDate.getRoleNames());
            List<UserAccreditRoleRelation> uAcRoleRelationList = new ArrayList<>();
            for (int i = 0; i < roleIds.length; i++) {
                UserAccreditRoleRelation userAccreditRoleRelation = new UserAccreditRoleRelation();
                userAccreditRoleRelation.setUserAccreditId(userAccreditInfo.getId());
                userAccreditRoleRelation.setUserId(userAccreditInfo.getUserId());
                userAccreditRoleRelation.setRoleId(roleIds[i]);
                userAccreditRoleRelation.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                userAccreditRoleRelation.setIsValid(userAccreditInfo.getIsValid());
                userAccreditInfo.setCreateOperator(userId);
                userAccreditRoleRelation.setCreateTime(userAccreditInfo.getCreateTime());
                userAccreditRoleRelation.setUpdateTime(userAccreditInfo.getUpdateTime());
                uAcRoleRelationList.add(userAccreditRoleRelation);
            }
            userAccreditInfoRoleRelationService.insertList(uAcRoleRelationList);
        }
    }

    private void checkUserAddPageDate(UserAddPageDate userAddPageDate) throws Exception {
        AssertUtil.notBlank(userAddPageDate.getPhone(), "用户手机号未输入");
        AssertUtil.notBlank(userAddPageDate.getName(), "用户姓名未输入");
        AssertUtil.notBlank(userAddPageDate.getUserType(), "用户类型未选择");
        AssertUtil.notBlank(userAddPageDate.getRoleNames(), "关联角色未选择");
        AssertUtil.notBlank(userAddPageDate.getIsValid(), "参数isValid不能为空");
    }

    /**
     * 根据ID查询
     *
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
            String msg = String.format("根据主键ID[id=%s]查询角色为空", id.toString());
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, msg);
        }
        userAddPageDate = new UserAddPageDate(userAccreditInfo);
        AssertUtil.notNull(userAddPageDate.getId(), "根据授权Id的查询用户userAccreditRoleRelation，参数Id为空");
        Example example = new Example(UserAccreditRoleRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userAccreditId", userAddPageDate.getId());
        example.orderBy("id").asc();
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
     *
     * @param userAddPageDate
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateUserAccredit(UserAddPageDate userAddPageDate) throws Exception {
        //非空校验
        AssertUtil.notBlank(userAddPageDate.getName(), "用户姓名未输入");
        AssertUtil.notBlank(userAddPageDate.getUserType(), "用户类型未选择");
        AssertUtil.notBlank(userAddPageDate.getRoleNames(), "关联角色未选择");
        AssertUtil.notNull(userAddPageDate.getId(), "更新用户时,参数Id为空");
        //采购组校验
        Long roles[] = StringUtil.splitByComma(userAddPageDate.getRoleNames());
        //采购组员角色的ID
        Example example = new Example(Role.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", ROLE_PURCHASE);
        List<Role> roleList = roleService.selectByExample(example);
        //如果在采购组中,就判断页面传进来的采购组角色有没有被选择
        String purchaseNames[] = purchaseRole(userAddPageDate.getId());
        if (purchaseNames != null) {
            if (purchaseNames.length > 0) {
                //该角色在采购组中
                boolean flag = false;
                for (Long role : roles) {
                    for (Role r : roleList) {
                        if (role == r.getId()) {
                            flag = true;//页面上已经勾选
                        }
                    }
                }
                if (!flag) {
                    String msg = String.format("%s用户在采购组中,不能取消采购组角色", JSON.toJSONString(userAddPageDate.getName()));
                    LOGGER.error(msg);
                    throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
                }
            }
        }

        //写入user_accredit_info表
        UserDO userDO = userDoService.getUserDo(userAddPageDate.getPhone());
        AssertUtil.notNull(userDO, "用户中心未查询到该用户");
        UserAccreditInfo userAccreditInfo = userAddPageDate;
        userAccreditInfo.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        userAccreditInfo.setUserId(userDO.getUserId());
        userAccreditInfoService.updateByPrimaryKeySelective(userAccreditInfo);

        //写入user_accredit_role_relation表
        if (StringUtils.isNotBlank(userAddPageDate.getRoleNames())) {
            int count = userAccreditInfoRoleRelationService.deleteByUserAccreditId(userAccreditInfo.getId());
            if (count == 0) {
                String msg = String.format("修改授权%s操作失败", JSON.toJSONString(userAddPageDate.getRoleNames()));
                LOGGER.error(msg);
                throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
            }
            Long roleIds[] = splitByComma(userAddPageDate.getRoleNames());
            List<UserAccreditRoleRelation> uAcRoleRelationList = new ArrayList<>();
            for (int i = 0; i < roleIds.length; i++) {
                UserAccreditRoleRelation userAccreditRoleRelation = new UserAccreditRoleRelation();
                userAccreditRoleRelation.setUserAccreditId(userAccreditInfo.getId());
                userAccreditRoleRelation.setUserId(userAccreditInfo.getUserId());
                userAccreditRoleRelation.setRoleId(roleIds[i]);
                userAccreditRoleRelation.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
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
     * 校验手机号
     *
     * @param phone
     * @return
     * @throws Exception
     */
    @Override
    public String checkPhone(String phone) throws Exception {
        AssertUtil.notBlank(phone, "校验手机号时输入参数phone为空");
        UserDO userDO = userDoService.getUserDo(phone);
        Example example = new Example(UserAccreditInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("phone", phone);
        int count = userAccreditInfoService.selectByExample(example).size();

        if (count > 0) {
            return "此手机号已关联用户";
        }
        if (userDO == null) {
            return "此手机号尚未在泰然城注册";
        }
        return null;
    }

    /**
     * 采购组员校验
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public String[] purchaseRole(Long id) throws Exception {

        AssertUtil.notNull(id, "采购组查询输入参数Id为空");
        //查询到用户
        UserAccreditInfo userAccreditInfo = new UserAccreditInfo();
        userAccreditInfo.setId(id);
        userAccreditInfo = userAccreditInfoService.selectOne(userAccreditInfo);
        AssertUtil.notNull(userAccreditInfo, "根据Id未查询到对应的用户");
        //根据UserID查询采购组
        Example example = new Example(PurchaseGroupUserRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userAccreditInfo.getUserId());
        criteria.andEqualTo("isValid", ZeroToNineEnum.ONE.getCode());
        List<PurchaseGroupUserRelation> purchaseGroupUserRelationList = purchaseGroupuUserRelationService.selectByExample(example);
        String[] purchaseGroupArray = new String[purchaseGroupUserRelationList.size()];
        //查到采购组,返回采购组名称
        if (purchaseGroupUserRelationList.size() > 0) {
            for (int i = 0; i < purchaseGroupUserRelationList.size(); i++) {
                PurchaseGroup purchaseGroup = new PurchaseGroup();
                purchaseGroup.setCode(purchaseGroupUserRelationList.get(i).getPurchaseGroupCode());
                purchaseGroup = purchaseGroupService.selectOne(purchaseGroup);
                AssertUtil.notNull(userAccreditInfo, "根据purchaseGroup未查询到对应的用户");
                purchaseGroupArray[i] = purchaseGroup.getName();
            }
            //返回所在采购组的名称,数组形式
            return purchaseGroupArray;
        }
        return null;
    }

/*
    @Override
    public int checkName(String name) throws Exception {
        Example example = new Example(UserAccreditInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", name);
        return userAccreditInfoService.selectByExample(example).size();
    }
*/

}
