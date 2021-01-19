package rpc.transport.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.factory.SingletonFactory;
import rpc.loadBalancer.LoadBalancer;
import rpc.loadBalancer.impl.FirstBalancer;
import rpc.pojo.RPCRequest;
import rpc.pojo.RPCResponse;
import rpc.registry.ServiceDiscovery;
import rpc.registry.impl.NacosServiceDiscovery;
import rpc.serialize.Serializer;
import rpc.serialize.impl.HessianSerializer;
import rpc.transport.UnprocessedRequests;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


/**
 * @author Lzs
 * @date 2021/1/6 10:46
 * @description
 */
public class ClientRequest {
    private static final Logger logger = LoggerFactory.getLogger(ClientRequest.class);
    private String host;
    private int port;
    private final ServiceDiscovery serviceDiscovery;
    private final Serializer serializer;
    private final LoadBalancer loadBalancer;
    private final UnprocessedRequests unprocessedRequests;

    public ClientRequest() {
        this(new FirstBalancer(), new HessianSerializer());
    }
    public ClientRequest(LoadBalancer loadBalancer, Serializer serializer) {
        this.serializer = serializer;
        this.loadBalancer = loadBalancer;
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    public RPCResponse sendRequest(RPCRequest rpcRequest) throws InterruptedException, ExecutionException {
        CompletableFuture<RPCResponse> resultFuture = new CompletableFuture<>();
        try{
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            unprocessedRequests.put(rpcRequest.getId(), resultFuture);
            // 发送请求
            ChannelFuture future = channel.writeAndFlush(rpcRequest);
            future.addListener((ChannelFutureListener) future1 -> {
                if(future1.isSuccess()){
                    logger.info("向服务器发送: " + rpcRequest.toString() + "成功");
                }else{
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    logger.error("发送消息时有错误发生: ", future1.cause());
                }
            });
        }catch (Exception e) {
            unprocessedRequests.remove(rpcRequest.getId());
            logger.error("发送消息时发生错误: ", e);
            Thread.currentThread().interrupt();
        }
        return resultFuture.get();
    }
}