package org.trc.biz.impl.impower;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.impower.IRoleBiz;
import org.trc.biz.impower.IRoleJurisdictionRelationBiz;
import org.trc.domain.impower.Role;
import org.trc.domain.impower.RoleExpand;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ValidEnum;
import org.trc.exception.ConfigException;
import org.trc.exception.ParamValidException;
import org.trc.form.impower.RoleForm;
import org.trc.service.impower.IRoleService;
import org.trc.util.AssertUtil;
import org.trc.util.CommonUtil;
import org.trc.util.Pagenation;
import org.trc.util.ParamsUtil;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import java.util.Calendar;

/**
 * Created by sone on 2017/5/11.
 */
@Service("roleBiz")
public class RoleBiz implements IRoleBiz{

    private final static Logger LOGGER = LoggerFactory.getLogger(RoleBiz.class);

    private final static Long SYS_ROLE_ID=1L; //系统角色的id
    @Resource
    private IRoleService roleService;
    @Resource
    private IRoleJurisdictionRelationBiz roleJurisdictionRelationBiz;


    @Override
    public Role findRoleById(Long roleId) throws Exception {
        /*
         根据id查询角色对象
         */
        AssertUtil.notNull(roleId,"根据角色id，查询角色，角色的id为空");
        Role role = new Role();
        role.setId(roleId);
        Role queryRole = roleService.selectOne(role);
        if(null == queryRole) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", roleId.toString(), "]查询角色为空").toString();
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_QUERY_EXCEPTION,msg);
        }
        return queryRole;

    }

    @Override
    public void updateRoleState(Role role) throws Exception {
        AssertUtil.notNull(role,"根据角色对象，修改角色的状态，角色对象为空");
        Role updateRole = new Role();
        if(role.getId()==SYS_ROLE_ID){ //防止恶意修改系统角色的状态
            String tip="系统角色的状态不能被修改";
            LOGGER.error(tip);
            throw  new ConfigException(ExceptionEnum.SYSTEM_SYS_ROLE_STATE_UPDATE_EXCEPTION,tip);
        }
        updateRole.setId(role.getId());
        if (role.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updateRole.setIsValid(ValidEnum.NOVALID.getCode());
            } else {
                updateRole.setIsValid(ValidEnum.VALID.getCode());
            }
        updateRole.setUpdateTime(Calendar.getInstance().getTime());
        int count = roleService.updateByPrimaryKeySelective(updateRole);
        if (count == 0) {
            String msg = CommonUtil.joinStr("修改角色", JSON.toJSONString(role), "数据库操作失败").toString();
            LOGGER.error(msg);
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
        }

    }

    @Override
    @Transactional
    public int findNumFromRoleAndAccreditInfoByRoleId(Long roleId) throws Exception {

        int num = roleService.findNumFromRoleAndAccreditInfoByRoleId(roleId);
        return num;

    }

    @Override
    public Pagenation<Role> rolePage(RoleForm form, Pagenation<Role> page) {
        Example example=new Example(Role.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(form.getName())) {
            criteria.andLike("name", "%" + form.getName() + "%");
        }
        if (StringUtil.isNotEmpty(form.getIsValid())) {
            criteria.andEqualTo("isValid", form.getIsValid());
        }
        if(StringUtil.isNotEmpty(form.getRoleType())){
            criteria.andEqualTo("roleType",form.getRoleType());
        }
        example.orderBy("updateTime").desc();
        return roleService.pagination(example,page,form);
    }

    @Override
    @Transactional
    public int saveRole(Role role,String roleJurisdiction) throws Exception {
        Role tmp = findRoleByName(role.getName());
        if (null != tmp) {
            String msg = CommonUtil.joinStr("角色名称[name=", role.getName(), "]的数据已存在,请使用其他名称").toString();
            LOGGER.error(msg);
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_SAVE_EXCEPTION, msg);
        }
        int count=0;
        ParamsUtil.setBaseDO(role);
        count=roleService.insert(role);
        if(count==0){
            String msg = CommonUtil.joinStr("保存角色", JSON.toJSONString(role), "数据库操作失败").toString();
            LOGGER.error(msg);
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_SAVE_EXCEPTION, msg);
        }
        count+=(roleJurisdictionRelationBiz.saveRoleJurisdictionRelationS(roleJurisdiction,role.getId()));
        return count;
    }
    @Override
    public Role findRoleByName(String name) throws Exception {
        if(StringUtil.isEmpty(name)  || name==null){
            String msg=CommonUtil.joinStr("根据角色名称查询角色的参数name为空").toString();
            LOGGER.error(msg);
            throw  new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        Role role = new Role();
        role.setName(name);
        return roleService.selectOne(role);
    }
}
