package rpc.serialize.impl;

import com.alibaba.fastjson.JSON;
import rpc.config.SerializerType;
import rpc.pojo.RPCRequest;
import rpc.serialize.Serializer;

/**
 * @author Lzs
 * @date 2021/1/6 9:41
 * @description
 */
public class JSONSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        String jsonObj = JSON.toJSONString(obj);
        return jsonObj.getBytes();
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> classType) {
        String jsonObj = new String(bytes);
        Object obj = JSON.parseObject(jsonObj, classType);
        /**
         * 由于请求中有Object数组的存在, 反序列化时根据字段类型进行反序列化,
         * 而Object本身比较模糊, 会出现反序列是失败的情况
         *  造成后续传入的参数强制类型转换失败 ClassCastException
         *  所以需要进一步操作
         */
        if(obj instanceof RPCRequest) {
            obj = deepDeSerializer(obj);
        }

        return obj;
    }

    public Object deepDeSerializer(Object obj) {
        RPCRequest rpcRequest = (RPCRequest) obj;
        for(int i = 0;i < rpcRequest.getParamTypes().length;i ++) {
            Class<?> classType = rpcRequest.getParamTypes()[i];
            // 如果类型对不上, 需要重新序列化
            if(!classType.isAssignableFrom(rpcRequest.getParameters()[i].getClass())) {
                // 先反序列化
                String jsonObj = JSON.toJSONString(rpcRequest.getParameters()[i]);
                // 重新序列化
                rpcRequest.getParameters()[i] = JSON.parseObject(jsonObj, classType);
            }
        }
        return rpcRequest;
    }
    @Override
    public int getCode() {
        return SerializerType.JSON;
    }
}