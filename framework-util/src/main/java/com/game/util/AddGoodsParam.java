package com.game.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 配置表物品参数格式
 * @Author: liguorui
 * @Date: 2020/11/24 下午9:45
 */
public class AddGoodsParam {

    private int modelId;
    private int num;

    public AddGoodsParam() {

    }

    public AddGoodsParam(int modelId, int num) {
        this.modelId = modelId;
        this.num = num;
    }

    public static AddGoodsParam parse(String configStr) {
        String[] modelId2NumStr = StringUtils.split(configStr, ",");
        int modelId = Integer.parseInt(modelId2NumStr[0]);
        int num = Integer.parseInt(modelId2NumStr[1]);
        return new AddGoodsParam(modelId, num);
    }

    public String serialize() {
        return modelId + "," + num;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + modelId;
        result = prime * result + num;
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
        AddGoodsParam other = (AddGoodsParam)obj;
        if (modelId != other.modelId)
            return false;
        if (num != other.num)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AddGoodsParam [modelId=" + modelId + ",num=" + num + "]";
    }
}
