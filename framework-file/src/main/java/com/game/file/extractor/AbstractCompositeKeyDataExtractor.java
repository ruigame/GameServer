package com.game.file.extractor;

import java.util.Arrays;

/**
 * @Author: liguorui
 * @Date: 2020/11/27 上午12:30
 */
public abstract class AbstractCompositeKeyDataExtractor implements CompositeKeyDataExtractor{

    private Class<?>[] idClazzs;

    public AbstractCompositeKeyDataExtractor(Class<?>[] idClazzs) {
        super();
        this.idClazzs = idClazzs;
    }

    @Override
    public boolean isHandle(Class<?>[] idClazzs) {
        return Arrays.equals(this.idClazzs, idClazzs);
    }
}
