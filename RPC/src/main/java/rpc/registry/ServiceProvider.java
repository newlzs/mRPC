package rpc.registry;

/**
 * @author Lzs
 * @date 2021/1/5 22:41
 * @description 本地注册表
 */
public interface ServiceProvider {
    <T> void register(T service, String name);
    public Object  getService(String name);
}