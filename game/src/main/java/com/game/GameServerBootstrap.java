package com.game;

import com.game.async.asyncdb.AsyncDBService;
import com.game.async.asynchttp.AsyncHttpClientUtils;
import com.game.file.ConfigPath;
import com.game.file.FileLoader;
import com.game.file.prop.PropConfigStore;
import com.game.file.xml.XmlManager;
import com.game.framework.NetHelper;
import com.game.framework.SystemPropertiesSetter;
import com.game.log.logback.AsyncDailyFileAppender;
import com.game.logic.common.ConfigService;
import com.game.logic.common.OnlineService;
import com.game.net.GameStartParams;
import com.game.net.WebSocketServer;
import com.game.net.admin.AdminServer;
import com.game.net.cross.CrossServer;
import com.game.timer.TimerService;
import com.game.timer.impl.TriggerTaskExecutor;
import com.game.util.*;
import com.game.util.server.CloseService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.ArrayUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 游戏服务器启动类
 * @Author: liguorui
 * @Date: 2020/12/14 下午8:29
 */
public class GameServerBootstrap {

    /**
     * 版本号，每次发版修改一下，让测试可以对照对否拉取到最新版本，或者又没拉对渠道
     */
    private static final int VERSION = 20210202;

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServerBootstrap.class);
    private static ConfigurableApplicationContext context;
    private static GameServerBootstrap instance;
    private static WebSocketServer webSocketServer;
    private static AdminServer adminServer;
    private static CrossServer crossServer;
    private static ListenerManager listenerManager = ListenerManager.getInstance();

    static {
//        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();
        SystemPropertiesSetter.setSystemProp();
        checkEnumUnique();
    }

    public GameServerBootstrap() {
        instance = this;
    }

    public static void main(String[] args) throws Exception{
        final GameServerBootstrap bootstrap = new GameServerBootstrap();
        try {
            bootstrap.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                bootstrap.stop();
            }
        });
    }

    public void start() throws Exception {
        context = new ClassPathXmlApplicationContext("game.xml");
        Preconditions.checkNotNull(context);
        Context.getBean(ConfigService.class).initAll();
        initListeners();
        PropConfigStore.inAdvanceLoad();
        serverStart();
        clearXmlLoadFuture();
        XmlManager.closeLoadExecutor();
        GameStartParams gameStartParams = NetHelper.getGameStartParams();
        webSocketServer = new WebSocketServer(gameStartParams);
        adminServer = new AdminServer(gameStartParams);
        crossServer =  new CrossServer(gameStartParams);
        webSocketServer.start(PropConfigStore.getPropConfig(ConfigPath.Independent.NET_PROPERTIES).getInt("PORT"));
        adminServer.start(PropConfigStore.getPropConfig(ConfigPath.Independent.NET_PROPERTIES).getInt("ADMIN_PORT"));
        crossServer.start(PropConfigStore.getPropConfig(ConfigPath.Independent.NET_PROPERTIES).getInt("CROSS_PORT"));

        LOGGER.info("-------- Server Started!--------版本号 version = " + VERSION + "----------");
        System.out.println(DateUtils.format(new Date()) + " Server started ! timelamp " + System.currentTimeMillis() + ",version=" + VERSION); //为了cmd打开看到启动提示
    }

    public void stop() {
        Context.getBean(ConfigService.class).setServerOpen(false);
        Context.getBean(OnlineService.class).closeAll();
        RunTimeUtils.sleep(5000);
        webSocketServer.close();
        adminServer.close();
        Context.getBean(TriggerTaskExecutor.class).shutdown(2, TimeUnit.SECONDS);
        Context.getBean(TimerService.class).stop();
        Context.getBean(CloseService.class).onServerClose();
        Context.getBean(AsyncDBService.class).stop();
        context.close();
        AsyncHttpClientUtils.stop();
        FileLoader.close();
        AsyncDailyFileAppender.stopWork();
        System.out.println("STOP Server");
    }

    public void destroy() {
        System.out.println("Destroy");
    }

    public void init() throws Exception {
        System.out.println("execute init method!");
    }

    public void init(String []args) throws Exception {
        System.out.println("execute init(args) method");
    }

    private void initListeners() {
        listenerManager.init(context);
    }

    private void serverStart() {
        List<ServerStarter> serverStarterList = new ArrayList<>(context.getBeansOfType(ServerStarter.class).values());
        Collections.sort(serverStarterList, new Comparator<ServerStarter>() {
            @Override
            public int compare(ServerStarter o1, ServerStarter o2) {
                return o1.getOrder() == o2.getOrder() ? 0 : o1.getOrder() > o2.getOrder() ? 1 : -1;
            }
        });
        for (ServerStarter serverStarter : serverStarterList) {
            try {
                UseTimer useTimer = new UseTimer(serverStarter + " init", 500);
                useTimer.start();
                serverStarter.init();
                useTimer.printUseTime();
            } catch (Exception e) {
                LOGGER.error("", e);
                throw e;
            }
        }
    }

    private void clearXmlLoadFuture() {
        for (XmlManager xmlManager : context.getBeansOfType(XmlManager.class).values()) {
            xmlManager.clearLoadFuture();
        }
    }

    private static void checkEnumUnique() {
        //检查枚举code的唯一性
        Map<Class<? extends Unique>, Set<Object>> map = Maps.newHashMap();
        Set<Class<? extends Unique>> uniqueClassSet = new Reflections("com.game.logic").getSubTypesOf(Unique.class);
        for (Class<? extends Unique> uniqueClz : uniqueClassSet) {
            if (!uniqueClz.isEnum()) {
                continue;
            }
            Unique[] uniques = uniqueClz.getEnumConstants();
            if (ArrayUtils.isEmpty(uniques)) {
                continue;
            }
            Set<Object> uniqueIds = Sets.newHashSetWithExpectedSize(uniques.length);
            Set<Object> repeatIds = Arrays.stream(uniques).map(Unique::uniqueID).filter(c -> !uniqueIds.add(c)).collect(Collectors.toSet());
            if (!repeatIds.isEmpty()) {
                map.put(uniqueClz, repeatIds);
            }
        }
        if (map.isEmpty()) {
            return;
        }
        String msg = map.entrySet().stream().map(e -> e.getKey() + " 存在重复ID: " + e.getValue()).collect(Collectors.joining("\n"));
        throw new RuntimeException(msg);
    }
}
