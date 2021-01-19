package rpc.registry.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.exception.RpcException;
import rpc.loadBalancer.LoadBalancer;
import rpc.loadBalancer.impl.FirstBalancer;
import rpc.registry.ServiceRegistry;
import rpc.utils.NacosUtils;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author Lzs
 * @date 2021/1/9 13:31
 * @description
 */
public class NacosServiceRegistry implements ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        NacosUtils.register(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        logger.info("注册服务 {} 成功, 服务地址 {}:{}", serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
    }
}