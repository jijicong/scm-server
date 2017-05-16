package org.trc.biz.impl.category;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.trc.biz.category.IBrandBiz;
import org.trc.biz.qinniu.IQinniuBiz;
import org.trc.domain.category.Brand;
import org.trc.enums.*;
import org.trc.exception.CategoryException;
import org.trc.exception.ParamValidException;
import org.trc.form.category.BrandForm;
import org.trc.form.FileUrl;
import org.trc.service.category.IBrandService;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * Created by hzqph on 2017/4/28.
 */
@Service("brandBiz")
public class BrandBiz implements IBrandBiz {

    private final static Logger log = LoggerFactory.getLogger(BrandBiz.class);
    private final static String BRAND_CODE_EX_NAME="PP";
    private final static int BRAND_CODE_LENGTH=5;

    @Autowired
    private IBrandService brandService;
    @Autowired
    private IQinniuBiz qinniuBiz;
    @Autowired
    private ISerialUtilService serialUtilService;

    @Override
    public Pagenation<Brand> brandPage(BrandForm queryModel, Pagenation<Brand> page) throws Exception {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
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
        example.orderBy("isValid").desc();
        example.orderBy("updateTime").desc();
        Pagenation<Brand> pagenation = brandService.pagination(example, page, queryModel);
        //得到所有图片的缩略图,并以fileKey为key，url为value的形式封装成map
        List<Brand> brandList = pagenation.getResult();
        Map<String, String> fileUrlMap = constructFileUrlMap(brandList);
        for (Brand brand : brandList) {
            if (!StringUtils.isBlank(brand.getLogo())) {
                brand.setLogo(fileUrlMap.get(brand.getLogo()));
            }
        }
        pagenation.setResult(brandList);
        return pagenation;
    }

    @Override
    public void saveBrand(Brand brand) throws Exception {
        AssertUtil.notNull(brand, "保存品牌信息，品牌不能为空");
        //插入固定信息
        brand.setSource(BrandSourceEnum.SCM.getCode());
        brand.setBrandCode(serialUtilService.getSerialCode(BRAND_CODE_LENGTH,BRAND_CODE_EX_NAME,DateUtils.dateToCompactString(new Date())));
        brand.setLastEditOperator("小明");//TODO 后期用户信息引入之后需要修改
        ParamsUtil.setBaseDO(brand);
        int count = brandService.insert(brand);
        if (count < 1) {
            String msg = CommonUtil.joinStr("保存品牌", JSON.toJSONString(brand), "到数据库失败").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_BRAND_UPDATE_EXCEPTION, msg);
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
    public void updateBrand(Brand brand) throws Exception {
        AssertUtil.notNull(brand.getId(), "更新品牌信息，品牌ID不能为空");
        brand.setUpdateTime(Calendar.getInstance().getTime());
        int count = brandService.updateByPrimaryKeySelective(brand);
        if (count < 1) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", brand.getId().toString(), "]更新品牌明细失败").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_BRAND_QUERY_EXCEPTION, msg);
        }
    }

    @Override
    public void updateBrandStatus(Brand brand) throws Exception {
        AssertUtil.notNull(brand.getId(), "需要更新品牌状态时，品牌不能为空");
        Brand updateBrand = new Brand();
        updateBrand.setId(brand.getId());
        updateBrand.setUpdateTime(Calendar.getInstance().getTime());
        if (brand.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updateBrand.setIsValid(ValidEnum.NOVALID.getCode());
        } else {
            updateBrand.setIsValid(ValidEnum.VALID.getCode());
        }
        int count = brandService.updateByPrimaryKeySelective(updateBrand);
        if (count < 1) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", brand.getId().toString(), "]更新品牌明细失败").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_BRAND_QUERY_EXCEPTION, msg);
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

}
