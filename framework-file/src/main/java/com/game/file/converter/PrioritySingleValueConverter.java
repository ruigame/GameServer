package com.game.file.converter;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * @Author: liguorui
 * @Date: 2020/11/25 上午12:22
 */
public interface PrioritySingleValueConverter extends SingleValueConverter {
    int getPriority();
}
