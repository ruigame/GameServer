package com.game.player.domain;

import com.game.util.TimeUtils;

/**
 * 玩家下的每个角色
 * @Author: liguorui
 * @Date: 2020/9/20 8:22 下午
 */
public class Role implements Cloneable {

    private int index;
    private RoleType roleType;
    private Gender gender;
    /**
     * 战力
     */
    private long fighting;

    /**
     * 形象
     */
    private String avatar;

    /**
     * 时装
     */
    private String weapon;

    private int createTime;

    public Role() {
        super();
    }

    public Role(int index, RoleType roleType, Gender gender) {
        super();
        this.index = index;
        this.roleType = roleType;
        this.gender = gender;
        this.createTime = TimeUtils.curTimestamp();
    }

    @Override
    protected Role clone() throws CloneNotSupportedException {
        return (Role) super.clone();
    }

    @Override
    public String toString() {
        return "Role [index=" + index + ", roleType=" + roleType + ",gender=" + gender + "]";
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public long getFighting() {
        return fighting;
    }

    public void setFighting(long fighting) {
        this.fighting = fighting;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getWeapon() {
        return weapon;
    }

    public void setWeapon(String weapon) {
        this.weapon = weapon;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }
}
