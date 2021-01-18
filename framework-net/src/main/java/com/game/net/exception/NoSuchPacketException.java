package com.game.net.exception;

/**
 * 
 * @author liguorui
 * @date 2016-01-06
 */
public class NoSuchPacketException extends NullPointerException {

	private static final long	serialVersionUID	= 356084109841403035L;

	/**
	 * @param packetId
	 */
	public NoSuchPacketException(int packetId) {
		super("Can not found packet which id is `" + packetId + "`, check if you have add a annotation `Packet` to the `" + packetId + "` packet.");
	}

	public NoSuchPacketException(Class<?> clazz) {
		super("Can not found associate commandId for packet:"+clazz.getName());
	}
}