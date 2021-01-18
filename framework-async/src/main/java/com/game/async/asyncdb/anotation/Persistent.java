package com.game.async.asyncdb.anotation;

import com.game.async.asyncdb.Synchronizer;

import java.lang.annotation.*;

/**
 * 持久化注解
 * @author liguorui
 * @date 2018/1/21 22:55
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Persistent {

    /**
     * 是否是异步持久化，默认是true
     * @return
     */
    boolean asyn() default true;

    /**
     * 获取数据库同步器类型
     * @return
     */
    Class<? extends Synchronizer> syncClass();
}
