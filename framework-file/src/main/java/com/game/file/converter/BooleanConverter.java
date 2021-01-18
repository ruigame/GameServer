package com.game.file.converter;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import org.springframework.stereotype.Component;

/**
 * @Author: liguorui
 * @Date: 2020/11/24 下午9:40
 */
@Component
public class BooleanConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(Class type) {
        return type.equals(boolean.class) || type.equals(Boolean.class);
    }

    @Override
    public Object fromString(String s) {
        if ("true".equalsIgnoreCase(s) || "1".equals(s)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
