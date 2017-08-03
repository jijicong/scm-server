package org.trc.biz.impl.purchase;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.purchase.IPurchaseGroupBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.domain.purchase.PurchaseGroupUserRelation;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.LogOperationEnum;
import org.trc.enums.ValidEnum;
import org.trc.enums.remarkEnum;
import org.trc.exception.PurchaseGroupException;
import org.trc.form.purchase.PurchaseGroupForm;
import org.trc.service.config.ILogInfoService;
import org.trc.service.purchase.IPurchaseGroupService;
import org.trc.service.purchase.IPurchaseGroupuUserRelationService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.util.IUserNameUtilService;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import org.trc.util.ParamsUtil;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

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

    private final static Integer LENGTH = 5;
    @Resource
    private ISerialUtilService serialUtilService;
    @Resource
    private ILogInfoService logInfoService;

    @Override
    public Pagenation<PurchaseGroup> purchaseGroupPage(PurchaseGroupForm form, Pagenation<PurchaseGroup> page)  {

        Example example = new Example(PurchaseGroup.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isBlank(form.getName())) {
            criteria.andLike("name", "%" + form.getName() + "%");
        }
        if (!StringUtils.isBlank(form.getIsValid())) {
            criteria.andEqualTo("isValid", form.getIsValid());
        }
        example.orderBy("updateTime").desc();
        Pagenation<PurchaseGroup> pagenation =  purchaseGroupService.pagination(example,page,form);
        userNameUtilService.handleUserName(pagenation.getResult());
        return pagenation;

    }

    @Override
    public List<AclUserAccreditInfo> findPurchaseGroupPersons(String purchaseGroupCode)  {

        AssertUtil.notBlank(purchaseGroupCode,"根据采购组编码查询采购组人员的参数code为空");
        List<AclUserAccreditInfo> aclUserAccreditInfoList = purchaseGroupService.selectPurchaseGroupPersons(purchaseGroupCode);
        return aclUserAccreditInfoList;

    }

    @Override
    public List<PurchaseGroup> findPurchaseGroupList()  {

        PurchaseGroup purchaseGroup = new PurchaseGroup();
        purchaseGroup.setIsValid(ValidEnum.VALID.getCode());
        List<PurchaseGroup> purchaseGroupList = purchaseGroupService.select(purchaseGroup);
        if(purchaseGroupList==null){
            purchaseGroupList=new ArrayList<PurchaseGroup>();
        }
        return purchaseGroupList;

    }

    @Override
    public List<AclUserAccreditInfo> findPurchaseGroupMemberStateById(Long id)  {//查询该组id下的无效状态的用户

        AssertUtil.notNull(id,"采购组id为空，查询采购组对应的无效状态的用户失败");
        return purchaseGroupService.findPurchaseGroupMemberStateById(id);

    }

    @Override
    public PurchaseGroup findPurchaseGroupByCode(String code)  {

        AssertUtil.notBlank(code,"根据采购组编码查询采购组的参数code为空");
        PurchaseGroup purchaseGroup = new PurchaseGroup();
        purchaseGroup.setCode(code);
        purchaseGroup = purchaseGroupService.selectOne(purchaseGroup);
        return purchaseGroup;

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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
    public PurchaseGroup findPurchaseByName(String name)  {

        AssertUtil.notBlank(name,"根据采购组名称查询采购组的参数name为空");
        PurchaseGroup purchaseGroup=new PurchaseGroup();
        purchaseGroup.setName(name);
        return purchaseGroupService.selectOne(purchaseGroup);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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
    public void savePurchaseGroup(PurchaseGroup purchaseGroup, AclUserAccreditInfo aclUserAccreditInfo)  {

        AssertUtil.notNull(purchaseGroup,"采购组管理模块保存采购组信息失败，采购组信息为空");
        PurchaseGroup tmp = findPurchaseByName(purchaseGroup.getName());
        if (null != tmp) {
            String msg = String.format("采购组名称[name=%s]的数据已存在,请使用其他名称",purchaseGroup.getName());
            logger.error(msg);
            throw new PurchaseGroupException(ExceptionEnum.PURCHASE_PURCHASEGROUP_SAVE_EXCEPTION, msg);
        }
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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class) //保存采购组与用户的对应关系
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
        selectInvalidUser(purchaseGroupUserRelationList,purchaseGroupUserRelationList.size());

        purchaseGroupuUserRelationService.insertList(purchaseGroupUserRelationList);

    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private List<AclUserAccreditInfo> selectInvalidUser(List<PurchaseGroupUserRelation>  list, int size) {
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
        return  purchaseGroupList;//TODO 返回的集合是页面显示信息的时候使用

    }

    @Override
    public PurchaseGroup findPurchaseById(Long id)  {

        AssertUtil.notNull(id,"采购组管理模块根据id查询采购组失败，采购组信息为空");
        PurchaseGroup purchaseGroup = new PurchaseGroup();
        purchaseGroup.setId(id);
        purchaseGroup = purchaseGroupService.selectOne(purchaseGroup);
        AssertUtil.notNull(purchaseGroup,"采购组管理模块根据id查询采购组失败，数据库查询失败");
        return purchaseGroup;

    }

}
