package com.game.file.extractor;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @Author: liguorui
 * @Date: 2020/11/25 下午12:54
 */
public interface CompositeKeyDataExtractor {

    boolean isHandle(Class<?>[] idClazzs);

    CompositeKey extract(Object obj, List<Field> idFieldList);
}
