package rpc.transport.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.coder.Decoder;
import rpc.coder.Encoder;
import rpc.handler.ServerHandler;
import rpc.serialize.Serializer;

/**
 * @author Lzs
 * @date 2021/1/5 15:46
 * @description 发布服务
 */
public class ServicePublish {
    private static final Logger logger = LoggerFactory.getLogger(ServicePublish.class);
    private int port;
    private Serializer serializer;
    public ServicePublish(int port, Serializer serializer) {
        this.port = port;
        this.serializer = serializer;
    }

    public void run() throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker);
            b.channel(NioServerSocketChannel.class);
            b.option(ChannelOption.SO_BACKLOG, 256);
            b.option(ChannelOption.SO_KEEPALIVE,true);
            b.childOption(ChannelOption.TCP_NODELAY, true);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 添加处理过程
                    // 不同的顺序会造成不同的结果
                    // https://www.cnblogs.com/qdhxhz/p/10234908.html
                    ch.pipeline().addLast(new Decoder());
                    ch.pipeline().addLast(new Encoder(serializer));
                    ch.pipeline().addLast(new ServerHandler());
                }
            });

            ChannelFuture f = b.bind(port).sync();
            logger.info("服务运行在 {} 端口", port);
            f.channel().closeFuture().sync();
        }finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }
}