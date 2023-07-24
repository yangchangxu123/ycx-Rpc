package com.github.ycx.common;

import com.github.ycx.codec.RpcMessage;
import com.github.ycx.codec.RpcMessageRequest;
import com.github.ycx.common.sender.Sender;
import com.github.ycx.common.sender.SenderNetty;

/**
 * @author Yangcx
 * @create 2023/7/20 11:34
 */
public interface BizProcessor {
    void execute(RpcMessageRequest request, Sender sender);
}
