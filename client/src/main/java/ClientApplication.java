import pojo.HelloObject;
import rpc.config.NacosConfig;
import rpc.loadBalancer.impl.CycleBalancer;
import rpc.loadBalancer.impl.KetamaConsistentHashLoadBalancer;
import rpc.serialize.impl.HessianSerializer;
import rpc.transport.client.ServiceProxy;
import rpc.utils.NacosUtils;
import service.HelloService;

/**
 * @author Lzs
 * @date 2021/1/6 11:51
 * @description
 */
public class ClientApplication {
    public static void main(String args[]) throws InterruptedException {
        NacosConfig.initialize("127.0.0.1:8848"); // 指定注册中心地址;
        ServiceProxy serviceProxy = new ServiceProxy(new KetamaConsistentHashLoadBalancer(), new HessianSerializer());
        HelloService helloService = serviceProxy.getProxy(HelloService.class);
        String ret = helloService.hello(new HelloObject(10, "我从客户端来"));
        System.out.println(ret);

        HelloService helloService1 = serviceProxy.getProxy(HelloService.class);
        String ret2 = helloService1.hello(new HelloObject(1234, "我从客户端来"));
        System.out.println(ret2);
        System.out.println("成功");
    }
}