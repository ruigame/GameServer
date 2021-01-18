package com.game.logic.commdata.domain.accesser.impl;

import com.game.logic.commdata.domain.accesser.DataWrapper;
import com.game.logic.commdata.domain.accesser.IStringDataAccessor;
import com.game.logic.commdata.entity.NewCommData;

/**
 * @author liguorui
 * @date 2018/2/5 20:35
 */
public class StringDataAccessor extends DataWrapper implements IStringDataAccessor {

    public StringDataAccessor(NewCommData data) {
        super(data);
    }

    @Override
    public String getString() {
        return data.getValue();
    }

    @Override
    public void setString(String value) {
        data.setValue(value);
    }

    @Override
    public void reset() {

    }
}
