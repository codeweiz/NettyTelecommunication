package cn.microboat;

import cn.microboat.codec.NettyKryoDecoder;
import cn.microboat.codec.NettyKryoEncoder;
import cn.microboat.dto.RpcRequest;
import cn.microboat.dto.RpcResponse;
import cn.microboat.handler.NettyClientHandler;
import cn.microboat.serializer.impl.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty 客户端
 *
 * @author zhouwei
 */
@Slf4j
public class NettyClient {

    /**
     * ip 地址
     */
    private final String host;

    /**
     * 端口
     */
    private final int port;

    /**
     * 引导启动类
     */
    private static final Bootstrap BOOTSTRAP;

    /**
     * 有参构造器
     *
     * @param host ip 地址
     * @param port 端口
     */
    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // 初始化相关资源：EventLoopGroup、Bootstrap
    static {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        BOOTSTRAP = new Bootstrap();
        KryoSerializer kryoSerializer = new KryoSerializer();
        BOOTSTRAP.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // 连接的超时时间
                // 如果 15 秒之内没有发送消息给服务端，就发送一次心跳请求
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        // 对服务端的返回加上解码器
                        socketChannel.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcResponse.class));

                        // 对客户端的请求加上编码器
                        socketChannel.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcRequest.class));

                        // 对客户端的请求加上 Handler
                        socketChannel.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }

    /**
     * 发送消息到服务端
     *
     * @param rpcRequest RPC请求消息体
     * @return RpcResponse
     */
    public RpcResponse sendMessage(RpcRequest rpcRequest) {
        try {
            ChannelFuture channelFuture = BOOTSTRAP.connect(host, port).sync();
            log.info("client connect {}", host + ":" + port);

            Channel channel = channelFuture.channel();
            log.info("send message");

            if (channel != null) {
                channel.writeAndFlush(rpcRequest).addListener(future -> {
                    if (future.isSuccess()) {
                        log.info("client send message: [{}]", rpcRequest.toString());
                    } else {
                        log.error("send failed:", future.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                return channel.attr(key).get();
            }
        } catch (InterruptedException e) {
            log.error("occur exception when connect server:", e);
        }
        return null;
    }
}
