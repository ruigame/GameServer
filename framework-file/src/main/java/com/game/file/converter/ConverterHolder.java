package com.game.file.converter;

import com.thoughtworks.xstream.converters.ConverterMatcher;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: liguorui
 * @Date: 2020/11/24 下午9:32
 */
@Component
public class ConverterHolder implements ApplicationContextAware {

    private List<ConverterMatcher> convertersList;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        List<ConverterMatcher> convertersList = new ArrayList<>(applicationContext.getBeansOfType(ConverterMatcher.class).values());
        this.convertersList = Collections.unmodifiableList(convertersList);
    }

    public List<ConverterMatcher> getConvertersList() {
        return convertersList;
    }

    public void setConvertersList(List<ConverterMatcher> convertersList) {
        this.convertersList = convertersList;
    }
}
