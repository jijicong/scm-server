package org.trc.biz.impl.impower;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.impower.IRoleJurisdictionRelationBiz;
import org.trc.domain.impower.RoleJurisdictionRelation;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ValidEnum;
import org.trc.exception.ConfigException;
import org.trc.exception.ParamValidException;
import org.trc.service.impower.IRoleJurisdictionRelationService;
import org.trc.util.AssertUtil;
import org.trc.util.CommonUtil;
import org.trc.util.ParamsUtil;
import org.trc.util.StringUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sone on 2017/5/16.
 */
@Service("roleJurisdictionRelationBiz")
public class RoleJurisdictionRelationBiz implements IRoleJurisdictionRelationBiz{

    private final static Logger LOGGER = LoggerFactory.getLogger(RoleJurisdictionRelationBiz.class);

    @Resource
    private IRoleJurisdictionRelationService roleJuridictionRelationService;

    @Override
    @Transactional
    public void updateRoleJurisdictionRelations(String roleJurisdiction, Long roleId) throws Exception {

        AssertUtil.notNull(roleId,"角色和权限关联保存失败，角色id为空");
        //1.先根据角色id，删除所有的该角色对应的权限
        //roleJuridictionRelationService.deleteByRoleId(roleId);
        //2.保存关联信息
        saveRoleJurisdictionRelations(roleJurisdiction,roleId);

    }

    @Override
    public int saveRoleJurisdictionRelations(String roleJurisdiction, Long roleId) throws Exception{

        AssertUtil.notNull(roleId,"角色和权限关联保存失败，角色id为空");
        if(StringUtils.isBlank(roleJurisdiction)){
            String msg = CommonUtil.joinStr("根据权限id,角色和权限关联保存失败,参数name[]为空").toString();
            LOGGER.error(msg);
            throw  new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        Long[]  roleJurisdictions=StringUtil.splitByComma(roleJurisdiction);
        List<RoleJurisdictionRelation>  roleJurisdictionRelationList=new ArrayList<>();
        for (Long roleJurisdictionLong:roleJurisdictions ){
            RoleJurisdictionRelation roleJurisdictionRelation=new RoleJurisdictionRelation();
            roleJurisdictionRelation.setRoleId(roleId);
            roleJurisdictionRelation.setJurisdictionId(roleJurisdictionLong);
            roleJurisdictionRelation.setIsValid(ValidEnum.VALID.getCode());
            ParamsUtil.setBaseDO(roleJurisdictionRelation);
            roleJurisdictionRelationList.add(roleJurisdictionRelation);
        }
        int count=0;
        count=roleJuridictionRelationService.insertList(roleJurisdictionRelationList);
        if(count==0){
            String msg = CommonUtil.joinStr("保存角色和权限关系",  "数据库操作失败").toString();
            LOGGER.error(msg);
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_SAVE_EXCEPTION, msg);
        }
        return count;
    }
}
