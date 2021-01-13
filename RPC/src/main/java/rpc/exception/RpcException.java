package rpc.exception;

import com.fasterxml.jackson.databind.ser.Serializers;

/**
 * @author Lzs
 * @date 2021/1/9 14:01
 * @description
 */
public class RpcException extends RuntimeException {
    public RpcException(String message) {
        super(message);
    }
}