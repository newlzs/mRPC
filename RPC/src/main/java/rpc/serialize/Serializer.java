package rpc.serialize;

import rpc.serialize.impl.JSONSerializer;

/**
 * @author Lzs
 * @date 2021/1/5 21:19
 * @description
 */
public interface Serializer {
    // 序列化
    byte[] serialize(Object obj);
    // 反序列化
    Object deserialize(byte[] bytes, Class<?> cla);
    // 获取指定序列化方式的代码
    int getCode();

    // 获取序列化对象
    static Serializer getByCode(int code){
        switch (code){
            case 0:
                return new JSONSerializer();
            default:
                return null;
        }
    }
}
