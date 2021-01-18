package com.game.logic.commdata.domain.accesser;

/**
 * @author liguorui
 * @date 2018/2/5 19:20
 */
public interface ILongDataAccessor extends IDataReset {

    long getLong();
    Long getLongLong();
    void incrLong(long delta);
    void decrLong(long delta);
    void alterLong(long delta);
    void setLong(long longValue);
}
