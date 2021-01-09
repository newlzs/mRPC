import ServiceImpl.HelloServiceImpl;
import pojo.HelloObject;
import rpc.registry.ServerRegistry;
import rpc.registry.impl.ServerRegisterImpl;
import rpc.serialize.impl.HessianSerializer;
import rpc.serialize.impl.KryoSerializer;
import rpc.transport.server.ServicePublish;
import rpc.serialize.impl.JSONSerializer;
import service.HelloService;

import java.lang.reflect.Method;

/**
 * @author Lzs
 * @date 2021/1/5 16:07
 * @description
 */
public class ServerApplication {
    public static void main(String args[]) throws Exception {
        ServerRegistry serverRegistry = new ServerRegisterImpl();
        serverRegistry.register(new HelloServiceImpl(), HelloService.class.getName());
        new ServicePublish(8080, new HessianSerializer()).run();
    }
}