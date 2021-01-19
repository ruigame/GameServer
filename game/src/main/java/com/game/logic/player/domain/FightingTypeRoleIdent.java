package com.game.logic.player.domain;

/**
 * @Author: liguorui
 * @Date: 2021/1/19 下午9:45
 */
public class FightingTypeRoleIdent {

    private int roleIndex;
    private final FightingType fightingType;

    public FightingTypeRoleIdent(int roleIndex, FightingType fightingType) {
        super();
        this.roleIndex = roleIndex;
        this.fightingType = fightingType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fightingType == null) ? 0 : fightingType.hashCode());
        result = prime * result + roleIndex;
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
        FightingTypeRoleIdent other = (FightingTypeRoleIdent)obj;
        if (fightingType != other.fightingType)
            return false;
        if (roleIndex != other.roleIndex)
            return false;
        return true;
    }
}
