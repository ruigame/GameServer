package com.game.logic.commdata;

import java.io.Serializable;

/**
 * @author liguorui
 * @date 2018/2/5 18:18
 */
public class CommDataKey implements Serializable {

    private static final long serialVersionUID = 7920864052122501129L;

    private long playerId;

    private int type;

    public CommDataKey() {
        super();
    }

    public CommDataKey(long playerId, int type) {
        super();
        this.playerId = playerId;
        this.type = type;
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
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int)(playerId ^ (playerId >>> 32));
        result = prime * result + type;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CommDataKey other = (CommDataKey) obj;
        if (playerId != other.getPlayerId())
            return false;
        if (type != other.type)
            return false;
        return true;
    }
}
