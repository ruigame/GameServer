package com.game.logic.server.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liguorui
 * @Date: 2020/12/15 下午11:54
 */
public class CeShiData extends ServerInfoObj{

    private int id;

    private Map<Integer, Integer> map = new HashMap<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Integer, Integer> getMap() {
        return map;
    }

    public void setMap(Map<Integer, Integer> map) {
        this.map = map;
    }
}
