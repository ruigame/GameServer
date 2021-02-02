package com.game.logic.client.tcp;

import com.game.PacketId;
import com.game.file.FileLoader;
import com.game.log.logback.AsyncDailyFileAppender;
import com.game.logic.client.tcp.net.TcpClientPacketFactory;
import com.game.logic.player.packet.resp.RespLoginAuthPacket;
import com.game.logic.player.packet.resp.RespRandomnamePacket;
import com.game.net.packet.ResponsePacket;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @Author: liguorui
 * @Date: 2021/1/28 下午9:47
 */
public class TcpClientApp {

    private static TcpClient tcpClient;

    public static void main(String[]args) throws Exception{
        tcpClient = new TcpClient();
        try {
            tcpClient.start();
            tcpClient.login(1, "a1");

            tcpClient.addListener(TcpClientApp:: onReceivePacket);
        } catch (Exception e) {
            e.printStackTrace();
            stop();
            return;
        }
        boolean running = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(running) {
            if (System.in.available() > 0) {
                if ("stop".equalsIgnoreCase(reader.readLine())) {
                    running = false;
                }
            } else {
                Thread.sleep(5000);
            }
        }
        stop();
    }

    private static void onReceivePacket(ResponsePacket packet) {
        short packetId = packet.getCmd();
        if (packetId == PacketId.Base.RESP_LOGIN_AUTH) { //登陆验证成功，请求登陆
            RespLoginAuthPacket loginAuthPacket = (RespLoginAuthPacket)packet;
            if (loginAuthPacket.isHashPlayer()) {
                tcpClient.sendReq(TcpClientPacketFactory.createPacket(PacketId.Base.REQ_LOGIN_ASK));
            } else {
                tcpClient.sendReq(TcpClientPacketFactory.createPacket(PacketId.Base.REQ_NAME_RANDOM));
            }
        } else if (packetId == PacketId.Base.RESP_NAME_RANDOM) {
            RespRandomnamePacket respRandomnamePacket = (RespRandomnamePacket)packet;
            tcpClient.createPlayer(respRandomnamePacket.getName());
        } else if (packetId == PacketId.Base.RESP_CREATE_PLAYER) {
            tcpClient.sendReq(TcpClientPacketFactory.createPacket(PacketId.Base.REQ_LOGIN_ASK));
        } else if (packetId == PacketId.Base.RESP_MODULE_INIT_END) {
            tcpClient.exeGm();
        } else {
            System.err.println("收到协议：" + packet.toString());
        }
    }

    private static void stop() {
        tcpClient.stop();
        FileLoader.close();
        AsyncDailyFileAppender.stopWork();
    }
}
