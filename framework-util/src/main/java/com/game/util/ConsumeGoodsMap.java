package com.game.util;

import com.koloboke.collect.map.hash.HashIntIntMaps;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 消耗材料配置格式
 * @Author: liguorui
 * @Date: 2020/11/24 下午10:58
 */
public class ConsumeGoodsMap {

    private Map<Integer, Integer> modleId2NumMap;

    public ConsumeGoodsMap(Map<Integer, Integer> modleId2NumMap) {
        super();
        this.modleId2NumMap = modleId2NumMap;
    }

    public ConsumeGoodsMap() {
        super();
        this.modleId2NumMap = new HashMap<>();
    }

    public int getNumByModleId(int modelId) {
        Integer num = modleId2NumMap.get(modelId);
        return num == null ? 0 : num;
    }

    public void addGoodsMap(ConsumeGoodsMap consumeGoodsMap) {
        if (Objects.isNull(consumeGoodsMap)) {
            return;
        }
        addGoodsMap(consumeGoodsMap.getModleId2NumMap());
    }

    public void addGoodsMap(Map<Integer, Integer> goodsIdNumMap) {
        if (CollectionUtil.isEmpty(goodsIdNumMap)) return;
        goodsIdNumMap.forEach(this::addGoods);
    }

    public void addGoods(int modelId, int count) {
        modleId2NumMap.merge(modelId, count, Integer::sum);
    }

    public Map<Integer, Integer> parseByNum(int num) {
        Map<Integer, Integer> totalMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : modleId2NumMap.entrySet()) {
            CollectionUtil.addIntegerMap(totalMap, entry.getKey(), entry.getValue() * num);
        }
        return totalMap;
    }

    /**
     * 格式：modelId,数量;modelId,数量
     * @param configStr
     * @return 返回不可变对象
     */
    public static ConsumeGoodsMap parse(String configStr) {
        return parse(configStr, false);
    }

    public static ConsumeGoodsMap parse(String configStr, boolean modelIdMayBeRepeat) {
        Map<Integer, Integer> modelId2NumMap = new HashMap<>();
        String []goodsParamsListStrs = StringUtils.split(configStr, ";");
        if (goodsParamsListStrs != null) {
            for (String goodsParamsListStr : goodsParamsListStrs) {
                String[] modelId2NumStr = StringUtils.split(goodsParamsListStr, ",");
                Integer modelId = Integer.valueOf(modelId2NumStr[0]);
                Integer num = Integer.valueOf(modelId2NumStr[1]);
                if (num > 0) {
                    if (modelIdMayBeRepeat) { //配置的modelId可能重复，例如1001,2;1002,5
                        modelId2NumMap.merge(modelId, num, Integer::sum);
                    } else {
                        modelId2NumMap.put(modelId, num);
                    }
                }
            }
        }
        return new ConsumeGoodsMap(HashIntIntMaps.newImmutableMap(modelId2NumMap));
    }

    public boolean isEmpty() {
        return modleId2NumMap.isEmpty();
    }

    public Map<Integer, Integer> getModleId2NumMap() {
        return modleId2NumMap;
    }

    public void setModleId2NumMap(Map<Integer, Integer> modleId2NumMap) {
        this.modleId2NumMap = modleId2NumMap;
    }
}
