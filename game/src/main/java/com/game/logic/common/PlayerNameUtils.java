package com.game.logic.common;

import com.game.logic.common.manger.ConfigRandomNameManger;
import com.game.logic.player.PlayerService;
import com.game.util.Context;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 角色名工具类
 * @Author: liguorui
 * @Date: 2021/1/25 下午9:36
 */
public class PlayerNameUtils {

    /**
     * 获取随机角色名
     * @param sex
     * @return
     */
    public static String getRandomName(int sex) {
        String name = null;
        for (int i = 0; i < 10; i++) {
            String []newNames = Context.getBean(ConfigRandomNameManger.class).getRandomName(sex == 1);
            int type = ThreadLocalRandom.current().nextInt(1, 5);
            for (int j = 0; j < 5; j++) {
                name = getName(newNames, type);
                if (name != null && !name.equals("") && !Context.getBean(PlayerService.class).isNameExist(name)
                        && Context.getBean(Wordservicle.isAvaliableFormatForCreate(name))) {
                    break;
                }
                name = null;
                type = (type > 5) ? 0 : type;
            }
            if (name != null) {
                break;
            }
        }
        return name;
    }

    private static String getName(String []names, int type) {
        switch (type) {
            case 1:
                return names[0] + names[2];
            case 2:
                return names[1] + names[2];
            default:
                return names[3];
        }
    }
}
