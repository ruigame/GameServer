package com.game.file.converter;

import com.game.util.AddGoodsParamList;
import com.game.util.ServerStarter;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liguorui
 * @Date: 2020/11/24 下午10:46
 */
@Component
public class AddGoodsParamListConverter extends AbstractSingleValueConverter implements ServerStarter {

    private volatile Map<String, AddGoodsParamList> cacheMap = new HashMap<>();

    @Override
    public int getOrder() {
        return LOWEST;
    }

    @Override
    public void init() {
        synchronized (this) {
            cacheMap = null;
        }
    }

    @Override
    public boolean canConvert(Class type) {
        return AddGoodsParamList.class.isAssignableFrom(type);
    }

    @Override
    public Object fromString(String s) {
        if (StringUtils.isBlank(s)) {
            return AddGoodsParamList.EMPTY_ADD_GOODS_PARAM_LIST;
        }
        if (cacheMap != null) {
            synchronized (this) {
                if (cacheMap != null) {
                    return cacheMap.computeIfAbsent(s, k -> AddGoodsParamList.parse(k));
                }
            }
        }
        return AddGoodsParamList.parse(s);
    }
}
