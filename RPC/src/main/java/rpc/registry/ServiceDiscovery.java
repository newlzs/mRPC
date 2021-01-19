package rpc.registry;

import rpc.pojo.RPCRequest;

import java.net.InetSocketAddress;

/**
 * @author Lzs
 * @date 2021/1/14 15:52
 * @description
 */
public interface ServiceDiscovery {
    InetSocketAddress lookupService(RPCRequest request);
}