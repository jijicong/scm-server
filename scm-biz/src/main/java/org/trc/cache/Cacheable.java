package org.trc.cache;

import javax.lang.model.type.NullType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {
    String cls() default "";
	String key() default "";
    int expireTime() default CacheExpire.DEFAULT;
    boolean isList() default false;
}
