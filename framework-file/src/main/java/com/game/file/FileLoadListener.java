package com.game.file;

import java.io.InputStream;

/**
 * 文件装置器监听器
 * @Author: liguorui
 * @Date: 2020/11/24 下午11:44
 */
public interface FileLoadListener {

    void load(InputStream inputStream);
}
