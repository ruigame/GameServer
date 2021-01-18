package com.game.logic.commdata.domain.accesser.impl;

import com.game.logic.commdata.domain.accesser.DataWrapper;
import com.game.logic.commdata.domain.accesser.IBooleanDataAccessor;
import com.game.logic.commdata.entity.NewCommData;

/**
 * @author liguorui
 * @date 2018/2/5 19:25
 */
public class BooleanDataAccessor extends DataWrapper implements IBooleanDataAccessor {

    private boolean cache;
    private boolean cached;

    public BooleanDataAccessor(NewCommData data) {
        super(data);
    }

    @Override
    public void reset() {
        cached = false;
    }

    private boolean getCache() {
        if (cached) {
            return cache;
        }

        cache = Boolean.parseBoolean(data.getValue());
        cached = true;
        return cache;
    }

    @Override
    public boolean isTrue() {
        return getCache();
    }

    @Override
    public void setBool(boolean booleanValue) {
        this.cache = booleanValue;
        data.setValue(String.valueOf(booleanValue));
    }
}
