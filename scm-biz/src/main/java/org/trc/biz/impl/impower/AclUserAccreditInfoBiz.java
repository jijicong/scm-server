package org.trc.biz.impl.impower;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.tairanchina.md.account.user.model.UserDO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.impower.IAclUserAccreditInfoBiz;
import org.trc.cache.CacheEvit;
import org.trc.cache.Cacheable;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Channel;
import org.trc.domain.System.ChannelExt;
import org.trc.domain.System.ChannelSellChannel;
import org.trc.domain.System.SellChannel;
import org.trc.domain.impower.*;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.domain.purchase.PurchaseGroupUserRelation;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.UserTypeEnum;
import org.trc.enums.ValidEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.UserAccreditInfoException;
import org.trc.form.impower.ChannelSelectMsg;
import org.trc.form.impower.SellChannelSelectMsg;
import org.trc.form.impower.UserAccreditInfoForm;
import org.trc.service.IPageNationService;
import org.trc.service.System.IChannelSellChannelService;
import org.trc.service.System.IChannelService;
import org.trc.service.System.ISellChannelService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.impl.UserDoService;
import org.trc.service.impower.IAclRoleService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.impower.IAclUserAccreditRoleRelationService;
import org.trc.service.impower.IAclUserChannelSellService;
import org.trc.service.purchase.IPurchaseGroupService;
import org.trc.service.purchase.IPurchaseGroupuUserRelationService;
import org.trc.service.util.IUserNameUtilService;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import org.trc.util.StringUtil;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.regex.Pattern;

import static org.trc.util.StringUtil.splitByComma;

/**
 *
 * @author sone
 * @date 2017/5/11
 */
@Service("userAccreditInfoBiz")
public class AclUserAccreditInfoBiz implements IAclUserAccreditInfoBiz {

    private Logger LOGGER = LoggerFactory.getLogger(AclUserAccreditInfoBiz.class);
    private final static String ROLE_PURCHASE = "采购组员";
    /**
     * 正则表达式：验证手机号
     */
    private static final String REGEX_MOBILE = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-9])|(147))\\\\d{8}$";
    @Autowired
    private IAclUserAccreditInfoService userAccreditInfoService;

    @Autowired
    private IAclRoleService roleService;

    @Autowired
    private IChannelService channelService;

    @Autowired
    private IAclUserAccreditRoleRelationService userAccreditInfoRoleRelationService;

    @Autowired
    private UserDoService userDoService;

    @Autowired
    private IPurchaseGroupuUserRelationService purchaseGroupuUserRelationService;

    @Autowired
    private IPurchaseGroupService purchaseGroupService;

    @Autowired
    private IUserNameUtilService userNameUtilService;

    @Autowired
    private ILogInfoService logInfoService;

    @Autowired
    private IPageNationService pageNationService;

    @Autowired
    private ISellChannelService sellChannelService;
    @Autowired
    private IChannelSellChannelService channelSellChannelService;
    @Autowired
    private IAclUserChannelSellService aclUserChannelSellService;

    @Value("${admin.user.id}")
    private String ADMIN_ID;


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
        if (pageDateList != null && !pageDateList.isEmpty() && pageDateList.size() > 0) {
            //1.按要求查询需要的授权用户
            pageDateRoleList = handleRolesStr(pageDateList);
        }
        int count = userAccreditInfoService.selectCountUser(map);
        page.setTotalCount(count);
        page.setResult(pageDateRoleList);
        return page;
    }



    @Override
    public List<AclUserAccreditInfo> findPurchase(AclUserAccreditInfo aclUserAccreditInfo) {
        String channelCode = aclUserAccreditInfo.getChannelCode();
        return userAccreditInfoService.findPurchase(channelCode);

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
    @CacheEvit
    public void updateUserAccreditInfoStatus(AclUserAccreditInfo aclUserAccreditInfo, AclUserAccreditInfo aclUserAccreditInfoContext) {
        AssertUtil.notNull(aclUserAccreditInfo, "授权管理模块修改授权信息失败，授权信息为空");
        AclUserAccreditInfo updateAclUserAccreditInfo = new AclUserAccreditInfo();
        updateAclUserAccreditInfo.setId(aclUserAccreditInfo.getId());
        String state;
        if (aclUserAccreditInfo.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updateAclUserAccreditInfo.setIsValid(ValidEnum.NOVALID.getCode());
            state = ValidEnum.NOVALID.getName();
        } else {
            updateAclUserAccreditInfo.setIsValid(ValidEnum.VALID.getCode());
            state = ValidEnum.VALID.getName();
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
     * 查询所有已启用的渠道(业务线)和业务线下已关联的销售渠道
     *
     * @return
     * @throws Exception
     */
    @Override
    @Cacheable(isList = true)
    public List<ChannelExt> findChannel() {
        Example example = new Example(Channel.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isValid", ZeroToNineEnum.ONE.getCode());
        example.orderBy("updateTime").desc();
        List<Channel> channelList = channelService.selectByExample(example);
        AssertUtil.notNull(channelList, "未查询到业务线");
        List<ChannelExt> channelExtList =  new ArrayList<>();
        for (Channel channel:channelList ) {
            ChannelExt channelExt =JSON.parseObject(JSON.toJSONString(channel),ChannelExt.class);
            ChannelSellChannel channelSellChannel= new ChannelSellChannel();
            channelSellChannel.setChannelId(channel.getId());
            List<ChannelSellChannel> channelSellChannelList=channelSellChannelService.select(channelSellChannel);
            if (!AssertUtil.collectionIsEmpty(channelSellChannelList)){
                List<Long> sellIdList = new ArrayList<>();
                for (ChannelSellChannel sellChannel:channelSellChannelList) {
                    sellIdList.add(sellChannel.getSellChannelId());
                }
                if (!AssertUtil.collectionIsEmpty(sellIdList)){
                    Example example2 = new Example(SellChannel.class);
                    Example.Criteria criteria2 = example2.createCriteria();
                    criteria2.andIn("id",sellIdList);
                    List<SellChannel> sellChannelList = sellChannelService.selectByExample(example2);
                    if (!AssertUtil.collectionIsEmpty(sellChannelList)){
                        channelExt.setSellChannelList(sellChannelList);

                    }
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("channelName",channelExt.getName());
            jsonObject.put("channelCode",channelExt.getCode());
            channelExt.setNameValue(jsonObject);
            channelExtList.add(channelExt);
        }
        return channelExtList;
    }


    /**
     * 查询 channelJurisdiction渠道角色
     * wholeJurisdiction全局角色
     * roleType为空时查询所有角色
     */
    @Override
    public List<AclRole> findChannelOrWholeJur(String roleType,AclUserAccreditInfo userAccreditInfo) {
        Example example = new Example(AclRole.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isBlank(roleType)) {
            criteria.andIsNotNull("roleType");
        } else {

            criteria.andEqualTo("roleType", roleType);
        }
        criteria.andEqualTo("isValid", ValidEnum.VALID.getCode());
        example.orderBy("updateTime").desc();
        List<AclRole> roleList = roleService.selectByExample(example);
        if (!StringUtils.equals(userAccreditInfo.getUserId(),ADMIN_ID)) {
            List<AclRole> roleList2 =new ArrayList<>();
            for (int i = 0; i < roleList.size(); i++) {
                AclRole aclRole = roleList.get(i);
                if (!(StringUtils.equals(aclRole.getName(),"渠道角色")||StringUtils.equals(aclRole.getName(),"全局角色"))){
                    roleList2.add(aclRole);
                }
            }
            return roleList2;
        }
        return roleList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvit
    public void saveUserAccreditInfo(AclUserAddPageDate userAddPageDate, AclUserAccreditInfo aclUserAccreditInfoContext) {
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
        //业务线关联信息校验,通过后组装数据userChannelSll数据;
        List<AclUserChannelSell> aclUserChannelSellList = new ArrayList<>();
        if (!StringUtils.equals(userAddPageDate.getUserType(), UserTypeEnum.OVERALL_USER.getCode())){
            aclUserChannelSellList =checkChannelMsg(userAddPageDate.getChannelMsg(),userAddPageDate);
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
        //写入业务线关联表
        if (!AssertUtil.collectionIsEmpty(aclUserChannelSellList)){
            for (AclUserChannelSell userChannelSell:aclUserChannelSellList) {
                userChannelSell.setUserId(aclUserAccreditInfo.getUserId());
                userChannelSell.setUserAccreditId(aclUserAccreditInfo.getId());
            }
            aclUserChannelSellService.insertList(aclUserChannelSellList);
        }

        //写入user_accredit_role_relation表
        if (StringUtils.isNotBlank(userAddPageDate.getRoleNames())) {
            Long roleIds[] = splitByComma(userAddPageDate.getRoleNames());
            List<AclUserAccreditRoleRelation> uAcRoleRelationList = new ArrayList<>();
            for (int i = 0; i < roleIds.length; i++) {
                AclUserAccreditRoleRelation aclUserAccreditRoleRelation = new AclUserAccreditRoleRelation();
                aclUserAccreditRoleRelation.setUserAccreditId(aclUserAccreditInfo.getId());
                aclUserAccreditRoleRelation.setUserId(aclUserAccreditInfo.getUserId());
                aclUserAccreditRoleRelation.setRoleId(roleIds[i]);
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

    private List<AclUserChannelSell> checkChannelMsg(String channelMsg,AclUserAddPageDate userAddPageDate) {
        List<AclUserChannelSell> aclUserChannelSellList = new ArrayList<>();
        List<ChannelSelectMsg> channelSelectMsgList = new ArrayList<>();
        try {
            channelSelectMsgList = JSON.parseArray(channelMsg, ChannelSelectMsg.class);
        } catch (Exception e) {
            LOGGER.error("业务线关联信息输入错误!",e);
        }
        AssertUtil.notEmpty(channelSelectMsgList,"业务线关联错误!");
        boolean isFlag = false;
        for (ChannelSelectMsg selectMsg : channelSelectMsgList) {
            if (StringUtils.isBlank(selectMsg.getChannelName())||StringUtils.isBlank(selectMsg.getChannelCode())){
                isFlag = true;
            }
        }
        if (isFlag){
            String msg ="请确认您是否选中了销售渠道却未选择业务线!";
            LOGGER.error(msg);
            throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACL_EXCEPTION, msg);
        }
        if (!AssertUtil.collectionIsEmpty(channelSelectMsgList)) {
            List<String> noSelectSellName = new ArrayList<>();
            for (ChannelSelectMsg channelSelectMsg : channelSelectMsgList) {
                //校验业务线是否存在
                Channel channel = new Channel();
                channel.setCode(channelSelectMsg.getChannelCode());
                channel.setName(channelSelectMsg.getChannelName());
                channel = channelService.selectOne(channel);
                AssertUtil.notNull(channel, "业务线" + channelSelectMsg.getChannelName() + "不存在!");
                if (!AssertUtil.collectionIsEmpty(channelSelectMsg.getSellChannelList())){
                    for (SellChannelSelectMsg selectSellMsg : channelSelectMsg.getSellChannelList()) {
                        //校验销售渠道是否存在
                        SellChannel sellChannel = new SellChannel();
                        sellChannel.setSellName(selectSellMsg.getSellChannelName());
                        sellChannel.setSellCode(selectSellMsg.getSellChannelCode());
                        sellChannel = sellChannelService.selectOne(sellChannel);
                        AssertUtil.notNull(sellChannel, "销售渠道" + selectSellMsg.getSellChannelName() + "不存在!");
                        //校验业务线与业务线是否关联
                        ChannelSellChannel channelSellChannel = new ChannelSellChannel();
                        channelSellChannel.setChannelCode(channel.getCode());
                        channelSellChannel.setSellChannelCode(sellChannel.getSellCode());
                        channelSellChannel = channelSellChannelService.selectOne(channelSellChannel);
                        AssertUtil.notNull(channelSellChannel, "业务线:" + channel.getName() + "和销售渠道" + sellChannel.getSellName() + "未关联!");
                        AclUserChannelSell aclUserChannelSell = new AclUserChannelSell();
                        aclUserChannelSell.setChannelCode(channel.getCode());
                        aclUserChannelSell.setSellChannelCode(selectSellMsg.getSellChannelCode());
                        aclUserChannelSellList.add(aclUserChannelSell);
                    }
                }else {
                    noSelectSellName.add( channelSelectMsg.getChannelName());
                }
            }
            if (!AssertUtil.collectionIsEmpty(noSelectSellName)){
                String msg ="业务线["+StringUtils.join(noSelectSellName,SupplyConstants.Symbol.COMMA)+"]未选择销售渠道!";
                LOGGER.error(msg);
                throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACL_EXCEPTION, msg);
            }
        }

        return aclUserChannelSellList;
    }

    private void checkUserAddPageDate(AclUserAddPageDate userAddPageDate) {
        AssertUtil.notBlank(userAddPageDate.getPhone(), "用户手机号未输入");
        AssertUtil.notBlank(userAddPageDate.getName(), "用户姓名未输入");
        AssertUtil.notBlank(userAddPageDate.getUserType(), "用户类型未选择");
        AssertUtil.notBlank(userAddPageDate.getRoleNames(), "关联角色未选择");
        AssertUtil.notBlank(userAddPageDate.getIsValid(), "参数isValid不能为空");
        AssertUtil.notBlank(userAddPageDate.getChannelMsg(),"业务线关联信息为空");
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
        AssertUtil.notNull(aclUserAccreditInfo,"根据主键ID"+id+"未查询到相关用户");
        userAddPageDate = new AclUserAddPageDate(aclUserAccreditInfo);
        //获取当前用户关联的业务线 以及关联的销售渠道
        setChannelResult(userAddPageDate);
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

    private void setChannelResult( AclUserAddPageDate userAddPageDate) {
        AclUserChannelSell aclUserChannelSell = new AclUserChannelSell();
        aclUserChannelSell.setUserId(userAddPageDate.getUserId());
        aclUserChannelSell.setUserAccreditId(userAddPageDate.getId());
        List<AclUserChannelSell> userChannelSellList = aclUserChannelSellService.select(aclUserChannelSell);
        //查询到所有的业务线
        Channel queryChannel = new Channel();
        List<Channel> channelList = channelService.select(queryChannel);
        //返回的业务线,和用户关联的销售渠道
        List<ChannelExt> channelExtList = new ArrayList<>();
        //查询所有的销售渠道
        SellChannel sellChannel = new SellChannel();
        List<SellChannel> sellChannelList = sellChannelService.select(sellChannel);
        if (!AssertUtil.collectionIsEmpty(userChannelSellList)) {
            //业务线去重
            Set<String> channelSet = new HashSet<>();
            Map<String, List<SellChannel>> channelMap = new HashMap<>();
            for (AclUserChannelSell userChannelSell : userChannelSellList) {
                channelSet.add(userChannelSell.getChannelCode());
                channelMap.put(userChannelSell.getChannelCode(), new ArrayList<>());
            }

            //组装业务线
            for (Channel channel : channelList) {
                for (String channelCode : channelSet) {
                    if (StringUtils.equals(channel.getCode(), channelCode)) {
                        ChannelExt channelExt = JSON.parseObject(JSON.toJSONString(channel), ChannelExt.class);
                        channelExtList.add(channelExt);
                    }
                }
            }
            //组装销售渠道
            for (String key : channelMap.keySet()) {
                List<SellChannel> linkSellChannel = new ArrayList<>();
                for (AclUserChannelSell userChannelSell : userChannelSellList) {
                    if (StringUtils.equals(userChannelSell.getChannelCode(), key)) {
                        for (SellChannel sell : sellChannelList) {
                            if (StringUtils.equals(sell.getSellCode(), userChannelSell.getSellChannelCode())) {
                                linkSellChannel.add(sell);
                            }
                        }
                    }

                }
                channelMap.put(key, linkSellChannel);
            }
            for (ChannelExt channelExt : channelExtList) {
                channelExt.setSellChannelList(channelMap.get(channelExt.getCode()));
            }
            userAddPageDate.setChannelExtList(channelExtList);
        }
    }






    /**
     * 修改授权
     *
     * @param userAddPageDate
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvit
    public void updateUserAccredit(AclUserAddPageDate userAddPageDate, AclUserAccreditInfo aclUserAccreditInfoContext) {
        //非空校验
        AssertUtil.notBlank(userAddPageDate.getName(), "用户姓名未输入");
        AssertUtil.notBlank(userAddPageDate.getUserType(), "用户类型未选择");
        AssertUtil.notBlank(userAddPageDate.getRoleNames(), "关联角色未选择");
        AssertUtil.notNull(userAddPageDate.getId(), "更新用户时,参数Id为空");
        AssertUtil.notBlank(userAddPageDate.getChannelMsg(),"业务线关联信息为空");
        //采购组校验
        Long[] roles = StringUtil.splitByComma(userAddPageDate.getRoleNames());
        //采购组员角色的ID
        Example example = new Example(AclRole.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", ROLE_PURCHASE);
        List<AclRole> aclRoleList = roleService.selectByExample(example);
        //如果在采购组中,就判断页面传进来的采购组角色有没有被选择
        String[] purchaseNames = purchaseRole(userAddPageDate.getId());
        if (purchaseNames != null) {
            if (purchaseNames.length > 0) {
                //该角色在采购组中
                boolean flag = false;
                for (Long role : roles) {
                    for (AclRole r : aclRoleList) {
                        if (role .equals( r.getId())) {
                            //页面上已经勾选
                            flag = true;
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
        //业务线关联信息校验,通过后组装数据userChannelSll数据;
        List<AclUserChannelSell> aclUserChannelSellList = new ArrayList<>();
        if (!StringUtils.equals(userAddPageDate.getUserType(), UserTypeEnum.OVERALL_USER.getCode())){
            aclUserChannelSellList =checkChannelMsg(userAddPageDate.getChannelMsg(),userAddPageDate);
        }
        //写入user_accredit_role_relation表
        if (StringUtils.isNotBlank(userAddPageDate.getRoleNames())) {
            int count = userAccreditInfoRoleRelationService.deleteByUserAccreditId(aclUserAccreditInfo.getId());
            if (count == 0) {
                String msg = String.format("修改授权%s操作失败", JSON.toJSONString(userAddPageDate.getRoleNames()));
                LOGGER.error(msg);
                throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
            }
            Long[] roleIds = splitByComma(userAddPageDate.getRoleNames());
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
                aclUserAccreditRoleRelation.setIsValid(aclUserAccreditInfo.getIsValid());
                aclUserAccreditRoleRelation.setCreateOperator(userId);
                aclUserAccreditRoleRelation.setCreateTime(aclUserAccreditInfo.getCreateTime());
                aclUserAccreditRoleRelation.setUpdateTime(Calendar.getInstance().getTime());
                uAcRoleRelationList.add(aclUserAccreditRoleRelation);
            }
            userAccreditInfoRoleRelationService.insertList(uAcRoleRelationList);
        }
        //写入user_channel_sell关联表
        if (!AssertUtil.collectionIsEmpty(aclUserChannelSellList)){
            //清空关联
            Example example2 = new Example(AclUserChannelSell.class);
            Example.Criteria criteria2 = example2.createCriteria();
            criteria2.andEqualTo("userAccreditId", aclUserAccreditInfo.getId());
            aclUserChannelSellService.deleteByExample(example2);
            for (AclUserChannelSell userChannelSell:aclUserChannelSellList) {
                userChannelSell.setUserId(aclUserAccreditInfo.getUserId());
                userChannelSell.setUserAccreditId(aclUserAccreditInfo.getId());
            }
            aclUserChannelSellService.insertList(aclUserChannelSellList);
        }
        AclUserAccreditInfo logAclUserAccreditInfo;
        logAclUserAccreditInfo = userAccreditInfoService.selectByPrimaryKey(userAddPageDate.getId());
        logInfoService.recordLog(logAclUserAccreditInfo, String.valueOf(logAclUserAccreditInfo.getId()), userId, "修改", "", null);


    }

    /**
     * 校验手机号
     *2.0
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
        AclUserAccreditInfo userAccreditInfo = new AclUserAccreditInfo();
        userAccreditInfo.setPhone(phone);
        userAccreditInfo = userAccreditInfoService.selectOne(userAccreditInfo);
        if (userDO == null) {
            return "此手机号尚未在泰然城注册";
        }
        return userAccreditInfo.getName();
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



}
