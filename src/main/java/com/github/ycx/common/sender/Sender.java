package com.github.ycx.common.sender;

import com.github.ycx.client.RpcFuture;

import java.net.SocketAddress;

/**
 * 消息发送器
 * @author Yangcx
 * @create 2023/7/19 21:25
 */
public interface Sender {
    /**
     * 直接发送消息
     * @param msg 消息内容
     */
     void send(Object msg);

    /**
     * 发送并返回RpcFuture
     * @param msg 消息
     * @return RpcFuture
     */
    RpcFuture sendAndRetRpcFuture(Object msg);

    /**
     * 发送并等待返回结果
     * @param msg 消息
     * @param timeout 超时时间
     * @return Object
     */
    Object sendAndRetResponse(Object msg,Long timeout);

    /**
     *  获取远程地址
     * @return SocketAddress
     */
    SocketAddress getRemoteAddress();

    /**
     * 是否可用
     * @return boolean
     */
    boolean available();
}
