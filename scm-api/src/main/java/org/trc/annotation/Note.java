package org.trc.annotation;

import java.lang.annotation.*;

/**
 * Created by ding on 2017/6/30.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Note {

    public String value() default "";
}
