package rpc.annotation;

import java.lang.annotation.*;

/**
 * @author Lzs
 * @date 2021/1/19 21:09
 * @description 用来标注服务的注解(搁置)
 * https://www.cnblogs.com/jajian/p/9695055.html
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Service {
    public String name() default "";
}
