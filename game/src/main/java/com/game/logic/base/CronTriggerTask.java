package com.game.logic.base;

import java.lang.annotation.*;

/**
 * @Author: liguorui
 * @Date: 2021/2/3 上午12:53
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CronTriggerTask {

    CronTaskType value();
}
