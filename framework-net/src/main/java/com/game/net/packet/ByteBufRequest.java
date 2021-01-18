package com.game.net.packet;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;

import java.nio.charset.Charset;

/**
 * Request实现类，封装byteBuf
 * @author liguorui
 * @date 2016-01-06
 *
 */
public class ByteBufRequest implements ReferenceCounted,Request {

	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	
	private ByteBuf byteBuf;
	private short packetId;
	private int byteSize;
	private boolean shell;
	
	public ByteBufRequest(ByteBuf byteBuf) {
		super();
		this.byteBuf = byteBuf;
		this.packetId = byteBuf.readShort();
		this.byteSize = byteBuf.readableBytes();
	}

	public ByteBufRequest(ByteBuf byteBuf, boolean shell) {
		super();
		this.byteBuf = byteBuf;
		this.packetId = byteBuf.readShort();
		this.byteSize = byteBuf.readableBytes();
		this.shell = shell;
	}

	/**
	 * 是否壳请求
	 * @return
	 */
	@Override
	public boolean isShell() {
		return shell;
	}
	
	@Override
	public short getPacketId() {
		return packetId;
	}
	
	@Override
	public ByteBuf getByteBuf() {
		return byteBuf;
	}

	@Override
	public int getByteSize() {
		return byteSize;
	}

	@Override
	public byte readByte() {
		return byteBuf.readByte();
	}

	@Override
	public short readUnsignedByte() {
		return byteBuf.readUnsignedByte();
	}

	@Override
	public short readShort() {
		return byteBuf.readShort();
	}

	@Override
	public int readUnsignedShort() {
		return byteBuf.readUnsignedShort();
	}

	@Override
	public int readInt() {
		return byteBuf.readInt();
	}

	@Override
	public long readUnsignedInt() {
		return byteBuf.readUnsignedInt();
	}

	@Override
	public long readLong() {
		return byteBuf.readLong();
	}

	@Override
	public double readDouble() {
		return byteBuf.readDouble();
	}
	
	@Override
	public String readString() {
		short length = byteBuf.readShort();
		byte[] content = new byte[length];
		byteBuf.readBytes(content);
		return new String(content , UTF8_CHARSET);
	}

	@Override
	public boolean readBoolean() {
		byte read = byteBuf.readByte();
		return read == 1;
	}

	@Override
	public float readFloat() {
		return byteBuf.readFloat();
	}

	@Override
	public void readBytes(byte[] dst) {
		byteBuf.readBytes(dst);
	}

	@Override
	public int refCnt() {
		return byteBuf.refCnt();
	}

	@Override
	public ReferenceCounted retain() {
		return byteBuf.retain();
	}

	@Override
	public ReferenceCounted retain(int increment) {
		return byteBuf.retain(increment);
	}

	@Override
	public ReferenceCounted touch() {
		return this;
	}

	@Override
	public ReferenceCounted touch(Object o) {
		return this;
	}

	@Override
	public boolean release() {
		return byteBuf.release();
	}

	@Override
	public boolean release(int decrement) {
		return byteBuf.release(decrement);
	}
}
