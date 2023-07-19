package com.github.ycx.codec;

/**
 * 响应
 * @author Yangcx
 * @create 2023/7/19 18:46
 */
public class RpcMessageResponse {
    /**
     * 响应是否成功
     */
    private boolean success;
    /**
     *错误消息
     */
    private String msg = "";
    /**
     * 响应结果
     */
    private Object result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * 响应成功
     * @param result 响应成功结果
     * @return RpcMessageResponse
     */
    public RpcMessageResponse ok(Object result){
        RpcMessageResponse response = new RpcMessageResponse();
        response.setSuccess(true);
        response.setResult(result);
        return response;
    }

    /**
     * 响应失败
     * @param errorMessage 失败信息
     * @return RpcMessageResponse
     */
    public RpcMessageResponse fail(String errorMessage){
        RpcMessageResponse response = new RpcMessageResponse();
        response.setSuccess(false);
        response.setMsg(errorMessage);
        return response;
    }

    @Override
    public String toString() {
        return "RpcMessageResponse{" +
                "success=" + success +
                ", msg='" + msg + '\'' +
                ", result=" + result +
                '}';
    }
}
