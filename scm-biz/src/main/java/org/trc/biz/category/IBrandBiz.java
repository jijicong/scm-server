package org.trc.biz.category;

import org.trc.domain.category.Brand;
import org.trc.form.category.BrandForm;
import org.trc.util.Pagenation;

import java.util.List;

/**
 * Created by hzqph on 2017/4/27.
 */
public interface IBrandBiz {

    /**
     * 品牌信息分页
     *
     * @param form
     * @param page
     * @return
     * @throws Exception
     */
    Pagenation<Brand> brandPage(BrandForm form, Pagenation<Brand> page) throws Exception;

    /**
     * 对泰然城提供分页
     *
     * @param form
     * @param page
     * @return
     * @throws Exception
     */
    Pagenation<Brand> brandList(BrandForm form, Pagenation<Brand> page) throws Exception;

    /**
     * 查询品牌列表
     *
     * @return
     * @throws Exception
     */
    List<Brand> queryBrands(BrandForm brandForm) throws Exception;

    /**
     * 保存品牌信息
     *
     * @param brand
     * @return
     * @throws Exception
     */
    void saveBrand(Brand brand) throws Exception;

    /**
     * 根据品牌id查询单个品牌
     *
     * @param id
     * @return
     * @throws Exception
     */
    Brand findBrandById(Long id) throws Exception;

    /**
     * 更新品牌信息
     *
     * @param brand
     * @return
     * @throws Exception
     */
    void updateBrand(Brand brand) throws Exception;

    /**
     * 更新品牌状态信息
     *
     * @param brand
     * @return
     * @throws Exception
     */
    void updateBrandStatus(Brand brand) throws Exception;

    /**
     * 根据品牌名称查询品牌列表
     *
     * @param name
     * @return
     * @throws Exception
     */
    List<Brand> findBrandsByName(String name) throws Exception;
}
