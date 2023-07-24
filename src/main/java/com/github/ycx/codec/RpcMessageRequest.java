package com.github.ycx.codec;

/**
 *Rpc 请求
 * @author Yangcx
 * @create 2023/7/19 18:41
 */
public class RpcMessageRequest {
    /**
     * 请求id
     */
    private String rid;
    /**
     * 业务码
     */
    private String BizCode;
    /**
     * 请求参数
     */
    private Object param;

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getBizCode() {
        return BizCode;
    }

    public void setBizCode(String bizCode) {
        BizCode = bizCode;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }

    public static RpcMessageRequest wrap(String bizCode , Object param){
        RpcMessageRequest rpcMessageRequest = new RpcMessageRequest();
        rpcMessageRequest.setBizCode(bizCode);
        rpcMessageRequest.setParam(param);
        return rpcMessageRequest;
    }

    @Override
    public String toString() {
        return "RpcMessageRequest{" +
                "rid='" + rid + '\'' +
                ", BizCode='" + BizCode + '\'' +
                ", param=" + param +
                '}';
    }
}
