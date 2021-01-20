package com.game.logic.common;

import com.game.logic.server.manager.ServerInfoManager;
import com.game.util.ServerStarter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: liguorui
 * @Date: 2021/1/19 下午11:59
 */
@Component
public class CommonService implements ServerStarter {

    @Autowired
    private ServerInfoManager serverInfoManager;

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void init() {

    }
}
