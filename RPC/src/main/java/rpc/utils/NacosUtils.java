package rpc.utils;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.config.NacosConfig;
import rpc.exception.RpcException;
import rpc.registry.ServiceProvider;
import rpc.registry.impl.ServiceProviderImpl;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Lzs
 * @date 2021/1/13 15:30
 * @description
 */
public class NacosUtils {
    private static final Logger logger = LoggerFactory.getLogger(NacosUtils.class);
    // 服务中心地址
    public static String NacosAddr = NacosConfig.NacosAddr;
    private static final NamingService namingService;
    // 从服务器开始只需连接一次，单例模式
    static {
        try {
            if(NacosAddr == null) {
                throw new IllegalArgumentException("注册中心地址未指定");
            }
            namingService = NamingFactory.createNamingService(NacosAddr);
        } catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException("连接Nacos失败！");
        }
    }

    /**
     * 注册到服务中心
     * @param serviceName
     * @param host
     * @param port
     */
    public static void register(String serviceName, String host, int port) {
        try {
            namingService.registerInstance(serviceName, host, port);
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生: ", e);
            throw new RpcException("注册服务时发生异常");
        }
    }

    /**
     * 获取包含服务的所有主机
     * @param serviceName
     * @return
     * @throws NacosException
     */
    public static List<Instance> getAllInstances(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    /**
     * 关闭服务时同时从服务中心删除
     * @param host 服务主机
     * @param port 服务主机端口
     */
    public static void clearRegistry(String host, int port) {
        Map<String, Object> serviceMap = ServiceProviderImpl.getInstance().getServiceMap();
        if(!serviceMap.isEmpty()) {
            Set<String> serviceNames = serviceMap.keySet();
            Iterator<String> iterator = serviceNames.iterator();
            while (iterator.hasNext()) {
                String serviceName = iterator.next();
                try {
                    namingService.deregisterInstance(serviceName, host, port);
                } catch (NacosException e) {
                    logger.error("注销服务 {} 失败", serviceName);
                }
            }
        }
    }
}