package rpc.registry;

import java.net.InetSocketAddress;

/**
 * @author Lzs
 * @date 2021/1/9 13:31
 * @description 远程注册中心
 */
public interface ServiceRegistry {
    void register(String serviceName, InetSocketAddress inetSocketAddress);
}