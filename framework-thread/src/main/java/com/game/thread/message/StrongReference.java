package com.game.thread.message;

import org.apache.poi.ss.formula.functions.T;

import java.lang.ref.WeakReference;

/**
 * @Author: liguorui
 * @Date: 2020/11/30 下午10:37
 */
public class StrongReference<T> extends WeakReference<T> {

    private T value;

    public StrongReference() {
        this(null);
    }

    public StrongReference(T value) {
        super(value);
        this.value = value;
    }

    public T get() {
        return value;
    }
}
