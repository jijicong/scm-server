package org.trc.service.trc;


import org.trc.domain.category.Category;

/**
 * 泰然城渠道分类回调
 * Created by hzdzf on 2017/5/25.
 */
public interface TCategoryService {

    String sendCategoryNotice(String action, Category category,long operateDate) throws Exception;
}
