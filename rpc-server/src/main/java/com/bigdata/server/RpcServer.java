package com.bigdata.server;

import com.bigdata.common.RpcDecoder;
import com.bigdata.common.RpcEncoder;
import com.bigdata.common.RpcRequest;
import com.bigdata.common.RpcResponse;
import com.bigdata.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;


/**
 * 框架rpc服务器(用于将用户的业务系统的service发布为rpc服务)
 * 使用时可由用户通过spring-bean注入到用户业务系统中
 * 由于本类实现了ApplicationContextAware, InitializingBean
 * Spring构造本类时会调用setApplicationContext()方法,从而可以通过自定义注解注入
 * 还会调用afterPropertiesSet()方法,在方法中启动netty服务器
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {

//    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private String serverAddress;

    private ServiceRegistry serviceRegistry;

    // 用于存储业务接口和实现类的实例对象(由spring构造)
    private Map<String, Object> handlerMap = new HashMap<>();

    public RpcServer(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    // 绑定服务器的地址和端口,由spring构造时从配置文件中注入
    public RpcServer(String serverAddress, ServiceRegistry serviceRegistry) {
        this.serverAddress = serverAddress;
        // 向zookeeper中注册名称服务的工具类
        this.serviceRegistry = serviceRegistry;
    }

    public void setApplicationContext(ApplicationContext ctx) throws BeansException {

        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);

        if (MapUtils.isNotEmpty(serviceBeanMap)) {

            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();

                handlerMap.put(interfaceName, serviceBean);
            }
        }
    }

    /**
     * 在此启动netty服务,绑定handler流水线
     * 1.接收请求数据反序列化得到request对象
     * 2.根据request中的参数,让requestHandler从handlerMap中获取到对应业务的impl,调用指定方法获取返回值
     * 3.将业务调用结果封装到response对象并序列化返回给客户端
     * @throws Exception
     */
    public void afterPropertiesSet() throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel channel) throws Exception {

                            channel.pipeline()
                                    .addLast(new RpcDecoder(RpcRequest.class)) // 注册解码 IN-1
                                    .addLast(new RpcEncoder(RpcResponse.class)) // 注册编码 OUT-1
                                    .addLast(new RpcHandler(handlerMap)); // 注册RpcHandler IN-2

                        }
                    }).option(ChannelOption.SO_BACKLOG, 128).option(ChannelOption.SO_KEEPALIVE, true);

            String [] addressArr = serverAddress.split(":");
            String host = addressArr[0];
            int port = Integer.parseInt(addressArr[1]);

            ChannelFuture future = bootstrap.bind(host, port).sync();
//            LOGGER.debug("server start on port {}", port);

            /**
             * 向zookeeper注册服务
             */
            if (null != serviceRegistry) {
                serviceRegistry.register(serverAddress);
            }

            future.channel().closeFuture().sync();
        }
        finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();

        }

        // 用于启动netty服务
    }
}
