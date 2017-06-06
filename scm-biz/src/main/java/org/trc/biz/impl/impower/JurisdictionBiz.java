package org.trc.biz.impl.impower;

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
import org.trc.exception.ConfigException;
import org.trc.service.impower.IJurisdictionService;
import org.trc.service.impower.IRoleJurisdictionRelationService;
import org.trc.service.impower.IUserAccreditInfoRoleRelationService;
import org.trc.service.impower.IUserAccreditInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.CommonUtil;

import javax.annotation.Resource;
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
        AssertUtil.notNull(wholeJurisdictionList,"查询全局权限列表,数据库操作失败");
        return wholeJurisdictionList;

    }

    @Override
    public List<Jurisdiction> findChannelJurisdiction() throws Exception {

        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setBelong(CHANNEL_JURISDICTION_ID);
        List<Jurisdiction> channelJurisdictionList = jurisdictionService.select(jurisdiction);
        AssertUtil.notNull(channelJurisdictionList,"查询渠道权限列表, 数据库操作失败");
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
        AssertUtil.notNull(JurisdictionIdList,"查询全局角色对应的权限关系,数据库操作失败");
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
        AssertUtil.notNull(JurisdictionIdList,"查询渠道角色对应的权限关系,数据库操作失败");
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
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, "用户授权信息不存在");
        }
        //2.查询用户所拥有的角色
        List<UserAccreditRoleRelation> userRoleRelationList = userAccreditInfoRoleRelationService.selectListByUserAcId(userAccreditInfo.getId());
        if (AssertUtil.CollectionIsEmpty(userRoleRelationList)) {
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, "用户角色信息不存在");
        }
        Long[] roleIds = new Long[userRoleRelationList.size()];
        for (int i = 0; i < userRoleRelationList.size(); i++) {
            roleIds[i] = userRoleRelationList.get(i).getRoleId();
        }
        //3.查询用户所有角色下的权限
        List<RoleJurisdictionRelation> roleJdRelationList = roleJurisdictionRelationService.selectListByRoleIds(roleIds);
        if (AssertUtil.CollectionIsEmpty(roleJdRelationList)) {
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, "用户权限信息不存在");
        }
        Long[] codes = new Long[roleJdRelationList.size()];
        for (int i = 0; i < roleJdRelationList.size(); i++) {
            codes[i] = roleJdRelationList.get(i).getJurisdictionCode();
        }
        //4.查询具体的权限
        List<Jurisdiction> jurisdictionList = jurisdictionService.selectJurisdictionListByCodes(codes);
        if (AssertUtil.CollectionIsEmpty(jurisdictionList)) {
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, "用户权限信息不存在");
        }
        //5.验证权限,正则匹配url，方法类型匹配
        for (Jurisdiction jurisdiction : jurisdictionList) {
            if(!url.matches(jurisdiction.getUrl())){
                return false;
            }
            if(!jurisdiction.getMethod().equals(method)){
                return false;
            }
            return true;
        }
        return false;
    }
}
