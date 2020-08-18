package com.zuzuche.rpc.server;

import com.zuzuche.rpc.protocol.Request;
import com.zuzuche.rpc.protocol.Response;
import com.zuzuche.rpc.registry.ServerRegister;
import com.zuzuche.rpc.service.TestService;
import com.zuzuche.rpc.service.impl.TestServiceImpl;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author zhouj
 * @since 2020-08-04
 */
@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<Request> {

    private Map<String, Object> map;

    public ServerHandler(Map map) {
        this.map = map;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) throws Exception {
        String interfaceName = request.getInterfaceName();
        Object o = map.get(interfaceName);
        Class c = o.getClass();
        Method method = c.getMethod(request.getMethodName(), request.getParamTypes());
        Object result = method.invoke(o, request.getParams());
        Response response = new Response();
        response.setRequestId(request.getRequestId());
        response.setResult(result);
        response.setTimestamp(request.getTimestamp());
        channelHandlerContext.writeAndFlush(response).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.info("send response requestId:{}", request.getRequestId());
            }
        });
    }

    public static void main(String[] args) {
        ServerRegister serverRegister = new ServerRegister("127.0.0.1:2181");
        RpcServer rpcServer = new RpcServer(serverRegister, "127.0.0.1:18888");
        rpcServer.addService(TestService.class.getName(), new TestServiceImpl());
    }
}
