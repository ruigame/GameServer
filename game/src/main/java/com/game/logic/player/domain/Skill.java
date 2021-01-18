package com.game.logic.player.domain;

/**
 * @Author: liguorui
 * @Date: 2021/1/18 下午11:52
 */
public class Skill {

    private int skillId;
    private int level;

    public Skill() {
        super();
    }

    public Skill(int skillId, int level) {
        this.skillId = skillId;
        this.level = level;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void levelUp() {
        level ++;
    }

    public int nextLevel() {
        return level + 1;
    }

    @Override
    public String toString() {
        return "Skill [skillId=" + skillId + ", level=" + level + "]";
    }
}
