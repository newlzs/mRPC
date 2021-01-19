package rpc.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.utils.NacosUtils;

import java.net.InetSocketAddress;

/**
 * @author Lzs
 * @date 2021/1/13 16:35
 * @description
 */
public class ShutDownHook {
    private static final Logger logger = LoggerFactory.getLogger(ShutDownHook.class);

    public static void clearAllServiceHook(InetSocketAddress inetSocketAddress) {
        logger.info("关闭后注销其所在注册中心的所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                NacosUtils.clearRegistry(inetSocketAddress.getHostName(), inetSocketAddress.getPort());
            }
        }));
    }
}