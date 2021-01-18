package com.game.logic.commdata.dao;

import com.game.async.asyncdb.orm.BaseDao;
import com.game.logic.commdata.entity.NewCommData;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author liguorui
 * @date 2018/1/29 00:00
 */
@Component
public interface NewCommDataDAO extends BaseDao<NewCommData> {

    NewCommData get(long playerId, int type);

    List<NewCommData> selectAll(long playerId);

    List<NewCommData> selectAllByType(int type);

    int updateByType(int type, String value);

    void deleteByType(int type);

}
