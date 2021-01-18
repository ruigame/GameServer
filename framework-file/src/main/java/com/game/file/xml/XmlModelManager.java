package com.game.file.xml;

import com.thoughtworks.xstream.XStream;

import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 模型加载器。模型用标签<model>表示
 * @Author: liguorui
 * @Date: 2020/11/27 上午12:23
 */
public class XmlModelManager<M extends Model> extends XmlManager {

    private volatile M model;
    private Class<M> modelClazz;

    public XmlModelManager(String filePath) {
        super(filePath);
        Type type = getClass().getGenericSuperclass();
        if (!ParameterizedType.class.isAssignableFrom(type.getClass())) {
            throw new RuntimeException("类" + getClass() + "继承必须使用泛型");
        }
        this.modelClazz = (Class)((ParameterizedType)type).getActualTypeArguments()[0];
    }

    @Override
    protected void loadXml(XStream xStream, InputStream inputStream) {
        xStream.alias("model", modelClazz);
        customise(xStream);
        M model = (M)xStream.fromXML(inputStream);
        if (model instanceof InitModel) {
            ((InitModel)model).init();
        }
        this.model = model;
    }

    protected void customise(XStream xStream) {}

    public M getModel() {
        cheakInit();
        return model;
    }


}
