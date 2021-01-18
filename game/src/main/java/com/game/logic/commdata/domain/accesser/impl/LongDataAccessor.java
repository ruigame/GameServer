package com.game.logic.commdata.domain.accesser.impl;

import com.game.logic.commdata.domain.accesser.DataWrapper;
import com.game.logic.commdata.domain.accesser.ILongDataAccessor;
import com.game.logic.commdata.entity.NewCommData;

/**
 * @author liguorui
 * @date 2018/2/5 20:28
 */
public class LongDataAccessor extends DataWrapper implements ILongDataAccessor {

    private long cache;
    private boolean cached;

    public LongDataAccessor(NewCommData data) {
        super(data);
    }

    private long getCache() {
        if (cached) {
            return cache;
        }

        cache = data.getLong();
        cached = true;
        return cache;
    }

    @Override
    public long getLong() {
        return getCache();
    }

    @Override
    public Long getLongLong() {
        return getCache();
    }

    @Override
    public void incrLong(long delta) {
//        checkArgument(delta > 0);
        alterLong(delta);
    }

    @Override
    public void decrLong(long delta) {
//        checkArgument(delta > 0);
        alterLong(-delta);
    }

    @Override
    public void alterLong(long delta) {
        if (delta == 0) {
            return;
        }
        setLong(getCache() + delta);
    }

    @Override
    public void setLong(long longValue) {
        cache = longValue;
        data.setLong(cache);
    }

    @Override
    public void reset() {
        cached = false;
    }
}
