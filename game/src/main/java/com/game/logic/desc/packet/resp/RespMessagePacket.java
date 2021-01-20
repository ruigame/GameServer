package com.game.logic.desc.packet.resp;

import com.game.PacketId;
import com.game.logic.desc.domain.MessageType;
import com.game.logic.desc.domain.MsgId;
import com.game.net.packet.Packet;
import com.game.net.packet.Response;
import com.game.net.packet.ResponsePacket;

/**
 * 信息包
 * @Author: liguorui
 * @Date: 2021/1/20 下午9:11
 */
@Packet(commandId = PacketId.Utils.RESP_MESSAGE)
public class RespMessagePacket extends ResponsePacket {

    private MessageType messageType;
    private int messageId;
    private Object[] args;

    public void init(MessageType messageType, int messageId, Object[] args) {
        init(messageType, messageId);
        this.args = args;
    }

    public void init(MessageType messageType, MsgId msgId, Object[] args) {
        init(messageType, msgId.getId());
        this.args = args;
    }

    public void init(MessageType messageType, int messageId) {
        this.messageType = messageType;
        this.messageId = messageId;
    }

    @Override
    protected void doResponse(Response response) {
        response.writeByte(messageType.getCode());
        response.writeInt(messageId);
        if (args == null) {
            response.writeByte(0);
        } else {
            response.writeByte(args.length);
            for (Object arg : args) {
                response.writeString(String.valueOf(arg));
            }
        }
    }

    public int getMessageId() {
        return messageId;
    }
}
