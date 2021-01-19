package rpc.transport.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import jdk.jfr.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.coder.Decoder;
import rpc.coder.Encoder;
import rpc.handler.ClientHandler;
import rpc.serialize.Serializer;
import rpc.serialize.impl.HessianSerializer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * @author Lzs
 * @date 2021/1/13 22:21
 * @description 获取管道
 */
public class ChannelProvider {
    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);

    // 记录以存在的通道，减少重复创建。string = host + serializerCode
    private static Map<String, Channel> channelMap = new ConcurrentHashMap<>();
    private static EventLoopGroup workGroup;
    private static Bootstrap bootstrap = initializeBootstrap();

    public static Channel get(InetSocketAddress inetSocketAddress, Serializer serializer) {
        String key = inetSocketAddress.toString() + serializer.getCode();
        if(channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            if(channel != null && channel.isActive()) {
                return channel;
            }else{
                channelMap.remove(key);
            }
        }
        // 添加新的channel
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new Decoder());
                ch.pipeline().addLast(new Encoder(serializer));
                ch.pipeline().addLast(new ClientHandler());
            }
        });
        Channel channel = null;
        try {
            channel = connect(bootstrap, inetSocketAddress);
        } catch (Exception e) {
            logger.error("连接客户端时出错");
            return null;
        }
        channelMap.put(key, channel);
        return channel;
    }

    /**
     * 连接获取channel
     * @param bootstrap
     * @param inetSocketAddress
     * @return
     */
    public static Channel connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress)
            throws Exception {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener)future -> {
            if(future.isSuccess()) {
                logger.info("客户端连接成功");
                completableFuture.complete(future.channel());
            }else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    /**
     * 初始化bootstrap
     * @return
     */
    public static Bootstrap initializeBootstrap() {
        workGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(workGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE,true);  // tcp心跳机制
        b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000); // 超时
        b.option(ChannelOption.TCP_NODELAY, true); // 包立即发送
        return b;
    }
}