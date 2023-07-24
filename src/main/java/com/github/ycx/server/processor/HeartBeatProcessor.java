package com.github.ycx.server.processor;

import com.github.ycx.codec.RpcMessage;
import com.github.ycx.codec.RpcMessageRequest;
import com.github.ycx.codec.RpcMessageResponse;
import com.github.ycx.common.sender.Sender;
import org.apache.log4j.Logger;

import java.util.Objects;

/**
 * 心跳检测业务处理器
 * @author Yangcx
 * @create 2023/7/24 22:08
 */
public class HeartBeatProcessor implements Processor{
    Logger logger = Logger.getLogger(HeartBeatProcessor.class);

    @Override
    public void execute(RpcMessageRequest request, Sender sender) {
        Object param = request.getParam();
        if (Objects.equals(param,"ping")){
            logger.info("Heart Beat Processor Execute is ping");
            sender.send(RpcMessage.createResponse(request.getRid(),RpcMessageResponse.ok("pong")));
        }
    }
}
