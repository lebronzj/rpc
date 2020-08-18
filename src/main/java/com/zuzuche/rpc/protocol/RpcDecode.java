package com.zuzuche.rpc.protocol;

import com.zuzuche.rpc.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author zhouj
 * @since 2020-08-04
 */
public class RpcDecode extends ByteToMessageDecoder {

    private Class<?> generateClass;

    public RpcDecode(Class<?> generateClass) {
        this.generateClass = generateClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int length = byteBuf.readableBytes();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Object o = SerializationUtil.deserialize(bytes,generateClass);
        list.add(o);
    }
}
