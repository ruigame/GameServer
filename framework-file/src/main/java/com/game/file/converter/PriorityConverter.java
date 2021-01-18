package com.game.file.converter;

import com.thoughtworks.xstream.converters.Converter;

/**
 * @Author: liguorui
 * @Date: 2020/11/25 上午12:19
 */
public interface PriorityConverter extends Converter {

    int getPriority();
}
