package com.game.logic.commdata.domain.accesser;

import com.game.logic.commdata.entity.NewCommData;

/**
 * @author liguorui
 * @date 2018/2/5 19:07
 */
public class DataWrapper {

    protected final NewCommData data;

    public DataWrapper(NewCommData data) {
        this.data = data;
    }

    public NewCommData unwrap() {
        return data;
    }
}
