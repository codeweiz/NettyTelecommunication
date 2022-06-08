package cn.microboat.serializer;

/**
 * 序列化接口
 *
 * @author zhouwei
 */
public interface Serializer {

    /**
     * 序列化
     *
     * @param object 要序列化的对象
     * @return 字节数组
     */
    byte[] serialize(Object object);

    /**
     * 反序列化
     *
     * @param bytes 序列化后的对象
     * @param clazz 类
     * @return T
     */
    <T> T deSerialize(byte[] bytes, Class<T> clazz);
}
