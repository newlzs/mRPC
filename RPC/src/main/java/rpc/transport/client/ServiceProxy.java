package rpc.transport.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.config.ResponseStatusCode;
import rpc.loadBalancer.LoadBalancer;
import rpc.loadBalancer.impl.FirstBalancer;
import rpc.pojo.RPCRequest;
import rpc.pojo.RPCResponse;
import rpc.serialize.Serializer;
import rpc.serialize.impl.HessianSerializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author Lzs
 * @date 2021/1/6 11:52
 * @description
 */
public class ServiceProxy implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServiceProxy.class);
    private LoadBalancer loadBalancer;
    private Serializer serializer;
    private ClientRequest clientRequest;

    public ServiceProxy() {
        this(new FirstBalancer(), new HessianSerializer());
    }
    public ServiceProxy(LoadBalancer loadBalancer, Serializer serializer) {
        this.loadBalancer = loadBalancer;
        this.serializer = serializer;
        this.clientRequest = new ClientRequest(loadBalancer, serializer);
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
                .id(UUID.randomUUID().toString())
                .build();
        logger.info(method.getDeclaringClass().getName());

        RPCResponse response = clientRequest.sendRequest(rpcRequest);
        if(response.getStatusCode() == ResponseStatusCode.FAIL) {
            logger.warn("远程调用失败");
        }
        return response.getData();
    }
}