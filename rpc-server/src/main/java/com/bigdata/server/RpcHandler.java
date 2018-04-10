package com.bigdata.server;

import com.bigdata.common.RpcRequest;
import com.bigdata.common.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 将业务方法返回值封装成RpcResponse对象写入到下一个handler(即编码handler--RpcEncoder)
 */
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final Map<String, Object> handlerMap;

    public RpcHandler(Map<String, Object> handlerMap) {

        this.handlerMap = handlerMap;
    }

    /**
     * 接收消息, 处理消息, 返回结果
     * @param ctx
     * @param request
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {

        RpcResponse response = new RpcResponse();

        response.setRequestId(request.getRequestId());

        try {

            Object result = handle(request);

            response.setResult(result);
        }
        catch (Throwable t) {

            response.setError(t);
        }

        // 写入下一个handler(即RpcEncoder)进行下一步处理(编码)后发送给channel给客户端
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }

    /**
     * 根据request来处理具体业务调用
     * 调用是通过反射的方式来完成
     *
     * @param request
     * @return
     */
    private Object handle(RpcRequest request) throws Throwable {

        String className = request.getClassName();

        // 拿到实例对象
        Object serviceBean = handlerMap.get(className);

        // 拿到要调用的方法名, 方法类型, 方法参数
        String methodName = request.getMethodName();
        Class<?>[] mehtodTypes = request.getParamTypes();
        Object[] parameters = request.getParameters();

        // 拿到业务类的class对象
        Class<?> forName = Class.forName(className);

        // 调用实例对象指定的方法并返回结果
        Method method = forName.getMethod(methodName);

        return method.invoke(mehtodTypes,parameters);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

//        LOGGER.error("server caught exception {}", cause);
        ctx.close();
    }
}
