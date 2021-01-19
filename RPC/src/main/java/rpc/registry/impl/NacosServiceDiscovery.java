package rpc.registry.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.exception.RpcException;
import rpc.loadBalancer.LoadBalancer;
import rpc.loadBalancer.impl.FirstBalancer;
import rpc.pojo.RPCRequest;
import rpc.registry.ServiceDiscovery;
import rpc.utils.NacosUtils;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author Lzs
 * @date 2021/1/14 15:53
 * @description
 */
public class NacosServiceDiscovery implements ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    private LoadBalancer loadBalancer;

    public NacosServiceDiscovery() {
        loadBalancer = new FirstBalancer();
    }

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public InetSocketAddress lookupService(RPCRequest request) {
        try {
            List<Instance> instances = NacosUtils.getAllInstances(request.getInterfaceName());
            // 负载均衡的算法选择一个instance
            Instance instance = loadBalancer.select(instances, request);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时出错");
            throw new RpcException("获取服务时出错。");
        }
    }
}