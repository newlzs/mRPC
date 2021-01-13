package rpc.serialize;

import rpc.config.SerializerType;
import rpc.exception.SerializeException;
import rpc.serialize.impl.HessianSerializer;
import rpc.serialize.impl.JSONSerializer;
import rpc.serialize.impl.KryoSerializer;

import java.io.IOException;

/**
 * @author Lzs
 * @date 2021/1/5 21:19
 * @description
 */
public interface Serializer {
    // 序列化
    byte[] serialize(Object obj) throws SerializeException, IOException;
    // 反序列化
    Object deserialize(byte[] bytes, Class<?> cla) throws SerializeException;
    // 获取指定序列化方式的代码
    int getCode();

    // 获取序列化对象
    static Serializer getByCode(int code){
        switch (code){
            case SerializerType.JSON:
                return new JSONSerializer();
            case SerializerType.Kryo:
                return new KryoSerializer();
            case SerializerType.Hessian:
                return new HessianSerializer();
            default:
                return null;
        }
    }
}
