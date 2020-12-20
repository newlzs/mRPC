package com.lizishi.mrpc.serviceConsumer;

import com.lizishi.mrpc.service.HelloService;
import com.lizishi.mrpc.serviceCenter.RpcFramework;

/**
 * @author Lzs
 * @date 2020/12/20 21:32
 * @description
 */
public class RpcConsumer {
    public static void main(String args[]) throws InterruptedException {
        HelloService service = RpcFramework.refer(HelloService.class, "127.0.0.1", 1234);
        for(int i = 0;i < 100;i ++) {
            String hello = service.hello(i + "");
            System.out.println(hello);
            Thread.sleep(1000);
        }
    }

}