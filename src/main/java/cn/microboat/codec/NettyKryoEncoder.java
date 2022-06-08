package cn.microboat.codec;

import cn.microboat.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * 自定义编码器
 *
 * @author zhouwei
 */
@AllArgsConstructor
public class NettyKryoEncoder extends MessageToByteEncoder<Object> {

    private final Serializer serializer;

    private final Class<?> genericClass;

    /**
     * 将对象转换为字节码，然后写入到 ByteBuf 对象中
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) {
        if (genericClass.isInstance(o)) {
            byte[] body = serializer.serialize(o);
            int dataLength = body.length;
            byteBuf.writeInt(dataLength);
            byteBuf.writeBytes(body);
        }
    }
}
