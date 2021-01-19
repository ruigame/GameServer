package com.game.base;

import com.game.util.ServerStarter;
import org.springframework.stereotype.Component;

/**
 * @Author: liguorui
 * @Date: 2021/1/19 下午11:59
 */
@Component
public class CommonService implements ServerStarter {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void init() {

    }
}
