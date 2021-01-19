package rpc.config;

/**
 * @author Lzs
 * @date 2021/1/19 15:59
 * @description 注册中心配置
 */
public class NacosConfig {
    public static String NacosAddr = ""; // 注册中心地址
    public static void initialize(String addr) {
        NacosAddr = addr;
    }
}