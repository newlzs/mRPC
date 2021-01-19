package rpc.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;
import rpc.pojo.RPCRequest;

import java.util.List;

/**
 * @author Lzs
 * @date 2021/1/13 20:49
 * @description
 */
public interface LoadBalancer {
    Instance select(List<Instance> instances, RPCRequest request);
}