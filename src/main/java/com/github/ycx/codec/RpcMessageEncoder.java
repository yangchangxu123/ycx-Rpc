package com.github.ycx.codec;

import com.github.ycx.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.log4j.Logger;


/**
 * Rpc消息编码
 * @author Yangcx
 * @create 2023/7/19 17:00
 */
public class RpcMessageEncoder extends MessageToByteEncoder<Object> {
    final Logger logger = Logger.getLogger(RpcMessageEncoder.class);
    //反序列化器
    private Serializer serializer;
    //反序列化对象类型
    private Class<?> clazz;

    public RpcMessageEncoder(Serializer serializer, Class<?> clazz) {
        this.serializer = serializer;
        this.clazz = clazz;
    }


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf in) throws Exception {
        //编码类型
        if (clazz.isInstance(o)){
            try {
                byte[] bytes = this.serializer.serializer(o);
                in.writeInt(bytes.length);
                in.writeBytes(bytes);
            } catch (Exception e) {
                logger.error("Rpc Encode Error "+ e.getMessage());
            }
        }
    }
}
