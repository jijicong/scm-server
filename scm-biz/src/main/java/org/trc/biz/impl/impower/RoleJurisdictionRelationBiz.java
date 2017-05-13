package org.trc.biz.impl.impower;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trc.biz.impower.IRoleJurisdictionRelationBiz;
import org.trc.domain.impower.RoleJurisdictionRelation;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.ConfigException;
import org.trc.exception.ParamValidException;
import org.trc.service.impower.IRoleJurisdictionRelationService;
import org.trc.util.CommonUtil;
import org.trc.util.ParamsUtil;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sone on 2017/5/12.
 */
@Service("roleJurisdictionRelationBiz")
public class RoleJurisdictionRelationBiz implements IRoleJurisdictionRelationBiz {

    private final static Logger log = LoggerFactory.getLogger(RoleJurisdictionRelationBiz.class);
    @Resource
    private IRoleJurisdictionRelationService roleJurisdictionRelationService;

    @Override
    public int saveRoleJurisdictionRelationS(String ids, Long roleId) throws Exception {
        if(StringUtil.isEmpty(ids)  || ids==null){
            String msg= CommonUtil.joinStr("角色的资源为空").toString();
            log.error(msg);
            throw  new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        Long[] jurisdictionIds=org.trc.util.StringUtil.splitByComma(ids);

        List<RoleJurisdictionRelation> roleJurisdictionRelationList=new ArrayList<>();

        for (Long jurisdictionId: jurisdictionIds) {
            RoleJurisdictionRelation roleJurisdictionRelation=new RoleJurisdictionRelation();
            roleJurisdictionRelation.setJurisdictionId(jurisdictionId);
            roleJurisdictionRelation.setRoleId(roleId);
            roleJurisdictionRelation.setIsValid("1");
            ParamsUtil.setBaseDO(roleJurisdictionRelation);
            roleJurisdictionRelationList.add(roleJurisdictionRelation);
        }
        int count=0;
        count=roleJurisdictionRelationService.insertList(roleJurisdictionRelationList);
        if(count==0){
            String msg = CommonUtil.joinStr("保存角色资源关系", ids, "数据库操作失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_SAVE_EXCEPTION, msg);
        }
        return count;
    }
}
