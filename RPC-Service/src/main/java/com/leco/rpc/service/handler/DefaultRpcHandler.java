package com.leco.rpc.service.handler;

import com.leco.rpc.core.entity.RequestSerialize;
import com.leco.rpc.core.entity.ResponseSerialize;
import com.leco.rpc.service.util.God;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * //RPC请求处理器
 * @创建人 leco
 * @创建时间 2018/7/30
 */
public class DefaultRpcHandler extends SimpleChannelInboundHandler<RequestSerialize> {
    private God god = God.getInstance();
    protected void channelRead0(ChannelHandlerContext ctx
            , RequestSerialize requestSerialize) {
        ResponseSerialize response = new ResponseSerialize();
        if ("".equals(requestSerialize.getRequestId())){
            response.setError(new NullPointerException("requestid null pointer"));
        }

        response.setRequestId(requestSerialize.getRequestId());
        try {
            Object o = god.handle(requestSerialize);
            response.setResult(o);
        } catch (Exception e) {
            e.printStackTrace();
            response.setError(e);
        }
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
