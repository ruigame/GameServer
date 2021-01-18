package com.game.file.converter;

import com.game.util.AddGoodsParam;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: liguorui
 * @Date: 2020/11/24 下午10:43
 */
public class AddGoodsParamConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(Class type) {
        return AddGoodsParam.class.isAssignableFrom(type);
    }

    @Override
    public Object fromString(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        return AddGoodsParam.parse(s);
    }
}
