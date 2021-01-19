package rpc.registry;

/**
 * @author Lzs
 * @date 2021/1/5 22:41
 * @description 本地注册表
 */
public interface ServiceProvider {
    <T> void register(String name, T service);
    public Object  getService(String name);
}