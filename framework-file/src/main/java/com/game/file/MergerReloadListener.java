package com.game.file;

import java.io.InputStream;

/**
 * @Author: liguorui
 * @Date: 2020/11/25 上午12:09
 */
public abstract class MergerReloadListener implements FileReloadListener {

    @Override
    public void reload(InputStream inputStream) {
        load(inputStream);
    }
}
