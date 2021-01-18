package com.game.net.packet;

import com.google.common.base.Preconditions;

import java.lang.reflect.Method;

/**
 * @author liguorui
 * @date 2018/1/7 22:12
 */
public class PacketHandlerWrapper {

    private final Object target;
    private final Method method;

    PacketHandlerWrapper(Object target, Method method) {
        Preconditions.checkNotNull(target, "target cannot be null");
        Preconditions.checkNotNull(method, "method cannot be null");

        this.target = target;
        this.method = method;
        method.setAccessible(true);
    }

    public void invoke(Object... args) throws Exception {
        method.invoke(target, args);
    }

    public Method getMethod() {
        return method;
    }
}
