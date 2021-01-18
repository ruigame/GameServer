package com.game.logic.commdata.entity;

import com.game.async.asyncdb.anotation.Persistent;
import com.game.async.asyncdb.orm.BaseDBEntity;
import com.game.logic.commdata.CommDataKey;
import com.game.logic.commdata.NewCommDataMonitor;
import com.game.logic.commdata.NewCommDataType;
import com.game.logic.commdata.dao.NewCommDataDAO;
import com.game.logic.commdata.domain.DataStatus;
import com.game.logic.commdata.domain.accesser.IDataAccessor;

import javax.persistence.*;

/**
 * @author liguorui
 * @date 2018/1/15 00:11
 */
@Entity
@Table(name = "NewCommData", indexes = {@Index(name="typeIndex", columnList = "type")})
@IdClass(CommDataKey.class)
@Persistent(syncClass = NewCommDataDAO.class)
public class NewCommData extends BaseDBEntity {

    @Id
    private long playerId;

    @Id
    private int type;

    @Column(columnDefinition = "text", nullable = true)
    private String value;
    private String monitorParam;

    @Transient
    private NewCommDataType typeEnum;
    @Transient
    private NewCommDataMonitor monitor;
    @Transient
    private DataStatus status;
    @Transient
    private boolean dirty = false;

    public NewCommData() {}

    public NewCommData(long playerId, NewCommDataType typeEnum) {
        this(playerId, typeEnum.getType(), typeEnum.getInitValue());
    }

    public NewCommData(long playerId, int type, String value) {
        this(playerId, type, value, "");
    }

    public NewCommData(long playerId, int type, String value, String monitorParam) {
        this.playerId = playerId;
        this.type = type;
        this.value = value;
        this.monitorParam = monitorParam;
        markNotInserted();
        wrapType(type);
    }

    public void reset() {
        setValue(typeEnum.getInitValue());
    }

    public void onLoad() {
        monitorLoad();
    }

    public void onMidnight(IDataAccessor accessor) {
        monitorMidnight(accessor);
    }

    public Integer getInt() {
        return Integer.valueOf(getValue());
    }

    public void setInt(Integer intValue) {
        setValue(intValue.toString());
    }

    public void setInt(int intValue) {
        setValue(String.valueOf(intValue));
    }

    public Long getLong() {
        return Long.valueOf(getValue());
    }

    public void setLong(Long longValue) {
        setValue(longValue.toString());
    }

    public void setLong(long longValue) {
        setValue(String.valueOf(longValue));
    }

    public String getValue() {
        monitorRead();
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        monitorWrite();
        updateValue();
    }

    /**
     * 即时入库
     */
    private void updateValue() {
        if (this.isNotInserted()) {
            this.insert();
            this.markInserted();
        } else {
            this.update();
        }
    }

    public void markNotInserted() {
        status = DataStatus.NOT_INSERTED;
    }

    public void markInserted() {
        status = DataStatus.INSERTED;
    }

    public boolean isNotInserted() {
        return status == DataStatus.NOT_INSERTED;
    }

    private void monitorRead() {
        if (monitor != null) {
            monitor.onRead(this);
        }
    }

    private void monitorWrite() {
        if (monitor != null) {
            monitor.onWrite(this);
        }
    }

    private void monitorMidnight(IDataAccessor accessor) {
        if (monitor != null) {
            monitor.onMidnight(this, accessor);
        }
    }

    private void monitorLoad() {
        if (monitor != null) {
            monitor.onLoad(this);
        }
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        wrapType(type);
    }

    public String getMonitorParam() {
        return monitorParam;
    }

    public void setMonitorParam(String monitorParam) {
        this.monitorParam = monitorParam;
    }

    public NewCommDataType getTypeEnum() {
        return typeEnum;
    }

    private void wrapType(int type) {
        typeEnum = NewCommDataType.wrap(type);
//        checkNotNull(typeEnum, "type:%s", type);
        monitor = typeEnum.getMonitor();
    }

    private void wrapTypeUnException(int type) {
        typeEnum = NewCommDataType.wrap(type);
        if (typeEnum == null) {
            return;
        }
        monitor = typeEnum.getMonitor();
    }

    public boolean existType() {
        return typeEnum != null;
    }

    @Override
    public void deserialize() {
        wrapTypeUnException(type);
    }

    public void markClean() {
        dirty = false;
    }

    public boolean isDirty() {
        return dirty;
    }



    private void markDirty() {
        dirty = true;
    }
}
