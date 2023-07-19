package com.github.ycx.serializer;

/**
 * 序列化、反序列化接口定义
 * @author Yangcx
 * @create 2023/7/19 16:14
 */
public interface Serializer {
    /**
     * 序列化
     * @param obj 消息
     * @param <T> 泛型方法
     * @return byte[]字节码数组
     */
    <T> byte[] serializer(T obj);

    /**
     * 反序列化
     * @param clazz 反序列化对象类型
     * @param bytes 反序列化字节数组
     * @param <T>   泛型方法
     * @return  任意对象
     */
    <T> Object deserializer(Class<T> clazz,byte[] bytes);
}
