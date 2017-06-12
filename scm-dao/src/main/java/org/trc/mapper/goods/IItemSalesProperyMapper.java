package org.trc.mapper.goods;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.goods.ItemSalesPropery;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/24.
 */
public interface IItemSalesProperyMapper extends BaseMapper<ItemSalesPropery>{

    Integer updateItemSalesPropery(List<ItemSalesPropery> itemSalesProperyList) throws Exception;

    Integer updateIsValidByPropertyValueId(@Param("isValid")String isValid,@Param("propertyValueId")Long propertyValueId)throws Exception;

    Integer updateIsValidByPropertyId(@Param("isValid")String isValid, @Param("propertyId")Long propertyId)throws Exception;
}
