package rpc.serialize.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.config.SerializerType;
import rpc.exception.SerializeException;
import rpc.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Lzs
 * @date 2021/1/9 11:11
 * @description
 */
public class HessianSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(HessianSerializer.class);
    @Override
    public byte[] serialize(Object obj) throws SerializeException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Hessian2Output output = new Hessian2Output(outputStream);
            output.writeObject(obj);
            output.getBytesOutputStream().flush();
            output.completeMessage();
            output.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.info("hessian2序列化出错");
            throw new SerializeException("Hessian2序列化出错");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> cla) throws SerializeException {
        try{
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            Hessian2Input input = new Hessian2Input(inputStream);
            return input.readObject();
        }catch (Exception e){
            logger.info("hessian2反序列化出错");
            throw new SerializeException("hessian2反序列化出错");
        }

    }

    @Override
    public int getCode() {
        return SerializerType.Hessian;
    }
}