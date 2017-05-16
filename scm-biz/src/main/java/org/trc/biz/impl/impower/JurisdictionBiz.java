package org.trc.biz.impl.impower;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.impower.IJurisdictionBiz;
import org.trc.domain.impower.Jurisdiction;
import org.trc.domain.impower.RoleJurisdictionRelation;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.ConfigException;
import org.trc.service.impower.IJurisdictionService;
import org.trc.service.impower.IRoleJurisdictionRelationService;
import org.trc.util.AssertUtil;
import org.trc.util.CommonUtil;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
@Service("jurisdictionBiz")
public class JurisdictionBiz implements IJurisdictionBiz {

    private final static Logger LOGGER = LoggerFactory.getLogger(JurisdictionBiz.class);
    @Resource
    private IJurisdictionService jurisdictionService;
    @Resource
    private IRoleJurisdictionRelationService roleJurisdictionRelationService;

    private final static Integer WHOLE_JURISDICTION_ID=1;//全局角色的所属

    private final static Integer CHANNEL_JURISDICTION_ID=2;//渠道角色的所属

    @Override
    public List<Jurisdiction> findWholeJurisdiction() throws Exception {

        Jurisdiction jurisdiction=new Jurisdiction();
        jurisdiction.setBelong(WHOLE_JURISDICTION_ID);
        List<Jurisdiction> wholeJurisdictionList=jurisdictionService.select(jurisdiction);
        if(wholeJurisdictionList==null){
            String msg = CommonUtil.joinStr("查询全局权限列表","数据库操作失败").toString();
            LOGGER.error(msg);
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, msg);
        }
        return wholeJurisdictionList;

    }

    @Override
    public List<Jurisdiction> findChannelJurisdiction() throws Exception {

        Jurisdiction jurisdiction=new Jurisdiction();
        jurisdiction.setBelong(CHANNEL_JURISDICTION_ID);
        List<Jurisdiction> channelJurisdictionList=jurisdictionService.select(jurisdiction);
        if(channelJurisdictionList==null){
            String msg = CommonUtil.joinStr("查询渠道权限列表","数据库操作失败").toString();
            LOGGER.error(msg);
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, msg);
        }
        return channelJurisdictionList;

    }

    @Override
    @Transactional
    public List<Jurisdiction> findWholeJurisdictionAndCheckedByRoleId(Long roleId) throws Exception {

        AssertUtil.notNull(roleId,"根据角色的id,查询被选中的权限,角色id为空");
        // 1.查询对应的权限列表
        List<Jurisdiction> wholeJurisdictionList=findWholeJurisdiction();
        //2.查询对应角色被选中权限
        List<Long> JurisdictionIdList = roleJurisdictionRelationService.selectJurisdictionIdList(roleId);
        if(JurisdictionIdList==null){
            String msg = CommonUtil.joinStr("查询全局角色对应的权限关系","数据库操作失败").toString();
            LOGGER.error(msg);
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, msg);
        }
        //3.赋值checked属性
        for (Jurisdiction jurisdiction : wholeJurisdictionList){
            if(JurisdictionIdList.contains(jurisdiction.getId())){
                jurisdiction.setChecked(true);
            }
        }
        return wholeJurisdictionList;

    }

    @Override
    @Transactional
    public List<Jurisdiction> findChannelJurisdictionAndCheckedByRoleId(Long roleId) throws Exception {

        AssertUtil.notNull(roleId,"根据角色的id,查询被选中的权限,角色id为空");
        // 1.查询对应的权限列表
        List<Jurisdiction> wholeJurisdictionList=findWholeJurisdiction();
        //2.查询对应角色被选中权限
        List<Long> JurisdictionIdList = roleJurisdictionRelationService.selectJurisdictionIdList(roleId);
        if(JurisdictionIdList==null){
            String msg = CommonUtil.joinStr("查询全局角色对应的权限关系","数据库操作失败").toString();
            LOGGER.error(msg);
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION, msg);
        }
        //3.赋值checked属性
        for (Jurisdiction jurisdiction : wholeJurisdictionList){
            if(JurisdictionIdList.contains(jurisdiction.getId())){
                jurisdiction.setChecked(true);
            }
        }
        return wholeJurisdictionList;

    }
}
