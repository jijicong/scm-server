package org.trc.mapper.goods;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.goods.ItemNaturePropery;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/24.
 */
public interface IItemNatureProperyMapper extends BaseMapper<ItemNaturePropery>{

    Integer updateItemNaturePropery(List<ItemNaturePropery> itemNatureProperyList) throws Exception;

    Integer updateIsValidByPropertyValueId(@Param("isValid")String isValid, @Param("propertyValueId")Long propertyValueId)throws Exception;

    Integer updateIsValidByPropertyId(@Param("isValid")String isValid, @Param("propertyId")Long propertyId)throws Exception;
}
