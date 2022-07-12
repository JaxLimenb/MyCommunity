package com.nowcoder.mycommunity.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sun.text.normalizer.Trie;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.Remote;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-09-16:52
 */
@Component
public class SensitiveFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensitiveFilter.class);
    // 敏感词替换符
    private static final String REPLACEMENT = "***";
    // 根节点
    private TrieNode rootNode = new TrieNode();

    /**
     * 初始化方法，在类构造之前运行
     */
    @PostConstruct
    public void init() {
        InputStream is = null;
        BufferedReader br = null;
        try {
            is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            br = new BufferedReader(new InputStreamReader(is));
            String keyword;
            while ((keyword = br.readLine()) != null) {
                if (!StringUtils.isBlank(keyword)) {
                    // 添加到前缀树对象中去
                    this.addKeyword(keyword);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将一个敏感词添加到前缀树中去
     * @param keyword 敏感词
     */
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        char[] charArray = keyword.toCharArray();
        int size = charArray.length;
        for (int i = 0; i < size; i++) {
            char c = charArray[i];
            if (tempNode.getSubNode(c) == null) {
                tempNode.addSubNode(c, new TrieNode());
            }
            tempNode = tempNode.getSubNode(c);
            if (i == size - 1) {
                tempNode.setSensitive(true);
            }
        }
    }

    /**
     * 过滤敏感词方法
     * @param text 待过滤文本（这里指帖子）
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        int position = 0;
        int begin = 0;
        StringBuffer result = new StringBuffer();
        TrieNode tempNode = rootNode;
        while (position < text.length()) {
            char c = text.charAt(position);
            // 跳过符号
            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    result.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            // 如果tempNode为null表示以begin开头的字符串不是敏感词
            if (tempNode == null) {
                // 存入begin字符
                result.append(text.charAt(begin));
                // 重新定位position到begin后面，继续查找
                position = ++begin;
                // 重新指向根节点
                tempNode = rootNode;
            }else if (tempNode.isSensitive()) {
                // 如果当前字符为敏感字符表示找到敏感词，进行替换
                result.append(REPLACEMENT);
                // begin指针指向敏感词后面
                begin = ++position;
                tempNode = rootNode;
            }else {
                // 如果当前字符在敏感词中，但并非最后一个字符，继续检查后一字符
                position++;
                // 如果指针超出范围，表示当前不是敏感词，重新定位
                if (position == text.length()) {
                    result.append(text.charAt(begin));
                    position = ++begin;
                    tempNode = rootNode;
                }
            }
        }
        return result.toString();
    }

    /**
     * 用于判断当前字符是否合法的工具
     * @param c 当前字符
     * @return 如果为东亚文字或者为abc123（数字字母）
     */
    private boolean isSymbol(Character c) {
        // c < 0x2E80 || c > 0x9FFF 是东亚文字范围（中日韩文）
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    /**
     * 内部类，定义了前缀树的节点
     */
    private class TrieNode {
        // 标识是否为敏感词
        private boolean isSensitive;
        // 子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isSensitive() {
            return isSensitive;
        }

        public void setSensitive(boolean sensitive) {
            isSensitive = sensitive;
        }

        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }

}
