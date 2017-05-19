package org.trc.biz.impl.supplier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.trc.biz.supplier.ISupplierBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Brand;
import org.trc.domain.supplier.*;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.ConfigException;
import org.trc.exception.SupplierException;
import org.trc.exception.ParamValidException;
import org.trc.form.supplier.SupplierBrandForm;
import org.trc.form.supplier.SupplierCategoryForm;
import org.trc.form.supplier.SupplierExt;
import org.trc.form.supplier.SupplierForm;
import org.trc.service.supplier.*;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.*;

/**
 * Created by hzwdx on 2017/5/5.
 */
@Service("supplierBiz")
public class SupplierBiz implements ISupplierBiz {

    private final static Logger log = LoggerFactory.getLogger(SupplierBiz.class);

    private final static String SERIALNAME = "GYS";
    private final static Integer LENGTH = 6;
    //供应商类型：国内供应商
    private static final String INTERNAL_SUPPLIER = "internalSupplier";
    //供应商类型：海外供应商
    private static final String OVERSEAS_SUPPLIER = "overseasSupplier";
    //证件类型:普通三证
    private static final String NORMAL_THREE_CERTIFICATE = "normalThreeCertificate";
    //证件类型:多证合一
    private static final String MULTI_CERTIFICATE_UNION = "multiCertificateUnion";

    @Autowired
    private ISupplierService supplierService;
    @Autowired
    private ICertificateService certificateService;
    @Autowired
    private ISerialUtilService serialUtilService;
    @Autowired
    private ISupplierChannelRelationService supplierChannelRelationService;
    @Autowired
    private ISupplierCategoryService supplierCategoryService;
    @Autowired
    private ISupplierBrandService supplierBrandService;
    @Autowired
    private ISupplierFinancialInfoService supplierFinancialInfoService;
    @Autowired
    private ISupplierAfterSaleInfoService supplierAfterSaleInfoService;

    @Override
    public Pagenation<Supplier> SupplierPage(SupplierForm queryModel, Pagenation<Supplier> page) throws Exception {
        Example example = new Example(Supplier.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(queryModel.getSupplierName())) {//供应商名称
            criteria.andLike("supplierName", "%" + queryModel.getSupplierName() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getSupplierCode())) {//供应商编码
            criteria.andLike("supplierCode", "%" + queryModel.getSupplierCode() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getContact())) {//联系人
            criteria.andLike("contact", "%" + queryModel.getContact() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getSupplierKindCode())) {//供应商性质
            criteria.andEqualTo("supplierKindCode", queryModel.getSupplierKindCode());
        }
        if (StringUtil.isNotEmpty(queryModel.getStartDate())) {//开始日期
            criteria.andGreaterThanOrEqualTo("updateTime", DateUtils.parseDate(queryModel.getStartDate()));
        }
        if (StringUtil.isNotEmpty(queryModel.getEndDate())) {//截止日期
            Date endDate = DateUtils.parseDate(queryModel.getEndDate());
            criteria.andLessThan("updateTime", DateUtils.addDays(endDate, 1));
        }
        if (StringUtil.isNotEmpty(queryModel.getIsValid())) {
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }
        example.orderBy("isValid").desc();
        //分页查询
        return supplierService.pagination(example, page, queryModel);
    }

    @Override
    public List<Supplier> querySuppliers(SupplierForm supplierForm) throws Exception {
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierForm, supplier);
        if (StringUtils.isEmpty(supplierForm.getIsValid())) {
            supplier.setIsValid(ZeroToNineEnum.ONE.getCode());
        }
        supplier.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        return supplierService.select(supplier);
    }

    @Override
    public void  saveSupplier(Supplier supplier, Certificate certificate, SupplierCategory supplierCategory, SupplierBrand supplierBrand,
                SupplierFinancialInfo supplierFinancialInfo, SupplierAfterSaleInfo supplierAfterSaleInfo) throws Exception {
        //参数校验
        supplierSaveCheck(supplier, certificate);
        String supplierCode = serialUtilService.getSerialCode(LENGTH,SERIALNAME);//FIXME 生成序列号的服务修改
        supplier.setSupplierCode(supplierCode);
        //保存供应商
        saveSupplierBase(supplier);
        if (StringUtils.equals(INTERNAL_SUPPLIER, supplier.getSupplierTypeCode())) {//国内供应商
            //保存证件
            certificate.setSupplierId(supplier.getId());
            certificate.setSupplierCode(supplier.getSupplierCode());
            saveCertificate(certificate);
        }
        //保存供应商代理类目
        supplierCategory.setSupplierId(supplier.getId());
        supplierCategory.setSupplierCode(supplier.getSupplierCode());
        saveCategory(supplierCategory);
        //保存供应商代理品牌
        supplierBrand.setSupplierId(supplier.getId());
        supplierBrand.setSupplierCode(supplier.getSupplierCode());
        saveBrand(supplierBrand);
        //保存供应商财务信息
        supplierFinancialInfo.setSupplierId(supplier.getId());
        supplierFinancialInfo.setSupplierCode(supplier.getSupplierCode());
        saveFinancial(supplierFinancialInfo);
        //保存供应商售后信息
        supplierAfterSaleInfo.setSupplierId(supplier.getId());
        supplierAfterSaleInfo.setSupplierCode(supplier.getSupplierCode());
        saveAfterSale(supplierAfterSaleInfo);
        //保存供应商渠道关系
        List<SupplierChannelRelation> supplierChannelRelations = new ArrayList<SupplierChannelRelation>();
        /**渠道channels，格式："渠道ID-渠道编号,...",多个渠道用逗号分隔,
         * 每个渠道里面包含渠道ID和渠道编号(渠道ID和编号用"-"号分隔)
         */
        String channels = supplier.getChannel();
        String[] sp1 = channels.split(SupplyConstants.Symbol.COMMA);
        for (String c : sp1) {
            Assert.doesNotContain(c, "\\" + SupplyConstants.Symbol.MINUS, "供应商新增提交的渠道参数中渠道信息必须是[渠道ID-渠道编号]格式");
            String[] sp2 = c.split(SupplyConstants.Symbol.MINUS);
            SupplierChannelRelation supplierChannelRelation = new SupplierChannelRelation();
            supplierChannelRelation.setSupplierId(supplier.getId());
            supplierChannelRelation.setSupplierCode(supplier.getSupplierCode());
            supplierChannelRelation.setChannelId(Long.parseLong(sp2[0]));
            supplierChannelRelation.setChannelCode(sp2[1]);
            supplierChannelRelations.add(supplierChannelRelation);
        }
        saveSupplierChannelRelation(supplierChannelRelations);
    }

    @Override
    public void updateSupplier(Supplier supplier, Certificate certificate, SupplierCategory supplierCategory, SupplierBrand supplierBrand, SupplierFinancialInfo supplierFinancialInfo, SupplierAfterSaleInfo supplierAfterSaleInfo) throws Exception {
        AssertUtil.notNull(supplier.getId(), "更新供应商供应商ID不能为空");
        AssertUtil.notNull(supplier.getSupplierCode(), "更新供应商供应商编号不能为空");
        //参数校验
        supplierSaveCheck(supplier, certificate);
        //保存供应商
        saveSupplierBase(supplier);
        if (StringUtils.equals(INTERNAL_SUPPLIER, supplier.getSupplierTypeCode())) {//国内供应商
            //保存证件
            certificate.setSupplierId(supplier.getId());
            certificate.setSupplierCode(supplier.getSupplierCode());
            saveCertificate(certificate);
        }
        //保存供应商代理类目
        supplierCategory.setSupplierId(supplier.getId());
        supplierCategory.setSupplierCode(supplier.getSupplierCode());
        saveCategory(supplierCategory);
        //保存供应商代理品牌
        supplierBrand.setSupplierId(supplier.getId());
        supplierBrand.setSupplierCode(supplier.getSupplierCode());
        saveBrand(supplierBrand);
        //保存供应商财务信息
        supplierFinancialInfo.setSupplierId(supplier.getId());
        supplierFinancialInfo.setSupplierCode(supplier.getSupplierCode());
        saveFinancial(supplierFinancialInfo);
        //保存供应商售后信息
        supplierAfterSaleInfo.setSupplierId(supplier.getId());
        supplierAfterSaleInfo.setSupplierCode(supplier.getSupplierCode());
        saveAfterSale(supplierAfterSaleInfo);
        //保存供应商渠道关系
        List<SupplierChannelRelation> supplierChannelRelations = new ArrayList<SupplierChannelRelation>();
        /**渠道channels，格式："渠道ID-渠道编号,...",多个渠道用逗号分隔,
         * 每个渠道里面包含渠道ID和渠道编号(渠道ID和编号用"-"号分隔)
         */
        String channels = supplier.getChannel();
        String[] sp1 = channels.split(SupplyConstants.Symbol.COMMA);
        for (String c : sp1) {
            Assert.doesNotContain(c, "\\" + SupplyConstants.Symbol.MINUS, "供应商新增提交的渠道参数中渠道信息必须是[渠道ID-渠道编号]格式");
            String[] sp2 = c.split(SupplyConstants.Symbol.MINUS);
            SupplierChannelRelation supplierChannelRelation = new SupplierChannelRelation();
            supplierChannelRelation.setSupplierId(supplier.getId());
            supplierChannelRelation.setSupplierCode(supplier.getSupplierCode());
            supplierChannelRelation.setChannelId(Long.parseLong(sp2[0]));
            supplierChannelRelation.setChannelCode(sp2[1]);
            supplierChannelRelations.add(supplierChannelRelation);
        }
        saveSupplierChannelRelation(supplierChannelRelations);
    }

    /**
     * 保存供应商参数校验
     *
     * @param supplier
     * @param certificate
     */
    private void supplierSaveCheck(Supplier supplier, Certificate certificate) {
        if (StringUtils.equals(INTERNAL_SUPPLIER, supplier.getSupplierTypeCode())) {//国内供应商
            AssertUtil.notBlank(supplier.getCertificateTypeId(), "证件类型ID不能为空");
            AssertUtil.notBlank(certificate.getLegalPersonIdCard(), "法人身份证不能为空");
            AssertUtil.notBlank(certificate.getLegalPersonIdCardPic1(), "法人身份证正面图片不能为空");
            AssertUtil.notBlank(certificate.getLegalPersonIdCardPic2(), "法人身份证背面图片不能为空");
            AssertUtil.notBlank(certificate.getIdCardStartDate(), "法人身份证有效期开始日期不能为空");
            AssertUtil.notBlank(certificate.getIdCardEndDate(), "法人身份证有效期截止日期不能为空");
            if (StringUtils.equals(NORMAL_THREE_CERTIFICATE, supplier.getCertificateTypeId())) {//普通三证
                AssertUtil.notBlank(certificate.getBusinessLicence(), "营业执照不能为空");
                AssertUtil.notBlank(certificate.getBusinessLicencePic(), "营业执照证件图片不能为空");
                AssertUtil.notBlank(certificate.getOrganRegistraCodeCertificate(), "组织机构代码证不能为空");
                AssertUtil.notBlank(certificate.getOrganRegistraCodeCertificatePic(), "组织机构代码证图片不能为空");
                AssertUtil.notBlank(certificate.getTaxRegistrationCertificate(), "税务登记证不能为空");
                AssertUtil.notBlank(certificate.getTaxRegistrationCertificatePic(), "税务登记证证件图片不能为空");
                AssertUtil.notBlank(certificate.getBusinessLicenceStartDate(), "营业执照有效期开始日期不能为空");
                AssertUtil.notBlank(certificate.getBusinessLicenceEndDate(), "营业执照有效期截止日期不能为空");
                AssertUtil.notBlank(certificate.getOrganRegistraStartDate(), "组织机构代码证效期开始日期不能为空");
                AssertUtil.notBlank(certificate.getOrganRegistraEndDate(), "组织机构代码证有效期截止日期不能为空");
                AssertUtil.notBlank(certificate.getTaxRegistrationStartDate(), "税务登记证有效期开始日期不能为空");
                AssertUtil.notBlank(certificate.getTaxRegistrationEndDate(), "税务登记证有效期截止日期不能为空");
            } else if (StringUtils.equals(MULTI_CERTIFICATE_UNION, supplier.getCertificateTypeId())) {//多证合一
                AssertUtil.notBlank(certificate.getMultiCertificateCombineNo(), "多证合一证号不能为空");
                AssertUtil.notBlank(certificate.getMultiCertificateCombinePic(), "多证合一证件图片不能为空");
            } else {
                String msg = String.format("证件类型ID[%s]错误", supplier.getCertificateTypeId());
                log.error(msg);
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
            }
        } else if (StringUtils.equals(OVERSEAS_SUPPLIER, supplier.getSupplierTypeCode())) {//国外供应商
            AssertUtil.notBlank(supplier.getCountry(), "所在国家不能为空");
        } else {
            String msg = String.format("供应商类型编码[%s]错误", supplier.getSupplierTypeCode());
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }

    }

    /**
     * 保存供应商基础信息
     *
     * @param supplier
     * @return
     * @throws Exception
     */
    private void saveSupplierBase(Supplier supplier) throws Exception {
        int count = 0;
        if (null != supplier.getId()) {
            //修改
            supplier.setUpdateTime(Calendar.getInstance().getTime());
            count = supplierService.updateByPrimaryKeySelective(supplier);
        } else {
            //新增
            ParamsUtil.setBaseDO(supplier);
            count = supplierService.insert(supplier);
        }
        if (count == 0) {
            String msg = CommonUtil.joinStr("保存供应商", JSON.toJSONString(supplier), "到数据库失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION, msg);
        }
    }

    /**
     * 更新供应商基本信息
     * @param supplier
     * @throws Exception
     */
    private void updateSupplierBase(Supplier supplier) throws Exception {
        supplier.setUpdateTime(Calendar.getInstance().getTime());
        int count = supplierService.updateByPrimaryKeySelective(supplier);
        if(count == 0){
            String msg = CommonUtil.joinStr("修改供应商基本信息",JSON.toJSONString(supplier),"数据库操作失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_UPDATE_EXCEPTION, msg);
        }
    }

    /**
     * 保存证件信息
     *
     * @param certificate
     * @return
     */
    private void saveCertificate(Certificate certificate) {
        int count = 0;
        if (null != certificate.getId()) {
            //修改
            certificate.setUpdateTime(Calendar.getInstance().getTime());
            count = certificateService.updateByPrimaryKeySelective(certificate);
        } else {
            //新增
            ParamsUtil.setBaseDO(certificate);
            count = certificateService.insert(certificate);
        }
        if (count == 0) {
            String msg = CommonUtil.joinStr("保存供应商证件图片", JSON.toJSONString(certificate), "到数据库失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION, msg);
        }
    }

    /**
     * 更新证件信息
     * @param certificate
     */
    private void updateCertificate(Certificate certificate) {
        certificate.setUpdateTime(Calendar.getInstance().getTime());
        int count = certificateService.updateByPrimaryKeySelective(certificate);
        if(count == 0){
            String msg = CommonUtil.joinStr("修改供应商证件信息",JSON.toJSONString(certificate),"数据库操作失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_UPDATE_EXCEPTION, msg);
        }
    }

    /**
     * 保存供应商渠道关系
     *
     * @param supplierChannelRelations
     * @return
     */
    private int saveSupplierChannelRelation(List<SupplierChannelRelation> supplierChannelRelations) {
        return supplierChannelRelationService.insertList(supplierChannelRelations);
    }

    /**
     * 保存供应商代理类目
     * @param supplierCategory
     */
    private void saveCategory(SupplierCategory supplierCategory) {
        int count = 0;
        JSONArray categoryArray = JSONArray.parseArray(supplierCategory.getSupplierCetegory());
        List<SupplierCategory> list = new ArrayList<SupplierCategory>();
        for(Object obj : categoryArray){
            JSONObject jbo = (JSONObject) obj;
            SupplierCategory s = new SupplierCategory();
            s.setSupplierId(supplierCategory.getSupplierId());
            s.setSupplierCode(supplierCategory.getSupplierCode());
            s.setCategoryId(jbo.getLong("categoryId"));
            s.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            list.add(s);
        }
        count = supplierCategoryService.insertList(list);
        if (count == 0) {
            String msg = CommonUtil.joinStr("保存供应商代理类目", JSON.toJSONString(supplierCategory), "到数据库失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION, msg);
        }
    }

    /**
     * 保存供应商代理类目
     * @param supplierCategory
     */
    private void updateCategory(SupplierCategory supplierCategory) {
        int count = 0;
        JSONArray categoryArray = JSONArray.parseArray(supplierCategory.getSupplierCetegory());
        List<SupplierCategory> list = new ArrayList<SupplierCategory>();
        for(Object obj : categoryArray){
            JSONObject jbo = (JSONObject) obj;
            SupplierCategory s = new SupplierCategory();
            s.setSupplierId(supplierCategory.getSupplierId());
            s.setSupplierCode(supplierCategory.getSupplierCode());
            s.setCategoryId(jbo.getLong("categoryId"));
            s.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            list.add(s);
        }
        count = supplierCategoryService.insertList(list);
        if (count == 0) {
            String msg = CommonUtil.joinStr("保存供应商代理类目", JSON.toJSONString(supplierCategory), "到数据库失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION, msg);
        }
    }

    /**
     * 保存供应商代理品牌
     * @param brand
     */
    private void saveBrand(SupplierBrand brand) {
        int count = 0;
        JSONArray categoryArray = JSONArray.parseArray(brand.getSupplierBrand());
        List<SupplierBrand> list = new ArrayList<SupplierBrand>();
        for(Object obj : categoryArray){
            JSONObject jbo = (JSONObject) obj;
            SupplierBrand s = new SupplierBrand();
            s.setSupplierId(brand.getSupplierId());
            s.setSupplierCode(brand.getSupplierCode());
            s.setCategoryId(jbo.getLong("categoryId"));
            s.setCategoryCode(jbo.getString("categoryCode"));
            s.setBrandId(jbo.getLong("brandId"));
            s.setBrandCode(jbo.getString("brandCode"));
            s.setProxyAptitudeId(jbo.getString("proxyAptitudeId"));
            s.setProxyAptitudeStartDate(jbo.getString("proxyAptitudeStartDate"));
            s.setProxyAptitudeEndDate(jbo.getString("proxyAptitudeEndDate"));
            s.setAptitudePic(jbo.getString("aptitudePic"));
            s.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            list.add(s);
        }
        count = supplierBrandService.insertList(list);
        if (count == 0) {
            String msg = CommonUtil.joinStr("保存供应商代理品牌", JSON.toJSONString(brand), "到数据库失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION, msg);
        }
    }

    /**
     * 保存供应商财务信息
     * @param supplierFinancialInfo
     */
    private void saveFinancial(SupplierFinancialInfo supplierFinancialInfo) {
        int count = 0;
        if (null != supplierFinancialInfo.getId()) {
            //修改
            supplierFinancialInfo.setUpdateTime(Calendar.getInstance().getTime());
            count = supplierFinancialInfoService.updateByPrimaryKeySelective(supplierFinancialInfo);
        } else {
            //新增
            ParamsUtil.setBaseDO(supplierFinancialInfo);
            count = supplierFinancialInfoService.insert(supplierFinancialInfo);
        }
        if (count == 0) {
            String msg = CommonUtil.joinStr("保存供应商财务信息", JSON.toJSONString(supplierFinancialInfo), "到数据库失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION, msg);
        }
    }

    /**
     * 修改供应商财务信息
     * @param supplierFinancialInfo
     */
    private void updateFinancial(SupplierFinancialInfo supplierFinancialInfo) {
        supplierFinancialInfo.setUpdateTime(Calendar.getInstance().getTime());
        int count = supplierFinancialInfoService.updateByPrimaryKeySelective(supplierFinancialInfo);
        if (count == 0) {
            String msg = CommonUtil.joinStr("修改供应商财务", JSON.toJSONString(supplierFinancialInfo), "数据库操作失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_UPDATE_EXCEPTION, msg);
        }
    }

    /**
     * 保存供应商售后信息
     * @param supplierAfterSaleInfo
     */
    private void saveAfterSale(SupplierAfterSaleInfo supplierAfterSaleInfo) {
        int count = 0;
        if (null != supplierAfterSaleInfo.getId()) {
            //修改
            supplierAfterSaleInfo.setUpdateTime(Calendar.getInstance().getTime());
            count = supplierAfterSaleInfoService.updateByPrimaryKeySelective(supplierAfterSaleInfo);
        } else {
            //新增
            ParamsUtil.setBaseDO(supplierAfterSaleInfo);
            count = supplierAfterSaleInfoService.insert(supplierAfterSaleInfo);
        }
        if (count == 0) {
            String msg = CommonUtil.joinStr("保存供应商售后信息", JSON.toJSONString(supplierAfterSaleInfo), "到数据库失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION, msg);
        }
    }

    /**
     * 修改供应商财务信息
     * @param supplierAfterSaleInfo
     */
    private void updateAfterSale(SupplierAfterSaleInfo supplierAfterSaleInfo) {
        supplierAfterSaleInfo.setUpdateTime(Calendar.getInstance().getTime());
        int count = supplierAfterSaleInfoService.updateByPrimaryKeySelective(supplierAfterSaleInfo);
        if (count == 0) {
            String msg = CommonUtil.joinStr("修改供应商售后信息", JSON.toJSONString(supplierAfterSaleInfo), "数据库操作失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_UPDATE_EXCEPTION, msg);
        }
    }

    @Override
    public void updateSupplier(Supplier supplier) throws Exception {
        AssertUtil.notNull(supplier.getId(), "修改供应商参数ID为空");
        supplier.setUpdateTime(Calendar.getInstance().getTime());
        int count = supplierService.updateByPrimaryKeySelective(supplier);
        if (count == 0) {
            String msg = CommonUtil.joinStr("修改供应商", JSON.toJSONString(supplier), "数据库操作失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_UPDATE_EXCEPTION, msg);
        }
    }

    @Override
    public Supplier findSupplierById(Long id) throws Exception {
        AssertUtil.notNull(id, "根据ID查询供应商参数ID为空");
        Supplier supplier = new Supplier();
        supplier.setId(id);
        supplier = supplierService.selectOne(supplier);
        if (null == supplier) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", id.toString(), "]查询供应商为空").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_QUERY_EXCEPTION, msg);
        }
        return supplier;
    }

    @Override
    public List<SupplierCategoryExt> querySupplierCategory(String supplierCode) throws Exception {
        AssertUtil.notBlank(supplierCode, "查询供应商代理分类供应商编码不能为空");
        return supplierCategoryService.selectSupplierCategorys(supplierCode);
    }

    @Override
    public List<SupplierBrandExt> querySupplierBrand(String supplierCode) throws Exception {
        AssertUtil.notBlank(supplierCode, "查询供应商代理品牌供应商编码不能为空");
        return supplierBrandService.selectSupplierBrands(supplierCode);
    }

    @Override
    public SupplierExt querySupplierInfo(String supplierCode) throws Exception {
        AssertUtil.notBlank(supplierCode, "查询供应商信息供应商编码不能为空");
        Supplier supplier = new Supplier();
        supplier.setSupplierCode(supplierCode);
        supplier.setIsValid(ZeroToNineEnum.ONE.getCode());
        supplier.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        supplier = supplierService.selectOne(supplier);
        AssertUtil.notNull(supplier, String.format("%s%s%s", "根据供应商编码[", supplierCode, "]查询供应商基本信息为空"));

        Certificate certificate = new Certificate();
        certificate.setSupplierCode(supplierCode);
        certificate = certificateService.selectOne(certificate);
        AssertUtil.notNull(certificate, String.format("%s%s%s", "根据供应商编码[", supplierCode, "]查询供应商证件信息为空"));

        SupplierFinancialInfo supplierFinancialInfo = new SupplierFinancialInfo();
        supplierFinancialInfo.setSupplierCode(supplierCode);
        supplierFinancialInfo = supplierFinancialInfoService.selectOne(supplierFinancialInfo);
        AssertUtil.notNull(supplierFinancialInfo, String.format("%s%s%s", "根据供应商编码[", supplierCode, "]查询供应商财务信息为空"));

        SupplierAfterSaleInfo supplierAfterSaleInfo = new SupplierAfterSaleInfo();
        supplierAfterSaleInfo.setSupplierCode(supplierCode);
        supplierAfterSaleInfo = supplierAfterSaleInfoService.selectOne(supplierAfterSaleInfo);
        AssertUtil.notNull(supplierAfterSaleInfo, String.format("%s%s%s", "根据供应商编码[", supplierCode, "]查询供应商售后信息为空"));

        SupplierChannelRelation supplierChannelRelation = new SupplierChannelRelation();
        supplierChannelRelation.setSupplierCode(supplierCode);
        List<SupplierChannelRelation> supplierChannelRelations = supplierChannelRelationService.select(supplierChannelRelation);
        AssertUtil.notEmpty(supplierChannelRelations, String.format("%s%s%s", "根据供应商编码[", supplierCode, "]查询供应商渠道为空"));

        SupplierExt supplierExt = new SupplierExt();
        supplierExt.setSupplier(supplier);
        supplierExt.setCertificate(certificate);
        supplierExt.setSupplierFinancialInfo(supplierFinancialInfo);
        supplierExt.setSupplierAfterSaleInfo(supplierAfterSaleInfo);
        supplierExt.setSupplierChannelRelations(supplierChannelRelations);
        return supplierExt;
    }


}
