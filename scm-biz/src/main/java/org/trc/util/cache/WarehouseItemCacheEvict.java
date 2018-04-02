package org.trc.util.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.trc.constants.SupplyConstants;

import java.lang.annotation.*;

/**
 * Created by hzcyn on 2018/4/2.
 */
@Caching(
        evict = {
                @CacheEvict(value = SupplyConstants.Cache.WAREHOUSE, allEntries = true),
                @CacheEvict(value = SupplyConstants.Cache.WAREHOUSE_ITEM, allEntries = true)

        }
)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface WarehouseItemCacheEvict {

}

