package rpc.exception;

import lombok.Data;

/**
 * @author Lzs
 * @date 2021/1/7 12:41
 * @description
 */
@Data
public class BaseException extends Exception {
    public int code;
    public String message;
}