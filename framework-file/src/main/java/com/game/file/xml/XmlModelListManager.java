package com.game.file.xml;

import com.game.file.extractor.CompositeKey;
import com.game.file.extractor.CompositeKeyDataExtractor;
import com.game.file.extractor.CompositeKeyDataExtractorManager;
import com.game.file.extractor.CompositeKeyFactory;
import com.google.common.collect.ImmutableMap;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.thoughtworks.xstream.XStream;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 模型加载器，模型用标签<model>表示，列表用标签<list>表示
 * @Author: liguorui
 * @Date: 2020/11/25 下午12:42
 */
public abstract class XmlModelListManager<M extends Model> extends XmlManager {

    private volatile Map<?, M> modelMap;
    private Class<M> modelClazz;

    @Autowired
    private CompositeKeyDataExtractorManager compositeKeyDataExtractorManager;

    public XmlModelListManager(String filePath) {
        super(filePath);
        Type type = getClass().getGenericSuperclass();
        if (!ParameterizedType.class.isAssignableFrom(type.getClass())) {
            throw new RuntimeException("类" + getClass() + "继承必须使用泛型");
        }
        this.modelClazz = (Class)((ParameterizedType)type).getActualTypeArguments()[0];
    }

    @Override
    protected void loadXml(XStream xStream, InputStream inputStream) {
        xStream.alias("list", ArrayList.class);
        xStream.alias("model", modelClazz);
        customise(xStream);
        List<M> modelList = fromXML(xStream, inputStream);

        List<Field> idFieldList = getIdFieldList();

        if (idFieldList.size() == 1 && idFieldList.get(0).getType() == Integer.TYPE) { //单个int的id
            Map<Integer, M> modelMap = new HashMap<>();
            Set<Integer> repeatedIdSet = new HashSet<>();
            Field idField = idFieldList.get(0);
            idField.setAccessible(true);
            try {
                for (M model : modelList) {
                    int id = idField.getInt(model);
                    if (model instanceof InitModel) {
                        try {
                            ((InitModel)model).init();
                        } catch (Exception e) {
                            throw new RuntimeException(String.format("%s Model Init ERROR %s", model.getClass().getSimpleName(), id), e);
                        }
                    }
                    if (!repeatedIdSet.add(id)) {
                        throw new RuntimeException(String.format("%s id %s repeated", model.getClass().getSimpleName(), id));
                    }
                    modelMap.put(id, model);
                }
                this.modelMap = HashIntObjMaps.newImmutableMap(modelMap);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (!idFieldList.isEmpty()) { //多id情况
            CompositeKeyDataExtractor compositeKeyDataExtractor = compositeKeyDataExtractorManager.getCompositeKeyDataExtractor(toIdClazzss(idFieldList));
            ImmutableMap.Builder<CompositeKey, M> compositeKeyMBuilder = ImmutableMap.builder();

            Set<CompositeKey> repeatedIdSet = new HashSet<>();
            for (M model : modelList) {
                CompositeKey compositeKey = compositeKeyDataExtractor.extract(model, idFieldList);
                if (model instanceof InitModel) {
                    try {
                        ((InitModel)model).init();
                    } catch (Exception e) {
                        throw new RuntimeException(String.format("%s Model Init ERROR %s", model.getClass().getSimpleName(), compositeKey), e);
                    }
                }
                if (!repeatedIdSet.add(compositeKey)) {
                    String idNames = idFieldList.stream().map(Field :: getName).collect(Collectors.joining(","));
                    throw new RuntimeException(String.format("%s id %s repeated,id列名字: [%s]", model.getClass().getSimpleName(), compositeKey, idNames));
                }
                compositeKeyMBuilder.put(compositeKey, model);
            }
            this.modelMap = compositeKeyMBuilder.build();
        } else {
            Map<Integer, M> modelMap = new HashMap<>();
            Set<Integer> repeartedIdSet = new HashSet<>();
            for (M model : modelList) {
                if (model instanceof InitModel) {
                    try {
                        ((InitModel)model).init();
                    } catch (Exception e) {
                        throw new RuntimeException(String.format("%s Model Init ERROR %s", model.getClass().getSimpleName(), model.getID()), e);
                    }
                }
                if (!repeartedIdSet.add(model.getID())) {
                    throw new RuntimeException(String.format("%s id %s repeated", model.getClass().getSimpleName(), model.getID()));
                }
                modelMap.put(model.getID(), model);
            }
            this.modelMap = HashIntObjMaps.newImmutableMap(modelMap);
        }
    }

    private List<Field> getIdFieldList() {
        Field[] fields = modelClazz.getDeclaredFields();
        List<Field> idFieldList = new ArrayList<>(fields.length);
        for (Field field : fields) {
            if (field.isAnnotationPresent(ModelId.class)) {
                idFieldList.add(field);
            }
        }
        return idFieldList;
    }

    private Class<?> [] toIdClazzss(List<Field> fieldList) {
        Class<?>[] clazzs = new Class<?>[fieldList.size()];
        for (int i = 0; i < fieldList.size(); i++) {
            clazzs[i] = fieldList.get(i).getType();
        }
        return clazzs;
    }

    protected List<M> fromXML(XStream xStream, InputStream inputStream) {
        return (List<M>)xStream.fromXML(inputStream);
    }

    protected void customise(XStream xStream) {

    }

    public Collection<M> getModelCollection() {
        cheakInit();
        return modelMap.values();
    }

    /**
     * 获取按id排序的集合
     * @param asc true：升序 false：降序
     * @return
     */
    public List<M> getSortList(boolean asc) {
        cheakInit();
        Comparator<Model> comparator = asc ? Model.ASC_COMPARATOR : Model.DESC_COMPARATOR;
        return modelMap.values().stream().sorted(comparator).collect(Collectors.toList());
    }

    public M getModelByID(int id) {
        cheakInit();
        return ((IntObjMap<M>)modelMap).get(id);
    }

    public int getModelSize() {
        cheakInit();
        return modelMap.size();
    }

    public boolean contains(int id) {
        cheakInit();
        return ((IntObjMap<M>)modelMap).containsKey(id);
    }

    public boolean contains(int id1, int id2) {
        cheakInit();
        return modelMap.containsKey(CompositeKeyFactory.create(id1, id2));
    }

    public boolean contains(Object... ids) {
        cheakInit();
        return modelMap.containsKey(CompositeKeyFactory.create(ids));
    }

    public M getModelByID(int id1, int id2) {
        cheakInit();
        return modelMap.get(CompositeKeyFactory.create(id1, id2));
    }

    public M getModelByID(Object... ids) {
        cheakInit();
        return modelMap.get(CompositeKeyFactory.create(ids));
    }
}
