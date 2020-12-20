package com.lizishi.mrpc.service.impl;

import com.lizishi.mrpc.service.HelloService;

/**
 * @author Lzs
 * @date 2020/12/20 17:01
 * @description
 */
public class HelloServiceImpl implements HelloService {
    public String hello(String name) {
        return "Hello, " + name;
    }
}