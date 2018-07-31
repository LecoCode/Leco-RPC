package com.leco.rpc.core.serialization;

/**
 * 序列化接口
 * @创建人 leco
 * @创建时间 2018/7/31
 */
public interface ISerialization {


    /**
     * 序列化
     * @param obj 序列化对象
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T obj);


    /**
     * 反序列化
     * @param data 数据源
     * @param cls 反序列化类
     * @param <T>
     * @return
     */
    <T> T dserialize(byte[] data, Class<T> cls);


}
