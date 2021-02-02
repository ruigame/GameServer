package com.game.logic.client.tcp;

import com.game.PacketId;
import com.game.file.ConfigPath;
import com.game.file.prop.PropConfig;
import com.game.file.prop.PropConfigStore;
import com.game.logic.client.tcp.net.TcpClientDecoder;
import com.game.logic.client.tcp.net.TcpClientEncoder;
import com.game.logic.client.tcp.net.TcpClientHandler;
import com.game.logic.client.tcp.net.TcpClientPacketFactory;
import com.game.logic.player.packet.req.ReqLoginAuthPacket;
import com.game.logic.player.packet.req.ReqPlayerCreatePacket;
import com.game.util.LoginAuthParam;
import com.game.net.packet.AbstractPacket;
import com.game.net.packet.ResponeFactory;
import com.game.net.packet.Response;
import com.game.net.packet.ResponsePacket;
import com.game.util.TimeUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @Author: liguorui
 * @Date: 2021/1/28 下午9:46
 */
public class TcpClient {

    private NioEventLoopGroup bossGroup;
    private Channel channel;
    private TcpClientHandler clientHandler;

    public void login(int serverId, String account) {
        LoginAuthParam loginAuthParam = new LoginAuthParam();
        loginAuthParam.setPlatform("37wan");
        loginAuthParam.setAccount(account);
        loginAuthParam.setGid("1");
        loginAuthParam.setPid("1");
        loginAuthParam.setServer("1服");
        loginAuthParam.setServerId(serverId);
        loginAuthParam.setTime(TimeUtils.timestamp());
        loginAuthParam.setSign("1");
        loginAuthParam.setToken("324214");
        loginAuthParam.setParam("pass=2");

        ReqLoginAuthPacket packet = TcpClientPacketFactory.createPacket(PacketId.Base.REQ_LOGIN_AUTH);
        packet.setLoginAuthParam(loginAuthParam);
        sendReq(packet);
    }

    public void createPlayer(String name) {
        ReqPlayerCreatePacket packet = TcpClientPacketFactory.createPacket(PacketId.Base.REQ_PLAYER_CREATE);
        packet.setNickName(name);
        packet.setGender((byte)1);
        packet.setRoleType((byte)1);
        sendReq(packet);
    }

    public void exeGm() {

    }

    public void sendReq(AbstractPacket request) {
        if (Objects.isNull(channel)) {
            return;
        }
        Response response = ResponeFactory.createResponse(request.getCmd());
        TcpClientPacketFactory.tryEncode(request, response);
        channel.writeAndFlush(response);
    }

    public void start() throws Exception {
        bossGroup = new NioEventLoopGroup(1);
        clientHandler = new TcpClientHandler();
        PropConfig propConfig = PropConfigStore.getPropConfig(ConfigPath.Independent.NET_PROPERTIES);
        int port = propConfig.getInt("PORT");
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast("decoder", new TcpClientDecoder());
                        channel.pipeline().addLast("encoder", new TcpClientEncoder());
                        channel.pipeline().addLast("handler", clientHandler);
                    }
                });
        channel = bootstrap.connect("127.0.0.1", port).sync().channel();
    }

    public void addListener(Consumer<ResponsePacket> consumer) {
        clientHandler.addConsumer(consumer);
    }

    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
    }

    public Channel getChannel() {
        return channel;
    }
}
