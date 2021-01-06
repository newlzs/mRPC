package rpc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

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
}