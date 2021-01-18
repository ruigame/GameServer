package com.game.net.packet;

import io.netty.buffer.ByteBuf;

public interface Response {

	Response setPacketId(int packetId) ;
	
	Response writeByte(int value);

	Response writeUnsignedByte(int value);
	
	Response writeShort(int value);

	Response writeUnsignedShort(int value);
	
	Response writeInt(int value);
	
	Response writeLong(long value);
	
	Response writeBytes(byte[] src);
	
	Response writeBytes(byte[] src, int srcIndex, int length);
	
	Response writeFloat(float value);
	
	Response writeDouble(double value);

	/**
	 * 写字符串，字符串长度是short，最大32767字节
	 * @param value
	 * @return
	 */
	Response writeString(String value);

	/**
	 * 写字符串，字符串长度是int
	 * @param value
	 * @return
	 */
	Response writeLongString(String value);
	
	int getIndex();
	
	Response setByte(int index, int value);

	Response setShort(int index, int value);

	Response setInt(int index, int value);

	Response setLong(int index, long value);

	Response writeValueByType(long value);
	
	/**
	 * 此方法只能写正数
	 * @param value
	 * @return
	 */
	Response writePositiveInt(int value);
	
	Response writePositiveInt(long value);
	
	Response writePositiveLong(long value);
	
	ByteBuf getByteBuf();

	Response writeBoolean(boolean value);

	Response writeBytes(ByteBuf src);
}
