package com.bigdata.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass) {

        this.genericClass = genericClass;
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() < 4) {
            return;
        }

        // 将byteBuff 转成byte[]
        int dataLength = in.readInt();
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        in.clear();

        // 将data转成object, 就是拿到客户端发过来的RpcRequest对象
        Object obj = SerializationUtil.deserialize(data, genericClass);

        // 将RpcRequest对象发往下一个inboundHandler去做业务调用
        out.add(obj);
    }
}
