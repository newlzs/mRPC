package rpc.exception.exactException;

import rpc.exception.BaseException;

/**
 * @author Lzs
 * @date 2021/1/7 12:44
 * @description
 */
public class SerializeException extends BaseException {
    public SerializeException(String message){
        this.message = message;
    }
}