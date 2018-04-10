package com.bigdata.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcEncoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass) {

        this.genericClass = genericClass;
    }

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

    }
}
