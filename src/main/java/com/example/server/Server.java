package com.example.server;

import com.example.server.codec.OrderFrameDecoder;
import com.example.server.codec.OrderFrameEncoder;
import com.example.server.codec.OrderProtocolDecoder;
import com.example.server.codec.OrderProtocolEncoder;
import com.example.server.handler.CountConnectionHandler;
import com.example.server.handler.OrderServerProcessHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

public class Server {
    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);

        //set max number of wait connector
        serverBootstrap.option(NioChannelOption.SO_BACKLOG, 1024);
        //start nagle
        serverBootstrap.childOption(NioChannelOption.TCP_NODELAY, true);

        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("boss"));
        NioEventLoopGroup workGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("worker"));

        try{
            serverBootstrap.group(bossGroup, workGroup);

            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {

                @Override
                protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                    ChannelPipeline pipeline = nioSocketChannel.pipeline();
                    pipeline.addLast("CountConnectionHandler", new CountConnectionHandler());
                    pipeline.addLast("OrderFrameDecoder",new OrderFrameDecoder());
                    pipeline.addLast("OrderFrameEncoder", new OrderFrameEncoder());
                    pipeline.addLast("OrderProtocolDecoder", new OrderProtocolDecoder());
                    pipeline.addLast("OrderProtocolEncoder", new OrderProtocolEncoder());
                    pipeline.addLast("LoggingHandler", new LoggingHandler(LogLevel.INFO));
                    pipeline.addLast("OrderServerProcessHandler", new OrderServerProcessHandler());
                }
            });

            ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }
}
