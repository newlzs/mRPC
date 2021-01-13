package rpc.registry.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.registry.ServiceProvider;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Lzs
 * @date 2021/1/5 23:05
 * @description
 */
public class ServiceProviderImpl implements ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);
    // 懒汉模式
    private static ConcurrentHashMap<String, Object> serviceMap;

    public ServiceProviderImpl() {
        if(serviceMap == null)
            serviceMap = new ConcurrentHashMap<String, Object>();
    }

    // service 实现接口, 一个接口可能有好多service
    @Override
    public synchronized <T> void register(T service, String interfaceName) {
        if(serviceMap.contains(interfaceName)) {
            logger.info("服务名 {} 已存在", interfaceName);
            return;
        }else {
            serviceMap.put(interfaceName, service);
            logger.info("向接口 {} 注册 {} 服务", interfaceName, service.getClass().getCanonicalName());
        }
    }

    @Override
    public synchronized Object getService(String interfaceName) {
        Object service = serviceMap.get(interfaceName);
        if(service == null){
            logger.warn("找不到对应的服务");
        }
        return service;
    }
}