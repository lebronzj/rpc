package com.zuzuche.rpc.server;

import com.zuzuche.rpc.protocol.Request;
import com.zuzuche.rpc.protocol.RpcDecode;
import com.zuzuche.rpc.protocol.RpcEncode;
import com.zuzuche.rpc.registry.ServerRegister;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouj
 * @since 2020-08-04
 */
public class RpcServer {

    private ServerRegister serverRegister;

    private String serverAddress;

    private Map<String, Object> map = new HashMap<>();

    public RpcServer(ServerRegister serverRegister, String serverAddress) {
        this.serverRegister = serverRegister;
        this.serverAddress = serverAddress;
        start();
    }

    public void addService(String interfaceName, Object serviceBean) {
        if (!map.containsKey(interfaceName)) {
            map.put(interfaceName, serviceBean);
        }
    }

    public void start() {
        ServerBootstrap serverBootStrap = new ServerBootstrap();
        serverBootStrap.group(new NioEventLoopGroup(), new NioEventLoopGroup()).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new RpcEncode());
                socketChannel.pipeline().addLast(new RpcDecode(Request.class));
                socketChannel.pipeline().addLast(new ServerHandler(map));
            }
        });
        String[] array = serverAddress.split(":");
        serverBootStrap.bind(array[0], Integer.parseInt(array[1]));

        serverRegister.registry(serverAddress);
    }

}
