package com.github.ycx.client.handler;

import com.github.ycx.client.RpcFuture;
import com.github.ycx.client.event.IdleEventListener;
import com.github.ycx.codec.RpcMessage;
import com.github.ycx.codec.RpcMessageRequest;
import com.github.ycx.codec.RpcMessageResponse;
import com.github.ycx.common.BizProcessor;
import com.github.ycx.common.sender.SenderNetty;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Yangcx
 * @create 2023/7/20 10:44
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcMessage> {
    private final IdleEventListener idleEventListener;
    private Logger logger = org.apache.log4j.Logger.getLogger(NettyClientHandler.class);
    /**
     * 消息发送器
     */
    private  SenderNetty senderNetty;

    private final Map<String, BizProcessor> bizProcessorMap;


    /**
     * 多线程业务处理请求
     */
    private static final ThreadPoolExecutor bizExecutor = new ThreadPoolExecutor(
            0,
            100,
            30L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(2000),
            new BasicThreadFactory.Builder().namingPattern("Netty Client Processor Pool").build(),
            (r,executor)->{
                throw new RuntimeException("Netty Client Processor is exhausted!");
            });

    public NettyClientHandler(IdleEventListener idleEventListener,Map<String, BizProcessor> bizProcessorMap){
        this.idleEventListener = idleEventListener;
        this.bizProcessorMap = bizProcessorMap;
    }

    /**
     *  连接服务器成功
     * @param ctx ChannelHandlerContext
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        Channel channel = ctx.channel();
        //初始化sender
        this.senderNetty = new SenderNetty(channel);
    }

    /**
     * @todo 单例获取channel
     */
    public Channel getChannel(){

        return null;
    }

    /**
     * 获取消息发送器
     * @return SenderNetty
     */
    public SenderNetty getSenderNetty(){
        return this.senderNetty;
    }

    /**
     * 断开
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        logger.info("Netty Client is exceptionCaught" + cause.getMessage());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcMessage message) throws Exception {
        String direction = message.getDirection();
        String traceId = message.getTraceId();
        if (direction.equals(RpcMessage.RESPONSE)){//响应处理
            if (StringUtils.isNotBlank(traceId)){
                SenderNetty.done(message.getTraceId(),message.getResponse());
            }
        }else {//请求处理
            RpcMessageRequest request = message.getRequest();
            //多线程处理
            bizExecutor.execute(()->{
                String bizCode = request.getBizCode();
                BizProcessor bizProcessor = bizProcessorMap.get(bizCode);
                if (bizProcessor != null){
                    bizProcessor.execute(request,senderNetty);
                }else {
                    senderNetty.send(RpcMessage.createResponse(request.getRid(), RpcMessageResponse.fail("Not Found Processor!")));
                }
            });

        }

    }
}

