package com.game.logic.player.domain.forbid;

/**
 * @Author: liguorui
 * @Date: 2021/1/21 下午10:23
 */
public interface Forbid {

    boolean isForbidTimeOut(int utime);

    int forbidLessTime(int utime);
}
