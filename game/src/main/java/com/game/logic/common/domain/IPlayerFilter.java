package com.game.logic.common.domain;

import com.game.logic.common.PlayerActor;

/**
 * @Author: liguorui
 * @Date: 2021/1/20 下午8:17
 */
public interface IPlayerFilter {

    boolean isMatch(PlayerActor player);

}
