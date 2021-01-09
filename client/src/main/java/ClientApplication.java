import pojo.HelloObject;
import rpc.transport.client.ServiceProxy;
import service.HelloService;

/**
 * @author Lzs
 * @date 2021/1/6 11:51
 * @description
 */
public class ClientApplication {
    public static void main(String args[]) {
        ServiceProxy serviceProxy = new ServiceProxy("127.0.0.1", 8080);
        HelloService helloService = serviceProxy.getProxy(HelloService.class);
        String ret = helloService.hello(new HelloObject(10, "我从客户端来"));
        System.out.println(ret);
    }

}