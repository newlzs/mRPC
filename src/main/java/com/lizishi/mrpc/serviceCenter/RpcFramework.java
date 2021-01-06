package com.lizishi.mrpc.serviceCenter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Lzs
 * @date 2020/12/20 17:04
 * @description 服务中心
 */
public class RpcFramework {

    /**
     * '添加服务到服务中心
     * @param service   服务实现
     * @param port      服务端口
     * @throws Exception
     */
    public static void export(final Object service, int port) throws Exception{
        if(service == null)
            throw new IllegalArgumentException("service instance == null");
        if(port < 0 || port > 65535)
            throw new IllegalArgumentException("Invalid port: " + port);
        logger.info("Export Service" + service.getClass().getName() + " on port " + port);
        ServerSocket server = new ServerSocket(port);
        while(true) {
            // 服务端监听
            final Socket socket = server.accept();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                        try {
                            // 接收客户端传来的方法名、参数类型、参数
                            String methodName = input.readUTF();
                            Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
                            Object[] arguments = (Object[]) input.readObject();
                            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                            try {
                                // 生成本地方法
                                Method method = service.getClass().getMethod(methodName, parameterTypes);
                                Object result = method.invoke(service, arguments); // 调用
                                output.writeObject(result); // 返回
                            } catch (Throwable t) {
                                output.writeObject(t);
                            }finally {
                                output.close();
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }finally {
                            input.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    /**
     *
     * @param interfaceClass 接口类型
     * @param host  主机名
     * @param port  端口
     * @param <T>   接口泛型
     * @return
     */
//    @SuppressWarnings("unchecked")
    public static <T> T refer(final Class<T> interfaceClass, final String host, final int port) {
        if(interfaceClass == null)
            throw new IllegalArgumentException("interface class == null");
        if(!interfaceClass.isInterface())
            throw new IllegalArgumentException("The " + interfaceClass.getName() + " must be interface class");
        if(host == null || host.length() == 0)
            throw new IllegalArgumentException("host == null !!");
        if(port < 0 || port > 65535)
            throw new IllegalArgumentException("Invalid port " + port);
        logger.info("Get remote service " + interfaceClass.getName() + " from server " + host + " : " + port);

        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass},
                new InvocationHandler() { // 动态代理进行包装, 看似是调用一个方法,实际上内部是通过socket传到服务器运行,并接受运行产生的结果
                    public Object invoke(Object o, Method method, Object[] arguments) throws Throwable {
                        Socket socket = new Socket(host, port);
                        try{
                            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                            try{
                                output.writeUTF(method.getName());
                                output.writeObject(method.getParameterTypes());
                                output.writeObject(arguments);
                                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                                try{
                                    Object result = input.readObject();
                                    if(result instanceof Throwable) {
                                        throw (Throwable) result;
                                    }
                                    return result; // 返回结果
                                }finally {
                                    input.close();
                                }
                            }finally {
                                output.close();
                            }
                        }finally {
                            socket.close();
                        }
                    }
                }
        );
    }
}