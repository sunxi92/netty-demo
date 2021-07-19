package com.example.client;

import com.example.client.codec.*;
import com.example.client.handler.OperationResultFuture;
import com.example.client.handler.RequestToResponse;
import com.example.client.handler.RequestToResponseHandler;
import com.example.common.OperationResult;
import com.example.common.RequestMessage;
import com.example.common.order.OrderOperation;
import com.example.util.IdUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.ExecutionException;

public class Client {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        //set the max value when connect server in client
        bootstrap.option(NioChannelOption.CONNECT_TIMEOUT_MILLIS, 3 * 1000);

        NioEventLoopGroup group = new NioEventLoopGroup();

        RequestToResponse requestToResponse = new RequestToResponse();

        try {
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                    ChannelPipeline channelPipeline = nioSocketChannel.pipeline();

                    channelPipeline.addLast("OrderFrameDecoder", new OrderFrameDecoder());
                    channelPipeline.addLast("OrderFrameEncoder", new OrderFrameEncoder());
                    channelPipeline.addLast("OrderProtocalDecoder", new OrderProtocalDecoder());
                    channelPipeline.addLast("OrderProtocolEncoder", new OrderProtocolEncoder());
                    channelPipeline.addLast("RequestToResponseHandler", new RequestToResponseHandler(requestToResponse));
                    channelPipeline.addLast("OperationToRequestMesageEncoder", new OperationToRequestMesageEncoder());
                    channelPipeline.addLast("LoggingHandler", new LoggingHandler(LogLevel.INFO));
                }
            });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090);
            channelFuture.sync();

            Long streamId = IdUtil.nextId();
            //OrderOperation orderOperation = new OrderOperation(0001, "dacan");
            RequestMessage requestMessage = new RequestMessage(
                    streamId, new OrderOperation(1001, "tudou"));
            OperationResultFuture operationResultFuture = new OperationResultFuture();
            requestToResponse.addRequestToResponseMap(streamId, operationResultFuture);

            channelFuture.channel().writeAndFlush(requestMessage);

            OperationResult operationResult = operationResultFuture.get();
            System.out.println("hello");
            System.out.println("operationResult: " +operationResult);

            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }

    }
}
