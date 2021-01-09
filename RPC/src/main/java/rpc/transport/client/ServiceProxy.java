package rpc.transport.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.config.ResponseStatusCode;
import rpc.pojo.RPCRequest;
import rpc.pojo.RPCResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Lzs
 * @date 2021/1/6 11:52
 * @description
 */
public class ServiceProxy implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServiceProxy.class);
    private String host;
    private int port;
    public ServiceProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public <T> T getProxy(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class<?>[]{interfaceClass}, this);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        // 构建请求
        RPCRequest rpcRequest = RPCRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();
        logger.info(method.getDeclaringClass().getName());
        // 连接获取结果
        ClientRequest clientRequest = new ClientRequest(host, port);
        RPCResponse response = clientRequest.sendRequest(rpcRequest);
        if(response.getStatusCode() == ResponseStatusCode.FAIL) {
            logger.warn("远程调用失败");
        }
        return response.getData();
    }
}