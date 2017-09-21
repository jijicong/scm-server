package org.trc.biz.impl.supplier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.search.MatchQuery;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.trc.biz.category.ICategoryBiz;
import org.trc.biz.supplier.ISupplierBiz;
import org.trc.cache.CacheEvit;
import org.trc.cache.Cacheable;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Channel;
import org.trc.domain.category.Brand;
import org.trc.domain.category.Category;
import org.trc.domain.category.CategoryBrand;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.supplier.*;
import org.trc.enums.*;
import org.trc.exception.GoodsException;
import org.trc.exception.ParamValidException;
import org.trc.exception.SupplierException;
import org.trc.form.supplier.SupplierChannelRelationForm;
import org.trc.form.supplier.SupplierExt;
import org.trc.form.supplier.SupplierForm;
import org.trc.model.SearchResult;
import org.trc.service.IPageNationService;
import org.trc.service.category.ICategoryBrandService;
import org.trc.service.category.ICategoryService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.impl.category.BrandService;
import org.trc.service.impl.system.ChannelService;
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

    private Logger log = LoggerFactory.getLogger(SupplierBiz.class);
    //停用供应商自动拒绝提交审核的申请原因
    private static final String STOP_SUPPLIER_REJECT_APPLY_REASON = "供应商停用，系统自动驳回";

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
    @Autowired
    private ISupplierApplyService supplierApplyService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ICategoryBiz categoryBiz;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ICategoryBrandService categoryBrandService;
    @Autowired
    private ILogInfoService logInfoService;
    @Autowired
    private IPageNationService pageNationService;

    @Override
    //@Cacheable(key="#queryModel.toString()+#page.pageNo+#page.pageSize",isList=true)
    public Pagenation<Supplier> supplierPage(SupplierForm queryModel, Pagenation<Supplier> page) throws Exception {
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
        example.orderBy("updateTime").desc();
        page = supplierService.pagination(example, page, queryModel);
        handlerSupplierPage(page);
        //分页查询
        return page;
    }

    @Override
    public Pagenation<Supplier> supplierPageES(SupplierForm form, Pagenation<Supplier> page) throws Exception {
        TransportClient clientUtil = TransportClientUtil.getTransportClient();
        HighlightBuilder hiBuilder = new HighlightBuilder();
        hiBuilder.preTags("<b style=\"color: red\">");
        hiBuilder.postTags("</b>");
        //设置高亮字段
        hiBuilder.fields().add(new HighlightBuilder.Field("supplier_name.pinyin"));
        hiBuilder.fields().add(new HighlightBuilder.Field("contact.pinyin"));

        SearchRequestBuilder srb = clientUtil.prepareSearch("supplier")//es表名
                .highlighter(hiBuilder)
                .setFrom(page.getStart())//第几个开始
                .setSize(page.getPageSize());//长度
        //查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(form.getSupplierName())) {
            QueryBuilder matchQuery = QueryBuilders.matchQuery("supplier_name.pinyin", form.getSupplierName());
            queryBuilder.should(matchQuery);
        }
        if (StringUtils.isNotBlank(form.getContact())) {
            QueryBuilder matchQuery = QueryBuilders.matchQuery("contact.pinyin", form.getContact());
            queryBuilder.should(matchQuery);
        }
        if (StringUtils.isNotBlank(form.getIsValid())) {
            QueryBuilder filterBuilder = QueryBuilders.termQuery("is_valid", form.getIsValid());
            queryBuilder.must(filterBuilder);
        }
        if (StringUtils.isNotBlank(form.getSupplierKindCode())) {
            QueryBuilder filterBuilder = QueryBuilders.termQuery("supplier_kind_code", form.getSupplierKindCode());
            queryBuilder.must(filterBuilder);
        }
        if (StringUtils.isNotBlank(form.getSupplierCode())) {
            QueryBuilder filterBuilder = QueryBuilders.multiMatchQuery(form.getSupplierCode(),"supplier_code" ).
                    type(MatchQuery.Type.PHRASE_PREFIX);
            queryBuilder.should(filterBuilder);
        }
        srb.setQuery(queryBuilder);
        SearchResult searchResult;
        try {
            searchResult = pageNationService.resultES(srb, clientUtil);
        } catch (Exception e) {
            log.error("es查询失败" + e.getMessage(), e);
            return page;
        }
        List<Supplier> supplierList = new ArrayList<>();
        for (SearchHit searchHit : searchResult.getSearchHits()) {
             Supplier supplier = JSON.parseObject(JSON.toJSONString(searchHit.getSource()), Supplier.class);
            if (StringUtils.isBlank(form.getSupplierName()) && StringUtils.isBlank(form.getContact())) {
                supplierList.add(supplier);
                continue;
            }
            for(Map.Entry<String, HighlightField> entry : searchHit.getHighlightFields().entrySet()) {
                if("supplier_name.pinyin".equals(entry.getKey())) {
                    for (Text text : entry.getValue().getFragments()) {
                        supplier.setHighLightName(text.string());
                    }
                } else if("contact.pinyin".equals(entry.getKey())){
                    for (Text text : entry.getValue().getFragments()) {
                        supplier.setHighContact(text.string());
                    }
                }
            }
            supplierList.add(supplier);
        }
        if (AssertUtil.collectionIsEmpty(supplierList)) {
            return page;
        }
        page.setResult(supplierList);
        handlerSupplierPage(page);
        page.setTotalCount(searchResult.getCount());
        return page;
    }

    @Override
    public Pagenation<Supplier> supplierPage(Pagenation<Supplier> page, AclUserAccreditInfo aclUserAccreditInfo, SupplierForm form) throws Exception {
        PageHelper.startPage(page.getPageNo(), page.getPageSize());
        Map<String, Object> map = new HashMap<>();
        map.put("supplierCode", form.getSupplierCode());
        map.put("supplierName", form.getSupplierName());
        map.put("supplierKindCode", form.getSupplierKindCode());
        map.put("channelId", aclUserAccreditInfo.getChannelId());
        List<Supplier> supplierList = supplierService.selectSupplierListByApply(map);
        int count = supplierService.selectSupplierListCount(map);
        page.setResult(supplierList);
        page.setTotalCount(count);
        handlerSupplierPage(page);
        return page;
    }


    /**
     * 处理供应商分页结果
     *
     * @param page
     */
    private void handlerSupplierPage(Pagenation<Supplier> page) {
        List<String> supplierCodes = new ArrayList<String>();
        for (Supplier s : page.getResult()) {
            supplierCodes.add(s.getSupplierCode());
        }
        if (supplierCodes.size() > 0) {
            //查询供应商品牌
            Example example = new Example(SupplierBrand.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
            criteria.andIn("supplierCode", supplierCodes);
            List<SupplierBrand> supplierBrands = supplierBrandService.selectByExample(example);
            if(CollectionUtils.isEmpty(supplierBrands)){
                log.error(String.format("根据供应商编码[%s]查询供应商品牌为空",
                        CommonUtil.converCollectionToString(supplierCodes)));
            }
            //查询供应商渠道
            Example example2 = new Example(SupplierApplyAudit.class);
            Example.Criteria criteria2 = example2.createCriteria();
            criteria2.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
            criteria2.andIn("supplierCode", supplierCodes);
            criteria2.andEqualTo("status", AuditStatusEnum.PASS.getCode());//审核通过
            List<SupplierApply> supplierChannels = supplierApplyService.selectByExample(example2);
            for (Supplier s : page.getResult()) {
                if (supplierChannels.size() > 0) {
                    //设置渠道名称
                    setChannelName(s, supplierChannels);
                }
                //设置品牌名称
                setBrandName(s, supplierBrands);
            }
        }
    }

    /**
     * 设置渠道名称
     *
     * @param supplier
     * @param supplierChannels
     */
    private void setChannelName(Supplier supplier, List<SupplierApply> supplierChannels) {
        String _channels = "";
        for (SupplierApply supplierApplyAudit : supplierChannels) {
            if (StringUtils.equals(supplier.getSupplierCode(), supplierApplyAudit.getSupplierCode())) {
                Channel channel = new Channel();
                channel.setCode(supplierApplyAudit.getChannelCode());
                channel.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                channel = channelService.selectOne(channel);
                AssertUtil.notNull(channel, String.format("根据渠道编码[%s]查询渠道信息为空", supplierApplyAudit.getChannelCode()));
                _channels = _channels + channel.getName() + ",";
            }
        }
        if (_channels.length() > 0) {
            _channels = _channels.substring(0, _channels.length() - 1);
            supplier.setChannelName(_channels);
        }
    }

    /**
     * 设置代理品牌名称
     *
     * @param supplier
     * @param supplierBrands
     */
    private void setBrandName(Supplier supplier, List<SupplierBrand> supplierBrands) {
        String _brands = "";
        for (SupplierBrand supplierBrand : supplierBrands) {
            if (StringUtils.equals(supplier.getSupplierCode(), supplierBrand.getSupplierCode())) {
                Brand brand = new Brand();
                brand.setBrandCode(supplierBrand.getBrandCode());
                brand.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                brand = brandService.selectOne(brand);
                AssertUtil.notNull(brand, String.format("根据品牌编码[%s]查询品牌信息为空", supplierBrand.getSupplierCode()));
                _brands = _brands + brand.getName() + ",";
            }
        }
        if (_brands.length() > 0) {
            _brands = _brands.substring(0, _brands.length() - 1);
            supplier.setBrandName(_brands);
        }
    }

    @Override
    @Cacheable(key="#supplierForm.toString()",isList=true)
    public List<Supplier> querySuppliers(SupplierForm supplierForm) throws Exception {
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierForm, supplier);
        if (StringUtils.isNotBlank(supplierForm.getIsValid())) {
            supplier.setIsValid(ZeroToNineEnum.ONE.getCode());
        }
        supplier.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        return supplierService.select(supplier);
    }

    @Override
    @CacheEvit
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveSupplier(Supplier supplier, Certificate certificate, SupplierCategory supplierCategory, SupplierBrand supplierBrand,
                             SupplierFinancialInfo supplierFinancialInfo, SupplierAfterSaleInfo supplierAfterSaleInfo, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        //参数校验
        supplierSaveCheck(supplier, certificate, ZeroToNineEnum.ZERO.getCode());
        //生成序列号
        String code = serialUtilService.generateCode(SupplyConstants.Serial.SUPPLIER_LENGTH, SupplyConstants.Serial.SUPPLIER_NAME);
        supplier.setSupplierCode(code);
        //保存供应商
        saveSupplierBase(supplier);
        if (StringUtils.equals(SupplyConstants.Supply.Supplier.INTERNAL_SUPPLIER, supplier.getSupplierTypeCode())) {//国内供应商
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
        String channels = supplier.getChannel();
        saveSupplierChannelRelation(getSupplierChannelRelations(channels, supplier));
        //记录操作日志
        logInfoService.recordLog(supplier, supplier.getId().toString(),aclUserAccreditInfo.getUserId(),
                LogOperationEnum.ADD.getMessage(), null, null);
    }

    /**
     * 获取供应商渠道关系列表
     *
     * @param channels
     * @param supplier
     * @return
     */
    private List<SupplierChannelRelation> getSupplierChannelRelations(String channels, Supplier supplier) {
        List<SupplierChannelRelation> supplierChannelRelations = new ArrayList<SupplierChannelRelation>();
        /**渠道channels，格式："渠道ID-渠道编号,...",多个渠道用逗号分隔,
         * 每个渠道里面包含渠道ID和渠道编号(渠道ID和编号用"-"号分隔)
         */
        String[] sp1 = channels.split(SupplyConstants.Symbol.COMMA);
        for (String c : sp1) {
            Assert.doesNotContain(c, "\\" + SupplyConstants.Symbol.MINUS, "供应商新增提交的渠道参数中渠道信息必须是[渠道ID-渠道编号]格式");
            String[] sp2 = c.split(SupplyConstants.Symbol.MINUS);
            SupplierChannelRelation supplierChannelRelation = new SupplierChannelRelation();
            supplierChannelRelation.setSupplierId(supplier.getId());
            supplierChannelRelation.setSupplierCode(supplier.getSupplierCode());
            supplierChannelRelation.setChannelId(Long.parseLong(sp2[0]));
            supplierChannelRelation.setChannelCode(sp2[1]);
            ParamsUtil.setBaseDO(supplierChannelRelation);
            supplierChannelRelations.add(supplierChannelRelation);
        }
        return supplierChannelRelations;
    }

    @Override
    @CacheEvit
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateSupplier(Supplier supplier, Certificate certificate, SupplierCategory supplierCategory, SupplierBrand supplierBrand,
                               SupplierFinancialInfo supplierFinancialInfo, SupplierAfterSaleInfo supplierAfterSaleInfo, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notNull(supplier.getId(), "更新供应商供应商ID不能为空");
        AssertUtil.notNull(supplier.getSupplierCode(), "更新供应商供应商编号不能为空");
        //参数校验
        supplierSaveCheck(supplier, certificate, ZeroToNineEnum.ONE.getCode());
        //是否也要修改启停用
        boolean isValidFlag = isSupplerValid(supplier);
        //更新供应商
        updateSupplierBase(supplier);
        if (StringUtils.equals(SupplyConstants.Supply.Supplier.INTERNAL_SUPPLIER, supplier.getSupplierTypeCode())) {//国内供应商
            //保存证件
            certificate.setSupplierId(supplier.getId());
            certificate.setSupplierCode(supplier.getSupplierCode());
            Certificate certificate2 = new Certificate();
            certificate2.setSupplierId(supplier.getId());
            certificate2.setSupplierCode(supplier.getSupplierCode());
            certificate2 = certificateService.selectOne(certificate2);
            if (null == certificate2) {
                saveCertificate(certificate);
            } else {
                updateCertificate(certificate);
            }
        }
        //更新供应商代理类目
        supplierCategory.setSupplierId(supplier.getId());
        supplierCategory.setSupplierCode(supplier.getSupplierCode());
        updateCategory(supplierCategory);
        //更新供应商代理品牌
        supplierBrand.setSupplierId(supplier.getId());
        supplierBrand.setSupplierCode(supplier.getSupplierCode());
        updateBrand(supplierBrand);
        //更新供应商财务信息
        supplierFinancialInfo.setSupplierId(supplier.getId());
        supplierFinancialInfo.setSupplierCode(supplier.getSupplierCode());
        updateFinancial(supplierFinancialInfo);
        //更新供应商售后信息
        supplierAfterSaleInfo.setSupplierId(supplier.getId());
        supplierAfterSaleInfo.setSupplierCode(supplier.getSupplierCode());
        updateAfterSale(supplierAfterSaleInfo);
        //更新供应商渠道关系
        String channels = supplier.getChannel();
        List<SupplierChannelRelation> supplierChannelRelations = getSupplierChannelRelations(channels, supplier);
        updateSupplierChannelRelation(supplierChannelRelations, supplier);
        //禁用供应商时将申请该供应商的审批状态为提交审批的供应商申请记录状态改为驳回
        if (StringUtils.equals(supplier.getIsValid(), ZeroToNineEnum.ZERO.getCode())) {
            Supplier _supplier = new Supplier();
            _supplier.setSupplierCode(supplier.getSupplierCode());
            _supplier = supplierService.selectOne(_supplier);
            AssertUtil.notNull(_supplier, String.format("根据供应商编码[%s]查询供应商信息为空", supplier.getSupplierCode()));
            rejectSupplierApply(supplier.getId(), aclUserAccreditInfo);
        }
        //记录操作日志
        String remark = null;
        if (isValidFlag)
            remark = String.format("状态更新为%s", ValidEnum.getValidEnumByCode(supplier.getIsValid()).getName());
        logInfoService.recordLog(supplier, supplier.getId().toString(),aclUserAccreditInfo.getUserId(), LogOperationEnum.UPDATE.getMessage(), remark, null);
    }

    /**
     * 供应商修改是否也修改了启停用
     *
     * @param supplier
     */
    private boolean isSupplerValid(Supplier supplier) {
        Supplier supplier2 = new Supplier();
        supplier2.setId(supplier.getId());
        supplier2 = supplierService.selectOne(supplier2);
        AssertUtil.notNull(supplier2, String.format("根据供应商主键ID[%s]查询供应商信息为空", supplier.getId()));
        if (!StringUtils.equals(supplier.getIsValid(), supplier2.getIsValid())) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 保存供应商参数校验
     *
     * @param supplier
     * @param certificate
     * @param flag 0-新增,1-修改
     */
    private void supplierSaveCheck(Supplier supplier, Certificate certificate, String flag) {
        if (StringUtils.equals(SupplyConstants.Supply.Supplier.INTERNAL_SUPPLIER, supplier.getSupplierTypeCode()) &&
                StringUtils.equals(SupplyConstants.Supply.Supplier.SUPPLIER_PURCHASE, supplier.getSupplierKindCode())) {//国内供应商
            AssertUtil.notBlank(supplier.getCertificateTypeId(), "证件类型ID不能为空");
            AssertUtil.notBlank(certificate.getLegalPersonIdCard(), "法人身份证不能为空");
            AssertUtil.notBlank(certificate.getLegalPersonIdCardPic1(), "法人身份证正面图片不能为空");
            AssertUtil.notBlank(certificate.getLegalPersonIdCardPic2(), "法人身份证背面图片不能为空");
            AssertUtil.notBlank(certificate.getIdCardStartDate(), "法人身份证有效期开始日期不能为空");
            AssertUtil.notBlank(certificate.getIdCardEndDate(), "法人身份证有效期截止日期不能为空");
            Date idCardStartDate = DateUtils.parseDate(certificate.getIdCardStartDate());
            Date idCardEndDate = DateUtils.parseDate(certificate.getIdCardEndDate());
            if(null == idCardStartDate)
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "法人身份证有效期开始日期格式错误");
            if(null == idCardEndDate)
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "法人身份证有效期截止日期格式错误");
            if(idCardStartDate.compareTo(idCardEndDate) > 0)
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "法人身份证有效期开始日期不能大于截止日期");
            if(certificate.getLegalPersonIdCardPic1().split(SupplyConstants.Symbol.COMMA).length > 1)
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("法人身份证正面图片最多只能上传1张"));
            if(certificate.getLegalPersonIdCardPic2().split(SupplyConstants.Symbol.COMMA).length > 1)
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("法人身份证背面图片最多只能上传1张"));
            if (StringUtils.equals(SupplyConstants.Supply.Supplier.NORMAL_THREE_CERTIFICATE, supplier.getCertificateTypeId())) {//普通三证
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
                Date businessLicenceStartDate = DateUtils.parseDate(certificate.getBusinessLicenceStartDate());
                Date businessLicenceEndDate = DateUtils.parseDate(certificate.getBusinessLicenceEndDate());
                Date organRegistraStartDate = DateUtils.parseDate(certificate.getOrganRegistraStartDate());
                Date organRegistraEndDate = DateUtils.parseDate(certificate.getOrganRegistraEndDate());
                Date taxRegistrationStartDate = DateUtils.parseDate(certificate.getTaxRegistrationStartDate());
                Date taxRegistrationEndDate = DateUtils.parseDate(certificate.getTaxRegistrationEndDate());
                if(null == businessLicenceStartDate)
                    throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "营业执照有效期开始日期格式错误");
                if(null == businessLicenceEndDate)
                    throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "营业执照有效期截止日期格式错误");
                if(null == organRegistraStartDate)
                    throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "组织机构代码证有效期开始日期格式错误");
                if(null == organRegistraEndDate)
                    throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "组织机构代码证有效期截止日期格式错误");
                if(null == taxRegistrationStartDate)
                    throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "税务登记证有效期开始日期格式错误");
                if(null == taxRegistrationEndDate)
                    throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "税务登记证有效期截止日期格式错误");
                if(businessLicenceStartDate.compareTo(businessLicenceEndDate) > 0)
                    throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "营业执照有效期开始日期不能大于截止日期");
                if(organRegistraStartDate.compareTo(organRegistraEndDate) > 0)
                    throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "组织机构代码证有效期开始日期不能大于截止日期");
                if(taxRegistrationStartDate.compareTo(taxRegistrationEndDate) > 0)
                    throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "税务登记证有效期开始日期不能大于截止日期");

                if(certificate.getBusinessLicencePic().split(SupplyConstants.Symbol.COMMA).length > 1)
                    throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("法人身份证正面图片最多只能上传1张"));
                if(certificate.getOrganRegistraCodeCertificatePic().split(SupplyConstants.Symbol.COMMA).length > 1)
                    throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("组织机构代码证图片最多只能上传1张"));
                if(certificate.getTaxRegistrationCertificatePic().split(SupplyConstants.Symbol.COMMA).length > 1)
                    throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("税务登记证证件图片最多只能上传1张"));

            } else if (StringUtils.equals(SupplyConstants.Supply.Supplier.MULTI_CERTIFICATE_UNION, supplier.getCertificateTypeId())) {//多证合一
                AssertUtil.notBlank(certificate.getMultiCertificateCombineNo(), "多证合一证号不能为空");
                AssertUtil.notBlank(certificate.getMultiCertificateCombinePic(), "多证合一证件图片不能为空");
            } else {
                String msg = String.format("证件类型ID[%s]错误", supplier.getCertificateTypeId());
                log.error(msg);
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
            }
        } else if (StringUtils.equals(SupplyConstants.Supply.Supplier.OVERSEAS_SUPPLIER, supplier.getSupplierTypeCode())) {//国外供应商
            AssertUtil.notBlank(supplier.getCountry(), "所在国家不能为空");
        } else {
            if(!StringUtils.equals(SupplyConstants.Supply.Supplier.SUPPLIER_ONE_AGENT_SELLING, supplier.getSupplierKindCode())){
                String msg = String.format("供应商类型编码[%s]错误", supplier.getSupplierTypeCode());
                log.error(msg);
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
            }
        }
        if (StringUtils.equals(SupplyConstants.Supply.Supplier.SUPPLIER_ONE_AGENT_SELLING, supplier.getSupplierKindCode())) {//一件代发供应商
            AssertUtil.notBlank(supplier.getSupplierInterfaceId(), "供应商接口ID不能为空");
            Supplier supplier2 = new Supplier();
            supplier2.setSupplierKindCode(SupplyConstants.Supply.Supplier.SUPPLIER_ONE_AGENT_SELLING);//一件代发
            supplier2.setSupplierInterfaceId(supplier.getSupplierInterfaceId());
            List<Supplier> supplierList = supplierService.select(supplier2);
            if(StringUtils.equals(flag, ZeroToNineEnum.ZERO.getCode()))
                AssertUtil.isTrue(supplierList.size()==0, String.format("供应商接口ID为“%s”的供应商已存在！", supplier.getSupplierInterfaceId()));
            else {
                if(supplierList.size() > 0){
                    if(supplierList.size() == 1){
                        AssertUtil.isTrue(StringUtils.equals(supplier.getSupplierCode(), supplierList.get(0).getSupplierCode()), String.format("供应商接口ID为“%s”的供应商已存在！", supplier.getSupplierInterfaceId()));
                    }else{
                        throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("供应商接口ID为“%s”的供应商已存在！", supplier.getSupplierInterfaceId()));
                    }
                }
            }
        }
    }


    /**
     * 供应链分类校验
     *
     * @param supplierCategory
     */
    private void checkSupplierCategory(SupplierCategory supplierCategory) {
        AssertUtil.notNull(supplierCategory.getSupplierId(), "供应链分类中供应链ID不能为空");
        AssertUtil.notBlank(supplierCategory.getSupplierCode(), "供应链分类中供应链编码不能为空");
        AssertUtil.notNull(supplierCategory.getCategoryId(), "供应链分类中分类ID不能为空");
    }

    /**
     * 供应链品牌校验
     *
     * @param supplierBrand
     */
    private void checkSupplierBrand(SupplierBrand supplierBrand) {
        AssertUtil.notNull(supplierBrand.getSupplierId(), "供应链品牌中供应链ID不能为空");
        AssertUtil.notBlank(supplierBrand.getSupplierCode(), "供应链品牌中供应链编码不能为空");
        AssertUtil.notNull(supplierBrand.getCategoryId(), "供应链品牌中分类ID不能为空");
        AssertUtil.notBlank(supplierBrand.getCategoryCode(), "供应链品牌中分类编码不能为空");
        AssertUtil.notNull(supplierBrand.getBrandId(), "供应链品牌中品牌ID不能为空");
        AssertUtil.notBlank(supplierBrand.getBrandCode(), "供应链品牌中品牌编码不能为空");
        AssertUtil.notBlank(supplierBrand.getProxyAptitudeId(), "供应链品牌中代理资质编号不能为空");
        AssertUtil.notBlank(supplierBrand.getProxyAptitudeStartDate(), "供应链品牌中资质有效期开始日期不能为空");
        AssertUtil.notBlank(supplierBrand.getProxyAptitudeEndDate(), "供应链品牌中资质有效期截止日期不能为空");
        AssertUtil.notBlank(supplierBrand.getAptitudePic(), "供应链品牌中资质证明图片不能为空");

    }

    /**
     * 保存供应商基础信息
     *
     * @param supplier
     * @return
     * @throws Exception
     */
    private void saveSupplierBase(Supplier supplier) throws Exception {
        //新增
        ParamsUtil.setBaseDO(supplier);
        int count = supplierService.insert(supplier);
        if (count == 0) {
            String msg = CommonUtil.joinStr("保存供应商", JSON.toJSONString(supplier), "到数据库失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION, msg);
        }
    }

    /**
     * 更新供应商基本信息
     *
     * @param supplier
     * @throws Exception
     */
    private void updateSupplierBase(Supplier supplier) throws Exception {
        supplier.setUpdateTime(Calendar.getInstance().getTime());
        int count = supplierService.updateByPrimaryKeySelective(supplier);
        if (count == 0) {
            String msg = CommonUtil.joinStr("修改供应商基本信息", JSON.toJSONString(supplier), "数据库操作失败").toString();
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
     *
     * @param certificate
     */
    private void updateCertificate(Certificate certificate) {
        Example example = new Example(Certificate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("supplierCode", certificate.getSupplierCode());
        criteria.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
        certificate.setUpdateTime(Calendar.getInstance().getTime());
        certificate.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        int count = certificateService.updateByExample(certificate, example);
        if (count == 0) {
            String msg = CommonUtil.joinStr("修改供应商证件信息", JSON.toJSONString(certificate), "数据库操作失败").toString();
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
    private void saveSupplierChannelRelation(List<SupplierChannelRelation> supplierChannelRelations) {
        int count = supplierChannelRelationService.insertList(supplierChannelRelations);
        if (count == 0) {
            String msg = CommonUtil.joinStr("保存供应商渠道关系", JSON.toJSONString(supplierChannelRelations), "到数据库失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION, msg);
        }
    }

    /**
     * 更新供应商渠道关系
     *
     * @param supplierChannelRelations
     * @return
     */
    private void updateSupplierChannelRelation(List<SupplierChannelRelation> supplierChannelRelations, Supplier supplier) {
        //查询当前供应商渠道关系
        SupplierChannelRelation relation = new SupplierChannelRelation();
        relation.setSupplierId(supplier.getId());
        relation.setSupplierCode(supplier.getSupplierCode());
        relation.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        List<SupplierChannelRelation> currentRelation = supplierChannelRelationService.select(relation);
        if (null == currentRelation) {
            String msg = String.format("根据供应商查询%s供应商渠道关系为空", JSON.toJSONString(relation));
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_QUERY_EXCEPTION, msg);
        }
        //新增关系列表
        List<SupplierChannelRelation> addRelations = new ArrayList<SupplierChannelRelation>();
        for (SupplierChannelRelation r : supplierChannelRelations) {
            Date currentDate = Calendar.getInstance().getTime();
            r.setCreateTime(currentDate);
            r.setUpdateTime(currentDate);
            Boolean flag = false;
            for (SupplierChannelRelation r2 : currentRelation) {
                if (StringUtils.equals(r.getChannelCode(), r2.getChannelCode())) {
                    flag = true;
                }
            }
            if (!flag) {
                r.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                addRelations.add(r);
            }
        }
        //删除关系列表
        List<Long> delIds = new ArrayList<Long>();
        for (SupplierChannelRelation r : currentRelation) {
            Boolean flag = false;
            for (SupplierChannelRelation r2 : supplierChannelRelations) {
                if (StringUtils.equals(r.getChannelCode(), r2.getChannelCode())) {
                    flag = true;
                }
            }
            if (!flag) {
                r.setIsDeleted(ZeroToNineEnum.ONE.getCode());
                delIds.add(r.getId());
            }
        }
        int count = 0;
        if (addRelations.size() > 0) {
            count = supplierChannelRelationService.insertList(addRelations);
            if (count == 0) {
                String msg = CommonUtil.joinStr("保存供应商渠道关系", JSON.toJSONString(addRelations), "到数据库失败").toString();
                log.error(msg);
                throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION, msg);
            }
        }
        if (delIds.size() > 0) {
            for (Long id : delIds) {
                count = supplierChannelRelationService.deleteByPrimaryKey(id);
                if (count == 0) {
                    String msg = CommonUtil.joinStr("根据供应商渠道关系ID[%s]删除供应商渠道关系", id.toString(), "失败").toString();
                    log.error(msg);
                    throw new SupplierException(ExceptionEnum.SUPPLIER_UPDATE_EXCEPTION, msg);
                }
            }
        }
    }

    /**
     * 保存供应商代理类目
     *
     * @param supplierCategory
     */
    private void saveCategory(SupplierCategory supplierCategory) throws Exception {
        AssertUtil.notBlank(supplierCategory.getSupplierCetegory(), "新增供应商代理类目不能为空");
        JSONArray categoryArray = JSONArray.parseArray(supplierCategory.getSupplierCetegory());
        AssertUtil.notEmpty(categoryArray, "新增供应商代理类目不能为空");
        List<SupplierCategory> list = new ArrayList<SupplierCategory>();
        for (Object obj : categoryArray) {
            JSONObject jbo = (JSONObject) obj;
            SupplierCategory s = new SupplierCategory();
            s.setSupplierId(supplierCategory.getSupplierId());
            s.setSupplierCode(supplierCategory.getSupplierCode());
            s.setCategoryId(jbo.getLong("categoryId"));
            s.setIsValid(ZeroToNineEnum.ONE.getCode());
            s.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            checkSupplierCategory(s);
            //检查分类启停用状态
            checkCategoryBrandValidStatus(s.getCategoryId(), null);
            list.add(s);
        }
        int count = supplierCategoryService.insertList(list);
        if (count == 0) {
            String msg = CommonUtil.joinStr("保存供应商代理类目", JSON.toJSONString(supplierCategory), "到数据库失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION, msg);
        }
    }

    /**
     * 保存供应商代理类目
     *
     * @param supplierCategory
     */
    private void updateCategory(SupplierCategory supplierCategory) throws Exception {
        int count = 0;
        JSONArray categoryArray = JSONArray.parseArray(supplierCategory.getSupplierCetegory());
        List<SupplierCategory> addList = new ArrayList<SupplierCategory>();
        List<Long> deleteList = new ArrayList<Long>();
        List<SupplierCategory> tmpList = new ArrayList<SupplierCategory>();
        SupplierCategory tmp = new SupplierCategory();
        tmp.setSupplierCode(supplierCategory.getSupplierCode());
        tmp.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        List<SupplierCategory> currentCategorys = supplierCategoryService.select(tmp);
        Date sysTime = Calendar.getInstance().getTime();
        for (Object obj : categoryArray) {
            JSONObject jbo = (JSONObject) obj;
            SupplierCategory s = new SupplierCategory();
            s.setId(jbo.getLong("id"));
            s.setSupplierId(supplierCategory.getSupplierId());
            s.setSupplierCode(supplierCategory.getSupplierCode());
            s.setCategoryId(jbo.getLong("categoryId"));
            s.setUpdateTime(sysTime);
            String isValid = jbo.getString("isValid");
            s.setIsValid(isValid);
            //检查分类启停用状态
            checkCategoryBrandValidStatus(s.getCategoryId(), null);
            s.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            checkSupplierCategory(s);
            tmpList.add(s);
            Boolean flag = false;
            for (SupplierCategory sp : currentCategorys) {
                if (s.getCategoryId().longValue() == sp.getCategoryId().longValue()) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                s.setIsValid(ValidEnum.VALID.getCode());
                addList.add(s);
            }
        }
        for (SupplierCategory s : currentCategorys) {
            Boolean flag = false;
            for (SupplierCategory s2 : tmpList) {
                if (s.getCategoryId() == s2.getCategoryId()) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                deleteList.add(s.getId());
            }
        }
        //新增
        if (addList.size() > 0) {
            count = supplierCategoryService.insertList(addList);
            if (count == 0) {
                String msg = CommonUtil.joinStr("保存供应商代理类目", JSON.toJSONString(supplierCategory), "到数据库失败").toString();
                log.error(msg);
                throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION, msg);
            }
        }
        //删除
        if (deleteList.size() > 0) {
            for (Long id : deleteList) {
                count = supplierCategoryService.deleteByPrimaryKey(id);
                if (count == 0) {
                    String msg = String.format("根据供应商代理类目主键[%s]删除供应商代理类目失败", id);
                    log.error(msg);
                    throw new SupplierException(ExceptionEnum.SUPPLIER_UPDATE_EXCEPTION, msg);
                }
            }
        }
    }

    /**
     * 保存供应商代理品牌
     *
     * @param brand
     */
    private void saveBrand(SupplierBrand brand) throws Exception {
        AssertUtil.notBlank(brand.getSupplierBrand(), "新增供应商代理品牌不能为空");
        JSONArray categoryArray = JSONArray.parseArray(brand.getSupplierBrand());
        AssertUtil.notEmpty(categoryArray, "新增供应商代理品牌不能为空");
        List<SupplierBrand> list = new ArrayList<SupplierBrand>();
        for (Object obj : categoryArray) {
            JSONObject jbo = (JSONObject) obj;
            checkSupplierBrand(jbo);
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
            s.setIsValid(ZeroToNineEnum.ONE.getCode());
            s.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            checkSupplierBrand(s);
            //检查品牌启停用状态
            checkCategoryBrandValidStatus(s.getCategoryId(), s.getBrandId());
            list.add(s);
        }
        int count = supplierBrandService.insertList(list);
        if (count == 0) {
            String msg = CommonUtil.joinStr("保存供应商代理品牌", JSON.toJSONString(list), "到数据库失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION, msg);
        }
    }

    /**
     * 检查分类品牌启停用状态
     *
     * @param categoryId
     * @param brandId
     * @throws Exception
     */
    public void checkCategoryBrandValidStatus(Long categoryId, Long brandId) throws Exception {
        AssertUtil.notNull(categoryId, "检查分类品牌启停用状态分类ID不能为空");
        Category category = categoryService.selectByPrimaryKey(categoryId);
        AssertUtil.notNull(category, String.format("根据主键ID[%s]查询分类信息为空", categoryId));
        if (StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), category.getIsValid())) {
            throw new GoodsException(ExceptionEnum.GOODS_DEPEND_DATA_INVALID, String.format("分类\"%s\"已停用,请删除!", category.getName()));
        } else {
            if (null != brandId) {
                Brand brand = brandService.selectByPrimaryKey(brandId);
                AssertUtil.notNull(brand, String.format("根据主键ID[%s]查询品牌信息为空", brandId));
                if (StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), brand.getIsValid())) {
                    throw new GoodsException(ExceptionEnum.GOODS_DEPEND_DATA_INVALID, String.format("品牌\"%s\"已停用,请删除!", brand.getName()));
                }
                CategoryBrand categoryBrand = new CategoryBrand();
                categoryBrand.setCategoryId(categoryId);
                categoryBrand.setBrandId(brandId);
                categoryBrand = categoryBrandService.selectOne(categoryBrand);
                if (null == categoryBrand || StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), categoryBrand.getIsValid())) {
                    throw new GoodsException(ExceptionEnum.GOODS_DEPEND_DATA_INVALID, String.format("分类\"%s\"和品牌\"%s\"关联已停用,请删除!", category.getName(), brand.getName()));
                }
            }
        }
    }

    /**
     * 更新供应商代理品牌
     *
     * @param brand
     */
    private void updateBrand(SupplierBrand brand) throws Exception {
        int count = 0;
        JSONArray categoryArray = JSONArray.parseArray(brand.getSupplierBrand());
        List<SupplierBrand> addlist = new ArrayList<SupplierBrand>();
        List<SupplierBrand> updateList = new ArrayList<SupplierBrand>();
        List<SupplierBrand> delList = new ArrayList<SupplierBrand>();
        Date sysTime = Calendar.getInstance().getTime();
        for (Object obj : categoryArray) {
            JSONObject jbo = (JSONObject) obj;
            checkSupplierBrand(jbo);
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
            s.setUpdateTime(sysTime);
            String isValid = jbo.getString("isValid");
            s.setIsValid(isValid);
            if (!StringUtils.equals(ZeroToNineEnum.THREE.getCode(), jbo.getString("status"))) {//不是删除状态的品牌
                //检查品牌启停用状态
                checkCategoryBrandValidStatus(s.getCategoryId(), s.getBrandId());
            }
            s.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            checkSupplierBrand(s);
            if (StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), jbo.getString("source"))) {//原始数据
                if (StringUtils.equals(ZeroToNineEnum.TWO.getCode(), jbo.getString("status"))) {//已修改
                    updateList.add(s);
                } else if (StringUtils.equals(ZeroToNineEnum.THREE.getCode(), jbo.getString("status"))) {//已删除
                    delList.add(s);
                }
            } else {//新增数据
                s.setIsValid(ValidEnum.VALID.getCode());
                addlist.add(s);
            }
        }
        if (updateList.size() > 0) {
            for (SupplierBrand supplierBrand : updateList) {
                Example example = new Example(SupplierBrand.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("supplierCode", supplierBrand.getSupplierCode());
                criteria.andEqualTo("categoryId", supplierBrand.getCategoryId());
                criteria.andEqualTo("brandId", supplierBrand.getBrandId());
                count = supplierBrandService.updateByExampleSelective(supplierBrand, example);
                if (count == 0) {
                    String msg = String.format("更新供应商代理品牌%s失败", JSON.toJSONString(supplierBrand));
                    log.error(msg);
                    throw new SupplierException(ExceptionEnum.SUPPLIER_UPDATE_EXCEPTION, msg);
                }
            }
        }
        if (delList.size() > 0) {
            for (SupplierBrand supplierBrand : delList) {
                Example example = new Example(SupplierBrand.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("supplierCode", supplierBrand.getSupplierCode());
                criteria.andEqualTo("categoryId", supplierBrand.getCategoryId());
                criteria.andEqualTo("brandId", supplierBrand.getBrandId());
                supplierBrandService.deleteByExample(example);
            }
        }
        if (addlist.size() > 0) {
            count = supplierBrandService.insertList(addlist);
            if (count == 0) {
                String msg = CommonUtil.joinStr("保存供应商代理品牌", JSON.toJSONString(addlist), "到数据库失败").toString();
                log.error(msg);
                throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION, msg);
            }
        }
    }

    /**
     * 供应商代理品牌校验
     * @param supplierBrand
     */
    private void checkSupplierBrand(JSONObject supplierBrand){
        AssertUtil.notNull(supplierBrand.getLong("categoryId"), "代理品牌所属分类ID不能为空");
        AssertUtil.notNull(supplierBrand.getLong("brandId"), "代理品牌ID不能为空");
        String categoryBrand = String.format("分类[%s]下品牌[%s]", supplierBrand.getString("categoryName"), supplierBrand.getString("brandName"));
        AssertUtil.notBlank(supplierBrand.getString("proxyAptitudeId"), String.format("%s代理资质不能为空", categoryBrand));
        AssertUtil.notBlank(supplierBrand.getString("proxyAptitudeStartDate"), String.format("%s资质有效期(开始)不能为空", categoryBrand));
        AssertUtil.notBlank(supplierBrand.getString("proxyAptitudeEndDate"), String.format("%s资质有效期(截止)不能为空", categoryBrand));
        AssertUtil.notBlank(supplierBrand.getString("aptitudePic"), String.format("%s资质证明不能为空", categoryBrand));
        AssertUtil.notBlank(supplierBrand.getString("isValid"), String.format("%s启停用不能为空", categoryBrand));
        Date proxyAptitudeStartDate = DateUtils.timestampToDate(supplierBrand.getLong("proxyAptitudeStartDate"));
        if(null == proxyAptitudeStartDate)
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("%s资质有效期(开始)格式错误", categoryBrand));
        Date proxyAptitudeEndDate = DateUtils.timestampToDate(supplierBrand.getLong("proxyAptitudeEndDate"));
        if(null == proxyAptitudeEndDate)
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("%s资质有效期(截止)格式错误", categoryBrand));
        if(proxyAptitudeStartDate.compareTo(proxyAptitudeEndDate) > 0)
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("%s资质有效期开始日期大于截止日期", categoryBrand));
        if(supplierBrand.getString("aptitudePic").split(SupplyConstants.Symbol.COMMA).length > 3)
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("%s资质证明图片最多只能上传3张", categoryBrand));
    }

    /**
     * 保存供应商财务信息
     *
     * @param supplierFinancialInfo
     */
    private void saveFinancial(SupplierFinancialInfo supplierFinancialInfo) {
        int count = 0;
        ParamsUtil.setBaseDO(supplierFinancialInfo);
        count = supplierFinancialInfoService.insert(supplierFinancialInfo);
        if (count == 0) {
            String msg = CommonUtil.joinStr("保存供应商财务信息", JSON.toJSONString(supplierFinancialInfo), "到数据库失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION, msg);
        }
    }

    /**
     * 修改供应商财务信息
     *
     * @param supplierFinancialInfo
     */
    private void updateFinancial(SupplierFinancialInfo supplierFinancialInfo) {
        Example example = new Example(SupplierFinancialInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("supplierCode", supplierFinancialInfo.getSupplierCode());
        criteria.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
        supplierFinancialInfo.setUpdateTime(Calendar.getInstance().getTime());
        supplierFinancialInfo.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        int count = supplierFinancialInfoService.updateByExample(supplierFinancialInfo, example);
        if (count == 0) {
            String msg = CommonUtil.joinStr("修改供应商财务", JSON.toJSONString(supplierFinancialInfo), "数据库操作失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_UPDATE_EXCEPTION, msg);
        }
    }

    /**
     * 保存供应商售后信息
     *
     * @param supplierAfterSaleInfo
     */
    private void saveAfterSale(SupplierAfterSaleInfo supplierAfterSaleInfo) {
        int count = 0;
        ParamsUtil.setBaseDO(supplierAfterSaleInfo);
        count = supplierAfterSaleInfoService.insert(supplierAfterSaleInfo);
        if (count == 0) {
            String msg = CommonUtil.joinStr("保存供应商售后信息", JSON.toJSONString(supplierAfterSaleInfo), "到数据库失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION, msg);
        }
    }

    /**
     * 修改供应商财务信息
     *
     * @param supplierAfterSaleInfo
     */
    private void updateAfterSale(SupplierAfterSaleInfo supplierAfterSaleInfo) {
        Example example = new Example(SupplierAfterSaleInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("supplierCode", supplierAfterSaleInfo.getSupplierCode());
        criteria.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
        supplierAfterSaleInfo.setUpdateTime(Calendar.getInstance().getTime());
        supplierAfterSaleInfo.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        int count = supplierAfterSaleInfoService.updateByExample(supplierAfterSaleInfo, example);
        if (count == 0) {
            String msg = CommonUtil.joinStr("修改供应商售后信息", JSON.toJSONString(supplierAfterSaleInfo), "数据库操作失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_UPDATE_EXCEPTION, msg);
        }
    }


    @Override
    @Cacheable(key="#supplierCode", isList = true)
    public List<SupplierCategoryExt> querySupplierCategory(String supplierCode) throws Exception {
        AssertUtil.notBlank(supplierCode, "查询供应商代理分类供应商编码不能为空");
        List<SupplierCategoryExt> supplierCategoryExtList = supplierCategoryService.selectSupplierCategorys(supplierCode);
        AssertUtil.notEmpty(supplierCategoryExtList, String.format("根据供应商编码[%s]查询供应商代理类目为空", supplierCode));
        for (SupplierCategoryExt supplierCategoryExt : supplierCategoryExtList) {
            String categoryName = categoryBiz.getCategoryName(supplierCategoryExt.getCategoryId());
            supplierCategoryExt.setCategoryName(categoryName);
        }
        return supplierCategoryExtList;
    }


    @Override
    @Cacheable(key="#supplierCode", isList = true)
    public List<SupplierBrandExt> querySupplierBrand(String supplierCode) throws Exception {
        AssertUtil.notBlank(supplierCode, "查询供应商代理品牌供应商编码不能为空");
        List<SupplierBrandExt> supplierBrandExtList = supplierBrandService.selectSupplierBrands(supplierCode);
        AssertUtil.notEmpty(supplierBrandExtList, String.format("根据供应商编码[%s]查询供应商代理品牌为空", supplierCode));
        for (SupplierBrandExt supplierBrandExt : supplierBrandExtList) {
            String categoryName = categoryBiz.getCategoryName(supplierBrandExt.getCategoryId());
            supplierBrandExt.setCategoryName(categoryName);
        }
        return supplierBrandExtList;
    }

    @Override
    public SupplierExt querySupplierInfo(String supplierCode) throws Exception {
        AssertUtil.notBlank(supplierCode, "查询供应商信息供应商编码不能为空");
        SupplierExt supplierExt = new SupplierExt();
        Supplier supplier = new Supplier();
        supplier.setSupplierCode(supplierCode);
        supplier.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        supplier = supplierService.selectOne(supplier);
        AssertUtil.notNull(supplier, String.format("%s%s%s", "根据供应商编码[", supplierCode, "]查询供应商基本信息为空"));

        if (StringUtils.equals(SupplyConstants.Supply.Supplier.INTERNAL_SUPPLIER, supplier.getSupplierTypeCode())) {
            Certificate certificate = new Certificate();
            certificate.setSupplierCode(supplierCode);
            certificate = certificateService.selectOne(certificate);
            AssertUtil.notNull(certificate, String.format("%s%s%s", "根据供应商编码[", supplierCode, "]查询供应商证件信息为空"));
            supplierExt.setCertificate(certificate);
        }
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

        supplierExt.setSupplier(supplier);
        supplierExt.setSupplierFinancialInfo(supplierFinancialInfo);
        supplierExt.setSupplierAfterSaleInfo(supplierAfterSaleInfo);
        supplierExt.setSupplierChannelRelations(supplierChannelRelations);
        return supplierExt;
    }

    @Override
    @Cacheable(key="#form.toString()",isList=true)
    public List<SupplierChannelRelationExt> queryChannelRelation(SupplierChannelRelationForm form) throws Exception {
        AssertUtil.notNull(form, "查询供应商渠道关系参数SupplierChannelRelationForm不能为空");
        if (null == form.getSupplierId() && StringUtils.isBlank(form.getSupplierCode()) &&
                null == form.getChannelId() && StringUtils.isBlank(form.getChannelCode())) {
            String msg = "查询供应商渠道关系参数供应商ID、供应商编码、渠道ID、渠道编码不能同时为空";
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        SupplierChannelRelation relation = new SupplierChannelRelation();
        BeanUtils.copyProperties(form, relation);
        relation.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        List<SupplierChannelRelationExt> relations = supplierChannelRelationService.
                selectSupplierChannels(BeanToMapUtil.convertBeanToMap(relation));
        if (null == relations) {
            String msg = String.format("根据供应商查询%s供应商渠道关系为空", JSON.toJSONString(form));
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_QUERY_EXCEPTION, msg);
        }
        return relations;
    }

    @Override
    @CacheEvit
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateValid(Long id, String isValid, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notNull(id, "供应商启用/停用操作供应商ID不能为空");
        AssertUtil.notBlank(isValid, "供应商启用/停用操作参数isValid不能为空");
        Supplier supplier = new Supplier();
        supplier.setId(id);
        supplier.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        if (StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), isValid)) {
            supplier.setIsValid(ZeroToNineEnum.ONE.getCode());
        } else {
            supplier.setIsValid(ZeroToNineEnum.ZERO.getCode());
        }
        int count = supplierService.updateByPrimaryKeySelective(supplier);
        if (count == 0) {
            String msg = "供应商启用/停用操作更新数据库失败";
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_UPDATE_EXCEPTION, msg);
        }
        //禁用供应商时将申请该供应商的审批状态为提交审批的供应商申请记录状态改为驳回
        if (StringUtils.equals(supplier.getIsValid(), ZeroToNineEnum.ZERO.getCode())) {
            rejectSupplierApply(id, aclUserAccreditInfo);
        }
        //记录操作日志
        logInfoService.recordLog(supplier, supplier.getId().toString(), aclUserAccreditInfo.getUserId(),
                LogOperationEnum.UPDATE.getMessage(), String.format("状态更新为%s", ValidEnum.getValidEnumByCode(supplier.getIsValid()).getName()), null);
    }

    /**
     * 驳回供应商申请
     *
     * @param supplierId 供应商ID
     * @throws Exception
     */
    private void rejectSupplierApply(Long supplierId, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notNull(supplierId, "供应商启/停用更新供应商申请审批状态参数供应商编码supplierCode不能为空");
        SupplierApply supplierApply = new SupplierApply();
        supplierApply.setSupplierId(supplierId);
        supplierApply.setStatus(AuditStatusEnum.COMMIT.getCode());//提交审核
        List<SupplierApply> supplierApplyList = supplierApplyService.select(supplierApply);
        Date sysDate = Calendar.getInstance().getTime();
        for (SupplierApply supplierApply2 : supplierApplyList) {
            supplierApply2.setStatus(AuditStatusEnum.REJECT.getCode());//审核驳回
            supplierApply2.setAuditOpinion(STOP_SUPPLIER_REJECT_APPLY_REASON);
            supplierApply2.setUpdateTime(sysDate);
            int count = supplierApplyService.updateByPrimaryKey(supplierApply2);
            if (count == 0) {
                String msg = String.format("停用ID为[%s]的供应商自动驳回供应商对应的申请%s失败", supplierId, JSONObject.toJSON(supplierApply2));
                log.error(msg);
                throw new SupplierException(ExceptionEnum.SUPPLIER_UPDATE_EXCEPTION, msg);
            } else {
                String userId = aclUserAccreditInfo.getUserId();
                AssertUtil.notBlank(userId, "记录供应商停用自动驳回供应商申请审批获取登录用户ID为空");
                //记录操作日志
                logInfoService.recordLog(supplierApply2, supplierApply2.getId().toString(), "admin",
                        LogOperationEnum.AUDIT_REJECT.getMessage(), "供应商停用，系统自动驳回", null);
            }
        }

    }

}
