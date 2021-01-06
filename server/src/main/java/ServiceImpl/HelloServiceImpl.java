package ServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.HelloObject;
import service.HelloService;


/**
 * @author Lzs
 * @date 2021/1/5 14:52
 * @description
 */
public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger("HelloService");

    public String hello(HelloObject obj) {
        // 操作
        logger.info("收到消息: " + obj.getMessage());
        return "调用结果: id = " + obj.getId();
    }
}