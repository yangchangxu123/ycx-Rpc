package com.github.ycx.client.event;

import com.github.ycx.common.sender.Sender;

/**
 * 心跳处理监听器
 * @author Yangcx
 * @create 2023/7/20 10:34
 */
public interface IdleEventListener {
    /**
     * 处理
     * @param sender    消息发送器
     */
    void complete(Sender sender);
}
