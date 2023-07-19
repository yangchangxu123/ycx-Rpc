package com.github.ycx.common.sender;

/**
 * 消息发送器
 * @author Yangcx
 * @create 2023/7/19 21:25
 */
public interface senderNetty {
    /**
     * 直接发送消息
     * @param msg 消息内容
     */
     void send(Object msg);


}
