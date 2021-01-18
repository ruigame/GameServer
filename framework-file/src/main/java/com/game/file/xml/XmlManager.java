package com.game.file.xml;

import com.game.file.FileLoader;
import com.game.file.MergerReloadListener;
import com.game.file.converter.ConverterHolder;
import com.game.file.converter.PriorityConverter;
import com.game.file.converter.PrioritySingleValueConverter;
import com.game.util.ExceptionUtils;
import com.game.util.FileUtils;
import com.game.util.SimpleThreadFactory;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterMatcher;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * @Author: liguorui
 * @Date: 2020/11/24 下午11:33
 */
public abstract class XmlManager {

    protected Logger log = LoggerFactory.getLogger(getClass());

    private String filePath;

    @Autowired
    private ConverterHolder converterHolder;

    private boolean load;

    private volatile boolean init;

    private Future<?> future;

    private static ExecutorService loadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
            new SimpleThreadFactory("XmlManager"));

    private Thread currentThread;

    private ThreadLocal<XStream> xstreamLocal = new ThreadLocal<>();

    protected XmlManager(String filePath) {
        this.filePath = filePath;
    }

    @PostConstruct
    public final void initLoad() throws FileNotFoundException {
        this.future = loadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    FileLoader.load(filePath, new MergerReloadListener() {
                        @Override
                        public void load(InputStream inputStream) {
                            currentThread = Thread.currentThread();
                            XmlManager.this.load(inputStream);
                            init = true;
                            currentThread = null;
                        }
                    });
                } catch (FileNotFoundException e) {
                    ExceptionUtils.log(e);
                }
            }
        });
    }

    public void manualLoad() {
        File file = FileUtils.getFile(filePath);
        InputStream inputStream =  null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            log.info("开始手动读取文件{}...", file.getName());
            load(inputStream);
            log.info("手动读取文件{}结束...", file.getName());
        } catch (Exception e) {
            log.error("", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private void load(InputStream inputStream) {
        XStream xStream = xstreamLocal.get();
        if (xStream == null) {
            xStream = new XStream();
            XStream.setupDefaultSecurity(xStream);
            xStream.allowTypeHierarchy(Model.class);
            xStream.autodetectAnnotations(true);
            if (converterHolder != null) {
                for (ConverterMatcher converter : converterHolder.getConvertersList()) {
                    if (converter instanceof Converter) {
                        int priority = XStream.PRIORITY_NORMAL;
                        if (converter instanceof PriorityConverter) {
                            priority = ((PriorityConverter)converter).getPriority();
                        }
                        xStream.registerConverter((Converter)converter, priority);
                    } else if (converter instanceof SingleValueConverter) {
                        int priority = XStream.PRIORITY_NORMAL;
                        if (converter instanceof PrioritySingleValueConverter) {
                            priority = ((PrioritySingleValueConverter)converter).getPriority();
                        }
                        xStream.registerConverter((SingleValueConverter)converter, priority);
                    }
                }
            }
            xstreamLocal.set(xStream);
        }
        loadXml(xStream, inputStream);
        afterLoad();
        if (load) {
            afterReLoad();
        }
        this.load = true;
    }

    protected abstract void loadXml(XStream xStream, InputStream inputStream);

    protected void afterLoad() {}

    public void setConverterHolder(ConverterHolder converterHolder) {
        this.converterHolder = converterHolder;
    }

    /**
     * 重载后触发，第一次加载不触发
     */
    protected void afterReLoad() {}

    protected void cheakInit() {
        if (!init && currentThread != Thread.currentThread()) {
            try {
                future.get();
            } catch (Exception e) {
                ExceptionUtils.log(e);
            }
        }
    }

    public void clearLoadFuture() {
        if (future != null) {
            try {
                future.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            future = null;
        }
    }

    public static void closeLoadExecutor() {
        if (loadExecutor != null) {
            loadExecutor.shutdown();
            loadExecutor = null;
        }
    }
}
