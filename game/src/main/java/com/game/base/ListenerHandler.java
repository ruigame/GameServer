package com.game.base;

import com.game.logic.cross.CrossClientCloseListener;
import com.game.logic.cross.CrossClientInitListener;
import com.game.net.cross.CrossClient;
import com.game.util.ExceptionUtils;
import com.game.util.ListenerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: liguorui
 * @Date: 2020/12/14 下午11:27
 */
public class ListenerHandler {

    private static final Logger log = LoggerFactory.getLogger(ListenerHandler.class);

    private final static ListenerHandler listenerMgr = new ListenerHandler();

    private final static Map<Class<?>, List<?>> listeners = new HashMap<>();

    public static ListenerHandler getInstance() {
        return listenerMgr;
    }

    public <T> List<T> getListeners(Class<T> clazz) {
        return ListenerManager.getListeners(clazz);
    }

    public void fireClientConnectSuc(String platform, int serverId) {
        Collection<CrossClientInitListener> listeners = getListeners(CrossClientInitListener.class);
        for (CrossClientInitListener crossClientInitListener : listeners) {
            try {
                crossClientInitListener.onClientConnectSuc(platform, serverId);
            } catch (Exception e) {
                ExceptionUtils.log(e);
            }
        }
    }

    public void fireCrossClientClose(CrossClient crossClient) {
        Collection<CrossClientCloseListener> listeners = getListeners(CrossClientCloseListener.class);
        for (CrossClientCloseListener closeListener : listeners) {
            try {
                closeListener.onCrossClientClose(crossClient);
            } catch (Exception e) {
                ExceptionUtils.log(e);
            }
        }
    }
}
