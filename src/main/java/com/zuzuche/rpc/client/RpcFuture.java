package com.zuzuche.rpc.client;

import com.zuzuche.rpc.protocol.Request;
import com.zuzuche.rpc.protocol.Response;
import lombok.Data;

import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author zhouj
 * @since 2020-08-04
 */
@Data
public class RpcFuture implements Future<Object> {

    private Request request;

    private Response response;

    private Sync sync;

    public RpcFuture(Request request) {
        this.request = request;
        this.sync = new Sync();
    }

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
        return sync.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire(-1);
        if (this.response != null) {
            return response.getResult();
        } else {
            return null;
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    public void done(Response response) {
        this.response = response;
        sync.release(1);
    }

    public static class Sync extends AbstractQueuedSynchronizer {

        //future status
        private final int done = 1;
        private final int pending = 0;

        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == done;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == pending) {
                if (compareAndSetState(pending, done)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        protected boolean isDone() {
            return getState() == done;
        }


    }
}
