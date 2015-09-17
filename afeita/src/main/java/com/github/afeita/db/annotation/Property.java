package com.github.afeita.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * bean 中的属性 对应 sqlite的字段名及默认的值
 * @author chensf5
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME) 
public @interface Property {
	 public String column() default "";
	 public String defaultValue() default "";
}