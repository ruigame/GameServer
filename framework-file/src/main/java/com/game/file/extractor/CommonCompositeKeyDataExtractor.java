package com.game.file.extractor;

import com.game.util.ExceptionUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 普通的主键提取
 * @Author: liguorui
 * @Date: 2020/11/25 下午12:57
 */
public class CommonCompositeKeyDataExtractor implements CompositeKeyDataExtractor {

    @Override
    public boolean isHandle(Class<?>[] idClazzs) {
        return true;
    }

    @Override
    public CompositeKey extract(Object obj, List<Field> idFieldList) {
        try {
            Object[] objects = new Object[idFieldList.size()];
            for (int i = 0; i < objects.length; i++) {
                Field field = idFieldList.get(i);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                objects[i] = field.get(obj);
            }
            return CompositeKeyFactory.create(objects);
        } catch (Exception e) {
            ExceptionUtils.log(e);
            throw new RuntimeException(e);
        }
    }

}
