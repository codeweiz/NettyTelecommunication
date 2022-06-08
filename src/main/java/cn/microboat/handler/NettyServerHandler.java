package cn.microboat.handler;

import cn.microboat.dto.RpcRequest;
import cn.microboat.dto.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义 ChannelHandler 处理客户端消息
 * NettyServerHandler 用于接收客户端发送过来的消息，并返回结果给客户端
 *
 * @author zhouwei
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            // 从客户端接收消息
            RpcRequest rpcRequest = (RpcRequest) msg;
            log.info("server receive msg: [{}], time: [{}]", rpcRequest, ATOMIC_INTEGER.getAndIncrement());

            // 向服务端发送消息
            RpcResponse messageFromServer = RpcResponse.builder().message("message from server").build();
            ChannelFuture channelFuture = ctx.writeAndFlush(messageFromServer);
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 捕获异常
     *
     * @param ctx   通道处理器上下文
     * @param cause 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server catch exception", cause);
        ctx.close();
    }
}
