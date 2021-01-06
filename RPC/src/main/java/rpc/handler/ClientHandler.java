package rpc.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import rpc.pojo.RPCResponse;

/**
 * @author Lzs
 * @date 2021/1/6 10:57
 * @description
 */
public class ClientHandler extends SimpleChannelInboundHandler<RPCResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCResponse msg) throws Exception {
        try{
            AttributeKey<RPCResponse> key = AttributeKey.valueOf("rpcResponse");
            ctx.channel().attr(key).set(msg);
            // 关闭通道
            ctx.channel().close();
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
}