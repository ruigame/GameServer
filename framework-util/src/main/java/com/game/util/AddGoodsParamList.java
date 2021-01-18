package com.game.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: liguorui
 * @Date: 2020/11/24 下午9:55
 */
public class AddGoodsParamList {

    private List<AddGoodsParam> addGoodsParamList;

    public static final AddGoodsParamList EMPTY_ADD_GOODS_PARAM_LIST = new AddGoodsParamList(Collections.emptyList());

    public AddGoodsParamList() {
        super();
        this.addGoodsParamList = Lists.newArrayList();
    }

    public AddGoodsParamList(List<AddGoodsParam> addGoodsParamList) {
        super();
        this.addGoodsParamList = addGoodsParamList;
    }

    public AddGoodsParamList(Map<Integer, Integer> goodsMap) {
        super();
        List<AddGoodsParam> addGoodsParamList = Lists.newArrayList();
        if (goodsMap != null && goodsMap.size() > 0) {
            for (Map.Entry<Integer, Integer> entry : goodsMap.entrySet()) {
                AddGoodsParam addGoodsParam = new AddGoodsParam(entry.getKey(), entry.getValue());
                addGoodsParamList.add(addGoodsParam);
            }
        }
        this.addGoodsParamList = addGoodsParamList;
    }

    public List<AddGoodsParam> getAddGoodsParamList() {
        return addGoodsParamList;
    }

    public void setAddGoodsParamList(List<AddGoodsParam> addGoodsParamList) {
        this.addGoodsParamList = addGoodsParamList;
    }

    public AddGoodsParamList addGoodsParamList(AddGoodsParamList addGoodsParamList) {
        if (addGoodsParamList != null) {
            this.addGoodsParamList.addAll(addGoodsParamList.getAddGoodsParamList());
        }
        return this;
    }

    public AddGoodsParamList addGoodsParam(AddGoodsParam addGoodsParam) {
        if (addGoodsParam != null) {
            this.addGoodsParamList.add(addGoodsParam);
        }
        return this;
    }

    public AddGoodsParamList addGoods(int goodsModelId, int num) {
        return addGoodsParam(new AddGoodsParam(goodsModelId, num));
    }

    public Map<Integer, Integer> toGoodsMap() {
        Map<Integer, Integer> totalMap = new HashMap<>();
        for (AddGoodsParam addGoodsParam : addGoodsParamList) {
            CollectionUtil.addIntegerMap(totalMap, addGoodsParam.getModelId(), addGoodsParam.getNum());
        }
        return totalMap;
    }

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(addGoodsParamList);
    }

    /**
     * 参数格式：modelId,数量;modelId,数量
     * @param configStr
     * @return 返回不可修改的对象
     */
    public static AddGoodsParamList parse(String configStr) {
        ImmutableList.Builder<AddGoodsParam> addGoodsParamBuilder = ImmutableList.builder();
        String[] goodsParamsListStrs = StringUtils.split(configStr, ";");
        for (String goodsParamsListStr : goodsParamsListStrs) {
            AddGoodsParam addGoodsParam = AddGoodsParam.parse(goodsParamsListStr);
            addGoodsParamBuilder.add(addGoodsParam);
        }
        AddGoodsParamList paramList = new AddGoodsParamList(addGoodsParamBuilder.build());
        return paramList;
    }

    public String serialize() {
        StringBuilder builder = new StringBuilder();
        for (AddGoodsParam addGoodsParam : addGoodsParamList) {
            builder.append(addGoodsParam.serialize()).append(";");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((addGoodsParamList == null) ? 0 : addGoodsParamList.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AddGoodsParamList other = (AddGoodsParamList)obj;
        if (addGoodsParamList == null) {
            if (other.addGoodsParamList != null)
                return false;
        } else if (!addGoodsParamList.equals(other.addGoodsParamList))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AddGoodsParamList [addGoodsParamList=" + addGoodsParamList + "]";
    }
 }
