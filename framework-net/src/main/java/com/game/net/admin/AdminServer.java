package com.game.net.admin;

import com.game.net.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 后台服务类
 * @Author: liguorui
 * @Date: 2020/12/14 下午8:54
 */
public class AdminServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServer.class);

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(1);
    private ChannelFuture channelFuture;
    private GameStartParams gameStartParams;

    public AdminServer(GameStartParams gameStartParams) {
        this.gameStartParams = gameStartParams;
    }

    public void start(int port) throws Exception {
        try {
            final AdminPacketHandler adminPacketHandler = new AdminPacketHandler(gameStartParams);
            final ResponseEncoder responseEncoder = new ResponseEncoder(gameStartParams);
            final PacketDecoder packetDecoder = new PacketDecoder();
            final ReqStatisHandler reqStatisHandler = new ReqStatisHandler();
            final RespStatisHandler respStatisHandler = new RespStatisHandler();
            final GameServerIdleHandler gameServerIdleHandler = new GameServerIdleHandler(gameStartParams);
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("IdleStateHandler", new IdleStateHandler(60, 0, 0));
                            pipeline.addLast("GameServerIdleHandler", gameServerIdleHandler);
                            pipeline.addLast(new BaseFrameDecoder());
                            pipeline.addLast(reqStatisHandler);
                            pipeline.addLast(packetDecoder);
                            pipeline.addLast(adminPacketHandler);
                            pipeline.addLast(responseEncoder);
                            pipeline.addLast(respStatisHandler);
                        }
                    });
            this.channelFuture = bootstrap.bind(port).sync();
            LOGGER.info("AdminServer Bind Port : {}", port);
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
}
