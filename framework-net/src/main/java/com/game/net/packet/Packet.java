package com.game.net.packet;

import java.lang.annotation.*;

/**
 * @Author: liguorui
 * @Date: 2020/12/2 下午11:50
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Packet {

    short[] commandId();

    int checkTimes() default 220;

    boolean codec() default false;
}
