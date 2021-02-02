package com.game.logic.commdata.domain.accesser.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.game.logic.commdata.PlayerCommObj;
import com.game.logic.commdata.domain.accesser.*;
import com.game.logic.commdata.entity.NewCommData;
import com.game.util.ExceptionUtils;
import com.game.framework.TypeReference;

/**
 * @author liguorui
 * @date 2018/2/5 19:29
 */
public class DataAccessor extends IDataAccessor {

    private volatile IDataReset curDataWrapper;

//    private IIntDataAccessor intDataAccessor;
//    private ILongDataAccessor longDataAccessor;
//    private IStringDataAccessor stringDataAccessor;
//    private IJsonDataAccessor jsonDataAccessor;
//    private IJsonArrayDataAccessor jsonArrayDataAccessor;
//    private IObjectDataAccessor objectDataAccessor;
//    private IBooleanDataAccessor booleanDataAccessor;

    public DataAccessor(NewCommData data) {
        super(data);
    }

//    public DataAccessor(PlayerCommData data, IObjectDataAccessor objectDataAccessor) {
//        super(data);
//        this.objectDataAccessor = objectDataAccessor;
//    }

    @Override
    public void reset() {
        if (curDataWrapper != null) {
            curDataWrapper.reset();
        }
        data.reset();
//        if (intDataAccessor != null) {
//            intDataAccessor.reset();
//        }
//
//        if (longDataAccessor != null) {
//            longDataAccessor.reset();
//        }
//        if (stringDataAccessor != null) {
//            stringDataAccessor.reset();
//        }
//        if (jsonDataAccessor != null) {
//            jsonDataAccessor.reset();
//        }
//        if (jsonArrayDataAccessor != null) {
//            jsonArrayDataAccessor.reset();
//        }
//        if (objectDataAccessor != null) {
//            objectDataAccessor.reset();
//        }
//        if (booleanDataAccessor != null) {
//            booleanDataAccessor.reset();
//        }
    }

    @Override
    public JSONArray getJsonArray() {
        return getJsonArrayDataAccessor().getJsonArray();
    }

    @Override
    public void saveJsonArray() {
        getJsonArrayDataAccessor().saveJsonArray();
    }

    @Override
    public JSONObject getJson() {
        return getJsonDataAccessor().getJson();
    }

    @Override
    public void saveJson() {
        getJsonDataAccessor().saveJson();
    }

    @Override
    public String getString() {
        return getStringDataAccessor().getString();
    }

    @Override
    public void setString(String value) {
        getStringDataAccessor().setString(value);
    }

    @Override
    public long getLong() {
        return getLongDataAccessor().getLong();
    }

    @Override
    public Long getLongLong() {
        return getLongDataAccessor().getLongLong();
    }

    @Override
    public void incrLong(long delta) {
        getLongDataAccessor().incrLong(delta);
    }

    @Override
    public void decrLong(long delta) {
        getLongDataAccessor().decrLong(delta);
    }

    @Override
    public void alterLong(long delta) {
        getLongDataAccessor().alterLong(delta);
    }

    @Override
    public void setLong(long longValue) {
        getLongDataAccessor().setLong(longValue);
    }

    @Override
    public int getInt() {
        return getIntDataAccessor().getInt();
    }

    @Override
    public Integer getInteger() {
        return getIntDataAccessor().getInteger();
    }

    @Override
    public void incrInt(int delta) {
        getIntDataAccessor().incrInt(delta);
    }

    @Override
    public void decrInt(int delta) {
        getIntDataAccessor().decrInt(delta);
    }

    @Override
    public void alterInt(int delta) {
        getIntDataAccessor().alterInt(delta);
    }

    @Override
    public void setInt(int intValue) {
        getIntDataAccessor().setInt(intValue);
    }

//    @Override
//    public void saveObject() {
//        getObjectDataAccessor().saveObject();
//    }

    public IIntDataAccessor getIntDataAccessor() {
        IDataReset intDataAccessor = this.curDataWrapper;
        if (intDataAccessor == null || !(intDataAccessor instanceof IIntDataAccessor)) {
            synchronized (this) {
                if (this.curDataWrapper == null || !(this.curDataWrapper instanceof IIntDataAccessor)) {
                    intDataAccessor = new IntDataAccessor(data);
                    checkDataAccessorError(intDataAccessor);
                } else {
                    intDataAccessor = this.curDataWrapper;
                }
            }
        }
        return (IIntDataAccessor)intDataAccessor;
    }

    public ILongDataAccessor getLongDataAccessor() {
        IDataReset longDataReset = this.curDataWrapper;
        if (longDataReset == null || !(longDataReset instanceof ILongDataAccessor)) {
            synchronized (this) {
                if (this.curDataWrapper == null || !(this.curDataWrapper instanceof ILongDataAccessor)) {
                    longDataReset = new LongDataAccessor(data);
                    checkDataAccessorError(longDataReset);
                } else {
                    longDataReset = this.curDataWrapper;
                }
            }
        }
        return (ILongDataAccessor)longDataReset;
    }

    public IStringDataAccessor getStringDataAccessor() {
        IDataReset stringDataReset = this.curDataWrapper;
        if (stringDataReset == null || !(stringDataReset instanceof IStringDataAccessor)) {
            synchronized (this) {
                if (this.curDataWrapper == null || !(this.curDataWrapper instanceof IStringDataAccessor)) {
                    stringDataReset = new StringDataAccessor(data);
                    checkDataAccessorError(stringDataReset);
                } else {
                    stringDataReset = this.curDataWrapper;
                }
            }
        }
        return (IStringDataAccessor)stringDataReset;
    }

    public IJsonDataAccessor getJsonDataAccessor() {
        IDataReset jsonDataReset = this.curDataWrapper;
        if (jsonDataReset == null || !(jsonDataReset instanceof IJsonDataAccessor)) {
            synchronized (this) {
                if (this.curDataWrapper == null || !(this.curDataWrapper instanceof IJsonDataAccessor)) {
                    jsonDataReset = new JsonDataAccessor(data);
                    checkDataAccessorError(jsonDataReset);
                } else {
                    jsonDataReset = this.curDataWrapper;
                }
            }
        }
        return (IJsonDataAccessor)jsonDataReset;
    }

    public IJsonArrayDataAccessor getJsonArrayDataAccessor() {
        IDataReset jsonArrayDataReset = this.curDataWrapper;
        if (jsonArrayDataReset == null || !(jsonArrayDataReset instanceof IJsonArrayDataAccessor)) {
            synchronized (this) {
                if (this.curDataWrapper == null || !(this.curDataWrapper instanceof IJsonArrayDataAccessor)) {
                    jsonArrayDataReset = new JsonArrayDataAccessor(data);
                    checkDataAccessorError(jsonArrayDataReset);
                } else {
                    jsonArrayDataReset = this.curDataWrapper;
                }
            }
        }
        return (IJsonArrayDataAccessor)jsonArrayDataReset;
    }

    public IObjectDataAccessor getObjectDataAccessor() {
        IDataReset objectDataReset = this.curDataWrapper;
        if (objectDataReset == null || !(objectDataReset instanceof IObjectDataAccessor)) {
            synchronized (this) {
                if (this.curDataWrapper == null || !(this.curDataWrapper instanceof IObjectDataAccessor)) {
                    objectDataReset = new JsonParseAccessor(data);
                    checkDataAccessorError(objectDataReset);
                } else {
                    objectDataReset = this.curDataWrapper;
                }
            }
        }
        return (IObjectDataAccessor)objectDataReset;
    }

    public IBooleanDataAccessor getBooleanDataAccessor() {
        IDataReset booleanDataReset = this.curDataWrapper;
        if (booleanDataReset == null || !(booleanDataReset instanceof IBooleanDataAccessor)) {
            synchronized (this) {
                if (this.curDataWrapper == null || !(this.curDataWrapper instanceof IBooleanDataAccessor)) {
                    booleanDataReset = new BooleanDataAccessor(data);
                    checkDataAccessorError(booleanDataReset);
                } else {
                    booleanDataReset = this.curDataWrapper;
                }
            }
        }
        return (IBooleanDataAccessor)booleanDataReset;
    }

    private void checkDataAccessorError(IDataReset dataReset) {
        IDataReset oldDateReset = this.curDataWrapper;
        this.curDataWrapper = dataReset;
        if (oldDateReset != null && oldDateReset != dataReset) {
            ExceptionUtils.log("处理器类型更换：old:" + oldDateReset.getClass().toString() + " to new:" + dataReset.getClass().toString());
        }
    }

    @Override
    public boolean isTrue() {
        return getBooleanDataAccessor().isTrue();
    }

    @Override
    public void setBool(boolean booleanValue) {
        getBooleanDataAccessor().setBool(booleanValue);
    }

    @Override
    public <T> T getObject(String json, TypeReference<T> typeReference) {
        return getObjectDataAccessor().getObject(json, typeReference);
    }

    @Override
    public <T extends PlayerCommObj> T getPlayerCommObj(Class<T> clazz) {
        return getObjectDataAccessor().getPlayerCommObj(clazz);
    }
}
