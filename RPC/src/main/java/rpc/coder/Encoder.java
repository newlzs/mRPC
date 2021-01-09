package rpc.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.config.PackageType;
import rpc.pojo.RPCResponse;
import rpc.serialize.Serializer;

/**
 * @author Lzs
 * @date 2021/1/5 21:14
 * @description 编码
 * https://www.cnblogs.com/qdhxhz/p/10245936.html
 */
public class Encoder extends MessageToByteEncoder {
    private static final Logger logger = LoggerFactory.getLogger(Encoder.class);

    private Serializer serializer;

    public Encoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // 序列化的时候不需要类型
        // 写入msg对应的类型
        if(msg instanceof RPCResponse) {
            out.writeInt(PackageType.RESPONSE_CODE);
        }else {
            out.writeInt(PackageType.REQUEST_CODE);
        }
        // 写入序列化的方式, 以便反序列化
        out.writeInt(serializer.getCode());
        // 序列化长度
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}