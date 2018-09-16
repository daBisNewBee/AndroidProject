package com.exa.eventbus;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by user on 2018/9/16.
 */

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface PrintMe {
}
