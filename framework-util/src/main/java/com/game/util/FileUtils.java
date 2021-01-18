package com.game.util;

import com.google.common.base.Preconditions;

import java.io.File;
import java.net.URL;

/**
 * 文件工具类
 * @Author: liguorui
 * @Date: 2020/11/24 下午11:55
 */
public class FileUtils {

    public static File getFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            URL url = FileUtils.class.getClassLoader().getResource(filePath);
            Preconditions.checkNotNull(url, "File[%s] NOT Found!", filePath);
            file = new File(url.getFile());
        }
        return file;
    }
}
