package com.game.util;

import java.lang.annotation.*;

/**
 * @Author: liguorui
 * @Date: 2020/12/2 下午11:53
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Listener {

    public final static int MIN_PRIORITY = 1;
    public final static int MINOR_PRIORITY = 3;
    public final static int NORM_PRIORITY = 5;
    public final static int SENIOR_PRIORITY = 7;
    public final static int MAX_PRIORITY = 9;

    /**
     * 制定执行顺序，数值越大越早执行
     * @return
     */
    int order() default NORM_PRIORITY;
}
