package rpc.loadBalancer.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import rpc.loadBalancer.LoadBalancer;
import rpc.pojo.RPCRequest;

import java.util.List;

/**
 * @author Lzs
 * @date 2021/1/13 20:55
 * @description 循环负载均衡
 */
public class CycleBalancer implements LoadBalancer {
    private int index = 0;
    @Override
    public Instance select(List<Instance> instances, RPCRequest request) {
        if(index >= instances.size()) {
            index %= instances.size();
        }
        return instances.get(index ++);
    }
}