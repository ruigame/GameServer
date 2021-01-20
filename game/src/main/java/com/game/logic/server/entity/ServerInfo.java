package com.game.logic.server.entity;

import com.game.async.asyncdb.anotation.Persistent;
import com.game.async.asyncdb.orm.BaseDBEntity;
import com.game.logic.server.dao.ServerInfoDao;
import com.game.logic.server.domain.ConfigKey;
import com.game.base.JSONUtils;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;

/**
 * @Author: liguorui
 * @Date: 2020/12/15 下午10:24
 */
@Entity
@Persistent(syncClass = ServerInfoDao.class)
@Table(name="ServerInfo")
public class ServerInfo extends BaseDBEntity {

    @Id
    private String ckey;

    @Column(columnDefinition = "mediumtext")
    private volatile String cvalue;

    private transient int intValue;

    @Transient
    private volatile boolean isInit = false;

    @Transient
    private transient Object jsonObj;

    public String getCkey() {
        return ckey;
    }

    public void setCkey(String ckey) {
        this.ckey = ckey;
    }

    public String getCvalue() {
        return cvalue;
    }

    public void setCvalue(String cvalue) {
        this.cvalue = cvalue;
        isInit = false;
    }

    public void initValue() {
        if (StringUtils.isEmpty(cvalue)) {
            intValue = 0;
            isInit = true;
        }
        try {
            intValue = Integer.valueOf(cvalue);
            isInit = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getIntValue() {
        if (!isInit) {
            initValue();
        }
        return intValue;
    }

    public ConfigKey getKey() {
        return ConfigKey.getConfigKey(ckey);
    }

    public <E> E getJsonObj(Class<E> clazz) {
        if (!isInit) {
            synchronized (this) {
                jsonObj = JSONUtils.toObject(cvalue, clazz);
                isInit = true;
            }
        }
        return (E)jsonObj;
    }
}
