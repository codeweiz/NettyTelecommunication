package cn.microboat.exception;

/**
 * 自定义序列化相关异常
 *
 * @author zhouwei
 */
public class SerializeException extends RuntimeException {

    public SerializeException(String message) {
        super(message);
    }
}
