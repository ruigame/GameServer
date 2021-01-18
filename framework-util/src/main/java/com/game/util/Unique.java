package com.game.util;

/**
 * 唯一对象，如果是枚举类，则在起服时会自动检测唯一性
 * @Author: liguorui
 * @Date: 2020/12/14 下午8:36
 */
public interface Unique {

    Object uniqueID();
}
