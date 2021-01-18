package com.game.net.packet;

/**
 * 客户端请求的包
 * 
 * @author liguorui
 * @date 2016-01-06
 */
public abstract class RequestPacket extends AbstractPacket {

	@Override
	public void read(Request request) throws Exception {

	}

	@Override
	protected void doResponse(Response response) {

	}
}
