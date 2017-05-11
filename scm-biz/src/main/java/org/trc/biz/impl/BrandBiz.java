package org.trc.biz.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.IBrandBiz;
import org.trc.biz.qinniu.IQinniuBiz;
import org.trc.domain.category.Brand;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ValidEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.CategoryException;
import org.trc.exception.ParamValidException;
import org.trc.form.BrandForm;
import org.trc.form.FileUrl;
import org.trc.service.IQinniuService;
import org.trc.service.category.IBrandService;
import org.trc.service.category.IPropertyService;
import org.trc.util.BeanToMapUtil;
import org.trc.util.CommonUtil;
import org.trc.util.Pagenation;
import org.trc.util.ParamsUtil;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * Created by hzqph on 2017/4/28.
 */
@Service("brandBiz")
public class BrandBiz implements IBrandBiz {

    private final static Logger log = LoggerFactory.getLogger(BrandBiz.class);

    @Autowired
    private IBrandService brandService;
//    @Autowired
//    private IQinniuBiz qinniuBiz;
    @Override
    public Pagenation<Brand> brandPage(BrandForm queryModel, Pagenation<Brand> page) throws Exception {
        Example example=new Example(Brand.class);
        Example.Criteria criteria=example.createCriteria();
        if(!StringUtils.isBlank(queryModel.getName())){
            criteria.andLike("name","%"+ queryModel.getName()+"%");
        }
        if(!StringUtils.isBlank(queryModel.getIsValid())){
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }
        if(!StringUtils.isBlank(queryModel.getStartUpdateTime())){
            criteria.andGreaterThan("updateTime", queryModel.getStartUpdateTime());
        }
        if(!StringUtils.isBlank(queryModel.getEndUpdateTime())){
            criteria.andLessThan("updateTime",queryModel.getEndUpdateTime());
        }
        example.orderBy("isValid").desc();
        example.orderBy("updateTime").desc();
        Pagenation<Brand> pagenation=brandService.pagination(example,page,queryModel);
//        //得到所有图片的缩略图,并以fileKey为key，url为value的形式封装成map
//        List<Brand> brandList=pagenation.getResult();
//        Map<String,String> fileUrlMap=constructFileUrlMap(brandList);
//        for (Brand brand:brandList) {
//            if(!StringUtils.isBlank(brand.getWebUrl())){
//                brand.setWebUrl(fileUrlMap.get(brand.getWebUrl()));
//            }
//        }
//        pagenation.setResult(brandList);
        return pagenation;
    }

    @Override
    public int saveBrand(Brand brand) throws Exception {
        int count=0;
        if(null!=brand.getId()){
            brand.setUpdateTime(new Date());
            count=brandService.updateByPrimaryKeySelective(brand);
        }else{
            ParamsUtil.setBaseDO(brand);
            count=brandService.insert(brand);
        }
        if(count<1){
            String msg= CommonUtil.joinStr("保存品牌", JSON.toJSONString(brand),"到数据库失败").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_BRAND_UPDATE_EXCEPTION,msg);
        }
        return count;
    }

    @Override
    public Brand findBrandById(Long id) throws Exception {
        if(null==id){
            String msg=CommonUtil.joinStr("根据ID查询品牌明细参数ID为空").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        Brand brand=new Brand();
        brand.setId(id);
        brand=brandService.selectOne(brand);
        if (null==brand){
            String msg=CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]查询品牌明细为空").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_BRAND_QUERY_EXCEPTION,msg);
        }
        return brand;
    }

    @Override
    public int updateBrand(Brand brand, Long id) throws Exception {
        if (null==id){
            String msg=CommonUtil.joinStr("根据ID更新品牌信息参数ID为空").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_BRAND_UPDATE_EXCEPTION,msg);
        }
        brand.setId(id);
        brand.setUpdateTime(new Date());
        int count =brandService.updateByPrimaryKeySelective(brand);
        if (count<1){
            String msg=CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]更新品牌明细失败").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_BRAND_QUERY_EXCEPTION,msg);
        }
        return count;
    }

    @Override
    public int updateBrandStatus(Brand brand) throws Exception {
        if(null==brand||null==brand.getId()){
            String msg=CommonUtil.joinStr("需要更新品牌状态的bean为空").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_BRAND_UPDATE_EXCEPTION,msg);
        }
        Brand updateBrand=new Brand();
        updateBrand.setId(brand.getId());
        if (brand.getIsValid().equals(ValidEnum.VALID.getCode())){
            updateBrand.setIsValid(ValidEnum.NOVALID.getCode());
        }else{
            updateBrand.setIsValid(ValidEnum.VALID.getCode());
        }
        int count=brandService.updateByPrimaryKeySelective(updateBrand);
        if (count<1){
            String msg=CommonUtil.joinStr("根据主键ID[id=",brand.getId().toString(),"]更新品牌明细失败").toString();
            log.error(msg);
            throw new CategoryException(ExceptionEnum.CATEGORY_BRAND_QUERY_EXCEPTION,msg);
        }
        return count;
    }

//    private Map<String,String> constructFileUrlMap(List<Brand> brandList)throws Exception{
//        Set<String> urlSet=new HashSet<>();
//        for (Brand brand:brandList) {
//            if(!StringUtils.isBlank(brand.getWebUrl())){
//                urlSet.add(brand.getWebUrl());
//            }
//        }
//        if(null!=urlSet&&urlSet.size()>0){
//            String[] urlStr=new String[urlSet.size()];
//            urlSet.toArray(urlStr);
//            List<FileUrl> fileUrlList=qinniuBiz.batchGetFileUrl(urlStr, ZeroToNineEnum.ONE.getCode());
//            if(null!=fileUrlList&&fileUrlList.size()>0){
//                Map<String,String> fileUrlMap=new HashMap<>();
//                for (FileUrl fileUrl :fileUrlList) {
//                    fileUrlMap.put(fileUrl.getFileKey(),fileUrl.getUrl());
//                }
//                return fileUrlMap;
//            }
//        }
//        return null;
//    }
}
