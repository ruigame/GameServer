package com.game.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: liguorui
 * @Date: 2020/12/2 上午12:06
 */
public class WebSocketServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServer.class);

    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;
    /**
     * 暴露出workerGroup为了跨服连接共用
     */
//    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ChannelFuture channelFuture;

    private GameStartParams gameStartParams;

    public WebSocketServer(GameStartParams gameStartParams) {
        this.gameStartParams = gameStartParams;
    }

    public void start(int port) throws Exception {
        try {
            if (Epoll.isAvailable()) {
                bossGroup = new EpollEventLoopGroup(1);
                workerGroup = new EpollEventLoopGroup();
            } else {
                bossGroup = new NioEventLoopGroup(1);
                workerGroup = new NioEventLoopGroup();
            }

            final GameServerHandler gameServerHandler = new GameServerHandler(gameStartParams);
            final ResponseEncoder responseEncoder = new ResponseEncoder(gameStartParams);
            final PacketDecoder packetDecoder = new PacketDecoder();
            final ByteToWebSocketFrameEncoder byteToWebSocketFrameEncoder = new ByteToWebSocketFrameEncoder();
            final WebSocketFrameToByteBufDecoder webSocketFrameToByteBufDecoder = new WebSocketFrameToByteBufDecoder();
            final ReqStatisHandler reqStatisHandler = new ReqStatisHandler();
            final RespStatisHandler respStatisHandler = new RespStatisHandler();
            final GameServerIdleHandler gameServerIdleHandler = new GameServerIdleHandler(null); //游戏端传入packet
            final SslAdapterHandler sslAdapterHandler = new SslAdapterHandler();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channelFactory(createChannelFactory())
//                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("sslAdapterHandler", sslAdapterHandler);
                            pipeline.addLast("IdleStateHandler", new IdleStateHandler(300, 0, 0));
                            pipeline.addLast("GameServerIdleHandler", gameServerIdleHandler);
                            pipeline.addLast("http-codec", new HttpServerCodec());
                            pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                            pipeline.addLast("websocket", new WebSocketServerProtocolHandler("/"));
                            pipeline.addLast("writeTimeoutHandler", new WriteTimeoutHandler(30));
                            pipeline.addLast(webSocketFrameToByteBufDecoder);
                            pipeline.addLast(new BaseFrameDecoder());
                            pipeline.addLast(reqStatisHandler);
                            pipeline.addLast(packetDecoder);
                            pipeline.addLast(gameServerHandler);
                            pipeline.addLast(byteToWebSocketFrameEncoder);
                            pipeline.addLast(responseEncoder);
                            pipeline.addLast(respStatisHandler);
                        }
                    });
            this.channelFuture = bootstrap.bind(port).sync();
            LOGGER.info("NetServer bind Port:{}", port);
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            throw e;
        }
    }

    public void close() {
        channelFuture.channel().close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    private ChannelFactory<ServerSocketChannel> createChannelFactory() {
        return () -> {
            if (Epoll.isAvailable()) {
                return new EpollServerSocketChannel();
            }
            return new NioServerSocketChannel();
        };
    }
}
