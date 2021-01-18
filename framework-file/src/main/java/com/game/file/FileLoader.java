package com.game.file;

import com.game.util.ExceptionUtils;
import com.game.util.FileUtils;
import com.game.util.SimpleThreadFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 文件装置器
 * @Author: liguorui
 * @Date: 2020/11/24 下午11:40
 */
public class FileLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileLoader.class);

    private static final FileAlterationMonitor monitor;

    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new SimpleThreadFactory("FileReloadMonitor"));

    static {
        int interval = 10000;
        monitor = new FileAlterationMonitor();
        scheduler.scheduleWithFixedDelay(monitor, interval, interval, TimeUnit.MILLISECONDS);
    }

    private List<FileLoadListener> listenerList = new ArrayList<>();

    private FileLoader(File file, List<FileLoadListener> listenerList) {
        if (hasReload(listenerList)) {
            FileAlterationObserver observer = new FileAlterationObserver(file.getParentFile(), new NameFileFilter(file.getName()));
            observer.addListener(new FileLoaderListener());
            monitor.addObserver(observer);
        }
        this.listenerList.addAll(listenerList);
    }

    private boolean hasReload(List<FileLoadListener> listenerList) {
        if (listenerList == null || listenerList.size() <= 0) {
            return false;
        }
        for (FileLoadListener listener : listenerList) {
            if (listener instanceof FileReloadListener) {
                return true;
            }
        }
        return false;
    }

    public static void load(String filePath, FileLoadListener... fileLoadListeners) throws FileNotFoundException {
        if (fileLoadListeners == null || fileLoadListeners.length == 0) {
            LOGGER.warn("file {} has not fileLoadListener", filePath);
            return;
        }
        File file = FileUtils.getFile(filePath);
        List<FileLoadListener> listenerList = Arrays.asList(fileLoadListeners);
        FileLoader fileLoader = new FileLoader(file, listenerList);
        fileLoader.load(file);
    }

    private void executeCommand(File file, DealCommand command) throws FileNotFoundException {
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            LOGGER.info("开始读取文件{}...", file.getName());
            command.execute(inputStream);
            LOGGER.info("读取文件{}结束...", file.getName());
        } catch (Exception e) {
            ExceptionUtils.log(e, "Exception occurred at read File [" + file.getName() + "]");
            throw e;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private void load(File file) throws FileNotFoundException {
        executeCommand(file, new DealCommand() {
            @Override
            public void execute(InputStream inputStream) {
                for (FileLoadListener fileLoadListener : listenerList) {
                    fileLoadListener.load(inputStream);
                }
            }
        });
    }

    private void reload(File file) {
        try {
            executeCommand(file, new DealCommand() {
                @Override
                public void execute(InputStream inputStream) {
                    for (FileLoadListener fileLoadListener : listenerList) {
                        if (fileLoadListener instanceof FileReloadListener) {
                            ((FileReloadListener)fileLoadListener).reload(inputStream);
                        }
                    }
                }
            });
        } catch (Exception e) {
            ExceptionUtils.log(e);
        }
    }

    private class FileLoaderListener extends FileAlterationListenerAdaptor {
        @Override
        public void onFileChange(File file) {
            reload(file);
        }
    }

    private static interface DealCommand {
        public void execute(InputStream inputStream);
    }

    public static void close() {
        scheduler.shutdown();
    }
}
