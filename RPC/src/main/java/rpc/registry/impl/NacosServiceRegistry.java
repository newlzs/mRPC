package rpc.registry.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.exception.RpcException;
import rpc.registry.ServiceRegistry;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author Lzs
 * @date 2021/1/9 13:31
 * @description
 */
public class NacosServiceRegistry implements ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);
    // 服务中心地址
    private static final String NacosAddr = "127.0.0.1:8848";
    private static final NamingService namingService;
    // 从服务器开始只需连接一次，单例模式
    static {
        try {
            namingService = NamingFactory.createNamingService(NacosAddr);
        } catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException("连接Nacos失败！");
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            namingService.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生: ", e);
            throw new RpcException("注册服务时发生异常");
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            // 负载均衡的算法选择一个instance
            Instance instance = instances.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时出错");
            throw new RpcException("获取服务时出错。");
        }
    }
}