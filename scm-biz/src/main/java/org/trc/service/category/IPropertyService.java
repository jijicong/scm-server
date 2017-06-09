package org.trc.service.category;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.category.Property;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by hzqph on 2017/5/4.
 */
public interface IPropertyService extends IBaseService<Property, Long> {
    /**
     * 查询关联属性集合
     *
     * @param id
     * @return
     * @throws Exception
     */
    List<Property> queryPropertyList(List<Long> id) throws Exception;

    /**
     * 根据id查询属性信息
     * @param id
     * @return
     * @throws Exception
     */
    Property selectOneById(Long id) throws Exception;
}
