package com.game.logic.common.manger;

import com.game.file.ConfigPath;
import com.game.file.xml.XmlModelListManager;
import com.game.logic.common.domain.ConfigRandomName;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: liguorui
 * @Date: 2021/1/25 下午9:38
 */
@Component
public class ConfigRandomNameManger extends XmlModelListManager<ConfigRandomName> {

    private final static int TYPE_FIRST_NAME = 1;
    private final static int TYPE_SECOND_NAME = 2;
    private final static int TYPE_THIRD_NAME_MALE = 3;
    private final static int TYPE_THIRD_NAME_FEMALE = 4;
    private final static int TYPE_ALL_NAME = 5; //该类型的昵称单独出现，不拼接

    private List<String> firstNames;
    private List<String> secondNames;
    private List<String> thirdNameMales;
    private List<String> thirdNameFeMales;
    private List<String> allNames;


    public ConfigRandomNameManger() {
        super(ConfigPath.RANDOM_NAME_PATH);
    }

    @Override
    protected void afterLoad() {
        List<String> firstNames = new ArrayList<>();
        List<String> secondNames = new ArrayList<>();
        List<String> thirdNameMales = new ArrayList<>();
        List<String> thirdNameFeMales = new ArrayList<>();
        List<String> allNames = new ArrayList<>();
        int maxId = 0;

        for (ConfigRandomName configRandomName : getModelCollection()) {
            String name = configRandomName.getName();
            if (configRandomName.getType() == TYPE_FIRST_NAME) {
                firstNames.add(name);
            } else if (configRandomName.getType() == TYPE_SECOND_NAME) {
                secondNames.add(name);
            } else if (configRandomName.getType() == TYPE_THIRD_NAME_MALE) {
                thirdNameMales.add(name);
            } else if (configRandomName.getType() == TYPE_THIRD_NAME_FEMALE) {
                thirdNameFeMales.add(name);
            } else if (configRandomName.getType() == TYPE_ALL_NAME) {
                allNames.add(name);
            }
            maxId = configRandomName.getID() > maxId ? configRandomName.getID() : maxId;
        }

        this.firstNames = firstNames;
        this.secondNames = secondNames;
        this.thirdNameMales = thirdNameMales;
        this.thirdNameFeMales = thirdNameFeMales;
        this.allNames = allNames;
        checkValid();
    }

    public String[] getRandomName(boolean isMale) {
        List<String> third = isMale ? this.thirdNameMales : this.thirdNameFeMales;
        String [] strs = new String[4];
        strs[0] = getRandomname(this.firstNames);
        strs[1] = getRandomname(this.secondNames);
        strs[2] = getRandomname(third);
        strs[3] = getRandomname(this.allNames);
        return strs;
    }

    private String getRandomname(List<String> list) {
        if (list.isEmpty()) return "";
        int index = ThreadLocalRandom.current().nextInt(list.size());
        return index > -1 ? list.get(index) : "";
    }

    private void checkValid() {
        //检查所有男组合名
        for (String firstName : firstNames) {
            for (String thirdNameMale : thirdNameMales) {
                String name = firstName + thirdNameMale;
                checkName(name);
            }
        }

        //检查所有女组合名
        for (String secondName : secondNames) {
            for (String thirdNameFeMale : thirdNameFeMales) {
                String name = secondName + thirdNameFeMale;
                checkName(name);
            }
        }

        //检查全名
        for (String name : allNames) {
            checkName(name);
        }
    }

    //检查合法性
    private void checkName(String name) {

    }
}
