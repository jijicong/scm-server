package org.trc.biz.impl.purchase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.purchase.IPurchaseGroupBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.domain.purchase.PurchaseGroupUser;
import org.trc.domain.purchase.PurchaseGroupUserRelation;
import org.trc.enums.*;
import org.trc.exception.PurchaseGroupException;
import org.trc.form.purchase.PurchaseGroupForm;
import org.trc.service.config.ILogInfoService;
import org.trc.service.purchase.IPurchaseGroupService;
import org.trc.service.purchase.IPurchaseGroupUserService;
import org.trc.service.purchase.IPurchaseGroupuUserRelationService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.util.IUserNameUtilService;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import org.trc.util.ParamsUtil;
import org.trc.util.cache.PurchaseGroupCacheEvict;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by sone on 2017/5/19.
 */
@Service("purchaseGroupBiz")
public class PurchaseGroupBiz implements IPurchaseGroupBiz{

    private Logger logger = LoggerFactory.getLogger(PurchaseGroupBiz.class);
    @Resource
    private IPurchaseGroupService purchaseGroupService;
    @Resource
    private IPurchaseGroupuUserRelationService purchaseGroupuUserRelationService;

    @Resource
    private IUserNameUtilService userNameUtilService;

    private final static String  SERIALNAME = "CGZ";

    /**
     * 正则表达式：验证手机号
     */
    private final static String REGEX_MOBILE = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-9])|(147))\\\\d{8}$";

    private final static Integer LENGTH = 5;
    @Resource
    private ISerialUtilService serialUtilService;
    @Resource
    private ILogInfoService logInfoService;
    @Resource
    private IPurchaseGroupUserService purchaseGroupUserService;

    @Override
    @Cacheable(value = SupplyConstants.Cache.PURCHASE_GROUP)
    public Pagenation<PurchaseGroup> purchaseGroupPage(PurchaseGroupForm form, Pagenation<PurchaseGroup> page, AclUserAccreditInfo aclUserAccreditInfo)  {

        Example example = new Example(PurchaseGroup.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isBlank(form.getName())) {
            criteria.andLike("name", "%" + form.getName() + "%");
        }
        if (!StringUtils.isBlank(form.getIsValid())) {
            criteria.andEqualTo("isValid", form.getIsValid());
        }
        criteria.andEqualTo("channelCode",aclUserAccreditInfo.getChannelCode());
        example.orderBy("updateTime").desc();
        Pagenation<PurchaseGroup> pagenation =  purchaseGroupService.pagination(example,page,form);
        userNameUtilService.handleUserName(pagenation.getResult());
        return pagenation;

    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.PURCHASE_GROUP)
    public List<AclUserAccreditInfo> findPurchaseGroupPersons(String purchaseGroupCode)  {

        AssertUtil.notBlank(purchaseGroupCode,"根据采购组编码查询采购组人员的参数code为空");
        List<AclUserAccreditInfo> aclUserAccreditInfoList = purchaseGroupService.selectPurchaseGroupPersons(purchaseGroupCode);
        return aclUserAccreditInfoList;

    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.PURCHASE_GROUP)
    public List<PurchaseGroup> findPurchaseGroupList(AclUserAccreditInfo aclUserAccreditInfo)  {

        PurchaseGroup purchaseGroup = new PurchaseGroup();
        purchaseGroup.setIsValid(ValidEnum.VALID.getCode());
        purchaseGroup.setChannelCode(aclUserAccreditInfo.getChannelCode());
        List<PurchaseGroup> purchaseGroupList = purchaseGroupService.select(purchaseGroup);
        if(purchaseGroupList==null){
            purchaseGroupList=new ArrayList<PurchaseGroup>();
        }

        return purchaseGroupList;

    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.PURCHASE_GROUP)
    public List<AclUserAccreditInfo> findPurchaseGroupMemberStateById(Long id)  {//查询该组id下的无效状态的用户

        AssertUtil.notNull(id,"采购组id为空，查询采购组对应的无效状态的用户失败");
        return purchaseGroupService.findPurchaseGroupMemberStateById(id);

    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.PURCHASE_GROUP)
    public PurchaseGroup findPurchaseGroupByCode(String code)  {

        AssertUtil.notBlank(code,"根据采购组编码查询采购组的参数code为空");
        PurchaseGroup purchaseGroup = new PurchaseGroup();
        purchaseGroup.setCode(code);
        purchaseGroup = purchaseGroupService.selectOne(purchaseGroup);
        return purchaseGroup;

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @PurchaseGroupCacheEvict
    public void updatePurchaseStatus(PurchaseGroup purchaseGroup, AclUserAccreditInfo aclUserAccreditInfo)  {
        AssertUtil.notNull(purchaseGroup,"采购组信息为空，修改采购组状态失败");
        PurchaseGroup updatePurchaseGroup = new PurchaseGroup();
        updatePurchaseGroup.setId(purchaseGroup.getId());
        String remark = null;
        if (purchaseGroup.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updatePurchaseGroup.setIsValid(ValidEnum.NOVALID.getCode());
            remark = remarkEnum.VALID_OFF.getMessage();
        } else {
            updatePurchaseGroup.setIsValid(ValidEnum.VALID.getCode());
            remark = remarkEnum.VALID_ON.getMessage();
        }

        Map<String,Object> map = new HashMap<>();
        map.put("isValid",updatePurchaseGroup.getIsValid());
        map.put("purchaseGroupCode",purchaseGroup.getCode());
        purchaseGroupuUserRelationService.updateIsValidByCode(map);

        updatePurchaseGroup.setUpdateTime(Calendar.getInstance().getTime());
        int count = purchaseGroupService.updateByPrimaryKeySelective(updatePurchaseGroup);
        if(count == 0){
            String msg = String.format("修改采购组状态%s数据库操作失败",JSON.toJSONString(purchaseGroup));
            logger.error(msg);
            throw new PurchaseGroupException(ExceptionEnum.PURCHASE_PURCHASEGROUP_UPDATE_EXCEPTION, msg);
        }
        String userId= aclUserAccreditInfo.getUserId();
        logInfoService.recordLog(purchaseGroup,purchaseGroup.getId().toString(),userId, LogOperationEnum.UPDATE.getMessage(),remark,null);

    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.PURCHASE_GROUP)
    public PurchaseGroup findPurchaseByName(String name)  {

        AssertUtil.notBlank(name,"根据采购组名称查询采购组的参数name为空");
        PurchaseGroup purchaseGroup=new PurchaseGroup();
        purchaseGroup.setName(name);
        return purchaseGroupService.selectOne(purchaseGroup);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @PurchaseGroupCacheEvict
    public void updatePurchaseGroup(PurchaseGroup purchaseGroup, AclUserAccreditInfo aclUserAccreditInfo)  {
        AssertUtil.notNull(purchaseGroup,"根据采购组信息修改采购组失败,采购信息为null");
        PurchaseGroup tmp = findPurchaseByName(purchaseGroup.getName());
        if(tmp!=null){
            if(!tmp.getId().equals(purchaseGroup.getId())){
                throw new PurchaseGroupException(ExceptionEnum.PURCHASE_PURCHASEGROUP_UPDATE_EXCEPTION, "其它的采购组已经使用该名称");
            }
        }
        purchaseGroup.setUpdateTime(Calendar.getInstance().getTime());

        PurchaseGroup _purchaseGroup = purchaseGroupService.selectByPrimaryKey(purchaseGroup.getId());
        String remark = null;
        if(!_purchaseGroup.getIsValid().equals(purchaseGroup.getIsValid())){
            if(purchaseGroup.getIsValid().equals(ValidEnum.VALID.getCode())){
                remark=remarkEnum.VALID_ON.getMessage();
            }else{
                remark=remarkEnum.VALID_OFF.getMessage();
            }
        }

        int count = purchaseGroupService.updateByPrimaryKeySelective(purchaseGroup);
        if(count == 0){
            String msg = String.format("修改采购组%s数据库操作失败",JSON.toJSONString(purchaseGroup));
            logger.error(msg);
            throw new PurchaseGroupException(ExceptionEnum.PURCHASE_PURCHASEGROUP_UPDATE_EXCEPTION, msg);
        }
        purchaseGroupuUserRelationService.deleteByPurchaseGroupCode(purchaseGroup.getCode());
        /*if (temp==0){ //初始化系统角色或者新增角色时，必须有对应的权限<权限不能为空>
            String msg = "根据采购组编码,采购组和用户关联删除失败";
            logger.error(msg);
            throw  new PurchaseGroupException(ExceptionEnum.PURCHASE_PURCHASEGROUP_UPDATE_EXCEPTION, msg);
        }*/
        savePurchaseGroupUserRelation(purchaseGroup.getCode(),purchaseGroup.getLeaderUserId(),purchaseGroup.getMemberUserId(),purchaseGroup.getIsValid());

        String userId= aclUserAccreditInfo.getUserId();
        logInfoService.recordLog(purchaseGroup,purchaseGroup.getId().toString(),userId,LogOperationEnum.UPDATE.getMessage(),remark,null);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @PurchaseGroupCacheEvict
    public void savePurchaseGroup(PurchaseGroup purchaseGroup, AclUserAccreditInfo aclUserAccreditInfo)  {

        AssertUtil.notNull(purchaseGroup,"采购组管理模块保存采购组信息失败，采购组信息为空");
        PurchaseGroup tmp = findPurchaseByName(purchaseGroup.getName());
        if (null != tmp) {
            String msg = String.format("采购组名称[name=%s]的数据已存在,请使用其他名称",purchaseGroup.getName());
            logger.error(msg);
            throw new PurchaseGroupException(ExceptionEnum.PURCHASE_PURCHASEGROUP_SAVE_EXCEPTION, msg);
        }
        purchaseGroup.setChannelCode(aclUserAccreditInfo.getChannelCode());
        ParamsUtil.setBaseDO(purchaseGroup);
        int count = 0;
        String code = serialUtilService.generateCode(LENGTH,SERIALNAME);
        purchaseGroup.setCode(code);
        count = purchaseGroupService.insert(purchaseGroup);
        if (count==0){
            String msg = "采购组保存,数据库操作失败";
            logger.error(msg);
            throw new PurchaseGroupException(ExceptionEnum.PURCHASE_PURCHASEGROUP_SAVE_EXCEPTION, msg);
        }
        //存储采购组与授权用户关系
        String purchaseGroupCode = purchaseGroup.getCode();
        String laederUserId = purchaseGroup.getLeaderUserId();
        String memberUserStrs = purchaseGroup.getMemberUserId();
        savePurchaseGroupUserRelation(purchaseGroupCode,laederUserId,memberUserStrs,purchaseGroup.getIsValid());

        String userId= aclUserAccreditInfo.getUserId();
        logInfoService.recordLog(purchaseGroup,purchaseGroup.getId().toString(),userId,LogOperationEnum.ADD.getMessage(),null,null);

    }

    //保存采购组与用户的对应关系
    private void savePurchaseGroupUserRelation(String purchaseGroupCode,String laederUserId,String memberUserStrs,String isValid) {

        List<PurchaseGroupUserRelation> purchaseGroupUserRelationList = new ArrayList<>();
        PurchaseGroupUserRelation purchaseGroupUserRelation = new PurchaseGroupUserRelation();//设置组长与采购组的关联关系
        purchaseGroupUserRelation.setIsValid(isValid);
        ParamsUtil.setBaseDO(purchaseGroupUserRelation);
        purchaseGroupUserRelation.setPurchaseGroupCode(purchaseGroupCode);
        purchaseGroupUserRelation.setUserId(laederUserId);
        purchaseGroupUserRelationList.add(purchaseGroupUserRelation);
        if(memberUserStrs!=null && memberUserStrs.trim()!="" && memberUserStrs.length()!=0) {
            String[]  memberUserIds = memberUserStrs.split(SupplyConstants.Symbol.COMMA);//3,4,6,7
            for (String memberUserId : memberUserIds) {//遍历存储采购组员与采购组的关联关系
                purchaseGroupUserRelation = new PurchaseGroupUserRelation();
                purchaseGroupUserRelation.setIsValid(isValid);
                ParamsUtil.setBaseDO(purchaseGroupUserRelation);
                purchaseGroupUserRelation.setPurchaseGroupCode(purchaseGroupCode);
                purchaseGroupUserRelation.setUserId(memberUserId);
                purchaseGroupUserRelationList.add(purchaseGroupUserRelation);
            }
        }
//        selectInvalidUser(purchaseGroupUserRelationList,purchaseGroupUserRelationList.size());

        purchaseGroupuUserRelationService.insertList(purchaseGroupUserRelationList);

    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<AclUserAccreditInfo> selectInvalidUser(List<PurchaseGroupUserRelation>  list, int size) {
        /*
        首先要确认所插入的用户，不能被停用
        再次考虑，使用的用户据用采购角色
         */
        String [] userIds = new String[list.size()];
        for (int i = 0 ;i<list.size() ; i++) {
            userIds[i] = list.get(i).getUserId();
        }
        List<AclUserAccreditInfo> purchaseGroupList = purchaseGroupService.selectInvalidUser(userIds);
        if(purchaseGroupList.size() != 0){
            throw  new PurchaseGroupException(ExceptionEnum.PURCHASE_PURCHASEGROUP_SAVE_EXCEPTION,"部分采购员被停用,请重新添加");
        }
        int num = purchaseGroupService.selectUserWithPurchaseNum(userIds);
        if(num < size){
            throw  new PurchaseGroupException(ExceptionEnum.PURCHASE_PURCHASEGROUP_SAVE_EXCEPTION,"部分采购员被取消采购角色,请重新添加");
        }
        return  purchaseGroupList;// 返回的集合是页面显示信息的时候使用

    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.PURCHASE_GROUP)
    public PurchaseGroup findPurchaseById(Long id)  {

        AssertUtil.notNull(id,"采购组管理模块根据id查询采购组失败，采购组信息为空");
        PurchaseGroup purchaseGroup = new PurchaseGroup();
        purchaseGroup.setId(id);
        purchaseGroup = purchaseGroupService.selectOne(purchaseGroup);
        AssertUtil.notNull(purchaseGroup,"采购组管理模块根据id查询采购组失败，数据库查询失败");
        return purchaseGroup;

    }

    @Override
    public List<PurchaseGroupUser> findPurchaseGroupUser(AclUserAccreditInfo aclUserAccreditInfo) {
        PurchaseGroupUser purchaseGroupUser = new PurchaseGroupUser();
        purchaseGroupUser.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        purchaseGroupUser.setChannelCode(aclUserAccreditInfo.getChannelCode());
        List<PurchaseGroupUser> list = purchaseGroupUserService.select(purchaseGroupUser);
        return list;
    }

    @Override
    @PurchaseGroupCacheEvict
    public void deletePurchaseCroupUserById(Long id) {
        AssertUtil.notNull(id,"采购组管理模块根据id查询采购组员失败，采购组员信息为空");
        PurchaseGroupUser purchaseGroupUser = new PurchaseGroupUser();
        purchaseGroupUser.setId(id);
        purchaseGroupUser.setIsDeleted(ZeroToNineEnum.ONE.getCode());
        purchaseGroupUserService.updateByPrimaryKeySelective(purchaseGroupUser);
    }

    @Override
    @PurchaseGroupCacheEvict
    public void savePurchaseCroupUser(PurchaseGroupUser purchaseGroupUser, AclUserAccreditInfo aclUserAccreditInfo) {
        //需判断用户是否有改变属性启停用状态如果有变更需要更改关联关系表
        List<PurchaseGroupUser> valueList = JSONArray.parseArray(purchaseGroupUser.getGridValue(), PurchaseGroupUser.class);
        AssertUtil.notNull(valueList, "采购组管理模块根据更新采购组员信息失败，采购组员信息为空");
        String userId= aclUserAccreditInfo.getUserId();
        String channelCode = aclUserAccreditInfo.getChannelCode();
        for (PurchaseGroupUser user : valueList) {
            AssertUtil.notNull(user.getName(), "采购组管理模块根据更新采购组员信息失败，采购组员信息为空");
            AssertUtil.notNull(user.getPhoneNumber(), "采购组管理模块根据更新采购组员信息失败，采购组员信息为空");
            if (Pattern.matches(REGEX_MOBILE, user.getPhoneNumber())) {
                String msg = "手机号格式错误," + user.getPhoneNumber();
                logger.error(msg);
                throw  new PurchaseGroupException(ExceptionEnum.PURCHASE_PURCHASEGROUP_SAVE_EXCEPTION, msg);
            }
            if(user.getName().length() > 10){
                String msg = "用户名格式错误," + user.getName();
                logger.error(msg);
                throw  new PurchaseGroupException(ExceptionEnum.PURCHASE_PURCHASEGROUP_SAVE_EXCEPTION, msg);
            }

            if (user.getStatus().equals(RecordStatusEnum.ADD.getCode())) {
                user.setCreateTime(Calendar.getInstance().getTime());
                user.setUpdateTime(Calendar.getInstance().getTime());
                user.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                user.setCreateOperator(userId);
                user.setChannelCode(channelCode);
                purchaseGroupUserService.insert(user);
            }
            if (user.getStatus().equals(RecordStatusEnum.DELETE.getCode())) {
                PurchaseGroupUser userDelete = new PurchaseGroupUser();
                userDelete.setId(user.getId());
                userDelete.setIsDeleted(ZeroToNineEnum.ONE.getCode());
                userDelete.setUpdateTime(Calendar.getInstance().getTime());
                purchaseGroupUserService.updateByPrimaryKeySelective(userDelete);
            }
        }
    }
}
