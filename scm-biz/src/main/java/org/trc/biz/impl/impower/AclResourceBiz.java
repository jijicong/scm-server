package org.trc.biz.impl.impower;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.trc.biz.impower.IAclResourceBiz;
import org.trc.domain.impower.*;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.UserTypeEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.CategoryException;
import org.trc.exception.JurisdictionException;
import org.trc.form.impower.JurisdictionTreeNode;
import org.trc.service.impower.*;
import org.trc.util.AssertUtil;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 *
 * @author sone
 * @date 2017/5/11
 */
@Service("jurisdictionBiz")
public class AclResourceBiz implements IAclResourceBiz {

    private Logger logger = LoggerFactory.getLogger(AclResourceBiz.class);
    @Autowired
    private IAclResourceService jurisdictionService;
    @Autowired
    private IAclResourceExtService aclResourceExtService;
    @Autowired
    private IAclRoleResourceRelationService roleJurisdictionRelationService;
    @Autowired
    private IAclUserAccreditInfoService userAccreditInfoService;
    @Autowired
    private IAclUserAccreditRoleRelationService userAccreditInfoRoleRelationService;
    @Autowired
    private  IAclUserChannelSellService aclUserChannelSellService;

    /**
     * 全局角色的所属
     */
    private final static Integer WHOLE_JURISDICTION_ID = 1;
    /**
     * 渠道角色的所属
     */
    private final static Integer CHANNEL_JURISDICTION_ID = 2;
    @Value("${admin.user.id}")
    private String ADMIN_ID;

    @Override
    public List<AclResource> findWholeJurisdiction() {

        AclResource aclResource = new AclResource();
        aclResource.setBelong(WHOLE_JURISDICTION_ID);
        List<AclResource> wholeAclResourceList = jurisdictionService.select(aclResource);
        AssertUtil.notNull(wholeAclResourceList, "查询全局权限列表,数据库操作失败");
        return wholeAclResourceList;

    }


    @Override
    public List<AclResource> findWholeJurisdictionModule(AclUserAccreditInfo userAccreditInfo) {
        AclResource aclResource = new AclResource();
        aclResource.setBelong(WHOLE_JURISDICTION_ID);
        aclResource.setType(ZeroToNineEnum.ZERO.getCode());
        List<AclResource> wholeAclResourceList = jurisdictionService.select(aclResource);
        AssertUtil.notNull(wholeAclResourceList, "查询全局权限列表,数据库操作失败");
        List<AclResource> wholeAclResourceList2 = new ArrayList<>();
        if (!StringUtils.equals(userAccreditInfo.getUserId(),ADMIN_ID)){
            for (int i = 0; i <wholeAclResourceList.size() ; i++) {
                AclResource resource =wholeAclResourceList.get(i);
                if (resource.getCode().equals(10402L)||
                        resource.getCode().equals(10404L)|| resource.getCode().equals(10405L)||
                        resource.getCode().equals(10406L)||resource.getCode().equals(10501L)||
                        resource.getCode().equals(105L)){
             }else {
                    wholeAclResourceList2.add(resource);
                }
           }
        }else {
            return wholeAclResourceList;
        }
        return wholeAclResourceList2;
    }

    @Override
    public List<AclResource> findChannelJurisdiction() {

        AclResource aclResource = new AclResource();
        aclResource.setBelong(CHANNEL_JURISDICTION_ID);
        List<AclResource> channelAclResourceList = jurisdictionService.select(aclResource);
        AssertUtil.notNull(channelAclResourceList, "查询渠道权限列表, 数据库操作失败");
        return channelAclResourceList;

    }

    @Override
    public List<AclResource> findChannelJurisdictionModule() {
        AclResource aclResource = new AclResource();
        aclResource.setBelong(CHANNEL_JURISDICTION_ID);
        aclResource.setType(ZeroToNineEnum.ZERO.getCode());
        List<AclResource> channelAclResourceList = jurisdictionService.select(aclResource);
        AssertUtil.notNull(channelAclResourceList, "查询渠道权限列表, 数据库操作失败");
        return channelAclResourceList;
    }

    @Override
    public List<AclResource> findWholeJurisdictionAndCheckedModuleByRoleId(Long roleId, AclUserAccreditInfo userAccreditInfo) {
        AssertUtil.notNull(roleId, "根据角色的id,查询被选中的权限,角色id为空");
        // 1.查询对应的权限列表
        List<AclResource> wholeAclResourceList = findWholeJurisdictionModule(userAccreditInfo);
        //2.查询对应角色被选中权限
        List<Long> JurisdictionIdList = roleJurisdictionRelationService.selectJurisdictionIdList(roleId);
        AssertUtil.notNull(JurisdictionIdList, "查询全局角色对应的权限关系,数据库操作失败");
        //3.赋值checked属性
        for (AclResource aclResource : wholeAclResourceList) {
            for (Long JurisdictionId : JurisdictionIdList) {
                if (aclResource.getId().equals(JurisdictionId)) {
                    aclResource.setCheck("true");
                }
            }
        }
        return wholeAclResourceList;
    }

    @Override
    public List<AclResource> findWholeJurisdictionAndCheckedByRoleId(Long roleId) {

        AssertUtil.notNull(roleId, "根据角色的id,查询被选中的权限,角色id为空");
        // 1.查询对应的权限列表
        List<AclResource> wholeAclResourceList = findWholeJurisdiction();
        //2.查询对应角色被选中权限
        List<Long> JurisdictionIdList = roleJurisdictionRelationService.selectJurisdictionIdList(roleId);
        AssertUtil.notNull(JurisdictionIdList, "查询全局角色对应的权限关系,数据库操作失败");
        //3.赋值checked属性
        for (AclResource aclResource : wholeAclResourceList) {
            for (Long JurisdictionId : JurisdictionIdList) {
                if (aclResource.getId().equals(JurisdictionId)) {
                    aclResource.setCheck("true");
                }
            }
        }
        return wholeAclResourceList;

    }

    @Override
    public List<AclResource> findChannelJurisdictionAndCheckedModuleByRoleId(Long roleId) {
        AssertUtil.notNull(roleId, "根据角色的id,查询被选中的权限,角色id为空");
        // 1.查询对应的权限列表
        List<AclResource> channelAclResourceList = findChannelJurisdictionModule();
        //2.查询对应角色被选中权限
        List<Long> JurisdictionIdList = roleJurisdictionRelationService.selectJurisdictionIdList(roleId);
        AssertUtil.notNull(JurisdictionIdList, "查询渠道角色对应的权限关系,数据库操作失败");
        //3.赋值checked属性
        for (AclResource aclResource : channelAclResourceList) {
            for (Long JurisdictionId : JurisdictionIdList) {
                if (aclResource.getId().equals(JurisdictionId)) {
                    aclResource.setCheck("true");
                }
            }
        }
        return channelAclResourceList;
    }

    @Override
    public JSONArray queryChannelList(String userId) {
        AssertUtil.notBlank(userId,"用户UserId为空,请检查用户");

        AclUserAccreditInfo aclUserAccreditInfo =new  AclUserAccreditInfo();
        aclUserAccreditInfo.setUserId(userId);
        aclUserAccreditInfo =  userAccreditInfoService.selectOne(aclUserAccreditInfo);
        AssertUtil.notNull(aclUserAccreditInfo,"查询用户信息为空");
        JSONArray jsonArray = new JSONArray();
        if (StringUtils.equals(aclUserAccreditInfo.getUserType(), UserTypeEnum.OVERALL_USER.getCode())){
            JSONObject object= new JSONObject();
            object.put("userType",aclUserAccreditInfo.getUserType());
            jsonArray.add(object);
            return jsonArray;
        }
        List<AclUserAccreditInfo> aclUserAccreditInfoList = userAccreditInfoService.selectUserListByUserId2(userId);
        AssertUtil.notEmpty(aclUserAccreditInfoList,"当前用户没有相关的销售渠道,请确认该用户是否已经授权!");
        for (AclUserAccreditInfo userAccreditInfo:aclUserAccreditInfoList) {
            JSONObject object= new JSONObject();
            object.put("userType",userAccreditInfo.getUserType());
            object.put("channelCode",userAccreditInfo.getChannelCode());
            object.put("channelName",userAccreditInfo.getChannelName());
            jsonArray.add(object);
        }
        return jsonArray;
    }

    @Override
    public List<AclResource> findChannelJurisdictionAndCheckedByRoleId(Long roleId) {

        AssertUtil.notNull(roleId, "根据角色的id,查询被选中的权限,角色id为空");
        // 1.查询对应的权限列表
        List<AclResource> channelAclResourceList = findChannelJurisdiction();
        //2.查询对应角色被选中权限
        List<Long> JurisdictionIdList = roleJurisdictionRelationService.selectJurisdictionIdList(roleId);
        AssertUtil.notNull(JurisdictionIdList, "查询渠道角色对应的权限关系,数据库操作失败");
        //3.赋值checked属性
        for (AclResource aclResource : channelAclResourceList) {
            for (Long JurisdictionId : JurisdictionIdList) {
                if (aclResource.getId().equals(JurisdictionId)) {
                    aclResource.setCheck("true");
                }
            }
        }
        return channelAclResourceList;

    }

    @Override
    public Boolean authCheck(String userId, String url, String method) {
        /*
        * 1.查询用户授权信息表
        * 2.查询用户所拥有的角色
        * 3.查询用户所有角色下的父级权限
        * 4.查询父级权限下的具体的权限
        * 5.验证权限
        * */
        //1.查询用户授权信息表
        List<AclResource> aclResourceList = getAclResources(userId);
        //5.验证权限,正则匹配url，方法类型匹配
        for (AclResource aclResource : aclResourceList) {
            if (url.matches(aclResource.getUrl())) {
                if (aclResource.getMethod().equals(method)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<AclResource> getAclResources(String userId) {
        AclUserAccreditInfo aclUserAccreditInfo = userAccreditInfoService.selectOneById(userId);
        try {
            AssertUtil.notNull(aclUserAccreditInfo, "用户授权信息不存在");
        } catch (IllegalArgumentException e) {
            throw new JurisdictionException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, "用户授权信息不存在");
        }
        //2.查询用户所拥有的角色
        List<AclUserAccreditRoleRelation> userRoleRelationList = userAccreditInfoRoleRelationService.selectListByUserAcId(aclUserAccreditInfo.getId());
        if (AssertUtil.collectionIsEmpty(userRoleRelationList)) {
            return new ArrayList<>();
        }
        Long[] roleIds = new Long[userRoleRelationList.size()];
        for (int i = 0; i < userRoleRelationList.size(); i++) {
            roleIds[i] = userRoleRelationList.get(i).getRoleId();
        }
        //3.查询用户所有角色下的权限
        List<AclRoleResourceRelation> roleJdRelationList = roleJurisdictionRelationService.selectListByRoleIds(roleIds);
        if (AssertUtil.collectionIsEmpty(roleJdRelationList)) {
            return new ArrayList<>();
        }
        Long[] codes = new Long[roleJdRelationList.size()];
        for (int i = 0; i < roleJdRelationList.size(); i++) {
            codes[i] = roleJdRelationList.get(i).getResourceCode();
        }
        //4.查询具体的权限
        List<AclResource> aclResourceList = jurisdictionService.selectJurisdictionListByCodes(codes);
        if (AssertUtil.collectionIsEmpty(aclResourceList)) {
            return new ArrayList<>();
        }
        return aclResourceList;
    }

    @Override
    public Boolean urlCheck(String url) {
        Example example = new Example(AclResource.class);
        List<AclResource> list = jurisdictionService.selectByExample(example);
        for (AclResource aclResource : list) {
            if (url.matches(aclResource.getUrl())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<JurisdictionTreeNode> getNodes(Long parentId, boolean isRecursive) {
        Example example = new Example(AclResource.class);
        Example.Criteria criteria = example.createCriteria();
        if (null == parentId) {
            List<Long> parentIdList = new ArrayList<>();
            parentIdList.add(1l);
            parentIdList.add(2l);
            criteria.andIn("parentId", parentIdList);
//            criteria.andEqualTo("parentId", Long.parseLong("1"));
//            criteria.orEqualTo("parentId", Long.parseLong("2"));
        } else {
            criteria.andEqualTo("parentId", parentId);
        }
        List<AclResource> childCategoryList = jurisdictionService.selectByExample(example);
        List<JurisdictionTreeNode> childNodeList = new ArrayList<>();
        for (AclResource aclResource : childCategoryList) {
            JurisdictionTreeNode treeNode = new JurisdictionTreeNode();
            treeNode.setCode(aclResource.getCode());
            treeNode.setName(aclResource.getName());
            treeNode.setUrl(aclResource.getUrl());
            treeNode.setMethod(aclResource.getMethod());
            treeNode.setParentId(aclResource.getParentId());
            treeNode.setBelong(aclResource.getBelong());
            //treeNode.setIsValid(aclResource.getIsValid());
            treeNode.setId(aclResource.getId());
            treeNode.setCreateOperator(aclResource.getCreateOperator());
            childNodeList.add(treeNode);
        }
        if (childNodeList.size() == 0) {
            return childNodeList;
        }
        if (isRecursive == true) {
            for (JurisdictionTreeNode childNode : childNodeList) {
                List<JurisdictionTreeNode> nextChildJurisdictionList = getNodes(childNode.getCode(), isRecursive);
                if (nextChildJurisdictionList.size() > 0) {
                    childNode.setChildren(nextChildJurisdictionList);
                }
            }
        }
        return childNodeList;
    }

    /**
     * 新增资源
     *
     * @param jurisdictionTreeNode
     * @throws Exception
     */
    @Override
    public void saveJurisdiction(JurisdictionTreeNode jurisdictionTreeNode, AclUserAccreditInfo aclUserAccreditInfo) {
        String code = "";
        //生成code
        if (jurisdictionTreeNode.getParentId() != null) {
            code = jurisdictionTreeNode.getParentId().toString();
        }
        String parentMethod = code;
        if (code.length() == 5) {
            parentMethod = parentMethod + ZeroToNineEnum.ZERO.getCode() + jurisdictionTreeNode.getOperationType();
        }
        AclResource aclResource = new AclResource();
//        aclResource.setId(jurisdictionService.selectMaxId()+1);
        //2.查询到当前方法,当前父资源下最大的序号,如果存在加1,如果不存在,自行组合
        Example example = new Example(AclResource.class);
        Example.Criteria criteria = example.createCriteria();
        if (parentMethod.length() == 3) {
            criteria.andLike("parentId", parentMethod);
            example.orderBy("code").desc();
        } else if (StringUtils.isBlank(code)) {
            criteria.andLessThanOrEqualTo("parentId", jurisdictionTreeNode.getBelong());
            criteria.andEqualTo("belong", jurisdictionTreeNode.getBelong());
//            criteria.andLessThanOrEqualTo("parentId", "2");
            example.orderBy("code").desc();
        } else {
            criteria.andLike("code", parentMethod + "%");
            example.orderBy("code").desc();
        }
        List<AclResource> aclResourceList = jurisdictionService.selectByExample(example);
        if (aclResourceList != null && aclResourceList.size() > 0) {
            //存在的情况
            aclResource.setCode(aclResourceList.get(0).getCode() + 1);
        } else {
            //不存在,手动组合,从一开始
            if (code.length() == 3) {
                code = code + ZeroToNineEnum.ZERO.getCode() + ZeroToNineEnum.ONE.getCode();
            } else {
                code = code + ZeroToNineEnum.ZERO.getCode() + jurisdictionTreeNode.getOperationType() + ZeroToNineEnum.ZERO.getCode() + ZeroToNineEnum.ONE.getCode();
            }
            aclResource.setCode(Long.parseLong(code));
        }
        if (code.length() == 3) {
            String acl = String.valueOf(aclResource.getCode());
            if (acl.length() >= 5) {// 判断是否长度大于等于4
                acl = acl.substring(0, 5);
            }
            aclResource.setCode(Long.parseLong(acl));
        }

        aclResource.setBelong(jurisdictionTreeNode.getBelong());
        aclResource.setMethod(jurisdictionTreeNode.getMethod());
        aclResource.setName(jurisdictionTreeNode.getName());
        if (aclResource.getCode().toString().length() == 3) {
            aclResource.setParentId(Long.valueOf(jurisdictionTreeNode.getBelong()));
        } else {
            aclResource.setParentId(jurisdictionTreeNode.getParentId());
        }
        aclResource.setUrl(jurisdictionTreeNode.getUrl());
        if (StringUtils.equals(aclResource.getUrl(), ZeroToNineEnum.ONE.getCode())) {
            aclResource.setType(ZeroToNineEnum.ZERO.getCode());
        } else {
            aclResource.setType(ZeroToNineEnum.ONE.getCode());
        }
//        aclResource.setCreateOperator((String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID));
        aclResource.setCreateOperator("admin");
        aclResource.setCreateTime(Calendar.getInstance().getTime());
        aclResource.setUpdateTime(Calendar.getInstance().getTime());
        //aclResource.setIsValid(jurisdictionTreeNode.getIsValid());
        aclResource.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        jurisdictionService.insertOne(aclResource);
    }

    /**
     * 编辑资源
     *
     * @param jurisdictionTreeNode
     * @return
     * @throws Exception
     */
    @Override
    public void updateJurisdiction(JurisdictionTreeNode jurisdictionTreeNode) {
        AclResourceExt aclResource = JSONObject.parseObject(JSON.toJSONString(jurisdictionTreeNode), AclResourceExt.class);
        AclResourceExt resourceExt = new AclResourceExt();
        resourceExt.setCode(aclResource.getId());
        resourceExt = aclResourceExtService.selectOne(resourceExt);
        aclResource.setId(resourceExt.getId());
        aclResource.setMethod(jurisdictionTreeNode.getOperationType());
        aclResource.setUpdateTime(Calendar.getInstance().getTime());
        int count = aclResourceExtService.updateByPrimaryKeySelective(aclResource);
        if (count == 0) {
            String msg = "更新资源" + JSON.toJSONString(aclResource.getName()) + "操作失败";
            logger.error(msg);
            throw new CategoryException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
        }
    }

    @Override
    public List<Map<String, Object>> getHtmlJurisdiction(String userId) {
        List<Map<String, Object>> jurisdictionList = new ArrayList<>();
        AclUserAccreditInfo aclUserAccreditInfo = userAccreditInfoService.selectOneById(userId);
        try {
            AssertUtil.notNull(aclUserAccreditInfo, "用户授权信息不存在");
        } catch (IllegalArgumentException e) {
            return jurisdictionList;
        }
        //2.查询用户所拥有的角色
        List<AclUserAccreditRoleRelation> userRoleRelationList = userAccreditInfoRoleRelationService.selectListByUserAcId(aclUserAccreditInfo.getId());
        if (AssertUtil.collectionIsEmpty(userRoleRelationList)) {
            return jurisdictionList;
        }
        Long[] roleIds = new Long[userRoleRelationList.size()];
        for (int i = 0; i < userRoleRelationList.size(); i++) {
            roleIds[i] = userRoleRelationList.get(i).getRoleId();
        }
        //3.查询用户所有角色下的权限
        Example example = new Example(AclRoleResourceRelation.class);
        Example.Criteria criteria = example.createCriteria();
        List<Long> roleIdList = new ArrayList<>();
        Collections.addAll(roleIdList, roleIds);
        criteria.andIn("roleId", roleIdList);
        List<AclRoleResourceRelation> roleJdRelationList = roleJurisdictionRelationService.selectByExample(example);
        if (AssertUtil.collectionIsEmpty(roleJdRelationList)) {
            return jurisdictionList;
        }
        Map<String, Object> map = new HashMap<>();
        Set<Long> resourceCodeSet = new HashSet<>();
        for (AclRoleResourceRelation aclRoleResourceRelation : roleJdRelationList) {
            //取得资源码前3位
            resourceCodeSet.add(aclRoleResourceRelation.getResourceCode() / 100);
        }
        for (Long resourceCode : resourceCodeSet) {
            Map<String, Object> jurisdictionMap = new HashMap<>();
            jurisdictionMap.put("parentCode", resourceCode);
            Set<Long> longSet = new HashSet<>();
            for (AclRoleResourceRelation aclRoleResourceRelation : roleJdRelationList) {
                //取得资源码前3位
                if (resourceCode.equals(aclRoleResourceRelation.getResourceCode() / 100)) {
                    //取得资源码前5位
                    longSet.add(aclRoleResourceRelation.getResourceCode());
                }
            }
            jurisdictionMap.put("codeList", longSet);
            jurisdictionList.add(jurisdictionMap);
        }
        //两个商品管理需要合并
        if(!AssertUtil.collectionIsEmpty(jurisdictionList)){
            List<Map<String, Object>> needRemoveJurisdictionList = new ArrayList<>();
            for (Map<String, Object> map1: jurisdictionList) {
                if(map1.get("parentCode").equals(102l)){
                    for (Map<String, Object> map2: jurisdictionList ) {
                        if(map2.get("parentCode").equals(201l)){
                            Set<Long> longSet1= (Set<Long>) map1.get("codeList");
                            Set<Long> longSet2= (Set<Long>) map2.get("codeList");
                            longSet1.addAll(longSet2);
                            needRemoveJurisdictionList.add(map2);
                        }
                    }
                }
                if(map1.get("parentCode").equals(103l)){
                    for (Map<String, Object> map2: jurisdictionList ) {
                        if(map2.get("parentCode").equals(202l)){
                            Set<Long> longSet1= (Set<Long>) map1.get("codeList");
                            Set<Long> longSet2= (Set<Long>) map2.get("codeList");
                            longSet1.addAll(longSet2);
                            needRemoveJurisdictionList.add(map2);
                        }
                    }
                }
            }
            if(!AssertUtil.collectionIsEmpty(needRemoveJurisdictionList)){
                jurisdictionList.removeAll(needRemoveJurisdictionList);
            }
        }
        return jurisdictionList;
    }

}
