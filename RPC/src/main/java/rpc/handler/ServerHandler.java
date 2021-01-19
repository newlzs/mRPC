package rpc.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.pojo.RPCRequest;
import rpc.pojo.RPCResponse;
import rpc.registry.ServiceProvider;
import rpc.registry.impl.ServiceProviderImpl;

import java.lang.reflect.Method;

/**
 * @author Lzs
 * @date 2021/1/5 22:24
 * @description
 */
public class ServerHandler extends SimpleChannelInboundHandler<RPCRequest> {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    public static ServiceProvider serverRegister;

    static {
        serverRegister = ServiceProviderImpl.getInstance();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCRequest msg) throws Exception {
        try{
            logger.info("收到请求: " + msg);
            // 拿到服务
            String interfaceName = msg.getInterfaceName();
            Object service = serverRegister.getService(interfaceName);
            RPCResponse response;
            // 找不到服务
            if(service == null){
                response = RPCResponse.fail(null);
            }else {
                // 找到服务
                String methodName = msg.getMethodName();
                Object[] arguments = msg.getParameters();
                Class<?>[] paramTypes = msg.getParamTypes();
                // 拿到方法
                Method method = service.getClass().getMethod(methodName, paramTypes);
                // 得到结果
                Object result = method.invoke(service, arguments);
                // 封装
                response = RPCResponse.success(result);
            }
            response.setId(msg.getId());

            if(ctx.channel().isActive() && ctx.channel().isWritable()){
                ctx.writeAndFlush(response);
            }else {
                logger.error("通道不可写入");
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}