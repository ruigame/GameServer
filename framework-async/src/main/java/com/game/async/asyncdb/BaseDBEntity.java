package com.game.async.asyncdb;

import com.game.util.Context;

import java.lang.reflect.Field;

/**
 * @Author: liguorui
 * @Date: 2020/9/19 6:39 下午
 */
public class BaseDBEntity extends AsynDBEntity {

    public void insert() {
        if (isRobot()) {
            return;
        }
        Context.getBean(AsyncDBService.class).insert(this);
    }

    public void update() {
        if (isRobot()) {
            return;
        }
        Context.getBean(AsyncDBService.class).update(this);
    }

    public void delete() {
        if (isRobot()) {
            return;
        }
        Context.getBean(AsyncDBService.class).delete(this);
    }

    public void serialize() {

    }


    public void deserialize() {

    }

    public ISaver saver() {
        return new BaseDBEntitySaver(this);
    }

    public boolean isRobot() {
        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append("=");
        builder.append("{");
        try {
            Field[] fields = getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                Field field = fields[i];
                field.setAccessible(true);
                builder.append(field.getName()).append("=").append(field.get(this));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.append("}");
        return builder.toString();
    }
}
