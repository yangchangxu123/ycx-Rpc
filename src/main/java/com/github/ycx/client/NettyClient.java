package com.github.ycx.client;

import com.github.ycx.client.event.IdleEventListener;
import com.github.ycx.client.handler.NettyClientHandler;
import com.github.ycx.client.handler.NettyClientInit;
import com.github.ycx.codec.RpcMessageRequest;
import com.github.ycx.codec.RpcMessageResponse;
import com.github.ycx.common.BizProcessor;
import com.github.ycx.common.sender.SenderNetty;
import com.github.ycx.utils.NetUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import sun.nio.ch.Net;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Rpc 客户端
 * @author Yangcx
 * @create 2023/7/20 9:29
 */
public class NettyClient implements Client{

    Logger logger = Logger.getLogger(NettyClient.class);

    /**
     * 地址
     */
    private final String host;
    /**
     * 端口
     */
    private final int port;

    /**
     * nettyClient 连接池
     */
    private static final Map<String,NettyClient> nettyClientPool = new ConcurrentHashMap<>();

    private final Bootstrap clientBootstrap = new Bootstrap();

    private static final NioEventLoopGroup loopGroup = new NioEventLoopGroup();

    /**
     * 心跳检测器
     */
    private static IdleEventListener idleEventListener = null;

    private static Map<String,BizProcessor> bizProcessorMap = new HashMap<String,BizProcessor>();

    /**
     * 是否连接标识
     */
    private boolean connectedFlag;

    /**
     * 启动器
     */
    CountDownLatch startCountDownLatch = null;

    NettyClientHandler clientHandler =null;

    /**
     * 初始化连接
     * @param host ip地址
     * @param port  端口
     */
    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.clientBootstrap
                .group(loopGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInit(idleEventListener,bizProcessorMap));
        this.connect();
    }

    /**
     * 创建连接
     */
    private void connect() {
        this.connectedFlag = false;
        this.startCountDownLatch = new CountDownLatch(1);

        InetSocketAddress socketAddress = new InetSocketAddress(host, port);
        //连接
        ChannelFuture channelFuture = this.clientBootstrap.connect(socketAddress);
        //异步监听连接结果
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()){
                    clientHandler = channelFuture.channel().pipeline().get(NettyClientHandler.class);
                    connectedFlag = true;
                    startCountDownLatch.countDown();
                    logger.info("Successfully connected to remote server!,Server is : "+host+":"+port);
                }else {
                    startCountDownLatch.countDown();
                    logger.info("Connection to remote server failed! Server is : " +host+":"+port);
                }
            }
        });
    }

    /**
     * 是否正在连接
     */
    private boolean connected(){
        if (!this.connectedFlag){   //未建立连接
            try {
                //5s中内等待连接
                boolean await = this.startCountDownLatch.await(5, TimeUnit.SECONDS);
                if (!await || !this.connectedFlag){
                    return false;
                }
            } catch (InterruptedException e) {
                logger.error("Waiting for remote server connection exception : " +e.getMessage());
            }
        }
        //已完成连接
        return true;

    }

    /**
     * 同步连接
     */
    private boolean  syncConnect(){
        this.connect();
        return connected();
    }


    /**
     *  通过请求地址，获取NettyClient实例
     * @param address 请求地址
     * @return NettyClient
     */
    public NettyClient getInstance(String address){
        NettyClient client = nettyClientPool.get(address);
        if (client == null){
            Pair<String, Integer> pair = NetUtils.splitAddress2IpAndPort(address);
            client = new NettyClient(pair.getKey(),pair.getValue());
            nettyClientPool.put(address,client);
        }
        return client;
    }

    /**
     * 发送请求
     * @param request 请求
     * @param timeout   超时时间
     * @return RpcMessageResponse
     */
    @Override
    public RpcMessageResponse request(RpcMessageRequest request, long timeout) {
        if (!this.connected()){
            if (!this.syncConnect()){
                logger.error("Connection to remote service timeout");
                return RpcMessageResponse.fail("Connection to remote service timeout!");
            }
        }
        SenderNetty senderNetty = this.clientHandler.getSenderNetty();
        if (!senderNetty.available()){
            if (!this.syncConnect()){
                return RpcMessageResponse.fail("Connection to remote service timeout!");
            }
            senderNetty = this.clientHandler.getSenderNetty();
        }
        return (RpcMessageResponse) senderNetty.sendAndRetResponse(request,timeout);
    }

    @Override
    public InetSocketAddress getAddress() {
        return Client.super.getAddress();
    }

    /**
     * 注册业务处理器
     */
    public void registerBizProcessor(String bizCode,BizProcessor processor){
        bizProcessorMap.put(bizCode,processor);
    }
}
