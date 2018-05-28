package org.trc.biz.impl.impower;

import com.alibaba.fastjson.JSON;
import com.tairanchina.csp.foundation.sdk.CSPKernelSDK;
import com.tairanchina.csp.foundation.sdk.dto.UserInfoDTO;
import com.tairanchina.csp.foundation.sdk.enumeration.UserInfoQueryType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.impower.IAclWmsUserAccreditInfoBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.*;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.*;
import org.trc.exception.ParamValidException;
import org.trc.exception.UserAccreditInfoException;
import org.trc.form.impower.WmsUserAccreditInfoForm;
import org.trc.service.config.ILogInfoService;
import org.trc.service.impower.IAclUserWarehouseRelationService;
import org.trc.service.impower.IAclWmsUserAccreditInfoService;
import org.trc.service.impower.IAclWmsUserResourceRelationService;
import org.trc.service.impower.IWmsResourceService;
import org.trc.service.util.IUserNameUtilService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.CommonConfigUtil;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.regex.Pattern;

@Service("aclWmsUserAccreditInfoBiz")
public class AclWmsUserAccreditInfoBiz implements IAclWmsUserAccreditInfoBiz {

    private Logger LOGGER = LoggerFactory.getLogger(AclWmsUserAccreditInfoBiz.class);

    @Autowired
    private IAclWmsUserAccreditInfoService aclWmsUserAccreditInfoService;
    @Autowired
    private IAclWmsUserResourceRelationService aclWmsUserResourceRelationService;
    @Autowired
    private IAclUserWarehouseRelationService aclUserWarehouseRelationService;
    @Autowired
    private IWarehouseInfoService warehouseInfoService;
    @Autowired
    private IUserNameUtilService userNameUtilService;
    @Autowired
    private IWmsResourceService wmsResourceService;
    @Autowired
    private ILogInfoService logInfoService;
    @Value("${apply.id}")
    private String applyId;

    @Value("${apply.secret}")
    private String applySecret;

    @Value("${apply.uri}")
    private String applyUri;

    /**
     * 正则表达式：验证手机号
     */
    private static final String REGEX_MOBILE = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";


    @Override
    public Pagenation<AclWmsUserAccreditInfo> wmsUserAccreditInfoPage(WmsUserAccreditInfoForm form, Pagenation<AclWmsUserAccreditInfo> page) {
        Example example = new Example(AclWmsUserAccreditInfo.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(form.getName())) {
            criteria.andLike("name", "%" + form.getName() + "%");
        }
        if (StringUtils.isNotBlank(form.getPhone())) {
            criteria.andLike("phone", "%" + form.getPhone() + "%");
        }
        if (StringUtils.isNotBlank(form.getIsValid())) {
            criteria.andEqualTo("isValid", form.getIsValid());
        }
        if (StringUtils.isNotBlank(form.getWarehouseName())) {
            setWarehouseQuery(form, criteria);
        }
        example.orderBy("updateTime").desc();
        Pagenation<AclWmsUserAccreditInfo> pagenation = aclWmsUserAccreditInfoService.pagination(example, page, form);
        List<AclWmsUserAccreditInfo> pageNationResult = pagenation.getResult();

        if (!AssertUtil.collectionIsEmpty(pageNationResult)) {
            handPageResult(pageNationResult);
        }
        pagenation.setResult(pageNationResult);
        return pagenation;
    }

    /**
     * 设置仓库名称,资源名称
     *
     * @param pageNationResult
     */
    private void handPageResult(List<AclWmsUserAccreditInfo> pageNationResult) {
        userNameUtilService.handleUserName(pageNationResult);
        for (AclWmsUserAccreditInfo wmsUserAccredit : pageNationResult) {
            List<String> warehouseNameList = new ArrayList<>();
            List<String> wmsResourceList = new ArrayList<>();
            //设置自营仓库名称
            //1.通过用户查询用户,仓库关联表
            AclUserWarehouseRelation warehouseRelation = new AclUserWarehouseRelation();
            warehouseRelation.setUserId(wmsUserAccredit.getUserId());
            warehouseRelation.setUserAccreditId(wmsUserAccredit.getId());
            List<AclUserWarehouseRelation> aclUserWarehouseRelationList = aclUserWarehouseRelationService.select(warehouseRelation);
            //2.通关查询到仓库编码去查询仓库表,获取仓库名称
            if (!AssertUtil.collectionIsEmpty(aclUserWarehouseRelationList)) {
                Set<String> warehouseCodeSet = new HashSet<>();
                for (AclUserWarehouseRelation warehouseRelationItem : aclUserWarehouseRelationList) {
                    warehouseCodeSet.add(warehouseRelationItem.getWarehouseCode());
                }
                Example exampleWarehouseItemInfo = new Example(WarehouseInfo.class);
                Example.Criteria criteriaWarehouseItemInfo = exampleWarehouseItemInfo.createCriteria();
                criteriaWarehouseItemInfo.andIn("code", warehouseCodeSet);
                criteriaWarehouseItemInfo.andEqualTo("operationalNature", OperationalNatureEnum.SELF_SUPPORT.getCode());
                List<WarehouseInfo> warehouseInfoList = warehouseInfoService.selectByExample(exampleWarehouseItemInfo);
                //记录仓库名称
                if (!AssertUtil.collectionIsEmpty(warehouseInfoList)) {
                    for (WarehouseInfo warehouseInfo : warehouseInfoList) {
                        warehouseNameList.add(warehouseInfo.getWarehouseName());
                    }
                    wmsUserAccredit.setWarehouseName(StringUtils.join(warehouseNameList, SupplyConstants.Symbol.COMMA));
                }
            }
            //设置关联的资源权限名称
            AclWmsUserResourceRelation wmsUserResourceRelation = new AclWmsUserResourceRelation();
            wmsUserResourceRelation.setWmsUserAccreditId(wmsUserAccredit.getId());
            wmsUserResourceRelation.setWmsUserId(wmsUserAccredit.getUserId());
            List<AclWmsUserResourceRelation> wmsUserResourceRelationList = aclWmsUserResourceRelationService.select(wmsUserResourceRelation);
            if (!AssertUtil.collectionIsEmpty(wmsUserResourceRelationList)) {
                Set<Long> resourceCodeSet = new HashSet<>();
                for (AclWmsUserResourceRelation wmsResourceRelation : wmsUserResourceRelationList) {
                    resourceCodeSet.add(wmsResourceRelation.getResourceCode());
                }
                Example exampleWmsResource = new Example(WmsResource.class);
                Example.Criteria criteriaWmsResource = exampleWmsResource.createCriteria();
                criteriaWmsResource.andIn("code", resourceCodeSet);
                List<WmsResource> wmsResources = wmsResourceService.selectByExample(exampleWmsResource);
                if (!AssertUtil.collectionIsEmpty(wmsResources)) {
                    for (WmsResource wmsResource : wmsResources) {
                        wmsResourceList.add(wmsResource.getName());
                    }
                    wmsUserAccredit.setResourceName(StringUtils.join(wmsResourceList, SupplyConstants.Symbol.COMMA));
                }
            }
        }
    }

    private void setWarehouseQuery(WmsUserAccreditInfoForm form, Example.Criteria criteria) {
        Example exampleWarehouseItemInfo = new Example(WarehouseInfo.class);
        Example.Criteria criteriaWarehouseItemInfo = exampleWarehouseItemInfo.createCriteria();
        criteriaWarehouseItemInfo.andEqualTo("code", form.getWarehouseName());
        criteriaWarehouseItemInfo.andEqualTo("operationalNature", OperationalNatureEnum.SELF_SUPPORT.getCode());
        List<WarehouseInfo> warehouseItemInfoList = warehouseInfoService.selectByExample(exampleWarehouseItemInfo);
        if (!AssertUtil.collectionIsEmpty(warehouseItemInfoList)) {
            List<String> warehouseCodeList = new ArrayList<>();
            for (WarehouseInfo warehouseInfo : warehouseItemInfoList) {
                warehouseCodeList.add(warehouseInfo.getCode());
            }
            Example exampleWarehouseRelation = new Example(AclUserWarehouseRelation.class);
            Example.Criteria criteriaWarehouseRelation = exampleWarehouseRelation.createCriteria();
            criteriaWarehouseRelation.andIn("warehouseCode", warehouseCodeList);
            List<AclUserWarehouseRelation> aclWmsUserResourceRelationList = aclUserWarehouseRelationService.selectByExample(exampleWarehouseRelation);
            if (!AssertUtil.collectionIsEmpty(aclWmsUserResourceRelationList)) {
                Set<Long> userAccreditIdList = new HashSet<>();
                for (AclUserWarehouseRelation warehouseRelation : aclWmsUserResourceRelationList) {
                    userAccreditIdList.add(warehouseRelation.getUserAccreditId());
                }
                criteria.andIn("id", userAccreditIdList);
            } else {
                criteria.andEqualTo("id", StringUtils.EMPTY);
            }
        } else {
            criteria.andEqualTo("id", StringUtils.EMPTY);
        }
    }

    /**
     * 新增用户
     *
     * @param aclWmsUserAccreditInfo
     * @param aclUserAccreditInfoContext
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveAclWmsUserAccreditInfo(AclWmsUserAccreditInfo aclWmsUserAccreditInfo, AclUserAccreditInfo aclUserAccreditInfoContext) {
        //校验必填字段
        checkWmsUserMessage(aclWmsUserAccreditInfo);
        if (!Pattern.matches(REGEX_MOBILE, aclWmsUserAccreditInfo.getPhone())) {
            String msg = "手机号格式错误," + aclWmsUserAccreditInfo.getPhone();
            LOGGER.error(msg);
            throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACCREDIT_SAVE_EXCEPTION, msg);
        }
        //手机号用户中心校验
        String userId = checkWmsPhone(aclWmsUserAccreditInfo.getPhone());
        //保存基本信息
        AclWmsUserAccreditInfo wmsUserAccreditInfo = new AclWmsUserAccreditInfo();
        wmsUserAccreditInfo.setName(aclWmsUserAccreditInfo.getName());
        wmsUserAccreditInfo.setPhone(aclWmsUserAccreditInfo.getPhone());
        wmsUserAccreditInfo.setRemark(aclWmsUserAccreditInfo.getRemark());
        wmsUserAccreditInfo.setIsValid(aclWmsUserAccreditInfo.getIsValid());
        wmsUserAccreditInfo.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        wmsUserAccreditInfo.setUserId(userId);
        wmsUserAccreditInfo.setCreateOperator(aclUserAccreditInfoContext.getUserId());
        aclWmsUserAccreditInfoService.insert(wmsUserAccreditInfo);
        //保存仓库管理信息
        List<String> warehouseCode = Arrays.asList(StringUtils.split(aclWmsUserAccreditInfo.getWarehouseCode(), SupplyConstants.Symbol.COMMA));
        //仓库编码
        if (!AssertUtil.collectionIsEmpty(warehouseCode)) {
            saveWarehouseRelation(aclUserAccreditInfoContext, wmsUserAccreditInfo, warehouseCode);
        } else {
            String msg = "用户关联仓库编码错误!";
            LOGGER.error(msg);
            throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACCREDIT_SAVE_EXCEPTION, msg);
        }
        //用户权限
        List<String> resourceCodeList = Arrays.asList(StringUtils.split(aclWmsUserAccreditInfo.getResourceCode(), SupplyConstants.Symbol.COMMA));
        if (!AssertUtil.collectionIsEmpty(resourceCodeList)) {
            List<AclWmsUserResourceRelation> wmsUserResourceRelationList = new ArrayList<>();
            saveResource(aclUserAccreditInfoContext, wmsUserAccreditInfo, resourceCodeList, wmsUserResourceRelationList);
        } else {
            String msg = "用户关联资源编码错误!";
            LOGGER.error(msg);
            throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACCREDIT_SAVE_EXCEPTION, msg);
        }
        //记录日志
        String userIdLog = aclUserAccreditInfoContext.getUserId();
        AssertUtil.notBlank(userIdLog, "获取当前登录的userId失败");
        logInfoService.recordLog(wmsUserAccreditInfo, String.valueOf(wmsUserAccreditInfo.getId()), userIdLog, LogOperationEnum.ADD.getMessage(), null, null);
    }

    /**
     * 编辑用户
     *
     * @param aclWmsUserAccreditInfo
     * @param aclUserAccreditInfoContext
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateAclWmsUserAccreditInfo(AclWmsUserAccreditInfo aclWmsUserAccreditInfo, AclUserAccreditInfo aclUserAccreditInfoContext) {
        AssertUtil.notNull(aclWmsUserAccreditInfo, "用户信息不能为空");
        AssertUtil.notNull(aclWmsUserAccreditInfo.getId(), "用户Id不能为空");
        //保存基本信息
        AclWmsUserAccreditInfo wmsUserAccreditInfo = new AclWmsUserAccreditInfo();
        wmsUserAccreditInfo.setId(aclWmsUserAccreditInfo.getId());
        wmsUserAccreditInfo.setName(aclWmsUserAccreditInfo.getName());
        wmsUserAccreditInfo.setRemark(aclWmsUserAccreditInfo.getRemark());
        wmsUserAccreditInfo.setIsValid(aclWmsUserAccreditInfo.getIsValid());
        wmsUserAccreditInfo.setUpdateTime(Calendar.getInstance().getTime());
        int count = aclWmsUserAccreditInfoService.updateByPrimaryKeySelective(wmsUserAccreditInfo);
        if (count == 0) {
            String msg = String.format("修改授权%s数据库操作失败", JSON.toJSONString(wmsUserAccreditInfo));
            LOGGER.error(msg);
            throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
        }
        wmsUserAccreditInfo = aclWmsUserAccreditInfoService.selectByPrimaryKey(wmsUserAccreditInfo.getId());
        //修改仓库关联
        //保存仓库管理信息
        if (StringUtils.isNotBlank(aclWmsUserAccreditInfo.getWarehouseCode())) {
            List<String> warehouseCode = Arrays.asList(StringUtils.split(aclWmsUserAccreditInfo.getWarehouseCode(), SupplyConstants.Symbol.COMMA));
            //仓库编码
            if (!AssertUtil.collectionIsEmpty(warehouseCode)) {
                //删除当前关联
                Example example = new Example(AclUserWarehouseRelation.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("userId", wmsUserAccreditInfo.getUserId());
                criteria.andEqualTo("userAccreditId", wmsUserAccreditInfo.getId());
                aclUserWarehouseRelationService.deleteByExample(example);
                saveWarehouseRelation(aclUserAccreditInfoContext, wmsUserAccreditInfo, warehouseCode);
            } else {
                String msg = "用户关联仓库编码错误!";
                LOGGER.error(msg);
                throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACCREDIT_SAVE_EXCEPTION, msg);
            }
        }
        //修改资源关联
        //用户权限
        if (StringUtils.isNotBlank(aclWmsUserAccreditInfo.getResourceCode())) {
            List<String> resourceCodeList = Arrays.asList(StringUtils.split(aclWmsUserAccreditInfo.getResourceCode(), SupplyConstants.Symbol.COMMA));
            if (!AssertUtil.collectionIsEmpty(resourceCodeList)) {
                //删除当前关联
                Example example = new Example(AclWmsUserResourceRelation.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("wmsUserId", wmsUserAccreditInfo.getUserId());
                criteria.andEqualTo("wmsUserAccreditId", wmsUserAccreditInfo.getId());
                aclWmsUserResourceRelationService.deleteByExample(example);
                List<AclWmsUserResourceRelation> wmsUserResourceRelationList = new ArrayList<>();
                saveResource(aclUserAccreditInfoContext, wmsUserAccreditInfo, resourceCodeList, wmsUserResourceRelationList);
            } else {
                String msg = "用户关联资源编码错误!";
                LOGGER.error(msg);
                throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACCREDIT_SAVE_EXCEPTION, msg);
            }
        }
        //记录日志
        String userId = aclUserAccreditInfoContext.getUserId();
        AssertUtil.notBlank(userId, "获取当前登录的userId失败");
        logInfoService.recordLog(aclWmsUserAccreditInfo, String.valueOf(aclWmsUserAccreditInfo.getId()), userId, LogOperationEnum.UPDATE.getMessage(), null, null);

    }

    /**
     * 修改wms用户状态
     *
     * @param aclWmsUserAccreditInfo
     * @param aclUserAccreditInfoContext
     */
    @Override
    public void updateAclWmsUserAccreditInfoState(AclWmsUserAccreditInfo aclWmsUserAccreditInfo, AclUserAccreditInfo aclUserAccreditInfoContext) {
        AssertUtil.notNull(aclWmsUserAccreditInfo, "WMS授权管理模块修改授权信息失败，授权信息为空");
        AssertUtil.notNull(aclWmsUserAccreditInfo.getId(), "需要修改的用户ID为空!");
        AclWmsUserAccreditInfo updateAclUserAccreditInfo = new AclWmsUserAccreditInfo();
        updateAclUserAccreditInfo.setId(aclWmsUserAccreditInfo.getId());
        String state;
        if (aclWmsUserAccreditInfo.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updateAclUserAccreditInfo.setIsValid(ValidEnum.NOVALID.getCode());
            state = ValidEnum.NOVALID.getName();
        } else {
            updateAclUserAccreditInfo.setIsValid(ValidEnum.VALID.getCode());
            state = ValidEnum.VALID.getName();
        }
        updateAclUserAccreditInfo.setUpdateTime(Calendar.getInstance().getTime());
        int count = aclWmsUserAccreditInfoService.updateByPrimaryKeySelective(updateAclUserAccreditInfo);
        if (count == 0) {
            String msg = String.format("修改WMS授权%s数据库操作失败", JSON.toJSONString(aclWmsUserAccreditInfo));
            LOGGER.error(msg);
            throw new UserAccreditInfoException(ExceptionEnum.SYSTEM_ACCREDIT_UPDATE_EXCEPTION, msg);
        }
        String userId = aclUserAccreditInfoContext.getUserId();
        AssertUtil.notBlank(userId, "获取当前登录的userId失败");
        logInfoService.recordLog(aclWmsUserAccreditInfo, String.valueOf(aclWmsUserAccreditInfo.getId()), userId, "修改", "状态改为" + state, null);

    }

    @Override
    public AclWmsUserAccreditInfo queryAclWmsUserAccreditInfo(Long id) {
        AssertUtil.notNull(id, "查询用户信息时,用户主键Id不能为空!");
        AclWmsUserAccreditInfo aclWmsUserAccreditInfo = aclWmsUserAccreditInfoService.selectByPrimaryKey(id);
        AssertUtil.notNull(aclWmsUserAccreditInfo, "根据主键查询用户信息为空!");
        aclWmsUserAccreditInfo.setWmsResourceList(queryResource(id));
        aclWmsUserAccreditInfo.setWarehouseInfoList(queryWarehouseInfo(id));
        return aclWmsUserAccreditInfo;
    }

    private void saveResource(AclUserAccreditInfo aclUserAccreditInfoContext, AclWmsUserAccreditInfo wmsUserAccreditInfo, List<String> resourceCodeList, List<AclWmsUserResourceRelation> wmsUserResourceRelationList) {
        for (String code : resourceCodeList) {
            AclWmsUserResourceRelation wmsUserResourceRelation = new AclWmsUserResourceRelation();
            wmsUserResourceRelation.setResourceCode(Long.parseLong(code));
            wmsUserResourceRelation.setWmsUserAccreditId(wmsUserAccreditInfo.getId());
            wmsUserResourceRelation.setWmsUserId(wmsUserAccreditInfo.getUserId());
            wmsUserResourceRelation.setCreateOperator(aclUserAccreditInfoContext.getUserId());
            wmsUserResourceRelationList.add(wmsUserResourceRelation);
        }
        aclWmsUserResourceRelationService.insertList(wmsUserResourceRelationList);
    }

    /**
     * 保存仓库关联
     *
     * @param aclUserAccreditInfoContext
     * @param wmsUserAccreditInfo
     * @param warehouseCode
     */
    private void saveWarehouseRelation(AclUserAccreditInfo aclUserAccreditInfoContext, AclWmsUserAccreditInfo wmsUserAccreditInfo, List<String> warehouseCode) {
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("code", warehouseCode);
        criteria.andEqualTo("operationalNature", ZeroToNineEnum.ONE.getCode());
        List<WarehouseInfo> warehouseInfoList = warehouseInfoService.selectByExample(example);
        AssertUtil.notEmpty(warehouseInfoList, "仓库编码对应自营仓库信息为空");
        List<String> errorCode = new ArrayList<>();
        List<AclUserWarehouseRelation> userWarehouseRelationList = new ArrayList<>();
        for (String code : warehouseCode) {
            boolean flag = false;
            for (WarehouseInfo warehouseInfo : warehouseInfoList) {
                if (StringUtils.equals(code, warehouseInfo.getCode())) {
                    flag = true;
                    AclUserWarehouseRelation userWarehouseRelation = new AclUserWarehouseRelation();
                    userWarehouseRelation.setCreateOperator(aclUserAccreditInfoContext.getUserId());
                    userWarehouseRelation.setWarehouseCode(code);
                    userWarehouseRelation.setWarehouseId(warehouseInfo.getId());
                    userWarehouseRelation.setUserAccreditId(wmsUserAccreditInfo.getId());
                    userWarehouseRelation.setIsValid(ValidEnum.VALID.getCode());
                    userWarehouseRelation.setUserId(wmsUserAccreditInfo.getUserId());
                    userWarehouseRelationList.add(userWarehouseRelation);
                    break;
                }
            }
            if (!flag) {
                errorCode.add(code);
            }
        }
        AssertUtil.isTrue(errorCode.size() <= 0, "[" + StringUtils.join(errorCode) + "],对应符合条件的仓库不存在!");
        //保存仓库关联信息
        if (!AssertUtil.collectionIsEmpty(userWarehouseRelationList)) {
            aclUserWarehouseRelationService.insertList(userWarehouseRelationList);
        }
    }


    /**
     * WMS-校验手机号
     * 3.0
     *
     * @param phone
     * @return
     * @throws Exception
     */
    @Override
    public String checkWmsPhone(String phone) {
        AssertUtil.notBlank(phone, "校验手机号时输入参数phone为空");
        CSPKernelSDK sdk = CommonConfigUtil.getCSPKernelSDK(applyUri,applyId,applySecret);
        UserInfoDTO userDO= null;
        try {
            userDO = sdk.user.singleGetUserInfo(UserInfoQueryType.USER_PHONE,phone);
        } catch (Exception e) {
            LOGGER.error("从用户中心根据手机号获取用户信息异常！",e);
        }
        AssertUtil.notNull(userDO, "该手机号尚未在泰然城注册！");
        AclWmsUserAccreditInfo aclWmsUserAccreditInfo = new AclWmsUserAccreditInfo();
        aclWmsUserAccreditInfo.setPhone(phone);
        List<AclWmsUserAccreditInfo> aclWmsUserAccreditInfos = aclWmsUserAccreditInfoService.select(aclWmsUserAccreditInfo);
        if (!AssertUtil.collectionIsEmpty(aclWmsUserAccreditInfos)) {
            String msg = "该手机号已存在！";
            LOGGER.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        return userDO.getUserId();
    }

    @Override
    public List<WmsResource> queryResource(Long Id) {
        //1.查询所有的资源
        Example example = new Example(WmsResource.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIsNotNull("id");
        List<WmsResource> wmsResourceList = wmsResourceService.selectByExample(example);
        AssertUtil.notEmpty(wmsResourceList, "查询所有仓级资源为空!");
        if (null == Id) {
            return wmsResourceList;
        }
        //1.查询用户资源关联表
        AclWmsUserResourceRelation aclWmsUserResourceRelation = new AclWmsUserResourceRelation();
        aclWmsUserResourceRelation.setWmsUserAccreditId(Id);
        List<AclWmsUserResourceRelation> aclWmsUserResourceRelationList = aclWmsUserResourceRelationService.select(aclWmsUserResourceRelation);
        AssertUtil.notEmpty(aclWmsUserResourceRelationList, "根据用户ID" + Id + "查询关联信息为空!");
        for (WmsResource wmsResource : wmsResourceList) {
            boolean flag = false;
            for (AclWmsUserResourceRelation relation : aclWmsUserResourceRelationList) {
                if (wmsResource.getCode().equals(relation.getResourceCode())) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                wmsResource.setCheck("true");
            }
        }
        return wmsResourceList;
    }

    @Override
    public List<WarehouseInfo> queryWarehouseInfo(Long Id) {
        if (null != Id) {
            //查询仓库用户关联表
            AclUserWarehouseRelation aclUserWarehouseRelation = new AclUserWarehouseRelation();
            aclUserWarehouseRelation.setUserAccreditId(Id);
            List<AclUserWarehouseRelation> aclUserWarehouseRelationList = aclUserWarehouseRelationService.select(aclUserWarehouseRelation);
            AssertUtil.notEmpty(aclUserWarehouseRelationList, "用户关联的仓库为空!");
            Set<String> warehouseCodeSet = new HashSet<>();
            for (AclUserWarehouseRelation warehouseRelation : aclUserWarehouseRelationList) {
                warehouseCodeSet.add(warehouseRelation.getWarehouseCode());
            }
            Example example = new Example(WarehouseInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("code", warehouseCodeSet);
            List<WarehouseInfo> warehouseInfoList = warehouseInfoService.selectByExample(example);
            AssertUtil.notEmpty(warehouseInfoList, "根据编码查询仓库信息为空!");
            return warehouseInfoList;
        } else {
            //查询出所有自营仓库
            Example exampleWarehouseItemInfo = new Example(WarehouseInfo.class);
            Example.Criteria criteriaWarehouseItemInfo = exampleWarehouseItemInfo.createCriteria();
            criteriaWarehouseItemInfo.andEqualTo("operationalNature", OperationalNatureEnum.SELF_SUPPORT.getCode());
            List<WarehouseInfo> warehouseInfoList = warehouseInfoService.selectByExample(exampleWarehouseItemInfo);
            AssertUtil.notEmpty(warehouseInfoList, "未查询到自营仓库");
            return warehouseInfoList;
        }
    }


    /**
     * 校验必填字段
     *
     * @param aclWmsUserAccreditInfo
     */
    private void checkWmsUserMessage(AclWmsUserAccreditInfo aclWmsUserAccreditInfo) {
        AssertUtil.notNull(aclWmsUserAccreditInfo, "用户信息不能为空!");
        AssertUtil.notBlank(aclWmsUserAccreditInfo.getName(), "用户名称不能为空!");
        AssertUtil.notBlank(aclWmsUserAccreditInfo.getWarehouseCode(), "所属自营仓编码不能为空!");
        AssertUtil.notBlank(aclWmsUserAccreditInfo.getResourceCode(), "用户权限不能为空!");
        AssertUtil.notBlank(aclWmsUserAccreditInfo.getIsValid(), "起停用状态不能为空!");
    }
}
