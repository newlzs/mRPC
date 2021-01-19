package rpc.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lzs
 * @date 2021/1/14 20:33
 * @description 单例工厂, 生成单例
 */
public class SingletonFactory {
    private static Map<Class, Object> objectMap = new HashMap<>();

    public static <T> T getInstance(Class<T> cla) {
        Object instance = objectMap.get(cla);
        synchronized (cla) {
            if(instance == null) {
                try {
                    instance = cla.getDeclaredConstructor().newInstance();
                    objectMap.put(cla, instance);
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return cla.cast(instance);
    }
}