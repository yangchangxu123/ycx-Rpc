package com.github.ycx.codec;

import com.github.ycx.utils.Utils;

import java.util.Objects;

/**
 * 传输报文 ，创建发送报文、响应报文
 * @author Yangcx
 * @create 2023/7/19 18:46
 */
public class RpcMessage {
    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";

    /**
     * 请求id,追踪id
     */
    private String traceId;
    /**
     * 传输方向：request/response
     */
    private String direction;
    /**
     * 消息内容
     */
    private Object content;

    /**
     *  创建请求报文
     * @param request 请求
     * @return RpcMessage
     */
    public static RpcMessage createRequest(RpcMessageRequest request){
        RpcMessage rpcMessage = new RpcMessage();
        rpcMessage.traceId = Utils.getRequestId();
        rpcMessage.direction = REQUEST;
        rpcMessage.content = request;
        return rpcMessage;
    }

    /**
     *
     * @param traceId 请求id
     * @param response  响应
     * @return RpcMessage
     */
    public static RpcMessage createResponse(String traceId ,RpcMessageResponse response){
        RpcMessage rpcMessage = new RpcMessage();
        rpcMessage.traceId = traceId;
        rpcMessage.direction = RESPONSE;
        rpcMessage.content = response;
        return rpcMessage;
    }

    /**
     *  获取请求消息
     * @return RpcMessageRequest
     */
    public RpcMessageRequest getRequest(){
        if (Objects.equals(this.direction,REQUEST)){
            RpcMessageRequest request = (RpcMessageRequest) this.content;
            request.setRid(this.traceId);
            return request;
        }
        throw  new RuntimeException("RpcMessage Type not be request!");
    }

    /**
     * 获取响应消息
     * @return RpcMessageResponse
     */
    public RpcMessageResponse getResponse(){
        if (Objects.equals(this.direction,RESPONSE)){
            return (RpcMessageResponse) this.content;
        }
        throw  new RuntimeException("RpcMessage Type not be response!");
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "RpcMessage{" +
                "traceId='" + traceId + '\'' +
                ", direction='" + direction + '\'' +
                ", content=" + content +
                '}';
    }
}
