package com.game.thread.gate;

/**
 * @Author: liguorui
 * @Date: 2020/12/4 下午4:24
 */
public interface RequestToGateKeeper {

    /**
     * 执行请求，返回这个请求是否计算执行
     * @return
     */
    boolean execute();
}
