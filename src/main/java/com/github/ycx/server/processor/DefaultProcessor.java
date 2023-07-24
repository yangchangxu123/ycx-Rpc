package com.github.ycx.server.processor;

import com.github.ycx.codec.RpcMessage;
import com.github.ycx.codec.RpcMessageRequest;
import com.github.ycx.codec.RpcMessageResponse;
import com.github.ycx.common.sender.Sender;
import org.apache.log4j.Logger;

/**
 * 默认处理器
 * @author Yangcx
 * @create 2023/7/24 22:09
 */
public class DefaultProcessor implements Processor{
    Logger logger = Logger.getLogger(DefaultProcessor.class);

    @Override
    public void execute(RpcMessageRequest request, Sender sender) {
        logger.error("Request processor not found !" + sender.getRemoteAddress() + ":" + request);
        sender.send(RpcMessage.createResponse(request.getRid(), RpcMessageResponse.fail("Request processor not found !")));
    }
}
