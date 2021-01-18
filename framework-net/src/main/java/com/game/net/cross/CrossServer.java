package com.game.net.cross;

import com.game.net.CrossBaseFrameDecoder;
import com.game.net.GameStartParams;
import com.game.net.RespStatisHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: liguorui
 * @Date: 2020/12/14 下午9:36
 */
public class CrossServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrossServer.class);

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(1);

    private ChannelFuture channelFuture;

    private GameStartParams gameStartParams;

    public CrossServer(GameStartParams gameStartParams) {
        this.gameStartParams = gameStartParams;
    }

    public void start(int port) throws Exception {
        try {
            final CrossPacketHandler crossPacketHandler = new CrossPacketHandler(gameStartParams);
            final CrossResponseEncoder crossResponseEncoder = new CrossResponseEncoder(gameStartParams.getCrossResponseEncoderExcludeIds());
            final CrossPacketDecoder packetDecoder = new CrossPacketDecoder();
            final RespStatisHandler respStatisHandler = new RespStatisHandler();
            final CrossPingRespHandler pingRespHandler = new CrossPingRespHandler(gameStartParams);
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
                            pipeline.addLast(new CrossServerCheckerHandler(gameStartParams.getNetServerBuild()));
                            pipeline.addLast(new CrossBaseFrameDecoder());
                            pipeline.addLast(packetDecoder);
                            pipeline.addLast(pingRespHandler);
                            pipeline.addLast(crossPacketHandler);
                            pipeline.addLast(crossResponseEncoder);
                            pipeline.addLast(respStatisHandler);
                        }
                    });
            this.channelFuture = bootstrap.bind(port).sync();
            LOGGER.info("CrossServer Bind Port {}", port);
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
