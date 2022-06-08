package cn.microboat.handler;

import cn.microboat.dto.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhouwei
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse rpcResponse = (RpcResponse) msg;
        log.info("client receive msg: [{}]", rpcResponse.toString());

        // 声明一个 AttributeKey 对象
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");

        // 将服务端的返回结果保存到 AttributeMap 上， AttributeMap 可以看作是一个 Channel 的共享数据源
        // AttributeMap 的 key 是 AttributeKey，value 是 Attribute
        ctx.channel().attr(key).set(rpcResponse);
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client caught exception", cause);
        ctx.close();
    }
}
