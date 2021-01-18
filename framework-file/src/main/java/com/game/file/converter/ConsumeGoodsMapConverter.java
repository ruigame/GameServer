package com.game.file.converter;

import com.game.util.ConsumeGoodsMap;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: liguorui
 * @Date: 2020/11/24 下午11:13
 */
public class ConsumeGoodsMapConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(Class aClass) {
        return ConsumeGoodsMap.class.isAssignableFrom(aClass);
    }

    @Override
    public Object fromString(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        return ConsumeGoodsMap.parse(s);
    }
}
