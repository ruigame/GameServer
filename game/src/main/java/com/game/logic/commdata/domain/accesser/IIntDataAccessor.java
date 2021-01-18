package com.game.logic.commdata.domain.accesser;

/**
 * @author liguorui
 * @date 2018/2/5 19:16
 */
public interface IIntDataAccessor extends IDataReset {

    int getInt();
    Integer getInteger();
    void incrInt(int delta);
    void decrInt(int delta);
    void alterInt(int delta);
    void setInt(int intValue);

}
