package com.sky.annotation;

import com.sky.enumeration.OperationType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 功能简述
 *
 * @author hssy
 * @version 1.0
 */

@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //数据库操作类型 Update 和 Insert
    OperationType value();
}
