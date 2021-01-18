package com.game.logic.server.dao;

import com.game.async.asyncdb.orm.GameDaoSupport;
import com.game.logic.server.entity.ServerInfo;
import org.springframework.stereotype.Component;

/**
 * @Author: liguorui
 * @Date: 2020/12/15 下午10:26
 */
@Component
public class ServerInfoDaoImpl extends GameDaoSupport<ServerInfo> implements ServerInfoDao {
}
