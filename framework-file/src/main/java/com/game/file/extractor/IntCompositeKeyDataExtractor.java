package com.game.file.extractor;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @Author: liguorui
 * @Date: 2020/11/27 上午12:34
 */
@Component
public class IntCompositeKeyDataExtractor extends AbstractCompositeKeyDataExtractor{

    public IntCompositeKeyDataExtractor() {
        super(new Class<?>[]{Integer.TYPE, Integer.TYPE});
    }

    @Override
    public CompositeKey extract(Object obj, List<Field> idFieldList) {
        try {
            for (Field field : idFieldList) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
            }

            int id1 = idFieldList.get(0).getInt(obj);
            int id2 = idFieldList.get(1).getInt(obj);
            return CompositeKeyFactory.create(id1, id2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
