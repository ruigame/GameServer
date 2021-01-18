package com.game.logic.commdata.domain;

import com.game.logic.commdata.NewCommDataType;
import com.game.logic.commdata.domain.accesser.IDataAccessor;
import com.game.logic.commdata.domain.accesser.impl.DataAccessor;
import com.game.logic.commdata.entity.NewCommData;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: liguorui
 * @Date: 2020/12/8 下午11:03
 */
public class PlayerNewCommData {

    private final long playerId;

    public static PlayerNewCommData create(long playerId) {
        return new PlayerNewCommData(playerId);
    }

    private final Map<NewCommDataType, IDataAccessor> accessors;

    private PlayerNewCommData(long playerId) {
        this.playerId = playerId;
        this.accessors = new HashMap<>();
    }

    public void init(List<NewCommData> newCommDataList) {
        synchronized (accessors) {
            for (NewCommData data : newCommDataList) {
                if (!data.existType()) {
                    continue;
                }
                data.markInserted();
                data.onLoad();
                cache(data);
            }
        }
    }

    public boolean contains(NewCommDataType type) {
        return accessors.containsKey(type);
    }

    private void cache(NewCommData data) {
        NewCommDataType type = data.getTypeEnum();
        accessors.put(type, new DataAccessor(data));
    }

    public IDataAccessor access(NewCommDataType type) {
        return access(type, null);
    }

    public IDataAccessor access(NewCommDataType type, Class<? extends IDataAccessor> clz) {
        IDataAccessor accessor = accessors.get(type);
        if (accessor != null) {
            return accessor;
        }

        synchronized (accessors) {
            //上线已经缓存已经有的，没有的表里也没数据，直接new新的
            accessor = accessors.get(type);
            if (accessor == null) {
                NewCommData data = new NewCommData(playerId, type);
                if (clz == null) {
                    accessor = new DataAccessor(data);
                } else {
                    try {
                        Constructor<? extends  IDataAccessor> c = clz.getConstructor(NewCommData.class);
                        accessor = c.newInstance(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                accessors.put(type, accessor);
            }
        }
        return accessor;
    }

    public void onMidnight() {
        synchronized (accessors) {
            for (IDataAccessor accessor : accessors.values()) {
                accessor.unwrap().onMidnight(accessor);
            }
        }
    }
}
