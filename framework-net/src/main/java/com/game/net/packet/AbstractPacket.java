package com.game.net.packet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息报父类
 * @Author: liguorui
 * @Date: 2020/12/1 上午12:11
 */
public abstract class AbstractPacket {

    protected static Logger log = LoggerFactory.getLogger(AbstractPacket.class);

    /**
     * 协议号
     */
    protected short cmd;
    /**
     * 创建时间
     */
    protected long createTime = System.currentTimeMillis();

    /**
     * 备用参数（正常开发不要使用）
     */
    private Object[] backupParams;

    protected void beforeRead(Request request) {

    }

    public boolean isAdmin() {
        return false;
    }

    public boolean isCross() {
        return false;
    }


    public abstract void read(Request request) throws Exception;

    protected abstract void doResponse(Response response);

    protected boolean canWrite() {
        return true;
    }

    public Response write() {
        if (!canWrite()) {
            return null;
        }
        Response response = createResponse();
        if (!PacketFactory.tryEncode(this, response)) {
            doResponse(response);
        }
        return response;
    }

    protected Response createResponse() {
        return ResponeFactory.createResponse(getCmd());
    }

    @Override
    public String toString() {
        return "Packet [" + cmd + "," + this.getClass().getSimpleName() + "]";
    }

    public short getCmd() {
        return cmd;
    }

    public void setCmd(short cmd) {
        this.cmd = cmd;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Object[] getBackupParams() {
        return backupParams;
    }

    public void setBackupParams(Object[] backupParams) {
        this.backupParams = backupParams;
    }
}
