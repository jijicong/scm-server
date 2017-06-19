package org.trc.biz.impl.impower;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.impower.IAclResourceBiz;
import org.trc.domain.impower.AclResource;
import org.trc.domain.impower.AclRoleResourceRelation;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.impower.AclUserAccreditRoleRelation;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.CategoryException;
import org.trc.exception.JurisdictionException;
import org.trc.form.impower.JurisdictionTreeNode;
import org.trc.service.impower.IAclResourceService;
import org.trc.service.impower.IAclRoleResourceRelationService;
import org.trc.service.impower.IAclUserAccreditRoleRelationService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.util.AssertUtil;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
@Service("jurisdictionBiz")
public class AclResourceBiz implements IAclResourceBiz {

    private Logger logger = LoggerFactory.getLogger(AclResourceBiz.class);
    @Resource
    private IAclResourceService jurisdictionService;
    @Resource
    private IAclRoleResourceRelationService roleJurisdictionRelationService;
    @Autowired
    private IAclUserAccreditInfoService userAccreditInfoService;
    @Autowired
    private IAclUserAccreditRoleRelationService userAccreditInfoRoleRelationService;

    private final static Integer WHOLE_JURISDICTION_ID = 1;//全局角色的所属

    private final static Integer CHANNEL_JURISDICTION_ID = 2;//渠道角色的所属

    @Override
    public List<AclResource> findWholeJurisdiction() throws Exception {

        AclResource aclResource = new AclResource();
        aclResource.setBelong(WHOLE_JURISDICTION_ID);
        List<AclResource> wholeAclResourceList = jurisdictionService.select(aclResource);
        AssertUtil.notNull(wholeAclResourceList, "查询全局权限列表,数据库操作失败");
        return wholeAclResourceList;

    }

    @Override
    public List<AclResource> findChannelJurisdiction() throws Exception {

        AclResource aclResource = new AclResource();
        aclResource.setBelong(CHANNEL_JURISDICTION_ID);
        List<AclResource> channelAclResourceList = jurisdictionService.select(aclResource);
        AssertUtil.notNull(channelAclResourceList, "查询渠道权限列表, 数据库操作失败");
        return channelAclResourceList;

    }

    @Override
    @Transactional
    public List<AclResource> findWholeJurisdictionAndCheckedByRoleId(Long roleId) throws Exception {

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
    @Transactional
    public List<AclResource> findChannelJurisdictionAndCheckedByRoleId(Long roleId) throws Exception {

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
    public Boolean authCheck(String userId, String url, String method) throws Exception {
        /*
        * 1.查询用户授权信息表
        * 2.查询用户所拥有的角色
        * 3.查询用户所有角色下的权限
        * 4.查询具体的权限
        * 5.验证权限
        * */
        //1.查询用户授权信息表
        AclUserAccreditInfo aclUserAccreditInfo = userAccreditInfoService.selectOneById(userId);
        try {
            AssertUtil.notNull(aclUserAccreditInfo, "用户授权信息不存在");
        } catch (IllegalArgumentException e) {
            throw new JurisdictionException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, "用户授权信息不存在");
        }
        //2.查询用户所拥有的角色
        List<AclUserAccreditRoleRelation> userRoleRelationList = userAccreditInfoRoleRelationService.selectListByUserAcId(aclUserAccreditInfo.getId());
        if (AssertUtil.CollectionIsEmpty(userRoleRelationList)) {
            throw new JurisdictionException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, "用户角色信息不存在");
        }
        Long[] roleIds = new Long[userRoleRelationList.size()];
        for (int i = 0; i < userRoleRelationList.size(); i++) {
            roleIds[i] = userRoleRelationList.get(i).getRoleId();
        }
        //3.查询用户所有角色下的权限
        List<AclRoleResourceRelation> roleJdRelationList = roleJurisdictionRelationService.selectListByRoleIds(roleIds);
        if (AssertUtil.CollectionIsEmpty(roleJdRelationList)) {
            throw new JurisdictionException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, "用户权限信息不存在");
        }
        Long[] codes = new Long[roleJdRelationList.size()];
        for (int i = 0; i < roleJdRelationList.size(); i++) {
            codes[i] = roleJdRelationList.get(i).getResourceCode();
        }
        //4.查询具体的权限
        List<AclResource> aclResourceList = jurisdictionService.selectJurisdictionListByCodes(codes);
        if (AssertUtil.CollectionIsEmpty(aclResourceList)) {
            throw new JurisdictionException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, "用户权限信息不存在");
        }
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
    public List<JurisdictionTreeNode> getNodes(Long parentId, boolean isRecursive) throws Exception {
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
    public void saveJurisdiction(JurisdictionTreeNode jurisdictionTreeNode, ContainerRequestContext requestContext) throws Exception {
        //生成code
        String code = jurisdictionTreeNode.getParentId().toString();
        String  parentMethod=code ;
        parentMethod = parentMethod+ZeroToNineEnum.ZERO.getCode() + jurisdictionTreeNode.getOperationType();
        AclResource aclResource = new AclResource();
//        aclResource.setId(jurisdictionService.selectMaxId()+1);
        //2.查询到当前方法,当前父资源下最大的序号,如果存在加1,如果不存在,自行组合
        Example example = new Example(AclResource.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("code", parentMethod+"%");
        example.orderBy("code").desc();
        List<AclResource> aclResourceList = jurisdictionService.selectByExample(example);
        if (aclResourceList != null && aclResourceList.size() > 0) {
            //存在的情况
            aclResource.setCode(aclResourceList.get(0).getCode() + 1);
        } else {
            //不存在,手动组合,从一开始
            code = code + ZeroToNineEnum.ZERO.getCode() + jurisdictionTreeNode.getOperationType() + ZeroToNineEnum.ZERO.getCode() + ZeroToNineEnum.ONE.getCode();
            aclResource.setCode(Long.parseLong(code));
        }
        aclResource.setBelong(jurisdictionTreeNode.getBelong());
        aclResource.setMethod(jurisdictionTreeNode.getMethod());
        aclResource.setParentId(jurisdictionTreeNode.getParentId());
        aclResource.setName(jurisdictionTreeNode.getName());
        aclResource.setUrl(jurisdictionTreeNode.getUrl());
//        aclResource.setCreateOperator((String) requestContext.getProperty("userId"));
        aclResource.setCreateOperator("admin");
        aclResource.setCreateTime(Calendar.getInstance().getTime());
        aclResource.setUpdateTime(Calendar.getInstance().getTime());
        //aclResource.setIsValid(jurisdictionTreeNode.getIsValid());
        aclResource.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        jurisdictionService.insert(aclResource);
    }

    /**
     *  编辑资源
     * @param jurisdictionTreeNode
     * @return
     * @throws Exception
     */
    @Override
    public void updateJurisdiction(JurisdictionTreeNode jurisdictionTreeNode) throws Exception {
        AclResource aclResource = JSONObject.parseObject(JSON.toJSONString(jurisdictionTreeNode),AclResource.class);
        aclResource.setMethod(jurisdictionTreeNode.getOperationType());
        aclResource.setUpdateTime(Calendar.getInstance().getTime());
        int count =   jurisdictionService.updateByPrimaryKeySelective(aclResource);
        if (count==0){
            String msg = "更新资源" + JSON.toJSONString(aclResource.getName()) + "操作失败";
            logger.error(msg);
            throw new CategoryException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
        }
    }

}
