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
    private ConcurrentHashMap<String, Object> serviceMap;

    // 单例模式 懒汉
    private static ServiceProviderImpl instance;

    public static synchronized ServiceProviderImpl getInstance() {
        if(instance == null) {
            instance = new ServiceProviderImpl();
        }
        return instance;
    }

    public ServiceProviderImpl() {
         this.serviceMap = new ConcurrentHashMap<String, Object>();
    }
    // service 实现接口, 一个接口可能有好多service
    @Override
    public synchronized <T> void register(String interfaceName, T service) {
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

    public ConcurrentHashMap<String, Object> getServiceMap() {
        return this.serviceMap;
    }
}