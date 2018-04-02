package org.trc.biz.impl.category;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
import org.trc.model.SearchResult;
import org.trc.service.IPageNationService;
import org.trc.service.category.IBrandService;
import org.trc.service.category.ICategoryBrandService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.supplier.ISupplierBrandService;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.*;
import org.trc.util.cache.BrandCacheEvict;
import tk.mybatis.mapper.entity.Example;

import java.text.Collator;
import java.util.*;

/**
 * Created by hzqph on 2017/4/28.
 */
@Service("brandBiz")
public class BrandBiz implements IBrandBiz {

    private Logger log = LoggerFactory.getLogger(BrandBiz.class);
    private final static String BRAND_CODE_EX_NAME = "PP";
    private final static int BRAND_CODE_LENGTH = 5;
    private final static String  EXTERNAL_IMG_URL_HEAD= "https";
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
    @Autowired
    private IPageNationService pageNationService;

    @Override
    @Cacheable(value = SupplyConstants.Cache.BRAND)
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
    public Pagenation<Brand> brandPageES(BrandForm queryModel, Pagenation<Brand> page) throws Exception {
        TransportClient clientUtil = TransportClientUtil.getTransportClient();
        HighlightBuilder hiBuilder = new HighlightBuilder();
        hiBuilder.preTags("<b style=\"color: red\">");
        hiBuilder.postTags("</b>");
        hiBuilder.field("name.pinyin");//http://172.30.250.164:9100/ 模糊字段可在这里找到
        SearchRequestBuilder srb = clientUtil.prepareSearch("item_brand")//es表名
                .highlighter(hiBuilder)
                .setFrom(page.getStart())//第几个开始
                .setSize(page.getPageSize());//长度
        if (StringUtils.isNotBlank(queryModel.getName())) {
            QueryBuilder matchQuery = QueryBuilders.matchQuery("name.pinyin", queryModel.getName());
            srb.setQuery(matchQuery);
        }
        if (!StringUtils.isBlank(queryModel.getIsValid())) {
            QueryBuilder filterBuilder = QueryBuilders.termQuery("is_valid", queryModel.getIsValid());
            srb.setPostFilter(filterBuilder);
        }
        SearchResult searchResult;
        try {
            searchResult = pageNationService.resultES(srb, clientUtil);
        } catch (Exception e) {
            log.error("es查询失败" + e.getMessage(), e);
            return page;
        }
        List<Brand> brandList = new ArrayList<>();
        for (SearchHit searchHit : searchResult.getSearchHits()) {
            Brand brand = JSON.parseObject(JSON.toJSONString(searchHit.getSource()), Brand.class);
            if (StringUtils.isNotBlank(queryModel.getName())) {
                for (Text text : searchHit.getHighlightFields().get("name.pinyin").getFragments()) {
                    brand.setHighLightName(text.string());
                }
            }
            brandList.add(brand);
        }

        if(AssertUtil.collectionIsEmpty(brandList)){
            return page;
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
        page.setResult(brandList);
        page.setTotalCount(searchResult.getCount());
        return page;
    }
    @Override
    public  List<String> associationSearch(String queryString) throws Exception{
        List<String> brandNameList = new ArrayList<>();
        if (StringUtils.isNotBlank(queryString)){
            TransportClient clientUtil = TransportClientUtil.getTransportClient();
            SearchRequestBuilder srb = clientUtil.prepareSearch("item_brand").setFrom(0)
                    //前10个
                    .setSize(10);
            MultiMatchQueryBuilder query = QueryBuilders.multiMatchQuery(queryString, "name.pinyin");
            srb.setQuery(query);
            pageNationService.resultES(srb, clientUtil);
            SearchResult searchResult;
            try {
                searchResult = pageNationService.resultES(srb, clientUtil);
            } catch (Exception e) {
                log.error("es查询失败" + e.getMessage(), e);
                return brandNameList;
            }
            for (SearchHit searchHit : searchResult.getSearchHits()) {
                Brand brand = JSON.parseObject(JSON.toJSONString(searchHit.getSource()), Brand.class);
                brandNameList.add(brand.getName());
            }
        }


        return  brandNameList;
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
            criteria.andLessThan("updateTime", DateUtils.formatDateTime(DateUtils.addDays(queryModel.getEndUpdateTime(),DateUtils.NORMAL_DATE_FORMAT,1)));
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
    //@Cacheable(value = SupplyConstants.Cache.BRAND)
    public List<Brand> queryBrands(BrandForm brandForm) throws Exception {
        Brand brand = new Brand();
        if (!StringUtils.isEmpty(brandForm.getIsValid())) {
            brand.setIsValid(brandForm.getIsValid());
        }
        brand.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        List<Brand> brandList = brandService.select(brand);
        Collections.sort(brandList, new Comparator<Brand>() {
            @Override
            public int compare(Brand o1, Brand o2) {
                return PinyinUtil.compare(o1.getName(), o2.getName());
            }
        });
        return brandList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @BrandCacheEvict
    public void saveBrand(Brand brand, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notNull(brand, "保存品牌信息，品牌不能为空");
        //初始化信息
        brand.setSource(SourceEnum.SCM.getCode());
        ParamsUtil.setBaseDO(brand);
        brand.setBrandCode(serialUtilService.generateCode(BRAND_CODE_LENGTH, BRAND_CODE_EX_NAME, DateUtils.dateToCompactString(brand.getCreateTime())));
        String userId= aclUserAccreditInfo.getUserId();
        if(!StringUtils.isBlank(userId)){
            brand.setCreateOperator(userId);
            brand.setLastEditOperator(userId);
        }
        try {
            brandService.insert(brand);
            //记录到日志表中不能影响到主体业务
            logInfoService.recordLog(brand,brand.getId().toString(),userId,LogOperationEnum.ADD.getMessage(),null,null);
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
    @Cacheable(value = SupplyConstants.Cache.BRAND)
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
    @BrandCacheEvict
    public void updateBrand(Brand brand, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notNull(brand.getId(), "更新品牌信息，品牌ID不能为空");
        String remark=null;
        Brand selectBrand=brandService.selectOneById(brand.getId());
        brand.setUpdateTime(Calendar.getInstance().getTime());
        String userId= aclUserAccreditInfo.getUserId();
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
                remark=remarkEnum.VALID_ON.getMessage();
            }else{
                remark=remarkEnum.VALID_OFF.getMessage();
            }
        }
        //记录到日志表中不能影响到主体业务
        logInfoService.recordLog(brand,brand.getId().toString(),userId,LogOperationEnum.UPDATE.getMessage(),remark,null);
        //通知渠道方
        Brand newBrand = brandService.selectOneById(brand.getId());
        try{
            trcBiz.sendBrand(TrcActionTypeEnum.EDIT_BRAND, selectBrand,newBrand,System.currentTimeMillis());
        }catch (Exception e){
            log.error("品牌修改通知调用出现异常:"+e.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @BrandCacheEvict
    public void updateBrandStatus(Brand brand, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
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
        String userId= aclUserAccreditInfo.getUserId();
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
        logInfoService.recordLog(brand,brand.getId().toString(),userId,LogOperationEnum.UPDATE.getMessage(),remark,null);
        //通知渠道方
        Brand newBrand = brandService.selectOneById(brand.getId());
            Runnable runnable = () -> {
                try {
                    trcBiz.sendBrand(TrcActionTypeEnum.EDIT_BRAND, selectBrand, newBrand, System.currentTimeMillis());
                } catch (Exception e) {
                    log.error("品牌状态变更通知调用出现异常" + e.getMessage(), e);
                }

            };

    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.BRAND)
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
        Set<String> externalImgUrlSet=new HashSet<>();
        for (Brand brand : brandList) {
            if (!StringUtils.isBlank(brand.getLogo())) {
                if(!brand.getLogo().startsWith(EXTERNAL_IMG_URL_HEAD)){
                    urlSet.add(brand.getLogo());
                }else{
                    externalImgUrlSet.add(brand.getLogo());
                }
            }
        }
        Map<String, String> fileUrlMap = new HashMap<>();
        if (null != urlSet && urlSet.size() > 0) {
            String[] urlStr = new String[urlSet.size()];
            urlSet.toArray(urlStr);
            List<FileUrl> fileUrlList = qinniuBiz.batchGetFileUrl(urlStr, ZeroToNineEnum.ONE.getCode());
            if (null != fileUrlList && fileUrlList.size() > 0) {
                for (FileUrl fileUrl : fileUrlList) {
                    fileUrlMap.put(fileUrl.getFileKey(), fileUrl.getUrl());
                }
            }
        }
        if (null != externalImgUrlSet && externalImgUrlSet.size() > 0) {
            for (String externalImgUrl:externalImgUrlSet) {
                fileUrlMap.put(externalImgUrl,externalImgUrl);
            }
        }
        return fileUrlMap;
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
