package org.trc.biz.impl.supplier;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.trc.biz.supplier.ISupplierBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.supplier.Certificate;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.supplier.SupplierChannelRelation;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.SupplierException;
import org.trc.exception.ParamValidException;
import org.trc.form.supplier.SupplierForm;
import org.trc.service.supplier.ICertificateService;
import org.trc.service.supplier.ISupplierChannelRelationService;
import org.trc.service.supplier.ISupplierService;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hzwdx on 2017/5/5.
 */
@Service("supplierBiz")
public class SupplierBiz implements ISupplierBiz {

    private final static Logger log = LoggerFactory.getLogger(SupplierBiz.class);

    private final static String  SERIALNAME="GYS";
    private final static Integer LENGTH=6;
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

    @Override
    public Pagenation<Supplier> SupplierPage(SupplierForm queryModel, Pagenation<Supplier> page) throws Exception {
        Example example = new Example(Supplier.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtil.isNotEmpty(queryModel.getSupplierName())) {//供应商名称
            criteria.andLike("supplierName", "%" + queryModel.getSupplierName() + "%");
        }
        if(StringUtil.isNotEmpty(queryModel.getSupplierCode())) {//供应商编码
            criteria.andLike("supplierCode", "%" + queryModel.getSupplierCode() + "%");
        }
        if(StringUtil.isNotEmpty(queryModel.getContact())) {//联系人
            criteria.andLike("contact", "%" + queryModel.getContact() + "%");
        }
        if(StringUtil.isNotEmpty(queryModel.getSupplierKindCode())) {//供应商性质
            criteria.andEqualTo("supplierKindCode", queryModel.getSupplierKindCode());
        }
        if(StringUtil.isNotEmpty(queryModel.getStartDate())) {//开始日期
            criteria.andGreaterThanOrEqualTo("updateTime", DateUtils.parseDate(queryModel.getStartDate()));
        }
        if(StringUtil.isNotEmpty(queryModel.getEndDate())) {//截止日期
            Date endDate = DateUtils.parseDate(queryModel.getEndDate());
            criteria.andLessThan("updateTime", DateUtils.addDays(endDate,1));
        }
        if(StringUtil.isNotEmpty(queryModel.getIsValid())) {
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }
        example.orderBy("isValid").desc();
        //分页查询
        return supplierService.pagination(example, page, queryModel);
    }

    @Override
    public List<Supplier> querySuppliers(SupplierForm supplierForm) throws Exception {
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierForm,supplier);
        if(StringUtils.isEmpty(supplierForm.getIsValid())){
            supplier.setIsValid(ZeroToNineEnum.ONE.getCode());
        }
        supplier.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        return supplierService.select(supplier);
    }

    @Override
    public int saveSupplier(Supplier supplier, Certificate certificate) throws Exception {
        //参数校验
        supplierSaveCheck(supplier, certificate);
        String supplierCode = serialUtilService.getSerilCode(SERIALNAME,LENGTH);
        //保存供应商
        saveSupplierBase(supplier, supplierCode);
        //保存证件
        certificate.setSupplierId(supplier.getId());
        certificate.setSupplierCode(supplier.getSupplierCode());
        saveCertificate(certificate);
        //保存供应商渠道关系
        List<SupplierChannelRelation> supplierChannelRelations = new ArrayList<SupplierChannelRelation>();
        /**渠道channels，格式："渠道ID-渠道编号,...",多个渠道用逗号分隔,
         * 每个渠道里面包含渠道ID和渠道编号(渠道ID和编号用"-"号分隔)
         */
        String channels = supplier.getChannel();
        String[] sp1 = channels.split(SupplyConstants.Symbol.COMMA);
        for(String c : sp1){
            Assert.doesNotContain(c, "\\"+SupplyConstants.Symbol.MINUS, "供应商新增提交的渠道参数中渠道信息必须是[渠道ID-渠道编号]格式");
            String[] sp2 = c.split(SupplyConstants.Symbol.MINUS);
            SupplierChannelRelation supplierChannelRelation = new SupplierChannelRelation();
            supplierChannelRelation.setSupplierId(supplier.getId());
            supplierChannelRelation.setSupplierCode(supplierCode);
            supplierChannelRelation.setChannelId(Long.parseLong(sp2[0]));
            supplierChannelRelation.setChannelCode(sp2[1]);
            supplierChannelRelations.add(supplierChannelRelation);
        }
        saveSupplierChannelRelation(supplierChannelRelations);
        return 1;
    }

    /**
     * 保存供应商参数校验
     * @param supplier
     * @param certificate
     */
    private void supplierSaveCheck(Supplier supplier, Certificate certificate){
        if(StringUtils.equals(INTERNAL_SUPPLIER, supplier.getSupplierTypeCode())){//国内供应商
            AssertUtil.notEmpty(supplier.getCertificateTypeId(), "证件类型ID不能为空");
            AssertUtil.notEmpty(certificate.getLegalPersonIdCard(), "法人身份证不能为空");
            AssertUtil.notEmpty(certificate.getLegalPersonIdCardPic1(), "法人身份证正面图片不能为空");
            AssertUtil.notEmpty(certificate.getLegalPersonIdCardPic2(), "法人身份证背面图片不能为空");
            AssertUtil.notEmpty(certificate.getIdCardStartDate(), "法人身份证有效期开始日期不能为空");
            AssertUtil.notEmpty(certificate.getIdCardEndDate(), "法人身份证有效期截止日期不能为空");
            if(StringUtils.equals(NORMAL_THREE_CERTIFICATE, supplier.getCertificateTypeId())){//普通三证
                AssertUtil.notEmpty(certificate.getBusinessLicence(), "营业执照不能为空");
                AssertUtil.notEmpty(certificate.getBusinessLicencePic(), "营业执照证件图片不能为空");
                AssertUtil.notEmpty(certificate.getOrganRegistraCodeCertificate(), "组织机构代码证不能为空");
                AssertUtil.notEmpty(certificate.getOrganRegistraCodeCertificatePic(), "组织机构代码证图片不能为空");
                AssertUtil.notEmpty(certificate.getTaxRegistrationCertificate(), "税务登记证不能为空");
                AssertUtil.notEmpty(certificate.getTaxRegistrationCertificatePic(), "税务登记证证件图片不能为空");
                AssertUtil.notEmpty(certificate.getBusinessLicenceStartDate(), "营业执照有效期开始日期不能为空");
                AssertUtil.notEmpty(certificate.getBusinessLicenceEndDate(), "营业执照有效期截止日期不能为空");
                AssertUtil.notEmpty(certificate.getOrganRegistraStartDate(), "组织机构代码证效期开始日期不能为空");
                AssertUtil.notEmpty(certificate.getOrganRegistraEndDate(), "组织机构代码证有效期截止日期不能为空");
                AssertUtil.notEmpty(certificate.getTaxRegistrationStartDate(), "税务登记证有效期开始日期不能为空");
                AssertUtil.notEmpty(certificate.getTaxRegistrationEndDate(), "税务登记证有效期截止日期不能为空");
            }else if(StringUtils.equals(MULTI_CERTIFICATE_UNION, supplier.getCertificateTypeId())){//多证合一
                AssertUtil.notEmpty(certificate.getMultiCertificateCombineNo(), "多证合一证号不能为空");
                AssertUtil.notEmpty(certificate.getMultiCertificateCombinePic(), "多证合一证件图片不能为空");
            }else {
                String msg = String.format("证件类型ID[%s]错误", supplier.getCertificateTypeId());
                log.error(msg);
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
            }
        }else if(StringUtils.equals(OVERSEAS_SUPPLIER, supplier.getSupplierTypeCode())){//国外供应商
            AssertUtil.notEmpty(supplier.getCountry(), "所在国家不能为空");
            AssertUtil.notEmpty(certificate.getMultiCertificateCombinePic(), "多证合一证件图片不能为空");
        }else {
            String msg = String.format("供应商类型编码[%s]错误", supplier.getSupplierTypeCode());
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }

    }

    /**
     * 保存供应商基础信息
     * @param supplier
     * @param supplierCode
     * @return
     * @throws Exception
     */
    private int saveSupplierBase(Supplier supplier, String supplierCode) throws Exception {
        int count = 0;
        if(null != supplier.getId()){
            //修改
            supplier.setUpdateTime(Calendar.getInstance().getTime());
            count = supplierService.updateByPrimaryKeySelective(supplier);
        }else{
            //新增
            ParamsUtil.setBaseDO(supplier);
            supplier.setSupplierCode(supplierCode);
            count = supplierService.insert(supplier);
        }
        if(count == 0){
            String msg = CommonUtil.joinStr("保存供应商", JSON.toJSONString(supplier),"到数据库失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION,msg);
        }
        return count;
    }

    /**
     * 保存证件信息
     * @param certificate
     * @return
     */
    private int saveCertificate(Certificate certificate){
        int count = 0;
        if(null != certificate.getId()){
            //修改
            certificate.setUpdateTime(Calendar.getInstance().getTime());
            count = certificateService.updateByPrimaryKeySelective(certificate);
        }else{
            //新增
            ParamsUtil.setBaseDO(certificate);
            count = certificateService.insert(certificate);
        }
        if(count == 0){
            String msg = CommonUtil.joinStr("保存供应商证件图片", JSON.toJSONString(certificate),"到数据库失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION,msg);
        }
        return count;
    }

    /**
     * 保存供应商渠道关系
     * @param supplierChannelRelations
     * @return
     */
    private int saveSupplierChannelRelation(List<SupplierChannelRelation> supplierChannelRelations){
        return supplierChannelRelationService.insertList(supplierChannelRelations);
    }

    @Override
    public int updateSupplier(Supplier supplier, Long id) throws Exception {
        if(null == id){
            String msg = CommonUtil.joinStr("修改供应商参数ID为空").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        int count = 0;
        supplier.setId(id);
        supplier.setUpdateTime(Calendar.getInstance().getTime());
        count = supplierService.updateByPrimaryKeySelective(supplier);
        if(count == 0){
            String msg = CommonUtil.joinStr("修改供应商",JSON.toJSONString(supplier),"数据库操作失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_UPDATE_EXCEPTION, msg);
        }
        return count;
    }

    @Override
    public Supplier findSupplierById(Long id) throws Exception {
        if(null == id){
            String msg = CommonUtil.joinStr("根据ID查询供应商参数ID为空").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        Supplier supplier = new Supplier();
        supplier.setId(id);
        supplier = supplierService.selectOne(supplier);
        if(null == supplier){
            String msg = CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]查询供应商为空").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_QUERY_EXCEPTION,msg);
        }
        return supplier;
    }
}
