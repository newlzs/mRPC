package rpc.exception;

/**
 * @author Lzs
 * @date 2021/1/7 12:44
 * @description
 */
public class SerializeException extends RuntimeException {
    public SerializeException(String message) {
        super(message);
    }
}