package com.github.ycx.client;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
