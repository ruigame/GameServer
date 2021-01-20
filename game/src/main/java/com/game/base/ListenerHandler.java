package com.game.base;

import com.game.logic.common.PlayerActor;
import com.game.logic.cross.CrossClientCloseListener;
import com.game.logic.cross.CrossClientInitListener;
import com.game.logic.player.domain.Role;
import com.game.logic.player.listener.*;
import com.game.logic.sys.PlayerCreateListener;
import com.game.net.cross.CrossClient;
import com.game.util.*;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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

    public void firePlayerCreateListener(PlayerActor playerActor, GameSession session) {
        Collection<PlayerCreateListener> listeners = getListeners(PlayerCreateListener.class);
        for (PlayerCreateListener listener : listeners) {
            try {
                listener.onCreate(playerActor, session);
            } catch (Exception e) {
                ExceptionUtils.log(e);
            }
        }
    }

    public void firePlayerLoadListener(PlayerActor playerActor) {
        Collection<PlayerLoadListener> listeners = getListeners(PlayerLoadListener.class);
        if (CollectionUtil.isEmpty(listeners)) return;
        List<PlayerLoadListener> sortList = Lists.newArrayList(listeners);
        Collections.sort(sortList, new Comparator<PlayerLoadListener>() {
            @Override
            public int compare(PlayerLoadListener o1, PlayerLoadListener o2) {
                return o1.getLoadOrder() - o2.getLoadOrder();
            }
        });
        for (PlayerLoadListener listener : listeners) {
            UseTimer useTimer = new UseTimer(playerActor.getAccount() + " " +
                    listener + " firePlayerLoadListener", 100);
             listener.onLoad(playerActor);
             useTimer.printUseTime();
        }
    }

    public void firePlayerLoginListener(PlayerActor playerActor) {
        Collection<PlayerLoginListener> listeners = getListeners(PlayerLoginListener.class);
        for (PlayerLoginListener listener : listeners) {
            try {
                UseTimer useTimer = new UseTimer(playerActor.getAccount() + " " +
                        listener + " firePlayerLoginListener", 200);
                listener.onLogin(playerActor);
                useTimer.printUseTime();
            } catch (Exception e) {
                ExceptionUtils.log(e);
            }
        }
    }

    public void fireBeforePlayerLogoutListener(PlayerActor playerActor) {
        Collection<PlayerBeforeLogoutListener> listeners = getListeners(PlayerBeforeLogoutListener.class);
        for (PlayerBeforeLogoutListener listener : listeners) {
            try {
                listener.onBeforeLogout(playerActor);
            } catch (Exception e) {
                ExceptionUtils.log(e);
            }
        }
    }

    public void firePlayerLogoutListener(PlayerActor playerActor) {
        Collection<PlayerLogoutListener> listeners = getListeners(PlayerLogoutListener.class);
        for (PlayerLogoutListener listener : listeners) {
            try {
                listener.onLogout(playerActor);
            } catch (Exception e) {
                ExceptionUtils.log(e);
            }
        }
    }

    public void fireRoleCreateListener(PlayerActor playerActor, Role role) {
        Collection<RoleCreateListener> listeners = getListeners(RoleCreateListener.class);
        for (RoleCreateListener listener : listeners) {
            try {
                listener.onCreate(playerActor, role);
            } catch (Exception e) {
                ExceptionUtils.log(e);
            }
        }
    }
}
