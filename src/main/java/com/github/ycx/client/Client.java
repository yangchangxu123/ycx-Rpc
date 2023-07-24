package com.github.ycx.client;

import com.github.ycx.codec.RpcMessageRequest;
import com.github.ycx.codec.RpcMessageResponse;

import java.net.InetSocketAddress;

/**
 * @author Yangcx
 * @create 2023/7/20 9:34
 */
public interface Client {
    RpcMessageResponse request(RpcMessageRequest request,long timeout);

    default InetSocketAddress getAddress(){
        return null;
    }

}
