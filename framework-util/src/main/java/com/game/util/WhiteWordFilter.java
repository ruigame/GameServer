package com.game.util;

import com.koloboke.collect.map.CharObjMap;
import com.koloboke.collect.map.hash.HashCharObjMaps;
import org.apache.commons.io.input.BOMInputStream;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 带白名单的单词过滤
 * @Author: liguorui
 * @Date: 2021/1/27 下午9:02
 */
public class WhiteWordFilter {

    private Node RootNode = null;
    private Node whiteRootNode = null;

    private static final char whiteTempChar = 1;

    public static WhiteWordFilter load(InputStream is) throws Exception {
        WhiteWordFilter filter = new WhiteWordFilter();
        filter.RootNode = loadNode(is);
        return filter;
    }

    public static WhiteWordFilter load(InputStream is, InputStream whiteIs) throws Exception {
        WhiteWordFilter filter = new WhiteWordFilter();
        filter.RootNode = loadNode(is);
        filter.whiteRootNode = loadNode(whiteIs);
        return filter;
    }

    private static Node loadNode(InputStream is) throws Exception {
        Node rootNode = new Node('R');
        BufferedReader reader = new BufferedReader(new InputStreamReader(new BOMInputStream(is), StandardCharsets.UTF_8));
        String line;
        do {
            line = reader.readLine();
            if ((line != null) && (line.length() > 0)) {
                char[] chars = line.toLowerCase().toCharArray();
                if (chars.length > 0) {
                    insertNode(rootNode, chars, 0);
                }
            }
        } while (line != null) ;

        is.close();
        reader.close();
        return rootNode;
    }

    public String filterWords(String content, char replacement) {
        char[] sourceChars = content.toCharArray();
        boolean whiteFilter = filterWords(this.whiteRootNode, sourceChars, whiteTempChar);
        filterWords(this.RootNode, sourceChars, replacement);
        if (whiteFilter) {//白名单生效，则需要恢复白名单替换字符
            char[] tempChars = content.toCharArray();
            for (int i = 0, len = sourceChars.length; i < len; i++) {
                if (sourceChars[i] == whiteTempChar) {
                    sourceChars[i] = tempChars[i];
                }
            }
        }
        return String.valueOf(sourceChars);
    }

    private boolean filterWords(Node rootNode, char[]sourceChars, char replacement) {
        if (rootNode == null) {
            return false;
        }
        Node node = rootNode;
        int mark = 0;
        int index = 0;
        boolean filter = false;
        char[] lowerChars = CollectionUtil.toLowerCase(sourceChars);
        while(index < sourceChars.length) {
            node = findNode(node, lowerChars[index]);
            if (node != null) {
                if (node.flag) {
                    for (int i = mark; i < index; i++) {
                        sourceChars[i] = replacement;
                    }
                    mark = index;
                    filter = true;
                }
                ++index;
            }

            if ((node == null) || (index >= sourceChars.length)) {
                node = rootNode;
                ++mark;
                index = mark;
            }
        }
        return filter;
    }

    public boolean hasBadWords(String content) {
        char[] chars = content.toLowerCase().toCharArray();
        Node node = this.RootNode;
        int mark = 0;
        int index = 0;
        while(index < chars.length) {
            node = findNode(node, chars[index]) ;
            if (node != null) {
                if (node.flag) {
                    return true;
                }
                ++index;
            }
            if ((node == null) || (index >= chars.length)) {
                node = this.RootNode;
                ++mark;
                index = mark;
            }
        }
        return false;
    }

    private static void insertNode(Node node, char[]cs, int index) {
        Node n = findNode(node, cs[index]);
        if (n == null) {
            n = new Node(cs[index]);
            node.nodes.put(cs[index], n);
        }
        if (index == cs.length - 1) {
            n.flag = true;
        }

        ++index;
        if (index < cs.length) {
            insertNode(n, cs, index);
        }
    }

    private static Node findNode(Node node, char c) {
        CharObjMap<Node> nodes = node.nodes;
        return nodes.get(c);
    }

    public static class Node {
        public char c;
        public boolean flag; //是否字符节点
        public CharObjMap<Node> nodes = HashCharObjMaps.newUpdatableMap();

        public Node(char c) {
            if ((c >= 'A') && (c <= 'Z')) {
                c = (char) (c + ' ');
            }
            this.c = c;
        }

        public String toString() {
            return "Node [c=" + this.c + ", flag=" + this.flag + "]";
        }
    }
}
