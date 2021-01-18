package com.game.file;

import java.io.InputStream;

/**
 * 可重载的监听器
 * @Author: liguorui
 * @Date: 2020/11/24 下午11:47
 */
public interface FileReloadListener extends FileLoadListener{

    void reload(InputStream inputStream);
}
