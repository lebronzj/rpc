package com.zuzuche.rpc.protocol;

import com.zuzuche.rpc.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author zhouj
 * @since 2020-08-04
 */
public class RpcEncode extends MessageToByteEncoder {

    private Class<?> generateClass;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        byte[] bytes = SerializationUtil.serialize(o);
        byteBuf.writeBytes(bytes);
    }
}
