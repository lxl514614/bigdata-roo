package com.bigdata.server;

import com.bigdata.registry.ServiceRegistry;
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

    public RpcServer (String serverAddress) {
        this.serverAddress = serverAddress;
    }

    // 绑定服务器的地址和端口,由spring构造时从配置文件中注入
    public RpcServer(String serverAddress, ServiceRegistry serviceRegistry) {
        this.serverAddress = serverAddress;
        // 向zookeeper中注册名称服务的工具类
        this.serviceRegistry = serviceRegistry;
    }

    public void afterPropertiesSet() throws Exception {

        // 用于启动netty服务
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

