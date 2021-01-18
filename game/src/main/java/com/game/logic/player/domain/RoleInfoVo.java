package com.game.logic.player.domain;

import com.game.net.packet.Response;

import java.util.Map;

/**
 * 玩家下每个角色的信息
 * @Author: liguorui
 * @Date: 2021/1/18 下午10:25
 */
public class RoleInfoVo {

    private int index;
    private RoleType roleType;
    private Gender gender;
    private String avatar;
    /**
     * 穿戴的武器
     */
    private String weapon;
    /**
     * 翅膀
     */
    private String wingAvatar;

    /**
     * 称号
     */
    private int[] titles;

    /**
     * 战力
     */
    private long fighting;
    /**
     * 角色属性
     */
    private Map<ResourceType, Long> attrs;

    public void write(Response response) {
        response.writeByte(index);
        response.writeByte(roleType.getId());
        response.writeByte(gender.getId());
        response.writeString(avatar);
        response.writeString(weapon);
        response.writeString(wingAvatar);
        response.writeInt(titles.length);
        for (int title :titles) {
            response.writeInt(title);
        }
        response.writeLong(fighting);
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

    public String getWingAvatar() {
        return wingAvatar;
    }

    public void setWingAvatar(String wingAvatar) {
        this.wingAvatar = wingAvatar;
    }

    public int[] getTitles() {
        return titles;
    }

    public void setTitles(int[] titles) {
        this.titles = titles;
    }

    public long getFighting() {
        return fighting;
    }

    public void setFighting(long fighting) {
        this.fighting = fighting;
    }

    public Map<ResourceType, Long> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<ResourceType, Long> attrs) {
        this.attrs = attrs;
    }
}
