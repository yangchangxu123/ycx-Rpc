package com.github.ycx.client;

import com.github.ycx.codec.RpcMessageResponse;

import java.util.concurrent.*;

/**
 * Netty RpcFuture
 * @author Yangcx
 * @create 2023/7/19 21:37
 */
public class RpcFuture implements Future<Object> {
    /**
     * 请求id
     */
    private String rId;
    /**
     * 响应
     */
    private Object response;

    private  final CountDownLatch downLatch = new CountDownLatch(1);

    public RpcFuture(String rId) {
        this.rId = rId;
    }

    /**
     * 用于取消任务的执行。默认的实现可能不支持取消任务，可以根据具体需求进行实现。
     * @param mayInterruptIfRunning 取消任务
     * @return boolean
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    /**
     * 判断任务是否被取消。
     * @return false
     */
    @Override
    public boolean isCancelled() {
        return false;
    }

    /**
     * 判断任务是否已经完成。
     * @return false
     */
    @Override
    public boolean isDone() {
        return false;
    }

    /**
     * 获取任务的结果，如果任务还没有完成，则会阻塞当前线程，直到任务完成并返回结果。
     * @return response
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Override
    public Object get() throws InterruptedException, ExecutionException {
        downLatch.await();
        return response;
    }

    /**
     * 在指定的时间内获取任务的结果，如果任务还没有完成，则会阻塞当前线程，直到任务完成并返回结果，或者超时。
     * @param timeout 超时时间
     * @param unit  单位
     * @return response
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        //阻塞等待结果，在指定时间内得到响应结果，则返回response
        boolean await = downLatch.await(timeout, unit);
        if (await){
            return response;
        }
        throw new RuntimeException("Rpc TimeOut Exception，request id is" + rId);
    }

    public void done(Object response){
        this.response = response;
        downLatch.countDown();

    }
}
