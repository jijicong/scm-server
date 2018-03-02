package org.trc.util.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.trc.constants.SupplyConstants;

import java.lang.annotation.*;

@Caching(
    evict = {
            @CacheEvict(value = SupplyConstants.Cache.SHOP_ORDER, allEntries = true),
            @CacheEvict(value = SupplyConstants.Cache.SUPPLIER_ORDER, allEntries = true),
            @CacheEvict(value = SupplyConstants.Cache.GOODS_QUERY, allEntries = true)
    }
)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SupplierOrderCacheEvict {

}
