package com.game.net.packet;

/**
 * 返回客户端的包
 * @Author: liguorui
 * @Date: 2020/12/4 下午1:19
 */
public abstract class ResponsePacket extends AbstractPacket {

    @Override
    public void read(Request request) throws Exception {

    }

    @Override
    protected void doResponse(Response response) {

    }
}
