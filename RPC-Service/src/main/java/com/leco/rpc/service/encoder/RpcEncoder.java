package com.leco.rpc.service.encoder;

import com.leco.rpc.core.serialization.ISerialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;


/**
 *编码
 * @创建人 leco
 * @创建时间 2018/7/30
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> entity;

    private ISerialization serialization;

    public RpcEncoder(Class<?> entity, ISerialization serialization) {
        this.entity = entity;
        this.serialization = serialization;
    }

    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (entity.isInstance(o)){
            byte[] serialize = serialization.serialize(o);
            byteBuf.writeInt(serialize.length);
            byteBuf.writeBytes(serialize);
        }
    }
}
