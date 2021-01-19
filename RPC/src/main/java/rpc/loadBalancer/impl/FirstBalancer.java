package rpc.loadBalancer.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import rpc.loadBalancer.LoadBalancer;
import rpc.pojo.RPCRequest;

import java.util.List;

/**
 * @author Lzs
 * @date 2021/1/13 20:51
 * @description 首次负载均衡
 */
public class FirstBalancer implements LoadBalancer {
    @Override
    public Instance select(List<Instance> instances, RPCRequest request) {
        return instances.get(0);
    }
}