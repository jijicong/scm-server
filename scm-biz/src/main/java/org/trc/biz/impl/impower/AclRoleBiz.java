package org.trc.biz.impl.impower;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.impower.IAclRoleBiz;
import org.trc.biz.impower.IAclRoleResourceRelationBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclRole;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.LogOperationEnum;
import org.trc.enums.ValidEnum;
import org.trc.enums.remarkEnum;
import org.trc.exception.ConfigException;
import org.trc.exception.RoleException;
import org.trc.form.impower.RoleForm;
import org.trc.service.config.ILogInfoService;
import org.trc.service.impower.IAclRoleService;
import org.trc.service.impower.IAclUserAccreditRoleRelationService;
import org.trc.service.util.IUserNameUtilService;
import org.trc.util.AssertUtil;
import org.trc.util.CommonUtil;
import org.trc.util.Pagenation;
import org.trc.util.ParamsUtil;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sone on 2017/5/11.
 */
@Service("roleBiz")
public class AclRoleBiz implements IAclRoleBiz {

    private Logger  LOGGER = LoggerFactory.getLogger(AclRoleBiz.class);

   // private final static Long SYS_ROLE_ID=1L; //系统角色的id wholeJurisdiction

    private final static String WHOLE_TYPE ="wholeJurisdiction";//全局角色
    @Resource
    private IAclRoleService roleService;
    @Resource
    private IUserNameUtilService userNameUtilService;
    @Resource
    private IAclRoleResourceRelationBiz roleJurisdictionRelationBiz;
    @Resource
    private IAclUserAccreditRoleRelationService userAccreditInfoRoleRelationService;
    @Resource
    private ILogInfoService logInfoService;


    @Override
    public AclRole findRoleById(Long roleId){
        /*
         根据id查询角色对象
         */
        AssertUtil.notNull(roleId,"根据角色id，查询角色，角色的id为空");
        AclRole aclRole = new AclRole();
        aclRole.setId(roleId);
        AclRole queryAclRole = roleService.selectOne(aclRole);
        AssertUtil.notNull(queryAclRole,String.format("根据主键ID[id=%s]查询角色为空",roleId.toString()));
        return queryAclRole;

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateRoleState(AclRole aclRole, ContainerRequestContext requestContext){

        AssertUtil.notNull(aclRole,"根据角色对象，修改角色的状态，角色对象为空");

        AclRole _aclRole = new AclRole();
        _aclRole.setName("采购组员");
        _aclRole = roleService.selectOne(_aclRole);
        AssertUtil.notNull(_aclRole,"查询系统角色,采购组员失败!");

        AclRole updateAclRole = new AclRole();
        if(aclRole.getId()==_aclRole.getId()){ //防止恶意修改系统角色的状态
            String tip="系统角色的状态不能被修改";
            LOGGER.error(tip);
            throw  new RoleException(ExceptionEnum.SYSTEM_SYS_ROLE_STATE_UPDATE_EXCEPTION,tip);
        }
        updateAclRole.setId(aclRole.getId());
        String remark = null;
        if (aclRole.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updateAclRole.setIsValid(ValidEnum.NOVALID.getCode());
            remark = remarkEnum.VALID_OFF.getMessage();
        } else {
            updateAclRole.setIsValid(ValidEnum.VALID.getCode());
            remark = remarkEnum.VALID_ON.getMessage();
        }
        updateAclRole.setUpdateTime(Calendar.getInstance().getTime());
        int count = roleService.updateByPrimaryKeySelective(updateAclRole);
        if (count == 0) {
            String msg = String.format("修改角色%s数据库操作失败",JSON.toJSONString(aclRole));
            LOGGER.error(msg);
            throw new RoleException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
        }
        //修改关联状态
        Map<String, Object> map=new HashMap<>();
        map.put("status", updateAclRole.getIsValid());
        map.put("roleId", updateAclRole.getId());
        userAccreditInfoRoleRelationService.updateStatusByRoleId(map);

        String userId= (String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        logInfoService.recordLog(aclRole,aclRole.getId().toString(),userId,LogOperationEnum.UPDATE.getMessage(),remark,null);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int findNumFromRoleAndAccreditInfoByRoleId(Long roleId){

        AssertUtil.notNull(roleId,"根据角色的id查询用户的数量，角色id为空");
        int num = roleService.findNumFromRoleAndAccreditInfoByRoleId(roleId);
        return num;

    }

    @Override
    public Pagenation<AclRole> rolePage(RoleForm form, Pagenation<AclRole> page) {

        Example example=new Example(AclRole.class);
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
        Pagenation<AclRole> pagination = roleService.pagination(example,page,form);
        userNameUtilService.handleUserName(pagination.getResult());
        return pagination;

    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateRole(AclRole aclRole, String roleJurisdiction,ContainerRequestContext requestContext){

        //判断是否是系统用户,系统用户只能修改，系统角色类型，对应的权限,和备注信息
        AssertUtil.notNull(aclRole,"角色更新时,角色对象为空");
        AclRole tmp = findRoleByName(aclRole.getName());
        if(tmp!=null){
            if(!tmp.getId().equals(aclRole.getId())){
                throw new RoleException(ExceptionEnum.SYSTEM_SYS_ROLE_STATE_UPDATE_EXCEPTION, "其它的角色已经使用该角色名称");
            }
        }

        AclRole enAclRole = new AclRole();
        enAclRole.setName("采购组员");
        enAclRole = roleService.selectOne(enAclRole);
        AssertUtil.notNull(enAclRole,"查询系统角色,采购组员失败!");

        if(aclRole.getId() == enAclRole.getId()){//为渠道用户
            if(aclRole.getRoleType()==WHOLE_TYPE){//渠道用户,反而传的是全局的类型
                String msg = CommonUtil.joinStr("修改渠道角色,角色类型不匹配").toString();
                LOGGER.error(msg);
                throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
            }//传的权限id不做校验
            aclRole.setName("采购组员");
            aclRole.setRoleType("channelJurisdiction");
            aclRole.setIsValid(ValidEnum.VALID.getCode());//为防止对不需要改变的值，做修改
        }
        AclRole _aclRole = roleService.selectByPrimaryKey(aclRole.getId());
        String remark = null;
        if(!_aclRole.getIsValid().equals(aclRole.getIsValid())){
            if(aclRole.getIsValid().equals(ValidEnum.VALID.getCode())){
                remark=remarkEnum.VALID_ON.getMessage();
            }else{
                remark=remarkEnum.VALID_OFF.getMessage();
            }
        }

        aclRole.setUpdateTime(Calendar.getInstance().getTime());
        int count = roleService.updateByPrimaryKeySelective(aclRole);
        if (count == 0) {
            String msg = String.format("修改角色%s数据库操作失败",JSON.toJSONString(aclRole));
            LOGGER.error(msg);
            throw new RoleException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
        }

        roleJurisdictionRelationBiz.updateRoleJurisdictionRelations(roleJurisdiction, aclRole.getId());
        Map<String, Object> map=new HashMap<>();
        map.put("status", aclRole.getIsValid());
        map.put("roleId", aclRole.getId());
        userAccreditInfoRoleRelationService.updateStatusByRoleId(map);

        String userId= (String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        AclRole aclRole1 = new AclRole();
        aclRole1.setCreateTime(aclRole.getCreateTime());
        logInfoService.recordLog(aclRole1,aclRole.getId().toString(),userId,LogOperationEnum.UPDATE.getMessage(),remark,null);

    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveRole(AclRole aclRole, String roleJurisdiction, ContainerRequestContext requestContext){
        AssertUtil.notNull(aclRole,"角色管理模块保存角色信息失败，角色信息为空");
        AclRole tmp = findRoleByName(aclRole.getName());
        AssertUtil.isNull(tmp,String.format("角色名称[name=%s]的名称已存在,请使用其他名称", aclRole.getName()));
        int count=0;
        ParamsUtil.setBaseDO(aclRole);
        aclRole.setCreateOperator((String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID));
        count=roleService.insert(aclRole);
        if(count==0){
            String msg = String.format("保存角色%s数据库操作失败", JSON.toJSONString(aclRole));
            LOGGER.error(msg);
            throw new ConfigException(ExceptionEnum.SYSTEM_ACCREDIT_SAVE_EXCEPTION, msg);
        }
        //用来保存角色和授权的关联信息
        roleJurisdictionRelationBiz.saveRoleJurisdictionRelations(roleJurisdiction, aclRole.getId());
        String userId= (String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        AclRole aclRole1 = new AclRole();
        aclRole1.setCreateTime(aclRole.getCreateTime());
        logInfoService.recordLog(aclRole1,aclRole.getId().toString(),userId, LogOperationEnum.ADD.getMessage(),null,null);

    }
    @Override
    public AclRole findRoleByName(String name){

        AssertUtil.notNull(name,"根据角色名称查询角色的参数name为空");
        AclRole aclRole = new AclRole();
        aclRole.setName(name);
        return roleService.selectOne(aclRole);

    }
}
