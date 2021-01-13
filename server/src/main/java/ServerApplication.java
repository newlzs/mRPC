import ServiceImpl.HelloServiceImpl;
import rpc.registry.ServiceProvider;
import rpc.registry.impl.ServiceProviderImpl;
import rpc.serialize.impl.HessianSerializer;
import rpc.serialize.impl.JSONSerializer;
import rpc.transport.server.ServicePublish;
import service.HelloService;

/**
 * @author Lzs
 * @date 2021/1/5 16:07
 * @description
 */
public class ServerApplication {
    public static void main(String args[]) throws Exception {
        ServicePublish servicePublish = new ServicePublish("127.0.0.1", 8080, new JSONSerializer(), true);
        // 添加要发布的服务
        servicePublish.publish(new HelloServiceImpl(), HelloService.class.getName());
        // 服务上线
        servicePublish.run();
    }
}