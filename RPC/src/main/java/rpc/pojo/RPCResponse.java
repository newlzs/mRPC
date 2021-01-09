package rpc.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import rpc.config.ResponseStatusCode;

import java.io.Serializable;

/**
 * @author Lzs
 * @date 2021/1/5 18:02
 * @description 响应结果
 */
@Data
public class RPCResponse<T> implements Serializable {
    // 状态码
    private Integer statusCode;
    // 信息
    private String  message;
    // 结果
    private T data;

    public static <T> RPCResponse<T> success(T data) {
        RPCResponse<T> response = new RPCResponse<>();
        response.setMessage("success");
        response.setStatusCode(ResponseStatusCode.SUCCESS);
        response.setData(data);
        return response;
    }

    public static <T> RPCResponse<T> fail(T data) {
        RPCResponse<T> response = new RPCResponse<>();
        response.setMessage("fail");
        response.setStatusCode(ResponseStatusCode.FAIL);
        response.setData(data);
        return response;
    }
}