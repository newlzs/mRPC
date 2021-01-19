package rpc.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.factory.SingletonFactory;
import rpc.pojo.RPCResponse;
import rpc.transport.UnprocessedRequests;

/**
 * @author Lzs
 * @date 2021/1/6 10:57
 * @description
 */
public class ClientHandler extends SimpleChannelInboundHandler<RPCResponse> {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private final UnprocessedRequests unprocessedRequests;

    public ClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCResponse msg) throws Exception {
        try{
            logger.info("客户端收到消息: {}", msg.toString());
            unprocessedRequests.complete(msg);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
}