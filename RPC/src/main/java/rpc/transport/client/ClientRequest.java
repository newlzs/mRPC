package rpc.transport.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.coder.Decoder;
import rpc.coder.Encoder;
import rpc.handler.ClientHandler;
import rpc.pojo.RPCRequest;
import rpc.pojo.RPCResponse;
import rpc.registry.ServiceRegistry;
import rpc.registry.impl.NacosServiceRegistry;
import rpc.serialize.impl.HessianSerializer;
import rpc.serialize.impl.JSONSerializer;
import rpc.serialize.impl.KryoSerializer;

import java.net.InetSocketAddress;


/**
 * @author Lzs
 * @date 2021/1/6 10:46
 * @description
 */
public class ClientRequest {
    private static final Logger logger = LoggerFactory.getLogger(ClientRequest.class);
    private String host;
    private int port;
    private static final Bootstrap b;
    private static ServiceRegistry serviceRegistry = new NacosServiceRegistry();
    static {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        b = new Bootstrap();
        b.group(workGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE,true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new Decoder());
                ch.pipeline().addLast(new Encoder(new HessianSerializer()));
                ch.pipeline().addLast(new ClientHandler());
            }
        });

    }
    // 直接给定服务地址
    public ClientRequest(String host, int port) {
        this.host = host;
        this.port = port;
    }
    // 向远程注册中心查找服务地址
    public ClientRequest(String serviceName) {
        InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(serviceName);
        this.host = inetSocketAddress.getHostName();
        this.port = inetSocketAddress.getPort();
    }

    public RPCResponse sendRequest(RPCRequest rpcRequest) throws InterruptedException {
        try{
            // 连接并开启
            ChannelFuture f = b.connect(host, port).sync();
            logger.info("客户端连接到服务器: {}:{}", host, port);
            Channel channel = f.channel();

            // 发送请求
            ChannelFuture future = channel.writeAndFlush(rpcRequest);
            future.addListener(future1 -> {
                if(future1.isSuccess()){
                    logger.info("向服务器发送: " + rpcRequest.toString() + "成功");
                }else{
                    logger.error("发送消息时有错误发生: ", future1.cause());
                }
            });
            // 通道关闭后停止阻塞
            channel.closeFuture().sync();

            AttributeKey<RPCResponse> key = AttributeKey.valueOf("rpcResponse");
            RPCResponse rpcResponse = channel.attr(key).get();
            return rpcResponse;
        }catch (Exception e) {
            logger.error("发送消息时发生错误: ", e);
        }
        return null;
    }
}