package org.trc.biz;

import org.trc.domain.category.Brand;
import org.trc.form.BrandForm;
import org.trc.util.Pagenation;

/**
 * Created by hzqph on 2017/4/27.
 */
public interface ICategoryBiz {

    public Pagenation<Brand> brandPage(BrandForm form,Pagenation<Brand> page)throws Exception;
}
