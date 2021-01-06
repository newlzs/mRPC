package com.lizishi.mrpc.serviceProvider;

import com.lizishi.mrpc.service.HelloService;
import com.lizishi.mrpc.service.impl.HelloServiceImpl;
import com.lizishi.mrpc.serviceCenter.RpcFramework;

/**
 * @author Lzs
 * @date 2020/12/20 21:19
 * @description
 */
public class RpcProvider {
    public static void main(String args[]) throws Exception {
        HelloService helloService = new HelloServiceImpl();
        RpcFramework.export(helloService, 1234);
//        ServiceLoader.load()
    }
}