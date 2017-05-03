package org.trc.biz;

import org.trc.domain.category.Brand;
import org.trc.form.BrandForm;
import org.trc.util.Pagenation;

/**
 * Created by hzqph on 2017/4/27.
 */
public interface ICategoryBiz {

    /**
     * 品牌分页
     * @param form
     * @param page
     * @return
     * @throws Exception
     */
    public Pagenation<Brand> brandPage(BrandForm form,Pagenation<Brand> page)throws Exception;

    /**
     * 保存品牌
     * @param brand
     * @return
     * @throws Exception
     */
    public int saveBrand(Brand brand)throws Exception;

    /**
     * 根据id查询单个品牌
     * @param id
     * @return
     * @throws Exception
     */
    public Brand findBrandById(Long id)throws Exception;

    /**
     * 更新品牌信息
     * @param brand
     * @param id
     * @return
     * @throws Exception
     */
    public int updateBrand(Brand brand ,Long id)throws Exception;

    /**
     * 更新品牌信息
     * @param brand
     * @return
     * @throws Exception
     */
    public int updateBrandStatus(Brand brand)throws Exception;
}
