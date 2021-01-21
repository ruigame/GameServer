package com.game.logic.player.dao;

import com.game.async.asyncdb.orm.GameDaoSupport;
import com.game.logic.player.entity.MutePlayer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: liguorui
 * @Date: 2021/1/21 下午11:21
 */
@Component
@Transactional
public class MutePlayerDaoImpl extends GameDaoSupport<MutePlayer> implements MutePlayerDao {
}
