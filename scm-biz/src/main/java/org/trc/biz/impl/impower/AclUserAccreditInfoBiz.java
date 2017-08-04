package org.trc.biz.impl.impower;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.tairanchina.md.account.user.model.UserDO;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.search.MatchQuery;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.impower.IAclUserAccreditInfoBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Channel;
import org.trc.domain.impower.AclRole;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.impower.AclUserAccreditRoleRelation;
import org.trc.domain.impower.AclUserAddPageDate;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.domain.purchase.PurchaseGroupUserRelation;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ValidEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.UserAccreditInfoException;
import org.trc.form.impower.UserAccreditInfoForm;
import org.trc.model.SearchResult;
import org.trc.service.IPageNationService;
import org.trc.service.System.IChannelService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.impl.UserDoService;
import org.trc.service.impower.IAclRoleService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.impower.IAclUserAccreditRoleRelationService;
import org.trc.service.purchase.IPurchaseGroupService;
import org.trc.service.purchase.IPurchaseGroupuUserRelationService;
import org.trc.service.util.IUserNameUtilService;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import org.trc.util.StringUtil;
import org.trc.util.TransportClientUtil;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;

import static org.trc.util.StringUtil.splitByComma;

/**
 * Created by sone on 2017/5/11.
 */
@Service("userAccreditInfoBiz")
public class AclUserAccreditInfoBiz implements IAclUserAccreditInfoBiz {

    private Logger LOGGER = LoggerFactory.getLogger(AclUserAccreditInfoBiz.class);
    private final static String ROLE_PURCHASE = "采购组员";
    /**
     * 正则表达式：验证手机号
     */
    private static final String REGEX_MOBILE = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-9])|(147))\\\\d{8}$";
    @Resource
    private IAclUserAccreditInfoService userAccreditInfoService;

    @Resource
    private IAclRoleService roleService;

    @Autowired
    private IChannelService channelService;

    @Autowired
    private IAclUserAccreditRoleRelationService userAccreditInfoRoleRelationService;

    @Resource
    private UserDoService userDoService;

    @Resource
    private IPurchaseGroupuUserRelationService purchaseGroupuUserRelationService;

    @Autowired
    private IPurchaseGroupService purchaseGroupService;
    @Autowired
    private IUserNameUtilService userNameUtilService;
    @Autowired
    private ILogInfoService logInfoService;
    @Autowired
    private IPageNationService pageNationService;


    /**
     * 分页查询
     *
     * @param form 授权信息查询条件
     * @param page 分页信息
     * @return
     * @throws Exception
     */
    @Override
    public Pagenation<AclUserAddPageDate> userAccreditInfoPage(UserAccreditInfoForm form, Pagenation<AclUserAddPageDate> page) {
        PageHelper.startPage(page.getPageNo(), page.getPageSize());
        Map<String, Object> map = new HashMap<>();
        map.put("name", form.getName());
        map.put("phone", form.getPhone());
        map.put("isValid", form.getIsValid());
        List<AclUserAccreditInfo> pageDateList = userAccreditInfoService.selectAccreditInfoList(map);
        List<AclUserAddPageDate> pageDateRoleList = page.getResult();
        userNameUtilService.handleUserName(pageDateList);
        if (pageDateList != null && !pageDateList.isEmpty() && pageDateList.size() > 0) {//1.按要求查处需要的授权用户
            pageDateRoleList = handleRolesStr(pageDateList);
        }
        int count = userAccreditInfoService.selectCountUser(map);
        page.setTotalCount(count);
        page.setResult(pageDateRoleList);
        return page;
    }

    @Override
    public Pagenation<AclUserAddPageDate> userAccreditInfoPageES(UserAccreditInfoForm form, Pagenation<AclUserAddPageDate> page) {
        TransportClient clientUtil = TransportClientUtil.getTransportClient();
        HighlightBuilder hiBuilder = new HighlightBuilder();
        hiBuilder.preTags("<b style=\"color: red\">");
        hiBuilder.postTags("</b>");
        //设置高亮字段
        hiBuilder.fields().add(new HighlightBuilder.Field("name.pinyin"));
        SearchRequestBuilder srb = clientUtil.prepareSearch("acl_user_accredit_info")//es表名
                .highlighter(hiBuilder)
                .setFrom(page.getStart())//第几个开始
                .setSize(page.getPageSize());//长度
        //查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(form.getName())) {
            QueryBuilder matchQuery = QueryBuilders.matchQuery("name.pinyin", form.getName());
            queryBuilder.should(matchQuery);
        }
        if (StringUtils.isNotBlank(form.getPhone())) {
            QueryBuilder filterBuilder = QueryBuilders.multiMatchQuery(form.getPhone(),"phone" ).
                    type(MatchQuery.Type.PHRASE_PREFIX);
            queryBuilder.should(filterBuilder);
        }
        if (StringUtils.isNotBlank(form.getIsValid())) {
            QueryBuilder filterBuilder = QueryBuilders.termQuery("is_valid", form.getIsValid());
            queryBuilder.must(filterBuilder);
        }
        srb.setQuery(queryBuilder);
        SearchResult searchResult;
        try {
            searchResult = pageNationService.resultES(srb, clientUtil);
        } catch (Exception e) {
            LOGGER.error("es查询失败" + e.getMessage(), e);
            return page;
        }
        List<AclUserAccreditInfo> accreditInfoList = new ArrayList<>();
        for (SearchHit searchHit : searchResult.getSearchHits()) {
            AclUserAccreditInfo aclUserAccreditInfo = JSON.parseObject(JSON.toJSONString(searchHit.getSource()), AclUserAccreditInfo.class);
            for(Map.Entry<String, HighlightField> entry : searchHit.getHighlightFields().entrySet()) {
                if("name.pinyin".equals(entry.getKey())) {
                    for (Text text : entry.getValue().getFragments()) {
                        aclUserAccreditInfo.setHighLightName(text.string());
                    }
                }
            }
            accreditInfoList.add(aclUserAccreditInfo);
        }
        if (AssertUtil.collectionIsEmpty(accreditInfoList)) {
            return page;
        }
        List<AclUserAddPageDate> pageDateRoleList = page.getResult();
        userNameUtilService.handleUserName(accreditInfoList);
        if (!AssertUtil.collectionIsEmpty(accreditInfoList)) {//1.按要求查处需要的授权用户
            pageDateRoleList = handleRolesStr(accreditInfoList);
        }
        page.setTotalCount(searchResult.getCount());
        page.setResult(pageDateRoleList);
        return page;
    }

    @Override
    public List<AclUserAccreditInfo> findPurchase() {
        return userAccreditInfoService.findPurchase();
    }

    @Override
    public List<AclUserAddPageDate> handleRolesStr(List<AclUserAccreditInfo> list) {
        List<AclUserAddPageDate> pageDateRoleList = new ArrayList<>();
        Long[] userIds = new Long[list.size()];
        int temp = 0;
        for (AclUserAccreditInfo aclUserAccreditInfo : list) {
            userIds[temp] = aclUserAccreditInfo.getId();
            temp += 1;
        }
        //2.查询出这些用户对应的对应的角色名称集合
        List<AclUserAddPageDate> userAddPageDateList = userAccreditInfoService.selectUserAddPageList(userIds);
        if (userAddPageDateList != null && !userAddPageDateList.isEmpty() && userAddPageDateList.size() > 0) {
            for (AclUserAccreditInfo aclUserAccreditInfo : list) {
                AclUserAddPageDate userAddPageDate = new AclUserAddPageDate(aclUserAccreditInfo);
                StringBuilder rolesStr = new StringBuilder();
                for (AclUserAddPageDate selectUserAddPageDate : userAddPageDateList) {
                    if (aclUserAccreditInfo.getId().equals(selectUserAddPageDate.getId())) {
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
    public void updateUserAccreditInfoStatus(AclUserAccreditInfo aclUserAccreditInfo, AclUserAccreditInfo aclUserAccreditInfoContext) {

        AssertUtil.notNull(aclUserAccreditInfo, "授权管理模块修改授权信息失败，授权信息为空");
        AclUserAccreditInfo updateAclUserAccreditInfo = new AclUserAccreditInfo();
        updateAclUserAccreditInfo.setId(aclUserAccreditInfo.getId());
        String state;
        if (aclUserAccreditInfo.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updateAclUserAccreditInfo.setIsValid(ValidEnum.NOVALID.getCode());
            state = "停用";
        } else {
            updateAclUserAccreditInfo.setIsValid(ValidEnum.VALID.getCode());
            state = "启用";
        }
        updateAclUserAccreditInfo.setUpdateTime(Calendar.getInstance().getTime());
        int count = userAccreditInfoService.updateByPrimaryKeySelective(updateAclUserAccreditInfo);
        if (count == 0) {
            String msg = String.format("修改授权%s数据库操作失败", JSON.toJSONString(aclUserAccreditInfo));
            LOGGER.error(msg);
            throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
        }
        String userId = aclUserAccreditInfoContext.getUserId();
        AssertUtil.notBlank(userId, "获取当前登录的userId失败");
        logInfoService.recordLog(aclUserAccreditInfo, String.valueOf(aclUserAccreditInfo.getId()), userId, "修改", "状态改为" + state, null);

    }

    /**
     * 用户名是否存在
     *
     * @param name 用户姓名
     * @return
     * @throws Exception
     */
    @Override
    @Deprecated
    public int checkUserByName(Long id, String name) {
        AssertUtil.notNull(id, "根据用户授权的用户名称查询角色的参数id为空");
        AssertUtil.notBlank(name, "根据用户授权的用户名称查询角色的参数name为空");
        Example example = new Example(AclUserAccreditInfo.class);
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
    public List<Channel> findChannel() {
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
    public List<AclRole> findChannelOrWholeJur(String roleType) {
        Example example = new Example(AclRole.class);
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
    public void saveUserAccreditInfo(AclUserAddPageDate userAddPageDate,  AclUserAccreditInfo aclUserAccreditInfoContext) {
        checkUserAddPageDate(userAddPageDate);
        if (Pattern.matches(REGEX_MOBILE, userAddPageDate.getPhone())) {
            String msg = "手机号格式错误," + userAddPageDate.getPhone();
            LOGGER.error(msg);
            throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACCREDIT_SAVE_EXCEPTION, msg);
        }
        UserDO userDO = userDoService.getUserDo(userAddPageDate.getPhone());
        AssertUtil.notNull(userDO, "该手机号未在泰然城注册");
        //手机号关联校验
        String phoneMsg = checkPhone(userDO.getPhone());
        if (StringUtils.isNoneBlank(phoneMsg)) {
            String msg = "新增授权用户失败," + phoneMsg;
            LOGGER.error(msg);
            throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACCREDIT_SAVE_EXCEPTION, msg);
        }

        //写入user_accredit_info表
        AclUserAccreditInfo aclUserAccreditInfo = new AclUserAccreditInfo();
        aclUserAccreditInfo.setName(userAddPageDate.getName());
        aclUserAccreditInfo.setChannelCode(userAddPageDate.getChannelCode());
        aclUserAccreditInfo.setPhone(userDO.getPhone());
        aclUserAccreditInfo.setRemark(userAddPageDate.getRemark());
        aclUserAccreditInfo.setUserType(userAddPageDate.getUserType());
        aclUserAccreditInfo.setUserId(userDO.getUserId());
        aclUserAccreditInfo.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        aclUserAccreditInfo.setCreateOperator(aclUserAccreditInfoContext.getUserId());
        aclUserAccreditInfo.setIsValid(userAddPageDate.getIsValid());
        aclUserAccreditInfo.setCreateTime(Calendar.getInstance().getTime());
        aclUserAccreditInfo.setUpdateTime(Calendar.getInstance().getTime());
        userAccreditInfoService.insert(aclUserAccreditInfo);

        //写入user_accredit_role_relation表
        if (StringUtils.isNotBlank(userAddPageDate.getRoleNames())) {
            Long roleIds[] = splitByComma(userAddPageDate.getRoleNames());
            List<AclUserAccreditRoleRelation> uAcRoleRelationList = new ArrayList<>();
            for (int i = 0; i < roleIds.length; i++) {
                AclUserAccreditRoleRelation aclUserAccreditRoleRelation = new AclUserAccreditRoleRelation();
                aclUserAccreditRoleRelation.setUserAccreditId(aclUserAccreditInfo.getId());
                aclUserAccreditRoleRelation.setUserId(aclUserAccreditInfo.getUserId());
                aclUserAccreditRoleRelation.setRoleId(roleIds[i]);
                //aclUserAccreditRoleRelation.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                aclUserAccreditRoleRelation.setIsValid(aclUserAccreditInfo.getIsValid());
                aclUserAccreditRoleRelation.setCreateOperator(aclUserAccreditInfo.getCreateOperator());
                aclUserAccreditRoleRelation.setCreateTime(aclUserAccreditInfo.getCreateTime());
                aclUserAccreditRoleRelation.setUpdateTime(aclUserAccreditInfo.getUpdateTime());
                uAcRoleRelationList.add(aclUserAccreditRoleRelation);
            }
            //检验被选中个的角色的起停用状态
            List<AclRole> selectAclRoleList = roleService.findRoleList(Arrays.asList(roleIds));
            for (AclRole selectAclRole : selectAclRoleList) {
                if (StringUtils.equals(selectAclRole.getIsValid(), ValidEnum.NOVALID.getCode())) {
                    String msg = String.format("%s角色已被停用", JSON.toJSONString(selectAclRole.getName()));
                    LOGGER.error(msg);
                    throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACCREDIT_SAVE_EXCEPTION, msg);
                }
            }
            userAccreditInfoRoleRelationService.insertList(uAcRoleRelationList);
        } else {
            String msg = "未选择关联角色";
            LOGGER.error(msg);
            throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
        }
        AclUserAccreditInfo logAclUserAccreditInfo;
        logAclUserAccreditInfo = userAccreditInfoService.selectByPrimaryKey(aclUserAccreditInfo.getId());
        logInfoService.recordLog(logAclUserAccreditInfo, String.valueOf(logAclUserAccreditInfo.getId()), aclUserAccreditInfo.getCreateOperator(), "新增", "", null);


    }

    private void checkUserAddPageDate(AclUserAddPageDate userAddPageDate) {
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
    public AclUserAddPageDate findUserAccreditInfoById(Long id) {
        AssertUtil.notNull(id, "根据授权Id的查询用户userAccreditInfo，参数Id为空");
        AclUserAddPageDate userAddPageDate;
        AclUserAccreditInfo aclUserAccreditInfo = new AclUserAccreditInfo();
        aclUserAccreditInfo.setId(id);
        aclUserAccreditInfo = userAccreditInfoService.selectOne(aclUserAccreditInfo);
        if (null == aclUserAccreditInfo) {
            String msg = String.format("根据主键ID[id=%s]查询角色为空", id.toString());
            throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, msg);
        }
        userAddPageDate = new AclUserAddPageDate(aclUserAccreditInfo);
        AssertUtil.notNull(userAddPageDate.getId(), "根据授权Id的查询用户userAccreditRoleRelation，参数Id为空");
        Example example = new Example(AclUserAccreditRoleRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userAccreditId", userAddPageDate.getId());
        example.orderBy("id").asc();
        List<AclUserAccreditRoleRelation> aclUserAccreditRoleRelationList = userAccreditInfoRoleRelationService.selectByExample(example);
        AssertUtil.notNull(aclUserAccreditRoleRelationList, "根据userAccreditId未查询到用户角色关系");
        List<Long> userAccreditroleIds = new ArrayList<>();

        for (AclUserAccreditRoleRelation aclUserAccreditRoleRelation : aclUserAccreditRoleRelationList) {
            //根据RoleId() 查询用到的角色
            userAccreditroleIds.add(aclUserAccreditRoleRelation.getRoleId());
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
    public void updateUserAccredit(AclUserAddPageDate userAddPageDate, AclUserAccreditInfo aclUserAccreditInfoContext) {
        //非空校验
        AssertUtil.notBlank(userAddPageDate.getName(), "用户姓名未输入");
        AssertUtil.notBlank(userAddPageDate.getUserType(), "用户类型未选择");
        AssertUtil.notBlank(userAddPageDate.getRoleNames(), "关联角色未选择");
        AssertUtil.notNull(userAddPageDate.getId(), "更新用户时,参数Id为空");
        //采购组校验
        Long roles[] = StringUtil.splitByComma(userAddPageDate.getRoleNames());
        //采购组员角色的ID
        Example example = new Example(AclRole.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", ROLE_PURCHASE);
        List<AclRole> aclRoleList = roleService.selectByExample(example);
        //如果在采购组中,就判断页面传进来的采购组角色有没有被选择
        String purchaseNames[] = purchaseRole(userAddPageDate.getId());
        if (purchaseNames != null) {
            if (purchaseNames.length > 0) {
                //该角色在采购组中
                boolean flag = false;
                for (Long role : roles) {
                    for (AclRole r : aclRoleList) {
                        if (role == r.getId()) {
                            flag = true;//页面上已经勾选
                        }
                    }
                }
                if (!flag) {
                    String msg = String.format("%s用户在采购组中,不能取消采购组角色", JSON.toJSONString(userAddPageDate.getName()));
                    LOGGER.error(msg);
                    throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
                }
            }
        }
        //写入user_accredit_info表
        UserDO userDO = userDoService.getUserDo(userAddPageDate.getPhone());
        AssertUtil.notNull(userDO, "用户中心未查询到该用户");
        AclUserAccreditInfo aclUserAccreditInfo = userAddPageDate;
        aclUserAccreditInfo.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        String userId = aclUserAccreditInfoContext.getUserId();
        if (!StringUtils.isBlank(userId)) {
            aclUserAccreditInfo.setCreateOperator(userId);
        }
        aclUserAccreditInfo.setUserId(userDO.getUserId());
        aclUserAccreditInfo.setUpdateTime(Calendar.getInstance().getTime());
        userAccreditInfoService.updateByPrimaryKeySelective(aclUserAccreditInfo);
        //写入user_accredit_role_relation表
        if (StringUtils.isNotBlank(userAddPageDate.getRoleNames())) {
            int count = userAccreditInfoRoleRelationService.deleteByUserAccreditId(aclUserAccreditInfo.getId());
            if (count == 0) {
                String msg = String.format("修改授权%s操作失败", JSON.toJSONString(userAddPageDate.getRoleNames()));
                LOGGER.error(msg);
                throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
            }
            Long roleIds[] = splitByComma(userAddPageDate.getRoleNames());
            //检验被选中个的角色的起停用状态
            List<AclRole> selectAclRoleList = roleService.findRoleList(Arrays.asList(roleIds));
            for (AclRole selectAclRole : selectAclRoleList) {
                if (StringUtils.equals(selectAclRole.getIsValid(), ValidEnum.NOVALID.getCode())) {
                    String msg = String.format("%s角色已被停用", JSON.toJSONString(selectAclRole.getName()));
                    LOGGER.error(msg);
                    throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACCREDIT_SAVE_EXCEPTION, msg);
                }
            }
            List<AclUserAccreditRoleRelation> uAcRoleRelationList = new ArrayList<>();
            for (int i = 0; i < roleIds.length; i++) {
                AclUserAccreditRoleRelation aclUserAccreditRoleRelation = new AclUserAccreditRoleRelation();
                aclUserAccreditRoleRelation.setUserAccreditId(aclUserAccreditInfo.getId());
                aclUserAccreditRoleRelation.setUserId(aclUserAccreditInfo.getUserId());
                aclUserAccreditRoleRelation.setRoleId(roleIds[i]);
                //aclUserAccreditRoleRelation.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                aclUserAccreditRoleRelation.setIsValid(aclUserAccreditInfo.getIsValid());
                aclUserAccreditRoleRelation.setCreateOperator(userId);
                aclUserAccreditRoleRelation.setCreateTime(aclUserAccreditInfo.getCreateTime());
                aclUserAccreditRoleRelation.setUpdateTime(Calendar.getInstance().getTime());
                uAcRoleRelationList.add(aclUserAccreditRoleRelation);
            }
            userAccreditInfoRoleRelationService.insertList(uAcRoleRelationList);
        }
        AclUserAccreditInfo logAclUserAccreditInfo;
        logAclUserAccreditInfo = userAccreditInfoService.selectByPrimaryKey(userAddPageDate.getId());
        logInfoService.recordLog(logAclUserAccreditInfo, String.valueOf(logAclUserAccreditInfo.getId()), userId, "修改", "", null);


    }

    /**
     * 校验手机号
     *
     * @param phone
     * @return
     * @throws Exception
     */
    @Override
    public String checkPhone(String phone) {
        AssertUtil.notBlank(phone, "校验手机号时输入参数phone为空");
        UserDO userDO = userDoService.getUserDo(phone);
        Example example = new Example(AclUserAccreditInfo.class);
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

    @Override
    public String getNameByPhone(String phone) {
        UserDO userDO = userDoService.getUserDo(phone);
 /*       Example example = new Example(AclUserAccreditInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("phone", phone);*/
        AclUserAccreditInfo userAccreditInfo = new AclUserAccreditInfo();
        userAccreditInfo.setPhone(phone);
        userAccreditInfo = userAccreditInfoService.selectOne(userAccreditInfo);
        if (userDO == null) {
            return "此手机号尚未在泰然城注册";
        }
        return  userAccreditInfo.getName();
    }

    /**
     * 采购组员校验
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public String[] purchaseRole(Long id) {

        AssertUtil.notNull(id, "采购组查询输入参数Id为空");
        //查询到用户
        AclUserAccreditInfo aclUserAccreditInfo = new AclUserAccreditInfo();
        aclUserAccreditInfo.setId(id);
        aclUserAccreditInfo = userAccreditInfoService.selectOne(aclUserAccreditInfo);
        AssertUtil.notNull(aclUserAccreditInfo, "根据Id未查询到对应的用户");
        //根据UserID查询采购组
        Example example = new Example(PurchaseGroupUserRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", aclUserAccreditInfo.getUserId());
        criteria.andEqualTo("isValid", ZeroToNineEnum.ONE.getCode());
        List<PurchaseGroupUserRelation> purchaseGroupUserRelationList = purchaseGroupuUserRelationService.selectByExample(example);
        String[] purchaseGroupArray = new String[purchaseGroupUserRelationList.size()];
        //查到采购组,返回采购组名称
        if (purchaseGroupUserRelationList.size() > 0) {
            for (int i = 0; i < purchaseGroupUserRelationList.size(); i++) {
                PurchaseGroup purchaseGroup = new PurchaseGroup();
                purchaseGroup.setCode(purchaseGroupUserRelationList.get(i).getPurchaseGroupCode());
                purchaseGroup = purchaseGroupService.selectOne(purchaseGroup);
                AssertUtil.notNull(aclUserAccreditInfo, "根据purchaseGroup未查询到对应的用户");
                purchaseGroupArray[i] = purchaseGroup.getName();
            }
            //返回所在采购组的名称,数组形式
            return purchaseGroupArray;
        }
        return null;
    }

    @Override
    public String[] checkRoleValid(Long id) {
        AssertUtil.notNull(id, "获取用户授权ID为空");
        Example example = new Example(AclUserAccreditRoleRelation.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userAccreditId", id);
        criteria.andEqualTo("isValid", ValidEnum.NOVALID.getCode());
        List<AclUserAccreditRoleRelation> aclUserAccreditRoleRelationList = userAccreditInfoRoleRelationService.selectByExample(example);
        String noValidNames[] = new String[aclUserAccreditRoleRelationList.size()];
        if (aclUserAccreditRoleRelationList != null && aclUserAccreditRoleRelationList.size() > 0) {
            for (int i = 0; i < aclUserAccreditRoleRelationList.size(); i++) {
                Long roleId = aclUserAccreditRoleRelationList.get(i).getRoleId();
                noValidNames[i] = roleService.selectByPrimaryKey(roleId).getName();
            }
            return noValidNames;
        }
        return null;
    }

/*
    @Override
    public int checkName(String name) throws Exception {
        Example example = new Example(AclUserAccreditInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", name);
        return userAccreditInfoService.selectByExample(example).size();
    }
*/

}
