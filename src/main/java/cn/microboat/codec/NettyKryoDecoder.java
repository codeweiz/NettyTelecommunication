package cn.microboat.codec;

import cn.microboat.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author zhouwei
 */
@AllArgsConstructor
@Slf4j
public class NettyKryoDecoder extends ByteToMessageDecoder {

    private final Serializer serializer;

    private final Class<?> genericClass;

    /**
     * Netty 传输的消息长度也就是对象序列化后对应的字节数组的大小，存储在 ByteBuf 头部
     */
    private static final int BODY_LENGTH = 4;

    /**
     * 解码 ByteBuf 对象
     *
     * @param channelHandlerContext 解码器关联的 ChannelHandlerContext 对象
     * @param byteBuf               "入站"数据，也就是 ByteBuf 对象
     * @param list                  解码之后的数据对象需要添加到 list 对象中
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        // byteBuf 中写入的消息长度所占的字节数已经是 4 了，所以 byteBuf 的可读字节必须大于 4
        if (byteBuf.readableBytes() >= BODY_LENGTH) {

            // 标记当前 readIndex 的位置，以便后面重置 readIndex 的时候使用
            byteBuf.markReaderIndex();

            // 读取消息的长度，一个 int 型数据，占 4 个字节
            int dataLength = byteBuf.readInt();

            // 消息长度小于 0 或者 byteBuf 的可读字节数小于 0，直接返回
            if (dataLength < 0 || byteBuf.readableBytes() < 0) {
                log.error("data length or byteBuf readBytes is not valid");
                return;
            }

            // 如果 可读字节数小于消息长度的话，说明是不完整的消息，重置 readIndex
            if (byteBuf.readableBytes() < dataLength) {
                byteBuf.resetReaderIndex();
                return;
            }

            // 进行反序列化
            byte[] body = new byte[dataLength];
            byteBuf.readBytes(body);
            Object obj = serializer.deSerialize(body, genericClass);
            list.add(obj);

            log.info("successful decode ByteBuf to Object");
        }
    }
}
