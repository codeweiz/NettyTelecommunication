package cn.microboat.serializer.impl;

import cn.microboat.dto.RpcRequest;
import cn.microboat.dto.RpcResponse;
import cn.microboat.exception.SerializeException;
import cn.microboat.serializer.Serializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 基于 Kryo 的序列化器
 *
 * @author zhouwei
 */
public class KryoSerializer implements Serializer {

    /**
     * 由于 Kryo 不是线程安全的，每个线程都应该有自己的 Kryo、Input、Output 实例
     * 所以使用 Thread 存放 Kryo 对象
     */
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);

        // 是否关闭注册行为
        kryo.setReferences(true);

        // 是否关闭循环引用
        kryo.setRegistrationRequired(false);

        return kryo;
    });

    @Override
    public byte[] serialize(Object object) {
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Output output = new Output(byteArrayOutputStream)
        ) {
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            kryo.writeObject(output, object);
            KRYO_THREAD_LOCAL.remove();
            return output.toBytes();
        } catch (IOException e) {
            throw new SerializeException("序列化失败");
        }
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> clazz) {
        try (
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                Input input = new Input(byteArrayInputStream)
        ) {
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            T t = kryo.readObject(input, clazz);
            KRYO_THREAD_LOCAL.remove();
            return t;
        } catch (IOException e) {
            throw new SerializeException("反序列化失败");
        }
    }
}
