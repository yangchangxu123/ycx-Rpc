package com.github.ycx.common.sender;

import com.github.ycx.client.RpcFuture;
import com.github.ycx.codec.RpcMessageRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.apache.log4j.Logger;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 具体消息发送器实现类
 * @author Yangcx
 * @create 2023/7/19 23:02
 */
public class SenderNetty implements Sender {

    Logger logger = Logger.getLogger(SenderNetty.class);
    /**
     *等待响应的future
     */
    private static final Map<String,RpcFuture> pendingFuture = new ConcurrentHashMap<>();

    /**
     * Netty消息信道
     */
    private Channel channel;

    public SenderNetty(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void send(Object msg) {
        try {
            ChannelFuture channelFuture = channel.writeAndFlush(msg).sync();
            if (!channelFuture.isSuccess()){
                logger.error("Channel send error message is : " + msg);
            }
        } catch (InterruptedException e) {
            logger.error("Channel send  message exception  " + e.getMessage());
        }
    }

    @Override
    public RpcFuture sendAndRetRpcFuture(Object msg) {
        this.isCheck(msg);
        RpcMessageRequest request = (RpcMessageRequest) msg;
        RpcFuture rpcFuture = new RpcFuture(request.getRid());
        pendingFuture.put(request.getRid(),rpcFuture);
        try {
            ChannelFuture channelFuture = channel.writeAndFlush(msg).sync();
            if (!channelFuture.isSuccess()){
                logger.error("Channel send error message is : " + msg);
            }
        } catch (InterruptedException e) {
            logger.error("Channel send  message exception  " + e.getMessage());
        }
        return rpcFuture;
    }


    @Override
    public Object sendAndRetResponse(Object msg, Long timeout) {
        RpcFuture rpcFuture = this.sendAndRetRpcFuture(msg);
        try {
            if (timeout <= -1){
                Object resp = rpcFuture.get();
                return resp;
            }
            Object resp = rpcFuture.get(timeout, TimeUnit.SECONDS);
            return resp;
        } catch (Exception e) {
            throw new RuntimeException("Rpc Channel send message Exception :" + e.getMessage());
        }
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return channel.remoteAddress();
    }

    @Override
    public boolean available() {
        //通道存在并可用
        return channel != null && channel.isActive();
    }

    /**
     *  功能校验
     * @param msg 请求消息
     */
    private void isCheck(Object msg) {
        if (!available()){
            throw new RuntimeException("Rpc Channel is not available");
        }
        if (!(msg instanceof RpcMessageRequest)){
            throw new RuntimeException("Rpc request msg`s  type must be ``com/github/ycx/codec/RpcMessageRequest``");
        }
    }

    /**
     * 完成响应
     * @param rId   请求id
     * @param response 响应报文
     */
    public static void done(String rId,Object response){
        RpcFuture rpcFuture = pendingFuture.get(rId);
        if (rpcFuture != null){
            pendingFuture.remove(rId);
            rpcFuture.done(response);
        }
    }

}
