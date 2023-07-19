package com.github.ycx.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.ycx.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 序列化、反序列化实现类
 * @author Yangcx
 * @create 2023/7/19 16:20
 */
public class KryoSerializer implements Serializer {

    //因为kryo存在线程安全，需要给每个线程提供一个kryo实例
    private static  final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        //设置支持对象循环引用，避免栈溢出
        kryo.setReferences(true);
        //不强制要求注册类（注册行为无法保证多个 JVM 内同一个类的注册编号相同；而且业务系统中大量的 Class 也难以一一注册）
        kryo.setRegistrationRequired(false);
        //设置类加载器为线程上下文类加载器
        kryo.setClassLoader(Thread.currentThread().getContextClassLoader());
        return kryo;
    });


    @Override
    public <T> byte[] serializer(T obj) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        Kryo kryo = kryoThreadLocal.get();
        try {
            kryo.writeClassAndObject(output,obj);
            output.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public <T> Object deserializer(Class<T> clazz, byte[] bytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(inputStream);
        Kryo kryo = kryoThreadLocal.get();
        try {
            Object obj = kryo.readObject(input,clazz);
            input.close();
            return obj;
        } catch (KryoException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
