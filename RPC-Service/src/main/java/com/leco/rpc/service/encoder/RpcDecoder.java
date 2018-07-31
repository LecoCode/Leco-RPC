package com.leco.rpc.service.encoder;

import com.leco.rpc.core.serialization.ISerialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;

/**
 * 解码
 * @创建人 leco
 * @创建时间 2018/7/30
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> entity;

    private ISerialization serialization;

    public RpcDecoder(Class<?> entity,ISerialization serialization) {
        this.entity = entity;
        this.serialization=serialization;
    }

    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf byteBuf, List<Object> out) throws Exception {
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);
        Object dserialize = serialization.dserialize(data, entity);
        if (null == dserialize){
            return;
        }
        out.add(dserialize);
    }
}
