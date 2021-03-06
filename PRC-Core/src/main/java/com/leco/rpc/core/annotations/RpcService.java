package com.leco.rpc.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 *
 * @创建人 leco
 * @创建时间 2018/7/30
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {
//    Class<?> serviceNorm(); //对外提供服务的接口
    String implName() default ""; //实现类的名称  默认为驼峰类名

}
