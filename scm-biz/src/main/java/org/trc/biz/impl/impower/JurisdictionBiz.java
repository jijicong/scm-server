package org.trc.biz.impl.impower;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.impower.IJurisdictionBiz;
import org.trc.domain.impower.Jurisdiction;
import org.trc.domain.impower.RoleJurisdictionRelation;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.domain.impower.UserAccreditRoleRelation;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.CategoryException;
import org.trc.exception.JurisdictionException;
import org.trc.form.impower.JurisdictionTreeNode;
import org.trc.service.impower.IJurisdictionService;
import org.trc.service.impower.IRoleJurisdictionRelationService;
import org.trc.service.impower.IUserAccreditInfoRoleRelationService;
import org.trc.service.impower.IUserAccreditInfoService;
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
public class JurisdictionBiz implements IJurisdictionBiz {

    private Logger logger = LoggerFactory.getLogger(JurisdictionBiz.class);
    @Resource
    private IJurisdictionService jurisdictionService;
    @Resource
    private IRoleJurisdictionRelationService roleJurisdictionRelationService;
    @Autowired
    private IUserAccreditInfoService userAccreditInfoService;
    @Autowired
    private IUserAccreditInfoRoleRelationService userAccreditInfoRoleRelationService;

    private final static Integer WHOLE_JURISDICTION_ID = 1;//全局角色的所属

    private final static Integer CHANNEL_JURISDICTION_ID = 2;//渠道角色的所属

    @Override
    public List<Jurisdiction> findWholeJurisdiction() throws Exception {

        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setBelong(WHOLE_JURISDICTION_ID);
        List<Jurisdiction> wholeJurisdictionList = jurisdictionService.select(jurisdiction);
        AssertUtil.notNull(wholeJurisdictionList, "查询全局权限列表,数据库操作失败");
        return wholeJurisdictionList;

    }

    @Override
    public List<Jurisdiction> findChannelJurisdiction() throws Exception {

        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setBelong(CHANNEL_JURISDICTION_ID);
        List<Jurisdiction> channelJurisdictionList = jurisdictionService.select(jurisdiction);
        AssertUtil.notNull(channelJurisdictionList, "查询渠道权限列表, 数据库操作失败");
        return channelJurisdictionList;

    }

    @Override
    @Transactional
    public List<Jurisdiction> findWholeJurisdictionAndCheckedByRoleId(Long roleId) throws Exception {

        AssertUtil.notNull(roleId, "根据角色的id,查询被选中的权限,角色id为空");
        // 1.查询对应的权限列表
        List<Jurisdiction> wholeJurisdictionList = findWholeJurisdiction();
        //2.查询对应角色被选中权限
        List<Long> JurisdictionIdList = roleJurisdictionRelationService.selectJurisdictionIdList(roleId);
        AssertUtil.notNull(JurisdictionIdList, "查询全局角色对应的权限关系,数据库操作失败");
        //3.赋值checked属性
        for (Jurisdiction jurisdiction : wholeJurisdictionList) {
            for (Long JurisdictionId : JurisdictionIdList) {
                if (jurisdiction.getId().equals(JurisdictionId)) {
                    jurisdiction.setCheck("true");
                }
            }
        }
        return wholeJurisdictionList;

    }

    @Override
    @Transactional
    public List<Jurisdiction> findChannelJurisdictionAndCheckedByRoleId(Long roleId) throws Exception {

        AssertUtil.notNull(roleId, "根据角色的id,查询被选中的权限,角色id为空");
        // 1.查询对应的权限列表
        List<Jurisdiction> channelJurisdictionList = findChannelJurisdiction();
        //2.查询对应角色被选中权限
        List<Long> JurisdictionIdList = roleJurisdictionRelationService.selectJurisdictionIdList(roleId);
        AssertUtil.notNull(JurisdictionIdList, "查询渠道角色对应的权限关系,数据库操作失败");
        //3.赋值checked属性
        for (Jurisdiction jurisdiction : channelJurisdictionList) {
            for (Long JurisdictionId : JurisdictionIdList) {
                if (jurisdiction.getId().equals(JurisdictionId)) {
                    jurisdiction.setCheck("true");
                }
            }
        }
        return channelJurisdictionList;

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
        UserAccreditInfo userAccreditInfo = userAccreditInfoService.selectOneById(userId);
        try {
            AssertUtil.notNull(userAccreditInfo, "用户授权信息不存在");
        } catch (IllegalArgumentException e) {
            throw new JurisdictionException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, "用户授权信息不存在");
        }
        //2.查询用户所拥有的角色
        List<UserAccreditRoleRelation> userRoleRelationList = userAccreditInfoRoleRelationService.selectListByUserAcId(userAccreditInfo.getId());
        if (AssertUtil.CollectionIsEmpty(userRoleRelationList)) {
            throw new JurisdictionException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, "用户角色信息不存在");
        }
        Long[] roleIds = new Long[userRoleRelationList.size()];
        for (int i = 0; i < userRoleRelationList.size(); i++) {
            roleIds[i] = userRoleRelationList.get(i).getRoleId();
        }
        //3.查询用户所有角色下的权限
        List<RoleJurisdictionRelation> roleJdRelationList = roleJurisdictionRelationService.selectListByRoleIds(roleIds);
        if (AssertUtil.CollectionIsEmpty(roleJdRelationList)) {
            throw new JurisdictionException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, "用户权限信息不存在");
        }
        Long[] codes = new Long[roleJdRelationList.size()];
        for (int i = 0; i < roleJdRelationList.size(); i++) {
            codes[i] = roleJdRelationList.get(i).getJurisdictionCode();
        }
        //4.查询具体的权限
        List<Jurisdiction> jurisdictionList = jurisdictionService.selectJurisdictionListByCodes(codes);
        if (AssertUtil.CollectionIsEmpty(jurisdictionList)) {
            throw new JurisdictionException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, "用户权限信息不存在");
        }
        //5.验证权限,正则匹配url，方法类型匹配
        for (Jurisdiction jurisdiction : jurisdictionList) {
            if (url.matches(jurisdiction.getUrl())) {
                if (jurisdiction.getMethod().equals(method)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Boolean urlCheck(String url) {
        Example example = new Example(Jurisdiction.class);
        List<Jurisdiction> list = jurisdictionService.selectByExample(example);
        for (Jurisdiction jurisdiction : list) {
            if (url.matches(jurisdiction.getUrl())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<JurisdictionTreeNode> getNodes(Long parentId, boolean isRecursive) throws Exception {
        Example example = new Example(Jurisdiction.class);
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
        List<Jurisdiction> childCategoryList = jurisdictionService.selectByExample(example);
        List<JurisdictionTreeNode> childNodeList = new ArrayList<>();
        for (Jurisdiction jurisdiction : childCategoryList) {
            JurisdictionTreeNode treeNode = new JurisdictionTreeNode();
            treeNode.setCode(jurisdiction.getCode());
            treeNode.setName(jurisdiction.getName());
            treeNode.setUrl(jurisdiction.getUrl());
            treeNode.setMethod(jurisdiction.getMethod());
            treeNode.setParentId(jurisdiction.getParentId());
            treeNode.setBelong(jurisdiction.getBelong());
            treeNode.setIsValid(jurisdiction.getIsValid());
            treeNode.setId(jurisdiction.getId());
            treeNode.setCreateOperator(jurisdiction.getCreateOperator());
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
        Jurisdiction jurisdiction = new Jurisdiction();
//        jurisdiction.setId(jurisdictionService.selectMaxId()+1);
        //2.查询到当前方法,当前父资源下最大的序号,如果存在加1,如果不存在,自行组合
        Example example = new Example(Jurisdiction.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("code", parentMethod+"%");
        example.orderBy("code").desc();
        List<Jurisdiction> jurisdictionList = jurisdictionService.selectByExample(example);
        if (jurisdictionList != null && jurisdictionList.size() > 0) {
            //存在的情况
            jurisdiction.setCode(jurisdictionList.get(0).getCode() + 1);
        } else {
            //不存在,手动组合,从一开始
            code = code + ZeroToNineEnum.ZERO.getCode() + jurisdictionTreeNode.getOperationType() + ZeroToNineEnum.ZERO.getCode() + ZeroToNineEnum.ONE.getCode();
            jurisdiction.setCode(Long.parseLong(code));
        }
        jurisdiction.setBelong(jurisdictionTreeNode.getBelong());
        jurisdiction.setMethod(jurisdictionTreeNode.getMethod());
        jurisdiction.setParentId(jurisdictionTreeNode.getParentId());
        jurisdiction.setName(jurisdictionTreeNode.getName());
        jurisdiction.setUrl(jurisdictionTreeNode.getUrl());
//        jurisdiction.setCreateOperator((String) requestContext.getProperty("userId"));
        jurisdiction.setCreateOperator("admin");
        jurisdiction.setCreateTime(Calendar.getInstance().getTime());
        jurisdiction.setUpdateTime(Calendar.getInstance().getTime());
        jurisdiction.setIsValid(jurisdictionTreeNode.getIsValid());
        jurisdiction.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        jurisdictionService.insert(jurisdiction);
    }

    /**
     *  编辑资源
     * @param jurisdictionTreeNode
     * @return
     * @throws Exception
     */
    @Override
    public void updateJurisdiction(JurisdictionTreeNode jurisdictionTreeNode) throws Exception {
        Jurisdiction jurisdiction = JSONObject.parseObject(JSON.toJSONString(jurisdictionTreeNode),Jurisdiction.class);
        jurisdiction.setMethod(jurisdictionTreeNode.getOperationType());
        jurisdiction.setUpdateTime(Calendar.getInstance().getTime());
        int count =   jurisdictionService.updateByPrimaryKeySelective(jurisdiction);
        if (count==0){
            String msg = "更新资源" + JSON.toJSONString(jurisdiction.getName()) + "操作失败";
            logger.error(msg);
            throw new CategoryException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
        }
    }

}
