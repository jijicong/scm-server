package org.trc.util.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.trc.constants.SupplyConstants;

import java.lang.annotation.*;

@Caching(
    evict = {
            @CacheEvict(value = SupplyConstants.Cache.OUT_GOODS, allEntries = true),
            @CacheEvict(value = SupplyConstants.Cache.OUT_GOODS_QUERY, allEntries = true),
    }
)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface OutGoodsCacheEvict {

}
