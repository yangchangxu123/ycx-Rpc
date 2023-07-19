package com.github.ycx.codec;

import com.github.ycx.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Rpc消息解码
 * @author Yangcx
 * @create 2023/7/19 16:43
 */
public class RpcMessageDecoder extends ByteToMessageDecoder {
    Logger logger = Logger.getLogger(RpcMessageDecoder.class);
    //反序列化器
    private Serializer serializer;
    //反序列化对象类型
    private Class<?> clazz;

    public RpcMessageDecoder(Serializer serializer, Class<?> clazz) {
        this.serializer = serializer;
        this.clazz = clazz;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) {
        //当可读长度小于消息头时
        if (in.readableBytes() < 4){
            return;
        }

        //标记读下表，用于重置
        in.markReaderIndex();
        //数据长度
        int readLength = in.readInt();
        if (in.readableBytes() < readLength){
            //重置读下表
            in.resetReaderIndex();
            return;
        }
        byte[] bytes = new byte[readLength];
        in.writeBytes(bytes);
        try {
            Object decodeMessage = serializer.deserializer(clazz, bytes);
            out.add(decodeMessage);
        } catch (Exception e) {
            logger.error("Rpc Decode Error " + e.getMessage());
        }

    }
}
