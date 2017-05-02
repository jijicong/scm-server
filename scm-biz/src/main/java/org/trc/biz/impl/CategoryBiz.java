package org.trc.biz.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.ICategoryBiz;
import org.trc.domain.category.Brand;
import org.trc.form.BrandForm;
import org.trc.service.IBrandService;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;

/**
 * Created by hzqph on 2017/4/28.
 */
@Service("categoryBiz")
public class CategoryBiz implements ICategoryBiz {
    @Autowired
    private IBrandService brandService;

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
        if(queryModel.getStartUpdateTime()!=null){
            criteria.andGreaterThan("createTime", queryModel.getStartUpdateTime());
        }
        if(queryModel.getEndUpdateTime()!=null){
            criteria.andLessThan("createTime",queryModel.getEndUpdateTime());
        }
        return brandService.pagination(example,page,queryModel);
    }
}
