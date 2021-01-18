package com.game.net.packet;

import com.game.util.ByteBufUtils;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * response实现类
 * @author liguorui
 * @date 2016-01-06
 *
 */
public class ByteBufResponse implements Response {

	private ByteBuf byteBuf;

	public static final int DEFAULT_MAX_CAPACITY = 1 * 1024 * 1024;
	
	ByteBufResponse(int packetId) {
		this(packetId, DEFAULT_MAX_CAPACITY);
	}

	ByteBufResponse(int packetId, int maxCapacity) {
		super();
		this.byteBuf = Unpooled.buffer(64, maxCapacity);
//		this.byteBuf = PooledByteBufAllocator.DEFAULT.heapBuffer(64); 好像会泄露
		this.byteBuf.writeShort(packetId);
	}

	public ByteBufResponse() {
		super();
		this.byteBuf = Unpooled.buffer(64);
	}

	public ByteBufResponse(ByteBuf byteBuf) {
		super();
		this.byteBuf = byteBuf;
	}
	
	@Override
	public ByteBuf getByteBuf() {
		return byteBuf;
	}
	
	public short getPacketId() {
		return byteBuf.getShort(0);
	}
	
	@Override
	public Response setPacketId(int packetId) {
		byteBuf.setShort(0, packetId);
		return this;
	}

	@Override
	public Response writeByte(int value) {
		Preconditions.checkArgument(value >= -128 && value <= 127, "value:%s", value);
		byteBuf.writeByte(value);
		return this;
	}

	@Override
	public Response writeUnsignedByte(int value) {
		Preconditions.checkArgument(value >= 0 && value <= 255, "value:%s", value);
		byteBuf.writeByte(value);
		return this;
	}

	@Override
	public Response writeShort(int value) {
		Preconditions.checkArgument(value >= Short.MIN_VALUE && value <= Short.MAX_VALUE, "value:%s", value);
		byteBuf.writeShort(value);
		return this;
	}

	@Override
	public Response writeUnsignedShort(int value) {
		Preconditions.checkArgument(value >= 0 && value <= Short.MAX_VALUE - Short.MIN_VALUE, "value:%s", value);
		byteBuf.writeShort(value);
		return this;
	}

	@Override
	public Response writeInt(int value) {
		byteBuf.writeInt(value);
		return this;
	}

	@Override
	public Response writeLong(long value) {
		byteBuf.writeLong(value);
		return this;
	}

	@Override
	public Response writeBytes(byte[] src) {
		byteBuf.writeBytes(src);
		return this;
	}

	@Override
	public Response writeBytes(byte[] src, int srcIndex, int length) {
		byteBuf.writeBytes(src, srcIndex, length);
		return this;
	}

	@Override
	public Response writeFloat(float value) {
		byteBuf.writeFloat(value);
		return this;
	}

	@Override
	public Response writeDouble(double value) {
		byteBuf.writeDouble(value);
		return this;
	}

	@Override
	public Response writeString(String value) {
		ByteBufUtils.writeString(byteBuf, value);
		return this;
	}

	@Override
	public int getIndex() {
		return byteBuf.writerIndex();
	}

	@Override
	public Response setByte(int index, int value) {
		byteBuf.setByte(index, value);
		return this;
	}

	@Override
	public Response setShort(int index, int value) {
		byteBuf.setShort(index, value);
		return this;
	}

	@Override
	public Response setInt(int index, int value) {
		byteBuf.setInt(index, value);
		return this;
	}

	@Override
	public Response setLong(int index, long value) {
		byteBuf.setLong(index, value);
		return this;
	}

	@Override
	public Response writeBoolean(boolean value) {
		byteBuf.writeByte(value ? 1 : 0);
		return this;
	}

	@Override
	public Response writeBytes(ByteBuf src) {
		byteBuf.writeBytes(src);
		return this;
	}

	@Override
	public Response writeLongString(String value) {
		ByteBufUtils.writeLongString(byteBuf, value);
		return this;
	}

	@Override
	public Response writeValueByType(long value) {
		return this;
	}

	@Override
	public Response writePositiveInt(int value) {
		return this;
	}

	@Override
	public Response writePositiveInt(long value) {
		return this;
	}

	@Override
	public Response writePositiveLong(long value) {
		return this;
	}
}
