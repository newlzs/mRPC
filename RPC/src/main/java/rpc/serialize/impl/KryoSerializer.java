package rpc.serialize.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.config.SerializerType;
import rpc.exception.exactException.SerializeException;
import rpc.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Lzs
 * @date 2021/1/7 12:22
 * @description kryo序列化
 * https://www.cnblogs.com/hntyzgn/p/7122709.html
 */
public class KryoSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    // 线程不安全,
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) throws SerializeException {
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream);
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        }catch (Exception e){
            logger.error("序列化时有错误发生: ", e);
            throw new SerializeException("序列化错误");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> cla) throws SerializeException {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream);
            Kryo kryo = kryoThreadLocal.get();
            Object obj = kryo.readObject(input, cla);
            kryoThreadLocal.remove();
            return obj;
        }catch (Exception e) {
            logger.error("反序列化错误: ", e);
            throw new SerializeException("反序列化错误");
        }
    }

    @Override
    public int getCode() {
        return SerializerType.Kryo;
    }
}