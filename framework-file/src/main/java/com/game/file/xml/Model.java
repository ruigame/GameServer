package com.game.file.xml;

import java.util.Comparator;

/**
 * 模型接口 可被XmlModelListManager,XmlModelManager管理
 * @Author: liguorui
 * @Date: 2020/11/24 下午11:27
 */
public interface Model {

    default int getID() {
        throw new UnsupportedOperationException();
    }

    Comparator<Model> ASC_COMPARATOR = Comparator.comparingInt(Model::getID);
    Comparator<Model> DESC_COMPARATOR = (o1, o2) -> Integer.compare(o2.getID(), o1.getID());
}
