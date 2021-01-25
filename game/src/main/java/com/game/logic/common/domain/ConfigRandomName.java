package com.game.logic.common.domain;

import com.game.file.xml.InitModel;

/**
 * @Author: liguorui
 * @Date: 2021/1/25 下午9:38
 */
public class ConfigRandomName implements InitModel {

    private int nameId;
    private String name;
    private int type; //类型：1：为姓 2：为男名 3：为女名

    @Override
    public int getID() {
        return nameId;
    }

    public int getNameId() {
        return nameId;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    @Override
    public void init() {

    }
}
