package com.leco.rpc.service.service;

import com.leco.rpc.core.serialization.ISerialization;
import com.leco.rpc.service.encoder.RpcDecoder;
import com.leco.rpc.service.encoder.RpcEncoder;
import com.leco.rpc.service.handler.DefaultRpcHandler;
import com.leco.rpc.service.util.ConfigProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * rpc服务器
 * @创建人 leco
 * @创建时间 2018/7/30
 */
public class RpcNettyServer implements IServer {


    /**
     * 启动rpc服务器
     */
    public void start() {
        System.out.println("----------- start rpc server -------------");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new DefaultChannelInitializer(ConfigProvider.requestEneity, ConfigProvider.responseEneity,ConfigProvider.serialization));
        bootstrap.option(ChannelOption.SO_BACKLOG, ConfigProvider.ChannelOption_SO_BACKLOG);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, ConfigProvider.ChannelOption_SO_KEEPALIVE);
        try {
            System.out.println("----------- bind "+ConfigProvider.host+" : "+ConfigProvider.port+"  -------------");
            ChannelFuture sync = bootstrap.bind(ConfigProvider.host, ConfigProvider.port).sync();
            System.out.println("----------- start rpc server finish -------------");
            System.out.println("----------- start monitor rpc request  -------------");
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }


    //管道初始化
    class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

        private Class<?> rpcDecoder;

        private Class<?> rpcEncoder;

        ISerialization serialization;
        public DefaultChannelInitializer(Class<?> rpcDecoder, Class<?> rpcEncoder, ISerialization serialization) {
            this.rpcDecoder = rpcDecoder;
            this.rpcEncoder = rpcEncoder;
            this.serialization=serialization;
        }

        //初始化管道
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();
            pipeline.addLast(new RpcDecoder(rpcDecoder,serialization));
            pipeline.addLast(new RpcEncoder(rpcEncoder,serialization));
            pipeline.addLast(new DefaultRpcHandler());
        }
    }
}
