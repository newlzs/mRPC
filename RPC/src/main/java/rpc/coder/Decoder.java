package rpc.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.config.PackageType;
import rpc.pojo.RPCRequest;
import rpc.pojo.RPCResponse;
import rpc.serialize.Serializer;

import java.util.List;

/**
 * @author Lzs
 * @date 2021/1/5 21:53
 * @description
 */
public class Decoder extends ReplayingDecoder {
    private static final Logger logger = LoggerFactory.getLogger(Decoder.class);

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int packageTypeCode = in.readInt();
        Class<?> packageClass;
        if(packageTypeCode == PackageType.REQUEST_CODE){
            packageClass = RPCRequest.class;
        }else if(packageTypeCode == PackageType.RESPONSE_CODE){
            packageClass = RPCResponse.class;
        }else {
            logger.error("包类型错误: {}", packageTypeCode);
            throw new IllegalArgumentException("包类型错误");
        }

        int serializerCode = in.readInt();
        Serializer serializer = Serializer.getByCode(serializerCode);
        if(serializer == null) {
            logger.error("序列化类型错误: {}", serializerCode);
            throw new IllegalArgumentException("serializerCode 找不到");
        }

        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object obj = serializer.deserialize(bytes, packageClass);
        out.add(obj);
    }
}