package com.zuzuche.rpc.client;

import com.zuzuche.rpc.protocol.Request;
import com.zuzuche.rpc.protocol.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhouj
 * @since 2020-08-04
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<Response> {

    private Channel channel;

    private Map<String, RpcFuture> map = new ConcurrentHashMap<>();

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response response) throws Exception {
        String requestId = response.getRequestId();
        RpcFuture rpcFuture = map.get(requestId);
        log.info("耗时:{}ms",System.currentTimeMillis()-response.getTimestamp());
        if (rpcFuture != null) {
            map.remove(requestId);
            rpcFuture.done(response);
        }
    }

    public RpcFuture sendRequest(Request request) {
        RpcFuture rpcFuture = new RpcFuture(request);
        map.put(request.getRequestId(), rpcFuture);
        channel.writeAndFlush(request);
        return rpcFuture;
    }
}
