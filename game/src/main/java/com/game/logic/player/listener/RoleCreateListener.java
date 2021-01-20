package com.game.logic.player.listener;

import com.game.logic.common.PlayerActor;
import com.game.logic.player.domain.Role;
import com.game.util.Listener;

/**
 * @Author: liguorui
 * @Date: 2021/1/20 下午11:00
 */
@Listener
public interface RoleCreateListener {

    void onCreate(PlayerActor playerActor, Role role);
}
