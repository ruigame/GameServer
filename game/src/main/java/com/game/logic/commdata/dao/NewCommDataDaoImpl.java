package com.game.logic.commdata.dao;//package com.game.logic.commdata.dao;


import com.game.async.asyncdb.orm.GameDaoSupport;
import com.game.logic.commdata.CommDataKey;
import com.game.logic.commdata.entity.NewCommData;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author liguorui
 * @date 2018/2/6 17:35
 */
@Component
@Transactional
public class NewCommDataDaoImpl extends GameDaoSupport<NewCommData> implements NewCommDataDAO {


    @Override
    public NewCommData get(long playerId, int type) {
        return get(new CommDataKey(playerId, type));
    }

    @Override
    public List<NewCommData> selectAll(long playerId) {
        Session session = getSession();
        List<NewCommData> newCommDatas = session.createQuery("select newCommData from NewCommData newCommData " +
                " where newCommData.playerId=:playerId", NewCommData.class)
                .setParameter("playerId", playerId).list();
        deserializeList(newCommDatas);
        return newCommDatas;
    }

    @Override
    public List<NewCommData> selectAllByType(int type) {
        List<NewCommData> newCommDataList = getSession().createQuery("select newCommData from NewCommData newCommData " +
                " where newCommData type=:type", NewCommData.class)
                .setParameter("type", type)
                .list();
        deserializeList(newCommDataList);
        return newCommDataList;
    }

    @Override
    public int updateByType(int type, String value) {
        return getSession().createQuery("update NewCommData newCommData set newCommData.value=:value where newCommData.type=:type")
                .setParameter("value", value)
                .setParameter("type", type).executeUpdate();
    }

    @Override
    public void deleteByType(int type) {
        getSession().createQuery("DELETE NewCommData newCommData where newCommData.type=:type where newCommData.type=:type")
                .setParameter("type", type)
                .executeUpdate();
    }

    private void deserializeList(List<NewCommData> newCommDataList) {
        for (NewCommData newCommData : newCommDataList) {
            newCommData.deserialize();
        }
    }
}
