package com.example.client.handler;

import com.example.common.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RequestToResponseHandler extends SimpleChannelInboundHandler<ResponseMessage> {
    private RequestToResponse requestToResponse;

    public RequestToResponseHandler(RequestToResponse requestToResponse){
        this.requestToResponse = requestToResponse;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ResponseMessage responseMessage) throws Exception {
        requestToResponse.setRequestToResponseMap(responseMessage.getMessageHeader().getStreamId(),
                responseMessage.getMessageBody());
    }
}
