package com.game.logic.player;

import com.game.player.manager.PlayerEntityManager;
import com.game.util.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 5:54 下午
 */
@Component
@Listener(order = Listener.MIN_PRIORITY)
public class PlayerService {

    private static final Logger LOGGER = LoggerFactory.getLogger("playerService");

    @Autowired
    private PlayerEntityManager playerEntityManager;


}
