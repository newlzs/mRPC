package rpc.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Lzs
 * @date 2021/1/5 17:55
 * @description 请求
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RPCRequest implements Serializable {
    private String id;
    // 接口名
    private String interfaceName;
    // 方法名
    private String methodName;
    // 参数
    private Object[] parameters;
    // 参数类型
    private Class<?>[] paramTypes;

    public String getHashKey() {
        return interfaceName + methodName;
    }
}