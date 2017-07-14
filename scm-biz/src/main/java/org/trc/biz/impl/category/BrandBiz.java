package org.trc.biz.impl.category;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.category.IBrandBiz;
import org.trc.biz.qinniu.IQinniuBiz;
import org.trc.biz.trc.ITrcBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Brand;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.*;
import org.trc.exception.CategoryException;
import org.trc.exception.ParamValidException;
import org.trc.form.FileUrl;
import org.trc.form.category.BrandForm;
import org.trc.service.category.IBrandService;
import org.trc.service.category.ICategoryBrandService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.supplier.ISupplierBrandService;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.*;

/**
 * Created by hzqph on 2017/4/28.
 */
@Service("brandBiz")
public class BrandBiz implements IBrandBiz {

    private Logger log = LoggerFactory.getLogger(BrandBiz.class);
    private final static String BRAND_CODE_EX_NAME = "PP";
    private final static int BRAND_CODE_LENGTH = 5;

    @Autowired
    private IBrandService brandService;
    @Autowired
    private IQinniuBiz qinniuBiz;
    @Autowired
    private ISerialUtilService serialUtilService;
    @Autowired
    private ISupplierBrandService supplierBrandService;
    @Autowired
    private IAclUserAccreditInfoService userAccreditInfoService;
    @Autowired
    private ICategoryBrandService categoryBrandService;
    @Autowired
    private ILogInfoService logInfoService;
    @Autowired
    private ITrcBiz trcBiz;
    @Override
    public Pagenation<Brand> brandPage(BrandForm queryModel, Pagenation<Brand> page) throws Exception {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        setQueryParam(example,criteria,queryModel);
        Pagenation<Brand> pagenation = brandService.pagination(example, page, queryModel);
        //得到所有图片的缩略图,并以fileKey为key，url为value的形式封装成map
        List<Brand> brandList = pagenation.getResult();
        if(AssertUtil.collectionIsEmpty(brandList)){
            return pagenation;
        }
        Map<String, String> fileUrlMap = constructFileUrlMap(brandList);
        Map<String, AclUserAccreditInfo> userAccreditInfoMap=constructUserAccreditInfoMap(brandList);
        for (Brand brand : brandList) {
            if (!StringUtils.isBlank(brand.getLogo())){
                brand.setLogo(fileUrlMap.get(brand.getLogo()));
            }
            if(!StringUtils.isBlank(brand.getLastEditOperator())){
                if(userAccreditInfoMap!=null){
                    AclUserAccreditInfo aclUserAccreditInfo =userAccreditInfoMap.get(brand.getLastEditOperator());
                    if(aclUserAccreditInfo !=null){
                        brand.setLastEditOperator(aclUserAccreditInfo.getName());
                    }
                }
            }
        }
        pagenation.setResult(brandList);
        return pagenation;
    }

    @Override
    public Pagenation<Brand> brandList(BrandForm queryModel, Pagenation<Brand> page) throws Exception {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        setQueryParam(example,criteria,queryModel);
        Pagenation<Brand> pagenation = brandService.pagination(example, page, queryModel);
        return pagenation;
    }

    public void setQueryParam(Example example,Example.Criteria criteria,BrandForm queryModel){
        if (!StringUtils.isBlank(queryModel.getName())) {
            criteria.andLike("name", "%" + queryModel.getName() + "%");
        }
        if (!StringUtils.isBlank(queryModel.getIsValid())) {
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }
        if (!StringUtils.isBlank(queryModel.getStartUpdateTime())) {
            criteria.andGreaterThan("updateTime", queryModel.getStartUpdateTime());
        }
        if (!StringUtils.isBlank(queryModel.getEndUpdateTime())) {
            criteria.andLessThan("updateTime", queryModel.getEndUpdateTime());
        }
        if (!StringUtils.isBlank(queryModel.getAlise())) {
            criteria.andEqualTo("alise", queryModel.getAlise());
        }
        if (!StringUtils.isBlank(queryModel.getBrandCode())) {
            criteria.andEqualTo("brandCode", queryModel.getBrandCode());
        }
        example.orderBy("updateTime").desc();
    }

    @Override
    public List<Brand> queryBrands(BrandForm brandForm) throws Exception {
        Brand brand = new Brand();
        if (StringUtils.isEmpty(brandForm.getIsValid())) {
            brand.setIsValid(ZeroToNineEnum.ONE.getCode());
        }
        brand.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        return brandService.select(brand);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveBrand(Brand brand, ContainerRequestContext requestContext) throws Exception {
        AssertUtil.notNull(brand, "保存品牌信息，品牌不能为空");
        //初始化信息
        brand.setSource(SourceEnum.SCM.getCode());
        ParamsUtil.setBaseDO(brand);
        brand.setBrandCode(serialUtilService.generateCode(BRAND_CODE_LENGTH, BRAND_CODE_EX_NAME, DateUtils.dateToCompactString(brand.getCreateTime())));
        String userId= (String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        AclUserAccreditInfo aclUserAccreditInfo= (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        if(!StringUtils.isBlank(userId)){
            brand.setCreateOperator(userId);
            brand.setLastEditOperator(userId);
        }
        try {
            brandService.insert(brand);
            //记录到日志表中不能影响到主体业务
            logInfoService.recordLog(brand,brand.getId().toString(),aclUserAccreditInfo,LogOperationEnum.ADD,null);
            //通知渠道方
            try{
                trcBiz.sendBrand(TrcActionTypeEnum.ADD_BRAND, null,brand,System.currentTimeMillis());
            }catch (Exception e){
                log.error("品牌新增通知调用出现异常:"+e.getMessage());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            String msg = CommonUtil.joinStr("保存品牌", JSON.toJSONString(brand), "到数据库失败").toString();
            throw new CategoryException(ExceptionEnum.CATEGORY_BRAND_SAVE_EXCEPTION, msg);
        }
    }

    @Override
    public Brand findBrandById(Long id) throws Exception {
        AssertUtil.notNull(id, "根据ID查询品牌明细,参数ID不能为空");
        Brand brand = new Brand();
        brand.setId(id);
        brand = brandService.selectOne(brand);
        if (null == brand) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", id.toString(), "]查询品牌明细为空").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_BRAND_QUERY_EXCEPTION, msg);
        }
        return brand;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateBrand(Brand brand, ContainerRequestContext requestContext) throws Exception {
        AssertUtil.notNull(brand.getId(), "更新品牌信息，品牌ID不能为空");
        String remark=null;
        Brand selectBrand=brandService.selectOneById(brand.getId());
        brand.setUpdateTime(Calendar.getInstance().getTime());
        String userId= (String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        AclUserAccreditInfo aclUserAccreditInfo= (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        if(!StringUtils.isBlank(userId)){
            brand.setLastEditOperator(userId);
        }
        int count = brandService.updateByPrimaryKeySelective(brand);
        if (count < 1) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", brand.getId().toString(), "]更新品牌明细失败").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_BRAND_QUERY_EXCEPTION, msg);
        }
        if(!selectBrand.getIsValid().equals(brand.getIsValid())){
            //品牌状态更新时需要更新品牌供应商关系表的is_valid字段，但可能此时该品牌还未使用，故不对返回值进行判断
            supplierBrandService.updateSupplerBrandIsValid(brand.getIsValid(), brand.getId());
            //品牌状态更新时需要更新品牌分类关系表的is_valid字段，但可能此时该品牌还未使用，故不对返回值进行判断
            categoryBrandService.updateCategoryBrandIsValid(brand.getIsValid(),brand.getId());
            if(brand.getIsValid().equals(ValidEnum.VALID.getCode())){
                remark=remarkEnum.VALID_OFF.getMessage();
            }else{
                remark=remarkEnum.VALID_ON.getMessage();
            }
        }
        //记录到日志表中不能影响到主体业务
        logInfoService.recordLog(brand,brand.getId().toString(),aclUserAccreditInfo,LogOperationEnum.UPDATE,remark);
        //通知渠道方
        try{
            trcBiz.sendBrand(TrcActionTypeEnum.EDIT_BRAND, selectBrand,brand,System.currentTimeMillis());
        }catch (Exception e){
            log.error("品牌修改通知调用出现异常:"+e.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateBrandStatus(Brand brand, ContainerRequestContext requestContext) throws Exception {
        AssertUtil.notNull(brand.getId(), "需要更新品牌状态时，品牌不能为空");
        Brand selectBrand=brandService.selectOneById(brand.getId());
        Brand updateBrand = new Brand();
        updateBrand.setId(brand.getId());
        updateBrand.setUpdateTime(Calendar.getInstance().getTime());
        String remark;
        if (brand.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updateBrand.setIsValid(ValidEnum.NOVALID.getCode());
            remark=remarkEnum.VALID_OFF.getMessage();
        } else {
            updateBrand.setIsValid(ValidEnum.VALID.getCode());
            remark=remarkEnum.VALID_ON.getMessage();
        }
        String userId= (String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        AclUserAccreditInfo aclUserAccreditInfo= (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        if(!StringUtils.isBlank(userId)){
            updateBrand.setLastEditOperator(userId);
        }
        int count = brandService.updateByPrimaryKeySelective(updateBrand);
        if (count < 1) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", brand.getId().toString(), "]更新品牌明细失败").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_BRAND_UPDATE_EXCEPTION, msg);
        }
        //品牌状态更新时需要更新品牌供应商关系表的is_valid字段，但可能此时该品牌还未使用，故不对返回值进行判断
        supplierBrandService.updateSupplerBrandIsValid(updateBrand.getIsValid(), updateBrand.getId());
        //品牌状态更新时需要更新品牌分类关系表的is_valid字段，但可能此时该品牌还未使用，故不对返回值进行判断
        categoryBrandService.updateCategoryBrandIsValid(updateBrand.getIsValid(),updateBrand.getId());
        //记录到日志表中不能影响到主体业务
        logInfoService.recordLog(brand,brand.getId().toString(),aclUserAccreditInfo,LogOperationEnum.UPDATE,remark);
        //通知渠道方
        try{
            Brand newBrand = brandService.selectOneById(brand.getId());
            trcBiz.sendBrand(TrcActionTypeEnum.EDIT_BRAND, selectBrand,newBrand,System.currentTimeMillis());
        }catch (Exception e){
            log.error("品牌状态变更通知调用出现异常:"+e.getMessage());
        }
    }

    @Override
    public List<Brand> findBrandsByName(String name) throws Exception {
        if (StringUtils.isBlank(name)) {
            String msg = CommonUtil.joinStr("根据品牌名称查询品牌明细参数name为空").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", name);
        List<Brand> brandList = brandService.selectByExample(example);
        return brandList;
    }

    private Map<String, String> constructFileUrlMap(List<Brand> brandList) throws Exception {
        Set<String> urlSet = new HashSet<>();
        for (Brand brand : brandList) {
            if (!StringUtils.isBlank(brand.getLogo())) {
                urlSet.add(brand.getLogo());
            }
        }
        if (null != urlSet && urlSet.size() > 0) {
            String[] urlStr = new String[urlSet.size()];
            urlSet.toArray(urlStr);
            List<FileUrl> fileUrlList = qinniuBiz.batchGetFileUrl(urlStr, ZeroToNineEnum.ONE.getCode());
            if (null != fileUrlList && fileUrlList.size() > 0) {
                Map<String, String> fileUrlMap = new HashMap<>();
                for (FileUrl fileUrl : fileUrlList) {
                    fileUrlMap.put(fileUrl.getFileKey(), fileUrl.getUrl());
                }
                return fileUrlMap;
            }
        }
        return null;
    }

    private Map<String,AclUserAccreditInfo> constructUserAccreditInfoMap(List<Brand> brandList){
        if(AssertUtil.collectionIsEmpty(brandList)){
           return null;
        }
        Set<String> userIdsSet=new HashSet<>();
        for (Brand brand:brandList) {
             userIdsSet.add(brand.getLastEditOperator());
        }
        String[] userIdArr=new String[userIdsSet.size()];
        userIdsSet.toArray(userIdArr);
        return userAccreditInfoService.selectByIds(userIdArr);
    }
}
