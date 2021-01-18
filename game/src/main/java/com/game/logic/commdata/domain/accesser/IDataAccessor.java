package com.game.logic.commdata.domain.accesser;

import com.game.logic.commdata.entity.NewCommData;

/**
 * @author liguorui
 * @date 2018/2/5 19:12
 */
public abstract class IDataAccessor extends DataWrapper implements IDataReset,
      IIntDataAccessor, ILongDataAccessor, IStringDataAccessor, IJsonDataAccessor,
        IJsonArrayDataAccessor, IObjectDataAccessor, IBooleanDataAccessor {

    public IDataAccessor(NewCommData data) {
        super(data);
    }

}
