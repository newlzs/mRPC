package rpc.loadBalancer.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import rpc.loadBalancer.LoadBalancer;
import rpc.pojo.RPCRequest;

import java.util.List;
import java.util.Random;

/**
 * @author Lzs
 * @date 2021/1/13 20:52
 * @description 随机负载均衡
 */
public class RandomBalancer implements LoadBalancer {

    @Override
    public Instance select(List<Instance> instances, RPCRequest request) {
        return instances.get(new Random().nextInt(instances.size()));
    }
}