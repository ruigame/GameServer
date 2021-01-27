package com.game.logic.common;

import com.game.file.ConfigPath;
import com.game.file.FileLoader;
import com.game.file.FileReloadListener;
import com.game.file.MergerReloadListener;
import com.game.file.prop.PropConfig;
import com.game.file.prop.PropConfigStore;
import com.game.util.ExceptionUtils;
import com.game.util.WhiteWordFilter;
import com.game.util.WordConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文字处理服务器，包括脏词过滤等等
 * @Author: liguorui
 * @Date: 2021/1/27 下午8:30
 */
@Component
public class WordService implements InitializingBean {

    /**
     * 缺省的敏感字替换字符
     */
    public final static char DEFAULT_BADWORD_REPLACEMENT = '*';

    /**
     * 系统禁用的特殊字符
     */
    public final static char[] FORBID_CHARS = {'~', '@', '!', '！', '?', '？', '#', '$', '%', '^',
            '&', '*', '(', ')', ' ','-', '=', '+', '\\', '|', '<', '>', ',', '/', '？', ']',
            '[', '{', '}', '`', '\'', '\r', '\n', '\t', '"'};


    private ThreadLocal<Pattern> NOT_CHINESE_PATTERN = new ThreadLocal<Pattern>() {
        @Override
        protected Pattern initialValue() {
            String pattern = "[^" + WordConstants.Chinese + "\\w]*";//"[^\u4e00-\u9fa5\\w]*"
            return Pattern.compile(pattern);
        }
    };

    private ThreadLocal<Pattern> NICKNAME_PATTERN = new ThreadLocal<Pattern>() {
        @Override
        protected Pattern initialValue() {
            String pattern = "^[" + WordConstants.Chinese + "a-zA-Z]+$";//"^[\u4e00-\u9fa5-zA-Z]+$"
            return Pattern.compile(pattern);
        }
    };

    private ThreadLocal<Pattern> CHINESE_PATTERN = new ThreadLocal<Pattern>() {
        @Override
        protected Pattern initialValue() {
            String pattern = "[" + WordConstants.Chinese + "]+";//"[\u4e00-\u9fa5]+"
            return Pattern.compile(pattern); //匹配字符串是否包括中文
        }
    };

    private volatile WhiteWordFilter charFilter;

    private volatile WhiteWordFilter nameFilter;

    private volatile WhiteWordFilter guildFilter;

    private PropConfig propConfig = PropConfigStore.getPropConfig(ConfigPath.Common.ROLE_NAME_WHITE_LIST_PROPERTIES);

    public void initFilter() {
        try {
            FileLoader.load(ConfigPath.BadWord.CHEAT_BADWORD_FILE, new MergerReloadListener() {
                @Override
                public void load(InputStream chatIns) {
                    try {
                        InputStream whiteIns = WhiteWordFilter.class.getClassLoader()
                                .getResourceAsStream(ConfigPath.BadWord.CHEAT_WHITE_FILE);
                        charFilter = WhiteWordFilter.load(chatIns, whiteIns);
                    } catch (Exception e) {
                        ExceptionUtils.log(e);
                    }
                }
            });

            FileLoader.load(ConfigPath.BadWord.CHEAT_WHITE_FILE, new FileReloadListener() {
                @Override
                public void reload(InputStream whiteIns) { //重载时候才需要更新，第一次上面已经更新过了
                    try {
                        InputStream chatIns = WhiteWordFilter.class.getClassLoader()
                                .getResourceAsStream(ConfigPath.BadWord.CHEAT_BADWORD_FILE);
                        charFilter = WhiteWordFilter.load(chatIns, whiteIns);
                    } catch (Exception e) {
                        ExceptionUtils.log(e);
                    }
                }

                @Override
                public void load(InputStream inputStream) {

                }
            });

            FileLoader.load(ConfigPath.BadWord.NAME_BADWORD_FILE, new MergerReloadListener() {
                @Override
                public void load(InputStream nameIs) {
                    try {
                        nameFilter = WhiteWordFilter.load(nameIs);
                    } catch (Exception e) {
                        ExceptionUtils.log(e);
                    }
                }
            });

            FileLoader.load(ConfigPath.BadWord.GUILD_BADWORD_FILE, new MergerReloadListener() {
                @Override
                public void load(InputStream guildIS) {
                    try {
                        guildFilter = WhiteWordFilter.load(guildIS);
                    } catch (Exception e) {
                        ExceptionUtils.log(e);
                    }
                }
            });
        } catch (Exception e) {
            ExceptionUtils.log(e);
        }
    }

    /**
     * 判断是否存在系统禁用的特殊字符
     * @param input
     * @return
     */
    public boolean hasForbidCharas(String input) {
        return StringUtils.containsAny(input, FORBID_CHARS);
    }

    /**
     * 是否符合用户注册规则只包含中文字符，0-9，a-z，A-Z
     * @param input
     * @return
     */
    public boolean isRegUserFormat(String input) {
        Matcher m = NICKNAME_PATTERN.get().matcher(input);
        return m.matches();
    }

    public boolean isRegUserFormatForRename(String input) {
        Pattern p = Pattern.compile(propConfig.getStr("WHITE_LIST", ""));
        Matcher wm = p.matcher(input);
        String str = wm.replaceAll("");
        Matcher m = NICKNAME_PATTERN.get().matcher(str);
        return m.matches();
    }

    /**
     * 判断指定的输入字符是否含有敏感字符
     * @param content
     * @return
     */
    public boolean chatHasBadWords(String content) {
        //处理敏感字中间用特殊字符拆分的情况
        //例如：习~近~平
        Matcher m = NOT_CHINESE_PATTERN.get().matcher(content);
        String tempContent = m.replaceAll("");
        return charFilter.hasBadWords(content) || charFilter.hasBadWords(tempContent);
    }

    /**
     * 判断角色名称是否含有敏感字符
     * @param content
     * @return
     */
    public boolean nameHasBadWords(String content) {
        //处理敏感字中间用特殊字符拆分的情况
        //例如：习~近~平
        Matcher m = NOT_CHINESE_PATTERN.get().matcher(content);
        String tempContent = m.replaceAll("");
        return nameFilter.hasBadWords(content) || nameFilter.hasBadWords(tempContent);
    }

    /**
     * 判断公会名称是否含有敏感字符
     * @param content
     * @return
     */
    public boolean guildHasBadWords(String content) {
        //处理敏感字中间用特殊字符拆分的情况
        //例如：习~近~平
        Matcher m = NOT_CHINESE_PATTERN.get().matcher(content);
        String tempContent = m.replaceAll("");
        return guildFilter.hasBadWords(content) || guildFilter.hasBadWords(tempContent);
    }

    /**
     * 过滤指定的字符串，并使用指定的字符替换
     * @param content
     * @param replacement
     * @return
     */
    public final String filterWords(String content, char replacement) {
        return charFilter.filterWords(content, replacement);
    }

    public final String filterWords(String content) {
        return filterWords(content, DEFAULT_BADWORD_REPLACEMENT);
    }

    private int start = Integer.valueOf("4e00", 16);
    private int end = Integer.valueOf("9fa5", 16);

    /**
     * 计算中英文字符混合情况下，字符总数，一个汉子相当2个字符长度
     */
    public final int getMixedStringLength(String input) {
        int length = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            length += (start <= c && end >= c) ? 2 : 1;
        }
        return length;
    }

    /**
     * 角色名称是否符合创角的条件
     * @param input
     * @return
     */
    public boolean isAvailableFormatForCreate(String input) {
        if (!isRoleNameLengthValid(input)) {
            return false;
        }
        if (hasForbidCharas(input)) {
            return false;
        }
        if (!this.isRegUserFormat(input)) {
            return false;
        }
        if (nameHasBadWords(input)) {
            return false;
        }
        return true;
    }

    /**
     * 角色名称是否长度是否符合
     * @param name
     * @return
     */
    public boolean isRoleNameLengthValid(String name) {
        if(StringUtils.isBlank(name)) {
            return false;
        }
        int size = this.getMixedStringLength(name);
        if (size < 4 || size > 12) { //6
            return false;
        }
        return true;
    }

    /**
     * 公会名称是否有效的名字格式
     * @param input
     * @return
     */
    public boolean isAvailableFormatGuild(String input) {
        if (StringUtils.isBlank(input)) {
            return false;
        }
        int size = this.getMixedStringLength(input);
        if (size < 4 || size > 14) { //最多7个汉子
            return false;
        }
        if (this.hasForbidCharas(input)) {
            return false;
        }
        if (!this.isRegUserFormat(input)) {
            return false;
        }
        if (this.guildHasBadWords(input)) {
            return false;
        }
        return true;
    }

    /**
     * 是否包含中文
     * @param input
     * @return
     */
    public boolean isContainChinese(String input) {
        Matcher m = CHINESE_PATTERN.get().matcher(input);
        return m.matches();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initFilter();
    }
}
