package org.trc.util.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.trc.constants.SupplyConstants;

import java.lang.annotation.*;

@Caching(
    evict = {
            @CacheEvict(value = SupplyConstants.Cache.GOODS, allEntries = true),
            @CacheEvict(value = SupplyConstants.Cache.GOODS_QUERY, allEntries = true),
            @CacheEvict(value = SupplyConstants.Cache.SUPPLIER, allEntries = true),
            @CacheEvict(value = SupplyConstants.Cache.PURCHASE_ORDER, allEntries = true)
    }
)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface GoodsCacheEvict {

}
