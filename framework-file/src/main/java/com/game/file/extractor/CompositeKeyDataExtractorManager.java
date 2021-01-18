package com.game.file.extractor;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liguorui
 * @Date: 2020/11/25 下午12:53
 */
@Component
public class CompositeKeyDataExtractorManager implements ApplicationContextAware {

    private List<CompositeKeyDataExtractor> extractorList;

    private CommonCompositeKeyDataExtractor commonCompositeKeyDataExtractor = new CommonCompositeKeyDataExtractor();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.extractorList = new ArrayList<>(applicationContext.getBeansOfType(CompositeKeyDataExtractor.class).values());
    }

    public CompositeKeyDataExtractor getCompositeKeyDataExtractor(Class<?>[] idClazzs) {
        for (CompositeKeyDataExtractor extractor : extractorList) {
            if (extractor.isHandle(idClazzs)) {
                return extractor;
            }
        }
        return commonCompositeKeyDataExtractor;
    }
}
