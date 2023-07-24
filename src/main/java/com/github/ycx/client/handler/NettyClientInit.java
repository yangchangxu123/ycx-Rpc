package com.github.ycx.client.handler;

import com.github.ycx.client.event.IdleEventListener;
import com.github.ycx.codec.RpcMessageDecoder;
import com.github.ycx.codec.RpcMessageEncoder;
import com.github.ycx.codec.RpcMessageRequest;
import com.github.ycx.codec.RpcMessageResponse;
import com.github.ycx.common.BizProcessor;
import com.github.ycx.serializer.kryo.KryoSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * netty客户端初始化
 * @author Yangcx
 * @create 2023/7/20 10:30
 */
public class NettyClientInit extends ChannelInitializer<SocketChannel> {
    /**
     * 心跳处理监听器
     */
    private final  IdleEventListener idleEventListener;

    private final Map<String, BizProcessor> bizProcessorMap;


    public NettyClientInit(IdleEventListener idleEventListener, Map<String, BizProcessor> bizProcessorMap) {
        this.idleEventListener = idleEventListener;
        this.bizProcessorMap = bizProcessorMap;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        KryoSerializer kryoSerializer = new KryoSerializer();
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("beat",new IdleStateHandler(0, 0, 30, TimeUnit.SECONDS));
        //处理半包、粘包
        pipeline.addLast("lengthFieldBasedFrameDecoder",new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        //编解码
        pipeline.addLast("rpcMessageEncoder",new RpcMessageEncoder(kryoSerializer, RpcMessageRequest.class));
        pipeline.addLast("rpcMessageDecoder",new RpcMessageDecoder(kryoSerializer, RpcMessageResponse.class));
        //消息出入栈处理
        pipeline.addLast("nettyClientHandler",new NettyClientHandler(idleEventListener,bizProcessorMap));
    }
}
