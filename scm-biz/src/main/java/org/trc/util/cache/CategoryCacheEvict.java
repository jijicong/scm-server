package org.trc.util.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.trc.constants.SupplyConstants;

import java.lang.annotation.*;

@Caching(
    evict = {
            @CacheEvict(value = SupplyConstants.Cache.CATEGORY, allEntries = true),
            @CacheEvict(value = SupplyConstants.Cache.GOODS, allEntries = true),
            @CacheEvict(value = SupplyConstants.Cache.SUPPLIER, allEntries = true)
    }
)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CategoryCacheEvict {

}
