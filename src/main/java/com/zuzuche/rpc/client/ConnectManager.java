package com.zuzuche.rpc.client;

import com.zuzuche.rpc.protocol.Request;
import com.zuzuche.rpc.protocol.Response;
import com.zuzuche.rpc.protocol.RpcDecode;
import com.zuzuche.rpc.protocol.RpcEncode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhouj
 * @since 2020-08-04
 */
@Slf4j
public class ConnectManager {

    private static volatile ConnectManager connectManager;

    public static AtomicInteger atomicInteger = new AtomicInteger(0);

    private List<ClientHandler> handlers = new CopyOnWriteArrayList<>();

    private volatile Boolean state = false;

    private ConnectManager() {

    }

    public static ConnectManager getInstance() {
        if (connectManager == null) {
            synchronized (ConnectManager.class) {
                if (connectManager == null) {
                    connectManager = new ConnectManager();
                }
            }
        }
        return connectManager;
    }

    public ClientHandler getRoundRobinHandle() throws Exception {
        if (!state) {
            throw new Exception("没有服务");
        }
        int size = handlers.size();
        return handlers.get(atomicInteger.getAndAdd(1) % size);
    }

    public void updateServices(List<String> nodeList) {
        if (!CollectionUtils.isEmpty(nodeList)) {
            nodeList.forEach(node -> {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(new NioEventLoopGroup()).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        channelPipeline.addLast(new RpcEncode());
                        channelPipeline.addLast(new RpcDecode(Response.class));
                        channelPipeline.addLast(new ClientHandler());
                    }
                });
                String[] strings = node.split(":");
                InetSocketAddress inetSocketAddress = new InetSocketAddress(strings[0], Integer.parseInt(strings[1]));
                bootstrap.connect(inetSocketAddress).addListener(new ChannelFutureListener() {

                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            ClientHandler clientHandler = channelFuture.channel().pipeline().get(ClientHandler.class);
                            handlers.add(clientHandler);
                            if (handlers.size() > 0) {
                                state = true;
                            }
                        }
                    }
                });
            });

        }
    }
}
