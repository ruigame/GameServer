package com.game.log;

import java.lang.annotation.*;

/**
 * @Author: liguorui
 * @Date: 2020/12/2 下午9:07
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogDesc {

    String value() default "";
}
