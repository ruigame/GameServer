package com.game.logic.common.manger;

import com.game.file.ConfigPath;
import com.game.file.xml.XmlModelListManager;
import com.game.logic.common.WordService;
import com.game.logic.common.domain.ConfigRandomName;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final static int TYPE_FIRST_NAME = 1; //性
    private final static int TYPE_NAME_MALE = 2; //男名
    private final static int TYPE_NAME_FEMALE = 3; //女名
    private final static int TYPE_ALL_NAME_MALE = 4; //男全名
    private final static int TYPE_ALL_NAME_FEMALE = 5; //女全名

    private List<String> firstNames;
    private List<String> nameMales;
    private List<String> nameFeMales;
    private List<String> allNamesMales;
    private List<String> allNamesFemales;

    @Autowired
    private WordService wordService;


    public ConfigRandomNameManger() {
        super(ConfigPath.RandomName.RANDOM_NAME_PATH);
    }

    @Override
    protected void afterLoad() {
        List<String> firstNames = new ArrayList<>();
        List<String> nameMales = new ArrayList<>();
        List<String> nameFeMales = new ArrayList<>();
        List<String> allNamesMales = new ArrayList<>();
        List<String> allNamesFemales = new ArrayList<>();
        int maxId = 0;

        for (ConfigRandomName config : getModelCollection()) {
            String name = config.getName();
            int type = config.getType();
            if (type == TYPE_FIRST_NAME) {
                firstNames.add(name);
            } else if (type == TYPE_NAME_MALE) {
                nameMales.add(name);
            } else if (type == TYPE_NAME_FEMALE) {
                nameFeMales.add(name);
            } else if (type == TYPE_ALL_NAME_MALE) {
                allNamesMales.add(name);
            } else if (type == TYPE_ALL_NAME_FEMALE) {
                allNamesFemales.add(name);
            }
            maxId = config.getID() > maxId ? config.getID() : maxId;
        }

        this.firstNames = firstNames;
        this.nameMales = nameMales;
        this.nameFeMales = nameFeMales;
        this.allNamesMales = allNamesMales;
        this.allNamesFemales = allNamesFemales;
        checkValid();
    }

    public String getName(boolean isMale) {
        List<String> nameList = new ArrayList<>();
        String firstName = getRandomname(this.firstNames);
        for (String secName : isMale ? this.nameMales : this.nameFeMales) {
            String name = firstName + secName;
            nameList.add(name);
        }

        nameList.addAll(isMale ? allNamesMales : allNamesFemales);
        return getRandomName(nameList);
    }

    private String getRandomName(List<String> list) {
        if (list.isEmpty()) return "";
        int index = ThreadLocalRandom.current().nextInt(list.size());
        return index > -1 ? list.get(index) : "";
    }

    private String getRandomname(List<String> list) {
        if (list.isEmpty()) return "";
        int index = ThreadLocalRandom.current().nextInt(list.size());
        return index > -1 ? list.get(index) : "";
    }

    private void checkValid() {
//        //检查所有男组合名
//        for (String firstName : firstNames) {
//            for (String thirdNameMale : thirdNameMales) {
//                String name = firstName + thirdNameMale;
//                checkName(name);
//            }
//        }
//
//        //检查所有女组合名
//        for (String secondName : secondNames) {
//            for (String thirdNameFeMale : thirdNameFeMales) {
//                String name = secondName + thirdNameFeMale;
//                checkName(name);
//            }
//        }
//
//        //检查全名
//        for (String name : allNames) {
//            checkName(name);
//        }
    }

    //检查合法性
    private void checkName(String name) {
        if (wordService.nameHasBadWords(name)) {
            System.err.println(name);
        }
    }
}
