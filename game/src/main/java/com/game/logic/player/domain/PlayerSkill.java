package com.game.logic.player.domain;

import com.game.base.JSONUtils;
import com.game.base.TypeReference;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liguorui
 * @Date: 2021/1/18 下午11:51
 */
public class PlayerSkill {

    private Map<Integer, Map<Integer, Skill>> skillMap = new HashMap<>();

    public PlayerSkill() {

    }

    public PlayerSkill(Map<Integer, Map<Integer, Skill>> skillMap) {
        super();
        this.skillMap = skillMap;
        if (this.skillMap == null) {
            this.skillMap = Maps.newHashMap();
        }
    }

    public PlayerSkill(String skill) {
        super();
        if (StringUtils.isNotBlank(skill)) {
            this.skillMap = JSONUtils.toObject(skill, TypeReference.MAP_SKILL);
        }
    }

    public Skill getSkill(int index, int skillId) {
        Map<Integer, Skill> skills = getSkillMap(index);
        return skills.get(skillId);
    }

    public Map<Integer, Skill> getSkillMap(int index) {
        Map<Integer, Skill> map = skillMap.get(index);
        if (map == null) {
            map = Maps.newHashMap();
            skillMap.put(index, map);
        }
        return map;
    }

    public Skill getMinSkillLevel(int index) {
        int minLevel = 0;
        Skill minSKill = null;
        for (Skill skill : getSkillMap(index).values()) {
            if (minLevel == 0 || minLevel > skill.getLevel()) {
                minLevel = skill.getLevel();
                minSKill = skill;
            }
        }
        return minSKill;
    }

    public Map<Integer, Map<Integer, Skill>> getAllSkillMap() {
        return skillMap;
    }

    public Skill addSkill(int index, int skillId) {
        Map<Integer, Skill> skills = getSkillMap(index);
        if (skills.containsKey(skillId)) {
            return null;
        }
        Skill skill = new Skill(skillId, 1);
        skills.put(skillId, skill);
        return skill;
    }

    public boolean containsSkill(int index, int skillId) {
        Map<Integer, Skill> skills = getSkillMap(index);
        return skills.containsKey(skillId);
    }
}
